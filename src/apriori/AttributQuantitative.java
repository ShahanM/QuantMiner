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
package src.apriori;

import src.database.DataColumn;



public class AttributQuantitative {
    
    private String m_sNomAttribut;
    private float m_fBorneMin = 0.0f;
    private float m_fBorneMax = 0.0f;
    
    public DataColumn m_colonneDonnees; // Colonne correspondant � l'attribut dans la base de donn�es
    
    public AttributQuantitative(String sNomAttribut, DataColumn colonneDonnees) {
        
        m_sNomAttribut = sNomAttribut;
        m_colonneDonnees = colonneDonnees;
    
        if (m_colonneDonnees!=null) {
            m_fBorneMin = m_colonneDonnees.ObtenirBorneMin();
            m_fBorneMax = m_colonneDonnees.ObtenirBorneMax();
        }
    }

    public String ObtenirNom() {
        return m_sNomAttribut;
    }

    public float getM_fBorneMin(){
        return m_fBorneMin;
    }

    public float getM_fBorneMax(){
        return m_fBorneMax;
    }

    // Deux attributes sont �gaux si et seulement si ils ont le m�me nom :
    public boolean equals(Object obj) {
        
        if (obj == null) return false;

        if (getClass() != obj.getClass())
            return false;
        
        if ( (m_sNomAttribut==null) || ( ((AttributQuantitative)obj).m_sNomAttribut==null) )
            return false;
        
        return ( m_sNomAttribut.equals( ((AttributQuantitative)obj).m_sNomAttribut ) );
    }
}
    
    
