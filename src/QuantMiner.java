package src;
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
/*
 * Realized by:
 * Students: Cyril Nortet and Xiangrong Kong
 * Advisors: Ansaf Salleb-Aouissi (CCLS, Columbia University)
 * 		     Christel Vrain (LIFO, University of Orleans)
 *           Daniel Cassard (BRGM, France)
 */

import src.graphicalInterface.JSplashWindow;
import src.graphicalInterface.MainWindow;
import src.tools.ENV;

import javax.swing.*;

public class QuantMiner {
    
    public QuantMiner() {
    }
    
    public static void main(String[] args) {
        ENV.Initialiser();
        
        if (ENV.LOOK_INTERFACE == ENV.LOOK_INTERFACE_OS) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch(Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        
        JSplashWindow splash = new JSplashWindow(2000, ENV.REPERTOIRE_RESSOURCES);
        splash.showSplash();
        new MainWindow().setVisible(true);
        
        //Register file parameters
        ENV.EnregistrerFichierParametrage();
    }
    
}
