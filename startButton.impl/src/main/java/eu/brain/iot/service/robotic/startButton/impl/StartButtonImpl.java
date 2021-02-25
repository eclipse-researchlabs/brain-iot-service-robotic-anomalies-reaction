package eu.brain.iot.service.robotic.startButton.impl;

import com.paremus.brain.iot.example.sensor.api.SensorReadingDTO;
import eu.brain.iot.eventing.annotation.SmartBehaviourDefinition;
import eu.brain.iot.eventing.api.EventBus;
import eu.brain.iot.redeployment.api.AnomaliesDTO;
import eu.brain.iot.service.robotic.startButton.api.StartDTO;
import eu.brain.iot.service.robotic.startButton.api.startButton;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * This component triggers sensor readings based on web clicks
 */
@Component(service=StartButtonImpl.class)
@JaxrsResource
@HttpWhiteboardResource(pattern="/service-robotic-ui/*", prefix="/static")
// SmartBehaviourDefinition is just so example sensor is added to repository
@SmartBehaviourDefinition(consumed = {}, // this component does not consume events
        author = "Xu Tao", name = "Service Robotic Example - Start Button",
        description = "Implements a system start button and UI to display it.")
public class StartButtonImpl implements startButton {

    @Reference
    private EventBus eventBus;

    @Path("startSystem")
    @POST
    public void startSystem(){
    	StartDTO event=new StartDTO();
        eventBus.deliver(event);
        System.out.println("Start event delivered");
    }
    
   @Path("trigger")
    @POST
    public void trigger(){
	    AnomaliesDTO anomalyEvent=new AnomaliesDTO();
	    anomalyEvent.Scenario="ROB";
	    eventBus.deliver(anomalyEvent);
	    System.out.println("Anomaly Behavior: One anomaly event of " + anomalyEvent.Scenario +" sent");
	    }
    	
    	
    
    }
    

