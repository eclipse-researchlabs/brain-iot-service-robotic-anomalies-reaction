package eu.brain.iot.service.robotic.startButton.impl;


import eu.brain.iot.eventing.annotation.SmartBehaviourDefinition;
import eu.brain.iot.eventing.api.EventBus;
import eu.brain.iot.redeployment.api.AnomaliesDTO;
import eu.brain.iot.service.robotic.startButton.api.StartDTO;
import eu.brain.iot.service.robotic.startButton.api.startButton;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * This component triggers sensor readings based on web clicks
 */
@Component(service=StartButtonImpl.class)
@JaxrsResource
@HttpWhiteboardResource(pattern="/service-robotic-ui/*", prefix="/static")
// SmartBehaviourDefinition is just so example sensor is added to repository
@SmartBehaviourDefinition(consumed = {}, // this component does not consume events
        author = "LINKS", name = "Service Robotic Example - Start Button",
        description = "Implements a system start button and UI to display it.")
public class StartButtonImpl implements startButton {

	 @ObjectClassDefinition
		public static @interface Config {
			String logPath() default "/opt/fabric/resources/logback.xml"; // "/opt/fabric/resources/";
		}
	   
	   private Logger logger;
	
	   @Activate
		void activate(BundleContext context, Config config) {
			
			System.setProperty("logback.configurationFile", config.logPath());
			
			logger = (Logger) LoggerFactory.getLogger(StartButtonImpl.class.getSimpleName());
			
			logger.info("Hello!  I am Start UI: \n");

		}
	   
	   
    @Reference
    private EventBus eventBus;

    @Path("startSystem")
    @POST
    public String startSystem(){
    	StartDTO event=new StartDTO();
        eventBus.deliver(event);
        logger.info("Start event delivered\n");
        logger.info("return system started\n");
        
        return "system started";
    }
    
   @Path("trigger")
    @POST
    public String trigger(){
	    AnomaliesDTO anomalyEvent=new AnomaliesDTO();
	    anomalyEvent.Scenario="ROB";
	    eventBus.deliver(anomalyEvent);
	    logger.info("Anomaly Behavior: One anomaly event of " + anomalyEvent.Scenario +" sent");
	    return "Anomaly Behavior: one anomaly event sent";
	    }
    	
    	
    
    }
    

