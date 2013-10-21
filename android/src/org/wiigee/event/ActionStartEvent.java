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

package org.wiigee.event;

import java.util.EventObject;

import org.wiigee.device.Device;

/**
 * An ActionStartEvent is an Event where other different events can
 * be derived from, if they can be considered as an event actually starting
 * a process like e.g. Training, Recognition, ...
 *
 * @author Benjamin 'BePo' Poppinga
 */
public class ActionStartEvent extends EventObject {

	protected boolean trainbutton;
	protected boolean recognitionbutton;
	protected boolean closegesturebutton;
	
	public ActionStartEvent(Device source) {
		super(source);
	}
	
	/**
	 * Is true if this button press has been done by the
	 * individual defined RecognitionButton which has to be
	 * set during initialization of a Wiimote.
	 * 
	 * @return Is this button press initiated by the recognition button.
	 * @see device.Wiimote#setRecognitionButton(int) setRecognitionButton()
	 */
	public boolean isRecognitionInitEvent() {
		return this.recognitionbutton;
	}
	
	/**
	 * Is true if this button press has been done by the
	 * individual defined TrainButton which has to be
	 * set during initialization of a Wiimote.
	 * 
	 * @return Is this button pres initiated by the training button.
	 * @see device.Wiimote#setTrainButton(int) setTrainButton()
	 */
	public boolean isTrainInitEvent() {
		return this.trainbutton;
	}
	
	/**
	 * Is true if this button press has been done by the
	 * individual defined CloseGestureButton which has to be
	 * set during initialization of a Wiimote.
	 * 
	 * @return Is this button press initiated by the close gesture button.
	 * @see device.Wiimote#setCloseGestureButton(int) setCloseGestureButton()
	 */
	public boolean isCloseGestureInitEvent() {
		return this.closegesturebutton;
	}

}
