package contmas.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: ProvidePopulatedBayMap
* @author ontology bean generator
* @version 2010/05/3, 17:06:09
*/
public class ProvidePopulatedBayMap implements AgentAction {

   /**
* Protege name: provides
   */
   private BayMap provides;
   public void setProvides(BayMap value) { 
    this.provides=value;
   }
   public BayMap getProvides() {
     return this.provides;
   }

}
