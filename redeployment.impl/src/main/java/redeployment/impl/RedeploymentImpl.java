package redeployment.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paremus.brain.iot.management.api.BehaviourManagement;

import eu.brain.iot.Query.api.ReportTargetNodeDTO;
import eu.brain.iot.Query.api.TargetNodeFIDResponse;
import eu.brain.iot.eventing.annotation.SmartBehaviourDefinition;
import eu.brain.iot.eventing.api.BrainIoTEvent;
import eu.brain.iot.eventing.api.EventBus;
import eu.brain.iot.eventing.api.SmartBehaviour;
import eu.brain.iot.installer.api.BehaviourDTO;
import eu.brain.iot.redeployment.api.AnomaliesDTO;
import eu.brain.iot.service.robotic.startButton.api.StartDTO;
@Component(service= {SmartBehaviour.class,RedeploymentImpl.class})
@SmartBehaviourDefinition(consumed= {AnomaliesDTO.class,ReportTargetNodeDTO.class},filter="(timestamp=*)", 
                         author="LINKS", name ="Redeployment Behaviour", 
                         description="Implements a behavior that is able to response to the anomalies events.")
public class RedeploymentImpl implements SmartBehaviour<BrainIoTEvent>{
   private String thisOSGiFrameworkID;
   private Collection<BehaviourDTO> behaviours= new LinkedList<BehaviourDTO>();
   private String TargetNode="";
   private Map<String,String> smartBehaviorList;
   @ObjectClassDefinition
	public static @interface Config {
		String logPath() default "/opt/fabric/resources/logback.xml"; // "/opt/fabric/resources/";
	}
   
   private Logger logger;
   
   @Reference
   BehaviourManagement bms;
   
   @Reference 
   EventBus eventBus;
   
   private ExecutorService worker;
   
  /* @Reference
//   OSGIFrameworkQuery OSGIFrameworkID_Query*/
   File myObj;
   
   @Activate
   public void start(BundleContext context,Config config) throws InterruptedException{
	   System.setProperty("logback.configurationFile",config.logPath() );
	   logger = (Logger) LoggerFactory.getLogger(RedeploymentImpl.class.getSimpleName());
	   thisOSGiFrameworkID = context.getProperty(Constants.FRAMEWORK_UUID);
	  // CreateFile();
	   String s="Redeployer: I am ["+this.thisOSGiFrameworkID+"]";
	   WriteToFile(s);
	   smartBehaviorList=new HashMap<String, String>();
	   getBehaviorsFromMarketplace();
	   worker = Executors.newSingleThreadExecutor();
	   
   }
   @Deactivate
	void stop() {
		
		worker.shutdown();
		try {
			worker.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// Propagate the interrupt
			logger.error("\n RedeploymentImple error:", e.getMessage());
		}
	}

public void findtheTargetNode(ReportTargetNodeDTO event) {
	 TargetNode =event.TargetNodeFID;
	 WriteToFile("Redeployer:"+ TargetNode+" is Runner");   
   }
   
public void installNewBehavior(String Behavior,String version,String targetNode) {
	   BehaviourDTO behaviour=new BehaviourDTO();
	   behaviour.bundle=Behavior;
	   behaviour.version=version;
	   bms.installBehaviour(behaviour, targetNode);
	   WriteToFile("Redeployer:event is delivered for installation of" +Behavior +":"+version);
	   
	   
   }
   
public void CreateFile(){
		  
	     try {
	       myObj = new File("Redeployer_log.txt");
	       if (myObj.createNewFile()) {
	         WriteToFile("File created: " + myObj.getName());
	         WriteToFile("Redeployer: File Path "+myObj.getAbsolutePath());
	       } else {
	    	WriteToFile("Redeployer:File already exists.");
	    	 myObj.delete();
	    	 WriteToFile("Redeployer:File deleted.");
	         myObj.createNewFile();
	       }
	     } catch (IOException e) {
	    	 WriteToFile("Redeployer: An error occurred.");
	       e.printStackTrace();
	     }
	   
}

public Collection<BehaviourDTO> getBehaviorsFromMarketplace() {
   
       try {
           behaviours = bms.findBehaviours(null);
           behaviours.forEach(b -> {
        	   System.out.println(b.bundle + ":" + b.version);
        	   WriteToFile(b.bundle + ":"+ b.version);
        	   smartBehaviorList.put(b.bundle, b.version);
           });
       } catch (Exception e) {
           e.printStackTrace();
       }
	   return behaviours;
   } 

public void ResponseToSecurityLightSystem() {
	 installNewBehavior("com.paremus.brain.iot.example.behaviour.impl", "0.0.1.SNAPSHOT",TargetNode);
	 installNewBehavior("com.paremus.brain.iot.example.light.impl", "0.0.1.SNAPSHOT",TargetNode);
	 installNewBehavior("com.paremus.brain.iot.example.sensor.impl", "0.0.1.SNAPSHOT", TargetNode);
}   

public void ResponseToRobotBatteryAnomaly() {
	 String version= smartBehaviorList.get("eu.brain.iot.service.robotic.eu.brain.iot.robot.behaviour");
	 installNewBehavior("eu.brain.iot.service.robotic.eu.brain.iot.robot.behaviour",version,TargetNode);
	 version= smartBehaviorList.get("eu.brain.iot.service.robotic.eu.brain.iot.ros.edge.node");
	 installNewBehavior("eu.brain.iot.service.robotic.eu.brain.iot.ros.edge.node",version,TargetNode);
	 try {
		logger.info("Redeployer: wait ROS Edge Node to get ready, for 10s.... ");
		Thread.sleep(5000);
		StartDTO start=new StartDTO();
		eventBus.deliver(start);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		logger.error("\n RedeploymentImple error:" + e.getMessage());
	}
	 
	 
}   


public void WriteToFile(String s){
	//System.out.println(s);
	logger.info(s+"\n");
	 
    /*  try {
	    	 
	       FileWriter myWriter = new FileWriter(myObj, true);
	       myWriter.write(s +"\n");
	       myWriter.close();
	     } catch (IOException e) {
	       System.out.println("An error occurred.");
	       e.printStackTrace();
	     }*/
	   }
public boolean checkBackupNode(){
	if(TargetNode=="") {
	    WriteToFile("Redeployer:The Backup device is not discovered"); 
	    return false;
	  }
	else 
		return true;
}

@Override
public void notify(BrainIoTEvent event) {
	// TODO Auto-generated method stub
	if(event instanceof ReportTargetNodeDTO) {
		WriteToFile("Redeployer: received ReportTargetNode event");
		ReportTargetNodeDTO targetNodeDTO=(ReportTargetNodeDTO) event;
		findtheTargetNode(targetNodeDTO);
		TargetNodeFIDResponse response=new TargetNodeFIDResponse();
		response.received=true;
		eventBus.deliver(response);
		WriteToFile("Redeploytor: send the response to Runner");
		
}
   if(event instanceof AnomaliesDTO) {
	   WriteToFile("Redeployer: received Anomalies event");
	   AnomaliesDTO AnomaliesEvent= (AnomaliesDTO) event;
	   worker.execute(()->{
		   while(!checkBackupNode());
		   switch(AnomaliesEvent.Scenario) {
			 case "ROB": 
				 WriteToFile("Redeployer: Robot system will be installed into the targetNode"+ TargetNode);
				 ResponseToRobotBatteryAnomaly();
				
				 break;
			 case "Light":
				 WriteToFile("Redeployer: Security light system will be installed into the targetNode"+ TargetNode);
				 ResponseToSecurityLightSystem();
				
				 break;
			 default:
				 WriteToFile("Redeploytor: The annomalies event is not recongnizable"); 
		     
			} 
	   });
	   
   }   
	
}
   
}
