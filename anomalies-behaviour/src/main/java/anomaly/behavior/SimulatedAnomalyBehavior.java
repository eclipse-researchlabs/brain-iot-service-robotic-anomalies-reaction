package anomaly.behavior;

import java.util.concurrent.Executors;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import eu.brain.iot.Query.api.TargetNodeFIDResponse;
import eu.brain.iot.eventing.annotation.SmartBehaviourDefinition;
import eu.brain.iot.eventing.api.EventBus;
import eu.brain.iot.eventing.api.SmartBehaviour;
import eu.brain.iot.redeployment.api.AnomaliesDTO;


@Component(service= {SmartBehaviour.class, SimulatedAnomalyBehavior.class},
		immediate=true)
@SmartBehaviourDefinition(consumed = {},
author = "LINKS", name = "anomaly behavior simulation",
description = "Implement a behavior which can inject a anomalise event.")
public class SimulatedAnomalyBehavior{
	 @Reference 
	 EventBus eventBus;
	
	 @Activate
	    public void start(BundleContext context){
	    System.out.println("Anomaly Behavior: I am the simulated Anomaly Behavior");
	    AnomaliesDTO anomalyEvent=new AnomaliesDTO();
	    anomalyEvent.Scenario="Light";
	    try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    eventBus.deliver(anomalyEvent);
	    System.out.println("Anomaly Behavior: One anomaly event of " + anomalyEvent.Scenario +" sent");
	    }

}
