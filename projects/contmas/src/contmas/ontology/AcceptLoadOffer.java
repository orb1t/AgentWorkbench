package contmas.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: AcceptLoadOffer
* @author ontology bean generator
* @version 2010/03/9, 21:12:43
*/
public class AcceptLoadOffer implements AgentAction {

   /**
* Protege name: load_offer
   */
   private TransportOrderChain load_offer;
   public void setLoad_offer(TransportOrderChain value) { 
    this.load_offer=value;
   }
   public TransportOrderChain getLoad_offer() {
     return this.load_offer;
   }

}
