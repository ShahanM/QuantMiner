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
package src.graphicalInterface;

import javax.swing.*;

import src.apriori.AttributQualitative;
import src.apriori.AttributQuantitative;
import src.database.DataColumn;
import src.database.DatabaseAdmin;
import src.solver.*;
import src.tools.*;

import java.awt.*;



public class PanelTechnConfig extends DatabasePanelAssistant { //step 3 parameter configuration

    // Identification des zones o� int�grer des panneaux de choose parameter du proc�d� d'extraction :
    static final int CONTENEUR_PARAM_REGLES = 1;  //rules parameter 
    static final int CONTENEUR_PARAM_TECH = 2;    //technique parameter
    
    // Identification des different panneaux de param�trage des r�gles � extraire :
    static final int PANNEAU_PARAM_REGLES_AUCUN = 0;
    static final int PANNEAU_PARAM_REGLES_STANDARD = 1;
    static final int PANNEAU_PARAM_REGLES_QUANTITATIVES_STANDARD = 2;
    
    // Identification des different panneaux de param�trage des techniques d'extraction :
    static final int PANNEAU_PARAM_TECH_SANS_CONFIGURATION = 1;
    static final int PANNEAU_PARAM_TECH_GENETIQUE = 2;
    static final int PANNEAU_PARAM_TECH_RECUIT = 3;
    static final int PANNEAU_PARAM_TECH_CHARGEMENT = 4;
    
    //Two panels, technique panel and rule panel
    PanelBaseParam m_panneauParamsRegles = null; //  Rule parameter Panel 
    PanelBaseParam m_panneauParamsTech = null;   // Technique parameter panel


    /** Creates new form PanneauConfigTechnique */
    public PanelTechnConfig(ResolutionContext contexteResolution) {
        super(contexteResolution);
        
        m_panneauParamsRegles = null;
        m_panneauParamsTech = null;
        
        initComponents();
        
        super.DefinirEtape(3, "Parameters configuration", ENV.REPERTOIRE_AIDE+"technical_setting.htm");
        super.DefinirPanneauPrecedent(MainWindow.PANNEAU_PRE_EXTRACION);  //previous step is step2
        super.DefinirPanneauSuivant(MainWindow.PANNEAU_TECH_GENERIQUE);   //next step is step4
        super.initBaseComponents();  
        ArrangerDisposition();
        
        switch (m_contexteResolution.m_iTechniqueResolution) {
            
            case ResolutionContext.TECHNIQUE_APRIORI_QUAL :
                jComboTechnique.setSelectedItem("Standard Apriori");
                ActiverPanneauAssistant(ResolutionContext.TECHNIQUE_APRIORI_QUAL);  
                break;
            
            case ResolutionContext.TECHNIQUE_ALGO_GENETIQUE :
                jComboTechnique.setSelectedItem("Genetic algorithm");
                ActiverPanneauAssistant(ResolutionContext.TECHNIQUE_ALGO_GENETIQUE); 
                break;
                
            case ResolutionContext.TECHNIQUE_RECUIT_SIMULE :
                jComboTechnique.setSelectedItem("Simulated annealing");
                ActiverPanneauAssistant(ResolutionContext.TECHNIQUE_RECUIT_SIMULE); 
                break;
                
            case ResolutionContext.TECHNIQUE_CHARGEMENT :
                jComboTechnique.setSelectedItem("Load a set of precomputed rules");
                ActiverPanneauAssistant(ResolutionContext.TECHNIQUE_CHARGEMENT); 
                break;
                
            default :
        }

    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jComboTechnique = new javax.swing.JComboBox();
        jButtonInfoTechnique = new javax.swing.JButton();
        jLabelTechnique = new javax.swing.JLabel();
        jScrollPaneParamRegles = new javax.swing.JScrollPane();  //rule scroll panel 
        jScrollPaneParamTech = new javax.swing.JScrollPane();    //technical rule scroll panel

        setLayout(null);
        
        //Start of ComboBox of Technique selection
        jComboTechnique.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Standard Apriori", "Genetic algorithm", "Simulated annealing", "Load a set of precomputed rules" }));
        jComboTechnique.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboTechniqueActionPerformed(evt);
            }
        });

        add(jComboTechnique);
        jComboTechnique.setBounds(170, 20, 390, 30);
        //End of ComboBox of Technique selection
        
        //Start of the help button next to ComboBox
        jButtonInfoTechnique.setText("?");
        jButtonInfoTechnique.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonInfoTechniqueActionPerformed(evt);
            }
        });

        add(jButtonInfoTechnique);
        jButtonInfoTechnique.setBounds(570, 20, 50, 30);
        //End of the help button next to ComboBox
        
        //Start of the label of the technique selection
        jLabelTechnique.setText("Technique:");
        add(jLabelTechnique);
        jLabelTechnique.setBounds(20, 20, 140, 30);
        //End of the label of the technique selection

        //Start of rules parameter selection panel
        jScrollPaneParamRegles.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Rule parameters", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 3, 14)));
        add(jScrollPaneParamRegles);
        jScrollPaneParamRegles.setBounds(20, 100, 600, 90);
        //End of rules parameter selection panel
        
        //Start of technical parameter selection panel
        jScrollPaneParamTech.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Technique parameters", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 3, 14)));
        add(jScrollPaneParamTech);
        jScrollPaneParamTech.setBounds(20, 230, 600, 120);
        //End of technical parameter selection panel

    }// </editor-fold>//GEN-END:initComponents

    
    private void jComboTechniqueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboTechniqueActionPerformed
        String sTechniqueSelectionnee = null;                                 //the name of the selected item
        int iTechniqueSelectionnee = ResolutionContext.TECHNIQUE_INDEFINIE;  //at the beginning, technique is undefined
        boolean bSuiteAutorisee = false;
            
        sTechniqueSelectionnee = (String)((JComboBox)evt.getSource()).getSelectedItem();
        if (sTechniqueSelectionnee != null) {
            if (sTechniqueSelectionnee.equals("Standard Apriori")) 
                iTechniqueSelectionnee = ResolutionContext.TECHNIQUE_APRIORI_QUAL;
            else if (sTechniqueSelectionnee.equals("Genetic algorithm"))
                iTechniqueSelectionnee = ResolutionContext.TECHNIQUE_ALGO_GENETIQUE;
            else if (sTechniqueSelectionnee.equals("Simulated annealing"))
                iTechniqueSelectionnee = ResolutionContext.TECHNIQUE_RECUIT_SIMULE;
            else if (sTechniqueSelectionnee.equals("Load a set of precomputed rules"))//("Chargement d'un fichier de r�gles pr�-calcul�es"))
                iTechniqueSelectionnee = ResolutionContext.TECHNIQUE_CHARGEMENT;
        }
        
        if (iTechniqueSelectionnee != ResolutionContext.TECHNIQUE_INDEFINIE) { //user selected an algorithm
            bSuiteAutorisee = true;
            
            if (m_panneauParamsRegles != null)
                bSuiteAutorisee = bSuiteAutorisee && m_panneauParamsRegles.EnregistrerParametres();
        
            if ( bSuiteAutorisee && (m_panneauParamsTech != null) )
                bSuiteAutorisee = bSuiteAutorisee && m_panneauParamsTech.EnregistrerParametres();
            
            if (bSuiteAutorisee)
                ActiverPanneauAssistant(iTechniqueSelectionnee);
        }
    }//GEN-LAST:event_jComboTechniqueActionPerformed

    
    private void jButtonInfoTechniqueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonInfoTechniqueActionPerformed
        DialogHelp dialogAide = null;
        String sNomFichierAide = null;
        
        switch (m_contexteResolution.m_iTechniqueResolution) {
            case ResolutionContext.TECHNIQUE_APRIORI_QUAL:
                sNomFichierAide = "apriori_english.htm";
                break;
            case ResolutionContext.TECHNIQUE_ALGO_GENETIQUE:
                sNomFichierAide = "genetic_algorithm.htm";
                break;
            case ResolutionContext.TECHNIQUE_RECUIT_SIMULE:
                sNomFichierAide = "simulated_annealing.htm";
                break;
            case ResolutionContext.TECHNIQUE_CHARGEMENT:
                sNomFichierAide = "rules_loading.htm";
                break;                
        }        
        
        if (sNomFichierAide != null) {
            dialogAide = new DialogHelp(ENV.REPERTOIRE_AIDE+sNomFichierAide, null, true);
            dialogAide.show();
        }
    }//GEN-LAST:event_jButtonInfoTechniqueActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonInfoTechnique;
    private javax.swing.JComboBox jComboTechnique;
    private javax.swing.JLabel jLabelTechnique;
    private javax.swing.JScrollPane jScrollPaneParamRegles;
    private javax.swing.JScrollPane jScrollPaneParamTech;
    // End of variables declaration//GEN-END:variables
    
    
    
    public void ActiverPanneauAssistant(int iTechnique) {
        
        switch (iTechnique) {
            
            // Technique, the chosen algorithm:
            case ResolutionContext.TECHNIQUE_ALGO_GENETIQUE :
                m_contexteResolution.m_iTechniqueResolution = ResolutionContext.TECHNIQUE_ALGO_GENETIQUE;
                ActiverPanneauAssistant(CONTENEUR_PARAM_REGLES, PANNEAU_PARAM_REGLES_QUANTITATIVES_STANDARD);
                ActiverPanneauAssistant(CONTENEUR_PARAM_TECH, PANNEAU_PARAM_TECH_GENETIQUE);
                break;
                
            // Technique simulated annealing
            case ResolutionContext.TECHNIQUE_RECUIT_SIMULE :
                m_contexteResolution.m_iTechniqueResolution = ResolutionContext.TECHNIQUE_RECUIT_SIMULE;
                ActiverPanneauAssistant(CONTENEUR_PARAM_REGLES, PANNEAU_PARAM_REGLES_QUANTITATIVES_STANDARD);
                ActiverPanneauAssistant(CONTENEUR_PARAM_TECH, PANNEAU_PARAM_TECH_RECUIT);
                break;
                
                //Load a set of precomputed rules
            case ResolutionContext.TECHNIQUE_CHARGEMENT :  
                m_contexteResolution.m_iTechniqueResolution = ResolutionContext.TECHNIQUE_CHARGEMENT;
                ActiverPanneauAssistant(CONTENEUR_PARAM_REGLES, PANNEAU_PARAM_REGLES_AUCUN);
                ActiverPanneauAssistant(CONTENEUR_PARAM_TECH, PANNEAU_PARAM_TECH_CHARGEMENT);
                break;               
            
            // By default, it is Apriori standard(i think it is for itemset, i.e. categorical)(qualitatif uniquement) :
            default:
                m_contexteResolution.m_iTechniqueResolution = ResolutionContext.TECHNIQUE_APRIORI_QUAL;
                ActiverPanneauAssistant(CONTENEUR_PARAM_REGLES, PANNEAU_PARAM_REGLES_STANDARD);
                ActiverPanneauAssistant(CONTENEUR_PARAM_TECH, PANNEAU_PARAM_TECH_SANS_CONFIGURATION);
        }
        
        DisposerPanneauxParams();
        jScrollPaneParamRegles.validate();
        jScrollPaneParamTech.validate();
    }
    
    
    
    public void ActiverPanneauAssistant(int iConteneur, int iPanneau) {

        PanelBaseParam panneauAncien = null;
        PanelBaseParam panneauActive = null;
        JScrollPane conteneur = null;
        
        
        // Dis-activate the existing panel
        
        if (iConteneur==CONTENEUR_PARAM_REGLES)
            panneauAncien = m_panneauParamsRegles;
        else if (iConteneur==CONTENEUR_PARAM_TECH)
            panneauAncien = m_panneauParamsTech;

        if (panneauAncien != null) {
            panneauAncien.setVisible(false);
            panneauAncien = null;
        }
   
        
        // Active the specific panel:
        
        if (iConteneur == CONTENEUR_PARAM_REGLES) { //if it is rule parameter panel
            
            switch (iPanneau) {
                
                case PANNEAU_PARAM_REGLES_AUCUN :  //no panel to display
                    panneauActive = null;
                    break;               
                
                
                case PANNEAU_PARAM_REGLES_STANDARD : //show standard rule parameter panel
                    panneauActive = (PanelRuleParam)( new PanelRuleParam(m_contexteResolution) );
                    break;
                    
                    
                case PANNEAU_PARAM_REGLES_QUANTITATIVES_STANDARD :  //show quantitative standard rule parameter panel
                    panneauActive = (PanelQuantitativeRuleParam)( new PanelQuantitativeRuleParam(m_contexteResolution) );
                    break;                    
                    
            }
    
            m_panneauParamsRegles = panneauActive;
        }
        
        else if (iConteneur==CONTENEUR_PARAM_TECH) {  //if it is technique parameter panel
            
            switch (iPanneau) {
                
                case PANNEAU_PARAM_TECH_SANS_CONFIGURATION : //
                    panneauActive = null;
                    break;
                
                case PANNEAU_PARAM_TECH_GENETIQUE :  //generic algorithm
                    panneauActive = (PanelGeneticParam)( new PanelGeneticParam(m_contexteResolution) );
                    break;

                case PANNEAU_PARAM_TECH_RECUIT :  //simulated annealing algorithm
                    panneauActive = (PanelSimulatedParam)( new PanelSimulatedParam(m_contexteResolution) );
                    break;
                    
                case PANNEAU_PARAM_TECH_CHARGEMENT : //load a precomputed rule file
                    panneauActive = (PanelParamLoading)( new PanelParamLoading(m_contexteResolution) );
                    break;                    

            }

            m_panneauParamsTech = panneauActive;
        }
      
        
        // Inclusion du panneau dans son conteneur ad�quat :
        
        conteneur = null;
        if (iConteneur==CONTENEUR_PARAM_REGLES) //if it is rule parameter panel
            conteneur = jScrollPaneParamRegles;
        else if (iConteneur==CONTENEUR_PARAM_TECH)//if it is technique parameter panel
            conteneur = jScrollPaneParamTech;
        
        if (conteneur != null) {
            if (panneauActive == null) {
                conteneur.setViewportView(new JPanel());
                conteneur.setVisible(false);
            }
            else {
                conteneur.setViewportView(panneauActive);
                conteneur.setVisible(true);
            }
        }
       
    }
    
    
    // Outrepassement de la m�thode m�re pour l'ajustement des champs :
    void ArrangerDisposition() {
        super.ArrangerDisposition();
       
        jLabelTechnique.setLocation(jLabelTechnique.getX(), super.m_zoneControles.y);
        jComboTechnique.setLocation(jComboTechnique.getX(), super.m_zoneControles.y);
        jButtonInfoTechnique.setLocation(jButtonInfoTechnique.getX(), super.m_zoneControles.y);
        
        DisposerPanneauxParams();
   }
    
    
    // Dispose dans la fen�tre les 2 panneaux de configuration, de la meilleure mani�re possible :
    void DisposerPanneauxParams() {
        int iMaxHauteurParamRegles = 0;
        int iMaxHauteurParamTech = 0;
        int iMaxHauteurParams = 0;
        int iHauteurParamRegles = 0;
        int iHauteurParamTech = 0;
        int iCumulInsetsY = 0;
        int iPositionYParamTech = 0;
        Insets insets = null;
        Dimension dimensionPanneau = null;
        Dimension dimensionElement = null;
        Point positionElement = null;
        Dimension dimensionSupport = null; // dimensions du panneau de support

        iMaxHauteurParams = super.m_zoneControles.height - (jLabelTechnique.getHeight() + 40);

        if (m_panneauParamsTech != null)
            iMaxHauteurParamRegles = iMaxHauteurParams / 2;
        else 
            iMaxHauteurParamRegles = iMaxHauteurParams;
        
        
        // Positionnement du panneau de configuration des r�gles :
        
        iHauteurParamRegles = 0;
        if (m_panneauParamsRegles != null) {
            
            insets = (jScrollPaneParamRegles.getBorder()).getBorderInsets(jScrollPaneParamRegles);
            iCumulInsetsY = insets.top + insets.bottom;
            
            dimensionPanneau = m_panneauParamsRegles.getPreferredSize();
            if ((dimensionPanneau.height+iCumulInsetsY) <= iMaxHauteurParamRegles) {
                iHauteurParamRegles = dimensionPanneau.height + iCumulInsetsY;
                jScrollPaneParamRegles.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            }
            else {
                iHauteurParamRegles = iMaxHauteurParamRegles;
                jScrollPaneParamRegles.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            }
        
            jScrollPaneParamRegles.setBounds(
                super.m_zoneControles.x,
                jLabelTechnique.getY() + jLabelTechnique.getHeight() + 20,
                super.m_zoneControles.width,
                iHauteurParamRegles);
        }    

        
        // Positionnement du panneau de configuration de la technique d'extraction :
        
        if (m_panneauParamsRegles != null) {
            iMaxHauteurParamTech = iMaxHauteurParams - jScrollPaneParamRegles.getHeight();
            iPositionYParamTech = jScrollPaneParamRegles.getY() + jScrollPaneParamRegles.getHeight() + 20;
        }
        else {
            iMaxHauteurParamTech = iMaxHauteurParams;
            iPositionYParamTech = jLabelTechnique.getY() + jLabelTechnique.getHeight() + 20;
        }

        iHauteurParamTech = 0;
        if (m_panneauParamsTech != null) {
            
            insets = (jScrollPaneParamRegles.getBorder()).getBorderInsets(jScrollPaneParamTech);
            iCumulInsetsY = insets.top + insets.bottom;
            
            dimensionPanneau = m_panneauParamsTech.getPreferredSize();
            if ((dimensionPanneau.height+iCumulInsetsY) <= iMaxHauteurParamTech) {
                iHauteurParamTech = dimensionPanneau.height + iCumulInsetsY;
                jScrollPaneParamTech.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            }
            else {
                iHauteurParamTech = iMaxHauteurParamTech;
                jScrollPaneParamTech.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            }
            
            jScrollPaneParamTech.setBounds(
                super.m_zoneControles.x,
                iPositionYParamTech,
                super.m_zoneControles.width,
                iHauteurParamTech);
         }
        
    }
    
    
    // Outrepassement de la m�thode m�re pour mettre � jour les structures de donn�es 
    // suivant ce qui a �t� entr� dans les champs de contr�le :
    public boolean SychroniserDonneesInternesSelonAffichage() {
        boolean bSuiteAutorisee = false;
        
        // Enregistrement des param�tres utilisateur :
        
        bSuiteAutorisee = true;
        
        if (m_panneauParamsRegles != null)
            bSuiteAutorisee = bSuiteAutorisee && m_panneauParamsRegles.EnregistrerParametres();
        
        if (m_panneauParamsTech != null)
            bSuiteAutorisee = bSuiteAutorisee && m_panneauParamsTech.EnregistrerParametres();
        
        return bSuiteAutorisee;
    }
    
    
    
    // Outrepassement de la m�thode m�re pour des traitements sp�cifiques :
    protected boolean TraitementsSpecifiquesAvantSuivant() {
        boolean bSuiteAutorisee = false;
        String sTechniqueSelectionnee = null;
        int iTechniqueSelectionnee = 0;

        bSuiteAutorisee = super.TraitementsSpecifiquesAvantSuivant();
        
        if (!bSuiteAutorisee)
            return false;
        
        sTechniqueSelectionnee = (String)(jComboTechnique.getSelectedItem());
        if (sTechniqueSelectionnee == null)
            return false;
        
        if (sTechniqueSelectionnee.equals("Standard Apriori")) {
        	if (getQuantitativeNumber() != 0){
        		JOptionPane.showMessageDialog(null, "Cannot goto Step 4 with Standard Apriori, as one or more attributes are quantitative", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        	}
            iTechniqueSelectionnee = ResolutionContext.TECHNIQUE_APRIORI_QUAL;
            super.DefinirPanneauSuivant(MainWindow.PANNEAU_TECH_GENERIQUE);
        }
        else if (sTechniqueSelectionnee.equals("Genetic algorithm")) {
            iTechniqueSelectionnee = ResolutionContext.TECHNIQUE_ALGO_GENETIQUE;
            super.DefinirPanneauSuivant(MainWindow.PANNEAU_TECH_GENERIQUE);
        }
        else if (sTechniqueSelectionnee.equals("Simulated annealing")) {
            iTechniqueSelectionnee = ResolutionContext.TECHNIQUE_RECUIT_SIMULE;
            super.DefinirPanneauSuivant(MainWindow.PANNEAU_TECH_GENERIQUE);
        }
        else if (sTechniqueSelectionnee.equals("Load a set of precomputed rules")){//("Chargement d'un fichier de r�gles pr�-calcul�es")) {
            iTechniqueSelectionnee = ResolutionContext.TECHNIQUE_CHARGEMENT;
            super.DefinirPanneauSuivant(MainWindow.PANNEAU_RESULTATS); //since you already have rules, just display the result
        }
        else
            return false;
        
        return true;
    }
    
    int getQuantitativeNumber(){
    	 DataColumn colonneDonnees = null;
    	 DatabaseAdmin gestionnaireBD = m_contexteResolution.m_gestionnaireBD;
         int iNombreColonnes = 0;
         int iIndiceColonne = 0;
         int iTypePriseEnCompte = 0;
         int numQuantitave = 0;
         iNombreColonnes = gestionnaireBD.ObtenirNombreColonnesPrisesEnCompte(); //obtain the number of selected columns??
         
         for (iIndiceColonne = 0; iIndiceColonne < iNombreColonnes; iIndiceColonne++) { 
             colonneDonnees = gestionnaireBD.ObtenirColonneBDPriseEnCompte(iIndiceColonne);
             iTypePriseEnCompte = m_contexteResolution.ObtenirTypePrisEnCompteAttribut(colonneDonnees.m_sNomColonne);
             
             if (colonneDonnees.m_iTypeValeurs == DatabaseAdmin.TYPE_VALEURS_COLONNE_REEL){
                     if (iTypePriseEnCompte != ResolutionContext.PRISE_EN_COMPTE_ITEM_NULLE_PART) 
                    	 numQuantitave++;
             }
              
         }
         return numQuantitave;
    }
             
}
