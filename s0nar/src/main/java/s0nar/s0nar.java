package s0nar;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import eu.brain.iot.eventing.annotation.SmartBehaviourDefinition;
import eu.brain.iot.eventing.api.BrainIoTEvent;
import eu.brain.iot.eventing.api.EventBus;
import eu.brain.iot.eventing.api.SmartBehaviour;


@Component(immediate=true
)
@SmartBehaviourDefinition(
consumed = {},
//filter = "(timestamp=*)",
author = "Improving Metrics",
name = "s0nar",
description = "Machine Learning for Anomalies Detection"
)
public class s0nar implements SmartBehaviour<BrainIoTEvent> {
    
	private Logger logger;
	
    @ObjectClassDefinition
    public static @interface Config {
	 String logPath() default "/opt/fabric/resources/logback.xml"; // "/opt/fabric/resources/";
	 }
	
	
	   @Activate
	   public void start(BundleContext context,Config config) throws InterruptedException{
		   System.setProperty("logback.configurationFile",config.logPath() );
		   logger = (Logger) LoggerFactory.getLogger(s0nar.class.getSimpleName());
		   logger.info("Hello, I am s0nar");
		  // ReadData();
	   }
	
	
	@Override
	public void notify(BrainIoTEvent event) {
		// TODO Auto-generated method stub
		
	}

}
