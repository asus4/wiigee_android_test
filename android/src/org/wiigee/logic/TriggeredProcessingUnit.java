/*
 * wiigee - accelerometerbased gesture recognition
 * Copyright (C) 2007, 2008, 2009 Benjamin Poppinga
 * 
 * Developed at University of Oldenburg
 * Contact: wiigee@benjaminpoppinga.de
 *
 * This file is part of wiigee.
 *
 * wiigee is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.wiigee.logic;

import java.util.Vector;
import org.wiigee.event.*;
import org.wiigee.util.Log;

/**
 * This class analyzes the AccelerationEvents emitted from a Wiimote
 * and further creates and manages the different models for each type
 * of gesture. 
 * 
 * @author Benjamin 'BePo' Poppinga
 */
public class TriggeredProcessingUnit extends ProcessingUnit {

	// gesturespecific values
	private Gesture current; // current gesture

	private Vector<Gesture> trainsequence;
	
	// State variables
	private boolean learning, analyzing;
	
	public TriggeredProcessingUnit() {
		super();
		this.learning=false;
		this.analyzing=false;
		this.current=new Gesture();
		this.trainsequence=new Vector<Gesture>();
	}

	/** 
	 * Since this class implements the WiimoteListener this procedure is
	 * necessary. It contains the filtering (directional equivalence filter)
	 * and adds the incoming data to the current motion, we want to train
	 * or recognize.
	 * 
	 * @param event The acceleration event which has to be processed by the
	 * directional equivalence filter and which has to be added to the current
	 * motion in recognition or training process.
	 */
	public void accelerationReceived(AccelerationEvent event) {
		if(this.learning || this.analyzing) {
			this.current.add(event); // add event to gesture			
		}		
	}

	/** 
	 * This method is from the WiimoteListener interface. A button press
	 * is used to control the data flow inside the structures. 
	 * 
	 */
	public void buttonPressReceived(ButtonPressedEvent event) {
		this.handleStartEvent(event);
	}

	public void buttonReleaseReceived(ButtonReleasedEvent event) {
		this.handleStopEvent(event);
	}
	
	public void motionStartReceived(MotionStartEvent event) {
		// this.handleStartEvent(event);
	}
	
	public void motionStopReceived(MotionStopEvent event) {
		// this.handleStopEvent(event);
	}
	
	public void handleStartEvent(ActionStartEvent event) {
		
		// TrainButton = record a gesture for learning
		if((!this.analyzing && !this.learning) && 
			event.isTrainInitEvent()) {
			Log.write("Training started!");
			this.learning=true;
		}
		
		// RecognitionButton = record a gesture for recognition
		if((!this.analyzing && !this.learning) && 
			event.isRecognitionInitEvent()) {
			Log.write("Recognition started!");
			this.analyzing=true;
		}
			
		// CloseGestureButton = starts the training of the model with multiple
		// recognized gestures, contained in trainsequence
		if((!this.analyzing && !this.learning) && 
			event.isCloseGestureInitEvent()) {
		
			if(this.trainsequence.size()>0) {
				Log.write("Training the model with "+this.trainsequence.size()+" gestures...");
				this.learning=true;
				
				GestureModel m = new GestureModel();
				m.train(this.trainsequence);
				m.print();
				this.classifier.addGestureModel(m);
				
				this.trainsequence=new Vector<Gesture>();
				this.learning=false;
			} else {
				Log.write("There is nothing to do. Please record some gestures first.");
			}
		}
	}
	
	public void handleStopEvent(ActionStopEvent event) {
		if(this.learning) { // button release and state=learning, stops learning
			if(this.current.getCountOfData()>0) {
				Log.write("Finished recording (training)...");
				Log.write("Data: "+this.current.getCountOfData());
				Gesture gesture = new Gesture(this.current);
				this.trainsequence.add(gesture);
				this.current=new Gesture();
				this.learning=false;
			} else {
				Log.write("There is no data.");
				Log.write("Please train the gesture again.");
				this.learning=false; // ?
			}
		}
		
		else if(this.analyzing) { // button release and state=analyzing, stops analyzing
			if(this.current.getCountOfData()>0) {
				Log.write("Finished recording (recognition)...");
				Log.write("Compare gesture with "+this.classifier.getCountOfGestures()+" other gestures.");
				Gesture gesture = new Gesture(this.current);
				
				int recognized = this.classifier.classifyGesture(gesture);
				if(recognized!=-1) {
					double recogprob = this.classifier.getLastProbability();
					this.fireGestureEvent(true, recognized, recogprob);
					Log.write("######");
					Log.write("Gesture No. "+recognized+" recognized: "+recogprob);
					Log.write("######");
				} else {
					this.fireGestureEvent(false, 0, 0.0);
					Log.write("######");
					Log.write("No gesture recognized.");
					Log.write("######");
				}
				
				this.current=new Gesture();
				this.analyzing=false;
			} else {
				Log.write("There is no data.");
				Log.write("Please recognize the gesture again.");
				this.analyzing=false; // ?
			}
		}
	}

	@Override
	public void loadGesture(String filename) {
		GestureModel g = org.wiigee.util.FileIO.readFromFile(filename);
		this.classifier.addGestureModel(g);	
	}

	@Override
	public void saveGesture(int id, String filename) {
		org.wiigee.util.FileIO.writeToFile(this.classifier.getGestureModel(id), filename);
	}

}
