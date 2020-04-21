/*                                             
 *Copyright 2007, 2011 CCLS Columbia University (USA), LIFO University of Orleans (France), BRGM (France)
 *
 *Authors: Cyril Nortet, Xiangrong Kong, Ansaf Salleb-Aouissi, Christel Vrain, Daniel Cassard
 *
 *This file is part of QuantMiner.
 *
 *QuantMiner is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 *
 *QuantMiner is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License along with QuantMiner.  If not, see <http://www.gnu.org/licenses/>.
 */
package src.solver;

import src.apriori.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class RuleTester extends Thread { //test rules
    // Classe permettant l'execution d'un code particulier durant le calcul des regles :
	// Class allowing to execute a particular code during the cumputation of rules:
    static public class IndicateurCalculRegles {
        public void IndiquerFinCalcul() { }
        public void EnvoyerInfo(String sNouvelleInfo) { }
        public void IndiquerNombreReglesATester(int iNombreReglesATester) { }
    }
    
    // Definition d'un traitement specifique a inserer au cours de l'execution de l'algorithme Apriori :
    // Definition of a specific treatment to do during the apriori algorithm execution
    public class TraitementPendantCalculFrequents extends AprioriQuantitative.TraitementExternePendantCalcul {
        
        public boolean ExecuterTraitementExterne() {
            try {
                sleep(0);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            return m_bEnExecution;
        }
    }
    
    private ResolutionContext m_contexteResolution;
    private RuleOptimizer m_optimiseurCourant = null; // Object containing the method of optimizing rules
    public boolean m_bEnExecution;
    private boolean m_bResultatDisponible;          // Indique qu'un ensemble de regles vient d'etre calcule en totalite
                                                    // Indicate that a set of rule are completely calculated
    private AprioriQuantitative m_apriori = null;
    private int m_iNombreTotalAttributsQuant = 0;
    private int m_iNombreItemsQuantConsideres = 0;
    private int m_iMinimumItemsQuantConsideres = 0; //minimum # of quantitative attributes in a rule
    private int m_iMaximumItemsQuantConsideres = 0; //maximum # of quantitative attributes in a rule
    private int m_iTailleFrequent = 0;                      //size of a frequent item set
    private int m_iIndiceFrequent = 0;
    private int m_iIndiceCombinaisonItemsQuant = 0;
    private int m_iIndiceRepartitionItems = 0;
    private boolean m_bFinTestRegles = false;
    public int m_iNombreReglesTestees;      // Compteur du nombre de regles deja testees par l'algorithme
                                            // Count the number of rules already tested by the algorithm
    private List<AssociationRule> m_listeRegles;
    private ArrayList<AttributQuantitative> m_listeAttributsQuant;
    private IndicateurCalculRegles m_indicateurCalcul;
    private boolean m_bIndiquerFinCalcul;
    
    private boolean m_bModeSpecialComptabilisationRegles;
    private int m_iNombreReglesComptabilisees;

    private int m_iNombreDisjonctionsGauche = 0;
    private int m_iNombreDisjonctionsDroite = 0;

    // TODO add this as an option the algorithm setup wizard
    private boolean removeRuleSubset = true; // Flag to remove rules which are derived from more general rule
    private boolean removeRedundantQuant = true; // Flag to remove quantitative item that spans whole domain.

    public RuleTester(ResolutionContext contexteResolution, IndicateurCalculRegles indicateurCalcul) {
        m_contexteResolution = contexteResolution;
        m_indicateurCalcul = indicateurCalcul;
        m_bIndiquerFinCalcul = true;
        m_bEnExecution = false;
        m_bResultatDisponible = false;
        m_iNombreReglesTestees = 0;
        m_bModeSpecialComptabilisationRegles = false;
        m_iNombreReglesComptabilisees = 0;

        m_listeRegles = new ArrayList<>();
        
        // Pre-calculate a l'aide de l'algorithme Apriori standard :
        // pre-calculate with apriori standard algorithm
        if (m_contexteResolution == null)
            return;

        float m_fMinSupp;
        switch (m_contexteResolution.m_iTechniqueResolution) {
            
            case ResolutionContext.TECHNIQUE_APRIORI_QUAL :
                m_fMinSupp= m_contexteResolution.m_parametresRegles.m_fMinSupp;
                float m_fMinConf=m_contexteResolution.m_parametresRegles.m_fMinConf;
                m_iMinimumItemsQuantConsideres = 0;
                m_iMaximumItemsQuantConsideres = 0;
                m_iNombreDisjonctionsGauche = 1;  //Disjunctions 'OR'
                m_iNombreDisjonctionsDroite = 1;   
                break;
                
            case ResolutionContext.TECHNIQUE_ALGO_GENETIQUE :

            case ResolutionContext.TECHNIQUE_RECUIT_SIMULE :
                m_fMinSupp= m_contexteResolution.m_parametresReglesQuantitatives.m_fMinSupp;
                m_fMinConf= m_contexteResolution.m_parametresReglesQuantitatives.m_fMinConf;
                m_iNombreDisjonctionsGauche = m_contexteResolution.m_parametresReglesQuantitatives.m_iNombreDisjonctionsGauche;
                m_iNombreDisjonctionsDroite = m_contexteResolution.m_parametresReglesQuantitatives.m_iNombreDisjonctionsDroite;   
                m_iMinimumItemsQuantConsideres = m_contexteResolution.m_parametresReglesQuantitatives.m_iNombreMinAttributsQuant;
                m_iMaximumItemsQuantConsideres = m_contexteResolution.m_parametresReglesQuantitatives.m_iNombreMaxAttributsQuant;
                break;

            default :
                return;    
        }
        
        if (m_contexteResolution.m_gestionnaireBD == null)
            return;

        // Mise en place d'une nouvelle execution de l'algorithme Arpriori, repertoriee dans le contexte d'execution :
        // Setting for a new execution of apriori defined in the context of execution:
        m_contexteResolution.m_aprioriCourant = null;
        m_apriori = new AprioriQuantitative(m_contexteResolution);
        m_contexteResolution.m_aprioriCourant = m_apriori;
        
        m_apriori.SpecifierSupportMinimal(m_fMinSupp);
        m_apriori.SpecifierTraitementExterne(new TraitementPendantCalculFrequents());
        m_apriori.ExecuterPretraitement(true);  //execute pre-process
    }
    
    public void AutoriserIndicationFinCalcul(boolean bIndiquerFinCalcul) {
        m_bIndiquerFinCalcul = bIndiquerFinCalcul;
    }

    //Define the optimizer(e.g. Genetic algorithm) used to optimize rules
    public void DefinirOptimiseurRegle(RuleOptimizer optimiseur) {
        if (optimiseur != null) {
            m_optimiseurCourant = optimiseur;
            m_optimiseurCourant.DefinirContexteResolution(m_contexteResolution);
        }
    }

    private void displayInfo(String msg){
        if (m_indicateurCalcul != null){
            m_indicateurCalcul.EnvoyerInfo(msg);
        }
    }
    
    //start to do optimization
    public void run() {

        if (m_apriori == null)
            return;

        int iTypePriseEnCompte;
        int iIndiceAttributsQuant;
        int iNombreAttributsQuantBase;
        AttributQuantitative attributQuant;

        int iTailleFrequents;
        int iNombreReglesATester;

        m_iNombreReglesTestees = 0;
        m_bResultatDisponible = false;

        boolean bFinInitApriori = false;
        m_bEnExecution = true;
        iTailleFrequents = 2;

        displayInfo("Pre-computation with the Apriori algorithm:\n");

        try {
            while ( (m_bEnExecution) && (!bFinInitApriori) && ( iTailleFrequents <= 3)) {   // suppress later the third condition

                // On genere les listes de K-items frequency successively:     
            	// We generate the list of the frequency of K-items successively:
                displayInfo("  Computing the set of " + iTailleFrequents + " consecutive frequent modalities...");

                bFinInitApriori = !m_apriori.GenererNouvelleListeItemSets();

                if (bFinInitApriori)
                    displayInfo("FINISH!\n");
                else
                    displayInfo("OK\n");

                iTailleFrequents++;

                if (bFinInitApriori || ( iTailleFrequents > 3) ) { // suppress later the second condition

                    m_apriori.ElaguerItemsetsSelonFiltre();

                    // On repertorie uniquement les attributs quantitatifs a prendre en compte :
                    // we list only the quantitative attributes to consider: 
                    m_listeAttributsQuant = new ArrayList<>();
                    m_iNombreTotalAttributsQuant = 0;

                    if (m_iMaximumItemsQuantConsideres > 0) {

                        iNombreAttributsQuantBase=m_apriori.ObtenirNombreAttributsQuantitatifs();
                        for (iIndiceAttributsQuant=0; iIndiceAttributsQuant < iNombreAttributsQuantBase; iIndiceAttributsQuant++) {
                            attributQuant=m_apriori.ObtenirAttributQuantitatif(iIndiceAttributsQuant);
                            if (attributQuant != null) {
                                iTypePriseEnCompte=m_contexteResolution.ObtenirTypePrisEnCompteAttribut(attributQuant.ObtenirNom());

                                if (iTypePriseEnCompte != ResolutionContext.PRISE_EN_COMPTE_ITEM_NULLE_PART) {
                                    m_listeAttributsQuant.add(attributQuant);
                                    m_iNombreTotalAttributsQuant++;
                                }
                            }
                        }

                        if (m_iMaximumItemsQuantConsideres > m_iNombreTotalAttributsQuant)
                            m_iMaximumItemsQuantConsideres=m_iNombreTotalAttributsQuant;
                    }

                    displayInfo("\n\nComputing the number of rules to test...");

                    //number of rules to test
                    iNombreReglesATester = ComptabiliserNombreMaxReglesTestees();

                    if (m_indicateurCalcul != null) {
                        m_indicateurCalcul.IndiquerNombreReglesATester(iNombreReglesATester);
                        m_indicateurCalcul.EnvoyerInfo(":  " + iNombreReglesATester + " rules.\n\n\n");
                    }
                }
            }

            // Calculate the rules:
            if (m_bEnExecution) {

                m_iNombreItemsQuantConsideres = m_iMinimumItemsQuantConsideres;
                m_iTailleFrequent = Math.max(2-m_iNombreItemsQuantConsideres, 0);          
                m_iIndiceFrequent = 0;
                m_iIndiceCombinaisonItemsQuant = 0;                
                m_iIndiceRepartitionItems = 0;
                m_bFinTestRegles = false;
                m_bModeSpecialComptabilisationRegles = false;

                //print the information of the context to the context text area
                displayInfo(m_contexteResolution.ObtenirInfosContexte(false));

                while ( (m_bEnExecution) && (!m_bFinTestRegles) ) {
                    //Calculate new rule
                    CalculerNouvelleRegle();

                    try { sleep(1); }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (removeRuleSubset) {
                // TODO workout something better than this brute force method to prune the ruleset
                displayInfo("Pruning Rule Set");
                m_listeRegles.sort(new Comparator<AssociationRule>() {
                    @Override
                    public int compare(AssociationRule o1, AssociationRule o2) {
                        return Integer.compare(o2.getLeftItems().size(), o1.getLeftItems().size());
                    }
                });
                // Collect all the single rul e component
                List<AssociationRule> finalRuleList=new ArrayList<>(m_listeRegles);
                for (AssociationRule rule : m_listeRegles) {
                    for (AssociationRule subRule : m_listeRegles) {
                        if (subRule.isSubRule(rule) && !subRule.equals(rule)) {
                            displayInfo("Comparing Rules : " + rule.toString() + " with "
                                    + subRule.toString() + "\n");
                            displayInfo("Removing : " + subRule.toString() + "\n");
                            finalRuleList.remove(subRule);
                        }
                    }
                }
                m_listeRegles=finalRuleList;
            }
        }
        catch (java.lang.OutOfMemoryError e) {
            displayInfo( "\n\n\nDEPASSEMENT DES CAPACITES MEMOIRE !" );
            displayInfo( "\nPlease intensify filtering or reduce the minimum support...\n" );
        }
        
        m_bResultatDisponible = true;
        
        //Calculation finishes
        if ( (m_indicateurCalcul != null) && (m_bIndiquerFinCalcul) )
            m_indicateurCalcul.IndiquerFinCalcul();
    }
    //END OF RUN

    // Simule une optimisation afin de compter le nombre de regles qui seront testees :
    // Simulate an optimization to count the number of rules that will be tested
    private int ComptabiliserNombreMaxReglesTestees() {
        int iNombreNouvellesBoucles = 0;
        
        if (m_apriori == null)
            return 0;

        m_iNombreItemsQuantConsideres = m_iMinimumItemsQuantConsideres;
        m_iTailleFrequent = Math.max(2-m_iNombreItemsQuantConsideres, 0);
        m_iIndiceFrequent = 0;
        m_iIndiceCombinaisonItemsQuant = 0;                
        m_iIndiceRepartitionItems = 0;
        m_bFinTestRegles = false;
        m_bModeSpecialComptabilisationRegles = true;
        m_iNombreReglesComptabilisees = 0;

        while ( (m_bEnExecution) && (!m_bFinTestRegles) ) {
            CalculerNouvelleRegle();
            iNombreNouvellesBoucles++;
            
            if (iNombreNouvellesBoucles > 25000) {
                try { sleep(1); }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                iNombreNouvellesBoucles = 0;
                if (m_indicateurCalcul != null)
                    m_indicateurCalcul.EnvoyerInfo(".");
            }
        }
        // System.out.println(m_iNombreReglesComptabilisees);
        return m_iNombreReglesComptabilisees;
    }
    
    //calculate new rules
    private void CalculerNouvelleRegle() {
        ItemSet itemSetFrequent = null;
        boolean [] tRepartitionItems;
        int iIndiceAttributQuantAjoute = 0;
        boolean [] tCombinaisonItemsQuant = null;

        if (m_optimiseurCourant == null) {
            m_bFinTestRegles = true;
            return;
        }           

        // On arrete quand on a teste toutes les regles possibles dont le nombre d'attributs quantitatifs ne depasse pas le nombre autorise :
        // We stop when we have tested all the possible rules for which the number of quantative attributes does not exceed the authorized number: 
        if (m_iNombreItemsQuantConsideres > m_iMaximumItemsQuantConsideres) {
            m_bFinTestRegles = true;
            return;
        }
        
        if (m_iTailleFrequent > 0) {
            
            itemSetFrequent = m_apriori.RecupererFrequent(m_iTailleFrequent, m_iIndiceFrequent);
            if (itemSetFrequent == null) {
                m_iTailleFrequent++;
                m_iIndiceFrequent = 0;
                m_iIndiceCombinaisonItemsQuant = 0;
                m_iIndiceRepartitionItems = 0;
                itemSetFrequent = m_apriori.RecupererFrequent(m_iTailleFrequent, m_iIndiceFrequent);
            }

            // Cas ou tous les items ont ete traites pour un nombre d'attributs quantitatifs donne :
            // Case where all the items are treated for a given number of quantitative attribute:
            if (itemSetFrequent == null) {
                m_iNombreItemsQuantConsideres++;
                m_iTailleFrequent = Math.max(2-m_iNombreItemsQuantConsideres, 0);
                m_iIndiceFrequent = 0;
                m_iIndiceCombinaisonItemsQuant = 0;
                m_iIndiceRepartitionItems = 0;
                return;
            }
        }
            
        if (m_iNombreItemsQuantConsideres > 0) {

            tCombinaisonItemsQuant = AprioriQuantitative.CalculerEnsemblesItems(m_iIndiceCombinaisonItemsQuant,
                    m_iNombreTotalAttributsQuant, m_iNombreItemsQuantConsideres);
            if (tCombinaisonItemsQuant==null) {
                if (m_iTailleFrequent == 0) {   // Cas ou on testait des regles 100% quantitatives
                                                // case where we tested pure quantitative rules
                    m_iTailleFrequent++;
                    m_iIndiceFrequent = 0;
                } else {
                    m_iIndiceFrequent++;
                }
                m_iIndiceCombinaisonItemsQuant = 0;
                m_iIndiceRepartitionItems = 0;
                return;
            }
        }

        // Calcul de la repartition des items a droite et a gauche. Dans le tableau
        // ou chaque indice correspond a un item (les "iTailleFrequent" premiers sont les items qualitatifs)
        // on placera les items ayant la valeur "vrai" dans la case du tableau qui lui correspond
        // dans la partie gauche de la regle, les autres integrant la partie droite.
        
        // Calculate repartition of items on the right and left. In the table
        // where each index corresponds to an item (the first "iTailleFrequent" are the qualitative items
        // we will place the items having "true" value in their corresponding table cell 
        // in the right side of the rule, the rest going to the right side.
        tRepartitionItems = AprioriQuantitative.CalculerRepartitionItems(m_iIndiceRepartitionItems,
                m_iTailleFrequent+m_iNombreItemsQuantConsideres);

        if (tRepartitionItems == null) {
            if (m_iNombreItemsQuantConsideres==0)
                m_iIndiceFrequent++;
            else
                m_iIndiceCombinaisonItemsQuant++;
            m_iIndiceRepartitionItems = 0;
        }
        else {

            // Construction du schema de la rule optimiser :
            // Construct rule optimizer scheme

            // On compte le nombre d'items qu'on va placer a gauche :
            // We count the number of items to be placed on the left:
            int iNombreItemsGauche = 0;
            int iNombreItemsDroite = 0;
            for(boolean repartitionItem : tRepartitionItems){
                if (repartitionItem){
                    iNombreItemsGauche++;
                } else {
                    iNombreItemsDroite++;
                }
            }

            //create a new rule template
            AssociationRule regle = new AssociationRule(iNombreItemsGauche, iNombreItemsDroite,
                    m_iNombreDisjonctionsGauche, m_iNombreDisjonctionsDroite);

            // On specifie les items qualitatifs :
            // We specify all qualitative items:
            for (int iIndiceItemRegle = 0; iIndiceItemRegle < m_iTailleFrequent; iIndiceItemRegle++) {
                ItemQualitative itemQual = Objects.requireNonNull(itemSetFrequent).ObtenirItem(iIndiceItemRegle);
                if (tRepartitionItems[iIndiceItemRegle]) {
                    regle.AssignerItemGauche(itemQual);
                } else {
                    regle.AssignerItemDroite(itemQual);
                }
            }

            // On specifie les attributs quantitatifs :
            // We specify all quantitative items:
            if (m_iNombreItemsQuantConsideres > 0) {
                for (int iIndiceAttributQuant = 0; iIndiceAttributQuant < m_iNombreTotalAttributsQuant; iIndiceAttributQuant++) {
                    if (Objects.requireNonNull(tCombinaisonItemsQuant)[iIndiceAttributQuant]) {
                        AttributQuantitative attributQuant = (m_listeAttributsQuant.get(iIndiceAttributQuant));
                        if (tRepartitionItems[m_iTailleFrequent + iIndiceAttributQuantAjoute]) {
                            ItemQuantitative itemQuant = new ItemQuantitative(attributQuant, m_iNombreDisjonctionsGauche);
                            regle.AssignerItemGauche(itemQuant);
                        }
                        else {
                            ItemQuantitative itemQuant = new ItemQuantitative(attributQuant, m_iNombreDisjonctionsDroite);
                            regle.AssignerItemDroite(itemQuant);
                        }
                        iIndiceAttributQuantAjoute++;
                    }
                }
            }

            if ( m_contexteResolution.EstRegleValide(regle) ) {
                // Dans le mode de comptabilisation des r�gles, on n'a rien � optimiser :
            	// if in the mode of calculating max rule to test, simply increase m_iNombreReglesComptabilisees
                if (m_bModeSpecialComptabilisationRegles)
                    m_iNombreReglesComptabilisees++;

                // Otherwise, optimise the rule:
                else {
                    if (m_optimiseurCourant.OptimiseRegle(regle)) {
                        if (removeRedundantQuant) {
                            List<Item> itemsToRemove=new ArrayList<>();
                            for (Item item : regle.getLeftItems()) {
                                if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {
                                    ItemQuantitative itemQuant=(ItemQuantitative) item;
                                    AttributQuantitative attrQuant=itemQuant.m_attributQuant;

                                    if (itemQuant.m_tBornes[0] == attrQuant.getM_fBorneMin()
                                            && itemQuant.m_tBornes[1] == attrQuant.getM_fBorneMax()) {
                                        itemsToRemove.add(item);
                                    }
                                }
                            }
                            for (Item item : itemsToRemove) {
                                if (regle.getLeftItems().size() > 1) {
                                    if (regle.removeLeftItem(item)) {
                                        displayInfo("Removed " + item + " from "
                                                + regle.getLeftItems().toString() + "\n");
                                    }
                                }
                            }
                            if (!m_listeRegles.contains(regle)) {
                                m_listeRegles.add(regle);
                            }
                        } else {
                            m_listeRegles.add(regle);
                        }
                    }
                    // On indique qu'on vient de tester une nouvelle forme de regle :
                    // we indicate that we just tested a new form of rule:
                    m_iNombreReglesTestees++;
                }
            }
            m_iIndiceRepartitionItems++;
        }
    }
    
    public void ArreterExecution() {
        m_bEnExecution = false;
    }
    
    public AssociationRule ObtenirRegleCalculee(int iIndiceRegle) {
        if (!m_listeRegles.isEmpty() && iIndiceRegle < m_listeRegles.size()){
            return m_listeRegles.get(iIndiceRegle);
        }

        return null;
    }
    
    public List<AssociationRule> ObtenirListeReglesOptimales() {
        return m_listeRegles;
    }

    public boolean EstResultatDisponible() {
        return m_bResultatDisponible;
    }
}