package contmas.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: Container
* @author ontology bean generator
* @version 2010/05/13, 15:13:18
*/
public class Container implements Concept {

   /**
* Protege name: weight
   */
   private float weight;
   public void setWeight(float value) { 
    this.weight=value;
   }
   public float getWeight() {
     return this.weight;
   }

   /**
* Protege name: bic_code
   */
   private String bic_code;
   public void setBic_code(String value) { 
    this.bic_code=value;
   }
   public String getBic_code() {
     return this.bic_code;
   }

}
