package contmas.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Represents an object's size in a 2D environment
* Protege name: phy:Size
* @author ontology bean generator
* @version 2010/04/22, 16:03:29
*/
public class Phy_Size implements Concept {

   /**
   * The object's width
* Protege name: phy:width
   */
   private float phy_width;
   public void setPhy_width(float value) { 
    this.phy_width=value;
   }
   public float getPhy_width() {
     return this.phy_width;
   }

   /**
   * The object's height
* Protege name: phy:height
   */
   private float phy_height;
   public void setPhy_height(float value) { 
    this.phy_height=value;
   }
   public float getPhy_height() {
     return this.phy_height;
   }

}