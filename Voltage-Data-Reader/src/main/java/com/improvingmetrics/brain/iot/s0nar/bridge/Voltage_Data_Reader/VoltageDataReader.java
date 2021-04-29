package com.improvingmetrics.brain.iot.s0nar.bridge.Voltage_Data_Reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.brain.iot.eventing.annotation.SmartBehaviourDefinition;
import eu.brain.iot.eventing.api.SmartBehaviour;
import eu.brain.iot.robot.events.BatteryVoltage;
import eu.brain.iot.service.robotic.startButton.api.StartDTO;
import eu.brain.iot.eventing.api.BrainIoTEvent;
import eu.brain.iot.eventing.api.EventBus;
/**
 * Hello world!
 *
 */
@Component(immediate=true,
configurationPid= "com.improvingmetrics.brain.iot.s0nar.bridge.s0nar-bridge.impl",
configurationPolicy=ConfigurationPolicy.REQUIRE
)
@SmartBehaviourDefinition(
consumed = {},
//filter = "(timestamp=*)",
author = "Improving Metrics",
name = "Robot Battery Voltage Reader",
description = "A CSV File Reader to inject Voltage Events to EventBus"
)

public class VoltageDataReader implements SmartBehaviour<BrainIoTEvent>
{
	
	private String pathToCsv="go-to-cart.robot-A.battery-converted-1min.csv";
	private Logger logger;
	private String RobotId="1";	
	@Reference
	EventBus eventBus;
	
    @ObjectClassDefinition
    public static @interface Config {
	 String logPath() default "/opt/fabric/resources/logback.xml"; // "/opt/fabric/resources/";
	 }
	
	
	   @Activate
	   public void start(BundleContext context,Config config) throws InterruptedException{
		   System.setProperty("logback.configurationFile",config.logPath() );
		   logger = (Logger) LoggerFactory.getLogger(VoltageDataReader.class.getSimpleName());
		   System.out.println(eventBus.getClass().toString());
		  // ReadData();
	   }
	
	
	
	public void ReadData() {
	BufferedReader csvReader;
	try {
		
		InputStream stream=getClass().getClassLoader().getResourceAsStream("go-to-cart.robot-A.battery-converted-1min.csv");
		InputStreamReader streamReader= new InputStreamReader(stream);
		BufferedReader reader = new BufferedReader(streamReader);
	   String row;
	  while ((row = reader.readLine()) != null) {
	    String[] data = row.split(",");
	    if(data[0].equals("index")) {
	    	System.out.println("the first line of the file with the column name" + data[0] +" "+data[1]);
	    	logger.info("the first line of the file with the column name" + data[0] +" "+data[1]);
	    }
	    else {
	    	BatteryVoltage batteryVoltageEvent = new BatteryVoltage();
	    	batteryVoltageEvent.robotID = Integer.parseInt(this.RobotId);
	    	batteryVoltageEvent.index = data[0];
	    	batteryVoltageEvent.target=Double.parseDouble(data[1]); 
	    	eventBus.deliver(batteryVoltageEvent);
	    	Thread.sleep(60000);
	    	System.out.println("emitting battery event:" + batteryVoltageEvent);
	    	
	    }

	}
	reader.close();
	
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
}


	@Override
	public void notify(BrainIoTEvent event) {
		// TODO Auto-generated method stub
		/*if(event instanceof StartDTO) {
		   ReadData();
		}*/
	}
	
}
