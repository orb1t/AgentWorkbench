package contmas.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: CallForProposalsOnLoadStage
* @author ontology bean generator
* @version 2010/03/25, 18:36:32
*/
public class CallForProposalsOnLoadStage implements AgentAction {

   /**
* Protege name: required_turnover_capacity
   */
   private LoadList required_turnover_capacity;
   public void setRequired_turnover_capacity(LoadList value) { 
    this.required_turnover_capacity=value;
   }
   public LoadList getRequired_turnover_capacity() {
     return this.required_turnover_capacity;
   }

}
