package agentgui.simulationService.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: SlaveTrigger
* @author ontology bean generator
* @version 2017/11/12, 21:16:28
*/
public class SlaveTrigger implements AgentAction {

   /**
* Protege name: slaveLoad
   */
   private PlatformLoad slaveLoad;
   public void setSlaveLoad(PlatformLoad value) { 
    this.slaveLoad=value;
   }
   public PlatformLoad getSlaveLoad() {
     return this.slaveLoad;
   }

   /**
* Protege name: triggerTime
   */
   private PlatformTime triggerTime;
   public void setTriggerTime(PlatformTime value) { 
    this.triggerTime=value;
   }
   public PlatformTime getTriggerTime() {
     return this.triggerTime;
   }

   /**
* Protege name: slaveBenchmarkValue
   */
   private BenchmarkResult slaveBenchmarkValue;
   public void setSlaveBenchmarkValue(BenchmarkResult value) { 
    this.slaveBenchmarkValue=value;
   }
   public BenchmarkResult getSlaveBenchmarkValue() {
     return this.slaveBenchmarkValue;
   }

}
