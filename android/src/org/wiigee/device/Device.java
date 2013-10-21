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
package org.wiigee.device;

import java.io.IOException;
import java.util.Vector;

import org.wiigee.logic.*;
import org.wiigee.event.*;
import org.wiigee.filter.*;

/**
 * Abstract representation of a device with very basic functionalities. This
 * class should be derived from, if anybody plans to add a new class of devices,
 * like Wiimote or AndroidDevice does. This class mainly consist of filter
 * management, recognition control and core event control.
 *
 * @author Benjamin 'BePo' Poppinga
 */
public class Device {

    // Fixed number values.
    public static final int MOTION = 0;

    // Buttons for action coordination
    protected int recognitionbutton;
    protected int trainbutton;
    protected int closegesturebutton;

    // Functional
    protected boolean accelerationEnabled;

    // Filters, can filter the data stream
    protected Vector<Filter> accfilters = new Vector<Filter>();

    // Listeners, receive generated events
    protected Vector<AccelerationListener> accelerationlistener = new Vector<AccelerationListener>();
    protected Vector<ButtonListener> buttonlistener = new Vector<ButtonListener>();

    // Processing unit to analyze the data
    protected ProcessingUnit processingunit = new TriggeredProcessingUnit();

    public Device(boolean autofiltering) {
        if (autofiltering) {
            this.addAccelerationFilter(new IdleStateFilter());
            this.addAccelerationFilter(new MotionDetectFilter(this));
            this.addAccelerationFilter(new DirectionalEquivalenceFilter());
        }
        this.addAccelerationListener(this.processingunit);
        this.addButtonListener(this.processingunit);
    }

    /**
     * Adds a Filter for processing the acceleration values.
     * @param filter The Filter instance.
     */
    public void addAccelerationFilter(Filter filter) {
        this.accfilters.add(filter);
    }

    /**
     * Resets all the accfilters, which are resetable.
     * Sometimes they have to be resettet if a new gesture starts.
     */
    public void resetAccelerationFilters() {
        for (int i = 0; i < this.accfilters.size(); i++) {
            this.accfilters.elementAt(i).reset();
        }
    }

    /**
     * Adds an AccelerationListener to the Device. Everytime an acceleration
     * on the Device is performed the AccelerationListener would receive
     * an event of this action.
     *
     * @param listener The Listener.
     */
    public void addAccelerationListener(AccelerationListener listener) {
        this.accelerationlistener.add(listener);
    }

    /**
     * Adds a ButtonListener to the Device. Everytime a Button has been
     * pressed or released, the Listener would be notified about this via
     * the corresponding Events.
     *
     * @param listener The Listener.
     */
    public void addButtonListener(ButtonListener listener) {
        this.buttonlistener.add(listener);
    }

    /**
     * Adds a GestureListener to the Device. Everytime a gesture
     * is performed the GestureListener would receive an event of
     * this gesture.
     *
     * @param listener The Listener.
     */
    public void addGestureListener(GestureListener listener) {
        this.processingunit.addGestureListener(listener);
    }

    public int getRecognitionButton() {
        return this.recognitionbutton;
    }

    public void setRecognitionButton(int b) {
        this.recognitionbutton = b;
    }

    public int getTrainButton() {
        return this.trainbutton;
    }

    public void setTrainButton(int b) {
        this.trainbutton = b;
    }

    public int getCloseGestureButton() {
        return this.closegesturebutton;
    }

    public void setCloseGestureButton(int b) {
        this.closegesturebutton = b;
    }

    public ProcessingUnit getProcessingUnit() {
        return this.processingunit;
    }

    public void setAccelerationEnabled(boolean enabled) throws IOException {
        this.accelerationEnabled = enabled;
    }

    public void loadGesture(String filename) {
        this.processingunit.loadGesture(filename);
    }

    public void saveGesture(int id, String filename) {
        this.processingunit.saveGesture(id, filename);
    }

    // ###### Event-Methoden
    /** Fires an acceleration event.
     * @param vector Consists of three values:
     * acceleration on X, Y and Z axis.
     */
    public void fireAccelerationEvent(double[] vector) {
        for (int i = 0; i < this.accfilters.size(); i++) {
            vector = this.accfilters.get(i).filter(vector);
            // cannot return here if null, because of time-dependent accfilters
        }

        // don't need to create an event if filtered away
        if (vector != null) {
            // 	calculate the absolute value for the accelerationevent
            double absvalue = Math.sqrt((vector[0] * vector[0]) +
                    (vector[1] * vector[1]) + (vector[2] * vector[2]));

            AccelerationEvent w = new AccelerationEvent(this,
                    vector[0], vector[1], vector[2], absvalue);
            for (int i = 0; i < this.accelerationlistener.size(); i++) {
                this.accelerationlistener.get(i).accelerationReceived(w);
            }
        }

    } // fireaccelerationevent

    /** Fires a button pressed event.
     * @param button
     * 		Integer value of the pressed button.
     */
    public void fireButtonPressedEvent(int button) {
        ButtonPressedEvent w = new ButtonPressedEvent(this, button);
        for (int i = 0; i < this.buttonlistener.size(); i++) {
            this.buttonlistener.get(i).buttonPressReceived(w);
        }

        if (w.isRecognitionInitEvent() || w.isTrainInitEvent()) {
            this.resetAccelerationFilters();
        }
    }

    /** Fires a button released event.
     */
    public void fireButtonReleasedEvent(int button) {
        ButtonReleasedEvent w = new ButtonReleasedEvent(this, button);
        for (int i = 0; i < this.buttonlistener.size(); i++) {
            this.buttonlistener.get(i).buttonReleaseReceived(w);
        }
    }

    /**
     * Fires a motion start event.
     */
    public void fireMotionStartEvent() {
        MotionStartEvent w = new MotionStartEvent(this);
        for (int i = 0; i < this.accelerationlistener.size(); i++) {
            this.accelerationlistener.get(i).motionStartReceived(w);
        }
    }

    /**
     * Fires a motion stop event.
     */
    public void fireMotionStopEvent() {
        MotionStopEvent w = new MotionStopEvent(this);
        for (int i = 0; i < this.accelerationlistener.size(); i++) {
            this.accelerationlistener.get(i).motionStopReceived(w);
        }
    }
}
