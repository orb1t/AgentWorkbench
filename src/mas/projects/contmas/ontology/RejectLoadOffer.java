package mas.projects.contmas.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: RejectLoadOffer
* @author ontology bean generator
* @version 2009/10/6, 22:51:49
*/
public class RejectLoadOffer implements AgentAction {

   /**
* Protege name: load_offer
   */
   private TransportOrder load_offer;
   public void setLoad_offer(TransportOrder value) { 
    this.load_offer=value;
   }
   public TransportOrder getLoad_offer() {
     return this.load_offer;
   }

}
