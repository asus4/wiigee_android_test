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

public class Classifier {

	private Vector<GestureModel> gesturemodel; // each gesturetype got its own 
										// gesturemodel in this vector
	private double lastprob;
	
	public Classifier() {
		this.gesturemodel=new Vector<GestureModel>();
		this.lastprob=0.0;
	}
	
	/** 
	 * This method recognize a specific gesture, given to the procedure.
	 * For classification a bayes classification algorithm is used.
	 * 
	 * @param g	gesture to classify
	 */
	public int classifyGesture(Gesture g) {
		//Log.write("Recognizing gesture...");
		
		// Wert im Nenner berechnen, nach Bayes
		double sum = 0;
		for(int i=0; i<this.gesturemodel.size(); i++) {
			sum+=this.gesturemodel.elementAt(i).getDefaultProbability()*
					this.gesturemodel.elementAt(i).matches(g);
		}
		
		int recognized = -1; // which gesture has been recognized
		double recogprob = Integer.MIN_VALUE; // probability of this gesture
		double probgesture = 0; // temporal value for bayes algorithm
		double probmodel = 0; // temporal value for bayes algorithm
		for(int i=0; i<this.gesturemodel.size(); i++) {
			//this.gesturemodel.elementAt(i).print(); // Debug
			double tmpgesture = this.gesturemodel.elementAt(i).matches(g);
			double tmpmodel = this.gesturemodel.elementAt(i).getDefaultProbability();
			
			if(((tmpmodel*tmpgesture)/sum)>recogprob) {
				probgesture=tmpgesture;
				probmodel=tmpmodel;
				recogprob=((tmpmodel*tmpgesture)/sum);
				recognized=i;
			}
		}
		
		// a gesture could be recognized
		if(recogprob>0 && probmodel>0 && probgesture>0 && sum>0) {
			this.lastprob=recogprob;
			return recognized;
		} else {
			// no gesture could be recognized
			return -1;
		}
		
	}
	
	public double getLastProbability() {
		return this.lastprob;
	}
	
	public void addGestureModel(GestureModel gm) {
		this.gesturemodel.add(gm);
	}

	public GestureModel getGestureModel(int id) {
		return this.gesturemodel.elementAt(id);
	}
	
	public Vector<GestureModel> getGestureModels() {
		return this.gesturemodel;
	}

    public int getCountOfGestures() {
        return this.gesturemodel.size();
    }
	
	public void clear() {
		this.gesturemodel = new Vector<GestureModel>();
	}
	

}
