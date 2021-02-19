package redeployment.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

import com.paremus.brain.iot.management.api.BehaviourManagement;

import eu.brain.iot.Query.api.OSGIFrameworkQuery;
import eu.brain.iot.Query.api.ReportTargetNodeDTO;
import eu.brain.iot.Query.api.TargetNodeFIDResponse;
import eu.brain.iot.eventing.annotation.SmartBehaviourDefinition;
import eu.brain.iot.eventing.api.BrainIoTEvent;
import eu.brain.iot.eventing.api.EventBus;
import eu.brain.iot.eventing.api.SmartBehaviour;
import eu.brain.iot.installer.api.BehaviourDTO;
import eu.brain.iot.redeployment.api.AnomaliesDTO;
@Component(service= {SmartBehaviour.class,RedeploymentImpl.class})
@SmartBehaviourDefinition(consumed= {AnomaliesDTO.class,ReportTargetNodeDTO.class},filter="(timestamp=*)", 
                         author="LINKS", name ="Redeplolyment Behavior", 
                         description="Implements a behavior that is able to response to the anomalies events.")
public class RedeploymentImpl implements SmartBehaviour<BrainIoTEvent>{
   private String thisOSGiFrameworkID;
   private Collection<BehaviourDTO> behaviours= new LinkedList<BehaviourDTO>();
   private String TargetNode="";
   @Reference
   BehaviourManagement bms;
   
   @Reference 
   EventBus eventBus;
   
   private ExecutorService worker;
   
  /* @Reference
//   OSGIFrameworkQuery OSGIFrameworkID_Query*/
   File myObj;
   
   @Activate
   public void start(BundleContext context) throws InterruptedException{
	   thisOSGiFrameworkID = context.getProperty(Constants.FRAMEWORK_UUID);
	   CreateFile();
	   String s="Redeployer: I am ["+this.thisOSGiFrameworkID+"]\n";
	   WriteToFile(s);
	   getBehaviorsFromMarketplace();
	   worker = Executors.newSingleThreadExecutor();
   }
   @Deactivate
	void stop() {
		
		worker.shutdown();
		try {
			worker.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException ie) {
			// Propagate the interrupt
			Thread.currentThread().interrupt();
		}
	}

public void findtheTargetNode(ReportTargetNodeDTO event) {
	 TargetNode =event.TargetNodeFID;
	 WriteToFile("Redeployer:"+ TargetNode+" is Runner\n");   
   }
   
public void installNewBehavior(String Behavior,String version,String targetNode) {
	   BehaviourDTO behaviour=new BehaviourDTO();
	   behaviour.bundle=Behavior;
	   behaviour.version=version;
	   bms.installBehaviour(behaviour, targetNode);
	   WriteToFile("Redeployer:event is delivered:" +Behavior +"\n");
	   
	   
   }
   
public void CreateFile(){
		  
	     try {
	       myObj = new File("Redeployer_log.txt");
	       if (myObj.createNewFile()) {
	         System.out.println("File created: " + myObj.getName());
	         System.out.println("Redeployer: File Path "+myObj.getAbsolutePath());
	       } else {
	         System.out.println("Redeployer:File already exists.");
	       }
	     } catch (IOException e) {
	       System.out.println("Redeployer: An error occurred.");
	       e.printStackTrace();
	     }
	   
}

public Collection<BehaviourDTO> getBehaviorsFromMarketplace() {
   
       try {
           behaviours = bms.findBehaviours(null);

           List<String> nameVersions = behaviours.stream()
                   .map(b -> b.bundle + ":" + b.version)
                   .collect(Collectors.toList());


           behaviours.forEach(b -> {
        	   WriteToFile(b.bundle + ":"+ b.version +"\n");
        	  
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
	
}   


public void WriteToFile(String s){
	System.out.println(s);
	 
	   /*  try {
	    	 
	       FileWriter myWriter = new FileWriter(myObj, true);
	       myWriter.write(s);
	       myWriter.close();
	     } catch (IOException e) {
	       System.out.println("An error occurred.");
	       e.printStackTrace();
	     } */
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
				 ResponseToRobotBatteryAnomaly();
				 WriteToFile("Redeployer: Robot system will be installed into the targetNode"+ TargetNode);
				 break;
			 case "Light":
				 ResponseToSecurityLightSystem();
				 WriteToFile("Redeployer: security light system will be installed into the targetNode"+ TargetNode);
				 break;
			 default:
				 System.out.println("Redeploytor: The annomalies event is not recongnizable"); 
		     
			} 
	   });
	   
   }   
	
}
   
}
