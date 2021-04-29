package eu.brain.iot.service.robotic.startButton_demo;

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
import eu.brain.iot.eventing.annotation.SmartBehaviourDefinition;
import eu.brain.iot.eventing.api.EventBus;
import eu.brain.iot.redeployment.api.AnomaliesDTO;
import eu.brain.iot.service.robotic.startButton.api.StartDTO;
import eu.brain.iot.service.robotic.startButton.api.StartROSDTO;
import eu.brain.iot.service.robotic.startButton.api.startButton;

/**
 * Unit test for simple App.
 */
/**
 * This component triggers sensor readings based on web clicks
 */
@Component(service=StartButtonDemoImpl.class)
@JaxrsResource
@HttpWhiteboardResource(pattern="/service-robotic-ui/*", prefix="/static")
// SmartBehaviourDefinition is just so example sensor is added to repository
@SmartBehaviourDefinition(consumed = {}, // this component does not consume events
        author = "LINKS", name = "Service Robotic Example - Start Button Demo",
        description = "Implements a system start button and UI to display it.")
public class StartButtonDemoImpl implements startButton {

	 @ObjectClassDefinition
		public static @interface Config {
			String logPath() default "/opt/fabric/resources/logback.xml"; // "/opt/fabric/resources/";
		}
	   
	   private Logger logger;
	
	   @Activate
		void activate(BundleContext context, Config config) {
			
			System.setProperty("logback.configurationFile", config.logPath());
			
			logger = (Logger) LoggerFactory.getLogger(StartButtonDemoImpl.class.getSimpleName());
			
			logger.info("Hello!  I am Start UI: \n");

		}
	   
	   
    @Reference
    private EventBus eventBus;

    @Path("startSystem")
    @POST
    public String startSystem(){
    	StartDTO event=new StartDTO();
    	StartROSDTO ros1=new StartROSDTO(1);
    	StartROSDTO ros2=new StartROSDTO(2);
    	StartROSDTO ros3=new StartROSDTO(3);
    	StartROSDTO ros4=new StartROSDTO(4);
    	eventBus.deliver(ros1);
    	eventBus.deliver(ros2);
    	eventBus.deliver(ros3);
    	eventBus.deliver(ros4);
        logger.info("Start event delivered to ros1, ros2, ros3, ros4\n");
        logger.info("return system started\n");
        return "system started";
    }
    
   @Path("trigger")
    @POST
    public String trigger(){
	    StartROSDTO ros5=new StartROSDTO(5);
	    eventBus.deliver(ros5);
	    logger.info("Start event delivered to ros5\n");
	    return "Anomaly Behavior: one anomaly event sent";
	    }
    }
