package agentgui.simulationService.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: BenchmarkResult
* @author ontology bean generator
* @version 2017/11/12, 21:16:27
*/
public class BenchmarkResult implements Concept {

   /**
* Protege name: benchmarkValue
   */
   private float benchmarkValue;
   public void setBenchmarkValue(float value) { 
    this.benchmarkValue=value;
   }
   public float getBenchmarkValue() {
     return this.benchmarkValue;
   }

}
