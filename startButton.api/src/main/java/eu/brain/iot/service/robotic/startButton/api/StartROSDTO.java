package eu.brain.iot.service.robotic.startButton.api;

import eu.brain.iot.eventing.api.BrainIoTEvent;

public class StartROSDTO extends BrainIoTEvent{
 public int robotID;
 public StartROSDTO(int robotID) {
	 this.robotID=robotID;
 }
}
