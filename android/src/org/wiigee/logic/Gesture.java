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
import org.wiigee.event.AccelerationEvent;

/**
 * This class represents ONE movement trajectory in a
 * concrete instance.
 * 
 * @author Benjamin 'BePo' Poppinga
 */

public class Gesture implements Cloneable {

	/** Min/MaxAcceleration setup manually? */
	private boolean minmaxmanual;
	private double minacc;
	private double maxacc;
	
	/** The complete trajectory as WiimoteAccelerationEvents
	 * as a vector. It's a vector because we don't want to
	 * loose the chronology of the stored events.
	 */
	private Vector<AccelerationEvent> data;

	/**
	 * Create an empty Gesture.
	 */
	public Gesture() {
		this.data = new Vector<AccelerationEvent>();
	}

	/** 
	 * Make a deep copy of another Gesture object.
	 * 
	 * @param original Another Gesture object
	 */
	public Gesture(Gesture original) {
		this.data = new Vector<AccelerationEvent>();
		Vector<AccelerationEvent> origin = original.getData();
		for (int i = 0; i < origin.size(); i++) {
			this.add((AccelerationEvent) origin.get(i));
		}
	}


	/**
	 * Adds a new acceleration event to this gesture.
	 * 
	 * @param event The WiimoteAccelerationEvent to add.
	 */
	public void add(AccelerationEvent event) {
		this.data.add(event);
	}

	/**
	 * Returns the last acceleration added to this gesture.
	 * 
	 * @return the last acceleration event added.
	 */
	public AccelerationEvent getLastData() {
		return (AccelerationEvent) this.data.get(this.data.size() - 1);
	}

	/**
	 * Returns the whole chronological sequence of accelerations as
	 * a vector.
	 * 
	 * @return chronological sequence of accelerations.
	 */
	public Vector<AccelerationEvent> getData() {
		return this.data;
	}
	
	/**
	 * Removes the first element of the acceleration queue of a gesture
	 */
	public void removeFirstData() {
		this.data.remove(0);
	}
	
	public int getCountOfData() {
		return this.data.size();
	}
	
	public void setMaxAndMinAcceleration(double max, double min) {
		this.maxacc = max;
		this.minacc = min;
		this.minmaxmanual = true;
	}
	
	public double getMaxAcceleration() {
		if(!this.minmaxmanual) {
		double maxacc = Double.MIN_VALUE;
		for(int i=0; i<this.data.size(); i++) {
			if(Math.abs(this.data.get(i).getX()) > maxacc) {
				maxacc=Math.abs(this.data.get(i).getX());
			}
			if(Math.abs(this.data.get(i).getY()) > maxacc) {
				maxacc=Math.abs(this.data.get(i).getY());
			}
			if(Math.abs(this.data.get(i).getZ()) > maxacc) {
				maxacc=Math.abs(this.data.get(i).getZ());
			}
		}
		return maxacc;
		} else {
			return this.maxacc;
		}
	}
	
	public double getMinAcceleration() {
		if(!this.minmaxmanual) {
		double minacc = Double.MAX_VALUE;
		for(int i=0; i<this.data.size(); i++) {
			if(Math.abs(this.data.get(i).getX()) < minacc) {
				minacc=Math.abs(this.data.get(i).getX());
			}
			if(Math.abs(this.data.get(i).getY()) < minacc) {
				minacc=Math.abs(this.data.get(i).getY());
			}
			if(Math.abs(this.data.get(i).getZ()) < minacc) {
				minacc=Math.abs(this.data.get(i).getZ());
			}
		}
		return minacc;
		} else {
			return this.minacc;	
		}
	}
}
