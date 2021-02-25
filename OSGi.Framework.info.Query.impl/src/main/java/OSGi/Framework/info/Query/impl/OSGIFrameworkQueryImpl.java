package OSGi.Framework.info.Query.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import eu.brain.iot.Query.api.OSGIFrameworkQuery;
import eu.brain.iot.Query.api.ReportTargetNodeDTO;
import eu.brain.iot.Query.api.TargetNodeFIDResponse;
import eu.brain.iot.eventing.annotation.SmartBehaviourDefinition;
import eu.brain.iot.eventing.api.BrainIoTEvent;
import eu.brain.iot.eventing.api.EventBus;
import eu.brain.iot.eventing.api.SmartBehaviour;
import eu.brain.iot.service.robotic.startButton.api.StartDTO;

@Component(immediate=true)
@SmartBehaviourDefinition(consumed = {TargetNodeFIDResponse.class, StartDTO.class}, filter="(timestamp=*)",
author = "LINKS", name = "Local OSGi Framework ID Report",
description = "Report the current running OSGi Framework ID.")
public class OSGIFrameworkQueryImpl implements OSGIFrameworkQuery,SmartBehaviour<BrainIoTEvent>{
    private String thisOSGiFrameworkID;
    //TODO add an implementation
    File myObj;
    private boolean discovered=false;
    @Reference
	private EventBus eventBus;
    private ExecutorService worker;
    @Activate
    public void start(BundleContext context){
    thisOSGiFrameworkID=context.getProperty(Constants.FRAMEWORK_UUID);
    CreateFile();
    WriteToFile("Runner: I am "+ thisOSGiFrameworkID);
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

	@Override
	public String getOSGIFrameworkID() {
		// TODO Auto-generated method stub
	
		return thisOSGiFrameworkID;
		
	}
	
	
	
public void Notification(String thisOSGiFrameworkID) {
	ReportTargetNodeDTO TargetNodeFID = new  ReportTargetNodeDTO();
    TargetNodeFID.TargetNodeFID=thisOSGiFrameworkID;
	 while(!discovered){
		 WriteToFile("Runner: I have not been discovered" +thisOSGiFrameworkID);
		 try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 eventBus.deliver(TargetNodeFID);
		 WriteToFile("Runner: TargetNodeEvent delivered");
	 }

	 	
}
	public void CreateFile(){
		  
	     try {
	       myObj = new File("Runner_log.txt");
	       if (myObj.createNewFile()) {
	         WriteToFile("Runner: File created: " + myObj.getName());
	        
	       } else {
	    	   WriteToFile("Runner: File already exists.");
	    	   myObj.delete();
	    	   myObj.createNewFile();
	    	   WriteToFile("Runner: File Path "+myObj.getAbsolutePath());
	       }
	     } catch (IOException e) {
	       System.out.println("Runner: An error occurred.");
	       e.printStackTrace();
	     }
	   
  }
  
  public void WriteToFile(String s){
	  System.out.println(s);
	     
	      try {
	      
	       FileWriter myWriter = new FileWriter(myObj, true);
	       myWriter.write(s +"\n");
	       myWriter.close();
	    	 
	     } catch (IOException e) {
	       System.out.println("Runner:An error occurred.");
	       e.printStackTrace();
	     } 
	   }



@Override
public void notify(BrainIoTEvent event) {
	// TODO Auto-generated method stub
	if(event instanceof StartDTO) {
		 WriteToFile("Runner: I received the Start event");
		
		    worker.execute(()->{
		    	Notification(thisOSGiFrameworkID);
		    });
	}
	
	if(event instanceof TargetNodeFIDResponse) {
		TargetNodeFIDResponse targetNodFIDResponse= (TargetNodeFIDResponse) event;
		if(targetNodFIDResponse.received) {
			 WriteToFile("Runner: I have received the response message");
			discovered=true;
		}
	}
	
}

}
	
