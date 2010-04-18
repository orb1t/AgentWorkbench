package contmas.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: AssignHarborQuay
* @author ontology bean generator
* @version 2010/04/18, 11:36:17
*/
public class AssignHarborQuay implements AgentAction {

   /**
* Protege name: assigned_quay
   */
   private Quay assigned_quay;
   public void setAssigned_quay(Quay value) { 
    this.assigned_quay=value;
   }
   public Quay getAssigned_quay() {
     return this.assigned_quay;
   }

   /**
* Protege name: available_cranes
   */
   private List available_cranes = new ArrayList();
   public void addAvailable_cranes(AID elem) { 
     List oldList = this.available_cranes;
     available_cranes.add(elem);
   }
   public boolean removeAvailable_cranes(AID elem) {
     List oldList = this.available_cranes;
     boolean result = available_cranes.remove(elem);
     return result;
   }
   public void clearAllAvailable_cranes() {
     List oldList = this.available_cranes;
     available_cranes.clear();
   }
   public Iterator getAllAvailable_cranes() {return available_cranes.iterator(); }
   public List getAvailable_cranes() {return available_cranes; }
   public void setAvailable_cranes(List l) {available_cranes = l; }

}
