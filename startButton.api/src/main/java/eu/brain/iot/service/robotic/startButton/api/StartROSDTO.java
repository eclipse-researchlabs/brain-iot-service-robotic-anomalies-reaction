package eu.brain.iot.service.robotic.startButton.api;

import eu.brain.iot.eventing.api.BrainIoTEvent;

public class StartROSDTO extends BrainIoTEvent{
 int id;
 public StartROSDTO(int id) {
	 this.id=id;
 }
}
