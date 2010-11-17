package agentgui.simulationService.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: PlatformLoad
* @author ontology bean generator
* @version 2010/11/4, 20:38:59
*/
public class PlatformLoad implements Concept {

   /**
* Protege name: loadMemorySystem
   */
   private float loadMemorySystem;
   public void setLoadMemorySystem(float value) { 
    this.loadMemorySystem=value;
   }
   public float getLoadMemorySystem() {
     return this.loadMemorySystem;
   }

   /**
* Protege name: loadNoThreads
   */
   private int loadNoThreads;
   public void setLoadNoThreads(int value) { 
    this.loadNoThreads=value;
   }
   public int getLoadNoThreads() {
     return this.loadNoThreads;
   }

   /**
* Protege name: loadMemoryJVM
   */
   private float loadMemoryJVM;
   public void setLoadMemoryJVM(float value) { 
    this.loadMemoryJVM=value;
   }
   public float getLoadMemoryJVM() {
     return this.loadMemoryJVM;
   }

   /**
* Protege name: loadExceeded
   */
   private int loadExceeded;
   public void setLoadExceeded(int value) { 
    this.loadExceeded=value;
   }
   public int getLoadExceeded() {
     return this.loadExceeded;
   }

   /**
* Protege name: loadCPU
   */
   private float loadCPU;
   public void setLoadCPU(float value) { 
    this.loadCPU=value;
   }
   public float getLoadCPU() {
     return this.loadCPU;
   }

}