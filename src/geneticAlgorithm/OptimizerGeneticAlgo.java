/*                                             
 *Copyright 2007, 2011 CCLS Columbia University (USA), LIFO University of Orl��ans (France), BRGM (France)
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
package src.geneticAlgorithm;

import src.apriori.AssociationRule;
import src.apriori.Item;
import src.apriori.StandardParametersQuantitative;
import src.graphicalInterface.DialogGraphQuality;
import src.solver.ResolutionContext;
import src.solver.RuleOptimizer;


public class OptimizerGeneticAlgo extends RuleOptimizer {
    
    private GeneticAlgo m_algoGenetique;
    private StandardParametersQuantitative m_parametresReglesQuantitatives = null;
    private ParametersGeneticAlgo m_parametresAlgo = null;
    
    // Tableaux r�pertoriant l'�volution de la qualit� d'une r�gle au fur et � mesure de son optimisation :
    private float [] m_tQualiteMoyenne = null;  //QualiteMean
    private float [] m_tQualiteMin = null;
    private float [] m_tQualiteMax = null;

    // METTRE CETTE VARIABLE A VRAI POUR AFFICHER UN GRAPHE D'EVOLUTION DE LA QUALITE APRES L'OPTIMISATION D'UNE REGLE :
    private static boolean m_bAfficherGrapheQualite = false;
    
    private static boolean m_bSortirQualite = true;

    public OptimizerGeneticAlgo() {
        m_algoGenetique = null;
    }

    // Outrepassement de la fonction de sp�cification du contexte :
    public void DefinirContexteResolution(ResolutionContext contexteResolution) {
        super.DefinirContexteResolution(contexteResolution);
        
        if (super.m_contexteResolution == null) {
            m_algoGenetique = null;
            return;
        }
        
        m_parametresReglesQuantitatives = super.m_contexteResolution.m_parametresReglesQuantitatives;
        m_parametresAlgo = super.m_contexteResolution.m_parametresTechAlgoGenetique;
        m_algoGenetique = new GeneticAlgo(m_parametresAlgo.m_iTaillePopulation, super.m_contexteResolution.m_gestionnaireBD);
        m_algoGenetique.SpecifierParametresStatistiques(m_parametresReglesQuantitatives.m_fMinSupp, m_parametresReglesQuantitatives.m_fMinConf, m_parametresReglesQuantitatives.m_fMinSuppDisjonctions);
        m_algoGenetique.SpecifierParametresGenetiques(m_parametresAlgo.m_fPourcentageCroisement, m_parametresAlgo.m_fPourcentageMutation);

        if (m_bAfficherGrapheQualite || m_bSortirQualite) {
            int m_iNombreEtapesCalculRegle = m_parametresAlgo.m_iNombreGenerations;
            m_tQualiteMoyenne = new float [m_iNombreEtapesCalculRegle];
            m_tQualiteMin = new float [m_iNombreEtapesCalculRegle];
            m_tQualiteMax = new float [m_iNombreEtapesCalculRegle];
        }
    }

    /**Optimize Rule Association
     * @param regle the Association rule
     */
    public boolean OptimiseRegle(AssociationRule regle) {
        long currentTime = System.currentTimeMillis();
        int iNombreItemsQuantitatifs = 0;
        int iIndiceEvolution = 0;
        boolean bRegleEstSolide = false;
        AssociationRule meilleureRegle = null;
        
        if ( (m_algoGenetique == null) || (regle == null) )
            return false;
        
        iNombreItemsQuantitatifs =    regle.CompterItemsGaucheSelonType(Item.ITEM_TYPE_QUANTITATIF)
        + regle.CompterItemsDroiteSelonType(Item.ITEM_TYPE_QUANTITATIF);
        
        // if the rule has uniquely qualitative, no need to optimize:
        if (iNombreItemsQuantitatifs <= 0) {
            
            regle.EvaluerSiQualitative(super.m_contexteResolution);
            
            return (  (regle.m_fSupport >= m_parametresReglesQuantitatives.m_fMinSupp)
            &&(regle.m_fConfiance >= m_parametresReglesQuantitatives.m_fMinConf)  );
            
        }

        // Calcul de la r�gle optimis�e, sur le sch�ma courant :
        
        // Indicate algorithm genetic the template of the rule to optimize :
        m_algoGenetique.SpecifierSchemaRegle(regle);
        m_algoGenetique.GenererReglesPotentiellesInitiales();
        
        do {
            for (iIndiceEvolution = 0; iIndiceEvolution < m_parametresAlgo.m_iNombreGenerations; iIndiceEvolution++) {
                m_algoGenetique.Evoluer();
                
                if (m_bAfficherGrapheQualite  || m_bSortirQualite ) {
                    m_tQualiteMoyenne[iIndiceEvolution] = m_algoGenetique.CalculerQualiteMoyenne();
                    m_tQualiteMin[iIndiceEvolution] = m_algoGenetique.ObtenirPireQualiteCourante();
                    m_tQualiteMax[iIndiceEvolution] = m_algoGenetique.ObtenirMeilleureQualiteCourante();
                }
            }
        }
        while ( m_algoGenetique.InitierNouvellePasse() );
        
        //obtain the best rule
        meilleureRegle = m_algoGenetique.ObtenirMeilleureRegle();

        //if the rule is not null and have enough support and confidence, copy it to rule
        if (meilleureRegle != null) {
            bRegleEstSolide = ((meilleureRegle.m_fSupport >= m_parametresReglesQuantitatives.m_fMinSupp)
            &&(meilleureRegle.m_fConfiance >= m_parametresReglesQuantitatives.m_fMinConf)  );
            if (bRegleEstSolide)
                regle.CopierRegleAssociation(meilleureRegle);
        }
        
        if (m_bAfficherGrapheQualite) {
            DialogGraphQuality fenetreDetailsRegle;
            fenetreDetailsRegle = new DialogGraphQuality(super.m_contexteResolution.m_fenetreProprietaire, true, super.m_contexteResolution);
            fenetreDetailsRegle.SpecifierQualitesMoyennes(m_tQualiteMoyenne);
            fenetreDetailsRegle.SpecifierQualitesMax(m_tQualiteMax);
            fenetreDetailsRegle.SpecifierQualitesMin(m_tQualiteMin);
            fenetreDetailsRegle.ConstruireGraphe();
            fenetreDetailsRegle.setVisible(true);
        }

        return bRegleEstSolide;
    }
}
