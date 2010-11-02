package mas.environment.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * An object's position.
* Protege name: Position
* @author ontology bean generator
* @version 2010/10/22, 21:15:52
*/
public class Position implements Concept {

   /**
   * The environments y coordinate
* Protege name: yPos
   */
   private float yPos;
   public void setYPos(float value) { 
    this.yPos=value;
   }
   public float getYPos() {
     return this.yPos;
   }

   /**
   * The objects x coordinate
* Protege name: xPos
   */
   private float xPos;
   public void setXPos(float value) { 
    this.xPos=value;
   }
   public float getXPos() {
     return this.xPos;
   }

}