package mas.projects.contmas.ontology;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * can move containers inside of a domain, e.g. train
* Protege name: PassiveContainerHolder
* @author ontology bean generator
* @version 2009/10/6, 22:51:49
*/
public class PassiveContainerHolder extends ContainerHolder{ 

   /**
* Protege name: administers
   */
   private LoadList administers;
   public void setAdministers(LoadList value) { 
    this.administers=value;
   }
   public LoadList getAdministers() {
     return this.administers;
   }

}
