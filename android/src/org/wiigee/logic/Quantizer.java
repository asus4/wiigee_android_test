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
import org.wiigee.util.Log;

/**
 * This class implements a quantization component. In this case a
 * k-mean-algorithm is used. In this case the initial values of the algorithm
 * are ordered as two intersected circles, representing an abstract globe with
 * k=14 elements. As a special feature the radius of this globe would be
 * calculated dynamically before the training of this component.
 * 
 * @author Benjamin 'BePo' Poppinga
 */
public class Quantizer {

	/** This is the initial radius of this model. */
	private double radius;

	/** Number of states from the following Hidden Markov Model */
	private int numStates;

	/** The representation of the so called Centeroids */
	private double[][] map;

	/** True, if map is already trained. */
	private boolean maptrained;

	/**
	 * Initialize a empty quantizer. The states variable is necessary since some
	 * algorithms need this value to calculate their values correctly.
	 * 
	 * @param numStates
	 *            number of hidden markov model states
	 */
	public Quantizer(int numStates) {
		this.numStates = numStates;
		this.map = new double[14][3];
		this.maptrained = false;
	}

	/**
	 * Trains this Quantizer with a specific gesture. This means that the
	 * positions of the centeroids would adapt to this training gesture. In our
	 * case this would happen with a summarized virtual gesture, containing all
	 * the other gestures.
	 * 
	 * @param gesture
	 *            the summarized virtual gesture
	 */
	public void trainCenteroids(Gesture gesture) {
		Vector<AccelerationEvent> data = gesture.getData();
		double pi = Math.PI;
		this.radius = (gesture.getMaxAcceleration() + gesture
				.getMinAcceleration()) / 2;
		Log.write("Using radius: " + this.radius);

		// x , z , y
		if (!this.maptrained) {
			this.maptrained = true;
			this.map[0] = new double[] { this.radius, 0.0, 0.0 };
			this.map[1] = new double[] { Math.cos(pi / 4) * this.radius, 0.0,
					Math.sin(pi / 4) * this.radius };
			this.map[2] = new double[] { 0.0, 0.0, this.radius };
			this.map[3] = new double[] { Math.cos(pi * 3 / 4) * this.radius,
					0.0, Math.sin(pi * 3 / 4) * this.radius };
			this.map[4] = new double[] { -this.radius, 0.0, 0.0 };
			this.map[5] = new double[] { Math.cos(pi * 5 / 4) * this.radius,
					0.0, Math.sin(pi * 5 / 4) * this.radius };
			this.map[6] = new double[] { 0.0, 0.0, -this.radius };
			this.map[7] = new double[] { Math.cos(pi * 7 / 4) * this.radius,
					0.0, Math.sin(pi * 7 / 4) * this.radius };

			this.map[8] = new double[] { 0.0, this.radius, 0.0 };
			this.map[9] = new double[] { 0.0, Math.cos(pi / 4) * this.radius,
					Math.sin(pi / 4) * this.radius };
			this.map[10] = new double[] { 0.0,
					Math.cos(pi * 3 / 4) * this.radius,
					Math.sin(pi * 3 / 4) * this.radius };
			this.map[11] = new double[] { 0.0, -this.radius, 0.0 };
			this.map[12] = new double[] { 0.0,
					Math.cos(pi * 5 / 4) * this.radius,
					Math.sin(pi * 5 / 4) * this.radius };
			this.map[13] = new double[] { 0.0,
					Math.cos(pi * 7 / 4) * this.radius,
					Math.sin(pi * 7 / 4) * this.radius };
		}

		int[][] g_alt = new int[this.map.length][data.size()];
		int[][] g = new int[this.map.length][data.size()];

		do {
			// Derive new Groups...
			g_alt = this.copyarray(g);
			g = this.deriveGroups(gesture);

			// calculate new centeroids
			for (int i = 0; i < this.map.length; i++) {
				double zaehlerX = 0;
				double zaehlerY = 0;
				double zaehlerZ = 0;
				int nenner = 0;
				for (int j = 0; j < data.size(); j++) {
					if (g[i][j] == 1) {
						zaehlerX += data.elementAt(j).getX();
						zaehlerY += data.elementAt(j).getY();
						zaehlerZ += data.elementAt(j).getZ();
						nenner++;
					}
				}
				if (nenner > 1) { // nur wenn der nenner>0 oder >1??? ist muss
									// was
					// geaendert werden
					// Log.write("Setze neuen Centeroid!");
					this.map[i] = new double[] {(zaehlerX / (double) nenner),
												(zaehlerY / (double) nenner),
												(zaehlerZ / (double) nenner) };
					// Log.write("Centeroid: "+i+": "+newcenteroid[0]+":"+newcenteroid[1]);
				}
			} // new centeroids

		} while (!equalarrays(g_alt, g));

		// Debug: Printout groups
		/*
		 * for (int i = 0; i < n; i++) { for (int j = 0; j < this.data.size();
		 * j++) { Log.write(g[i][j] + "|"); } Log.write(""); }
		 */

	}

	/**
	 * This methods looks up a Gesture to a group matrix, used by the
	 * k-mean-algorithm (traincenteroid method) above.
	 * 
	 * @param gesture
	 *            the gesture
	 */
	public int[][] deriveGroups(Gesture gesture) {
		Vector<AccelerationEvent> data = gesture.getData();
		int[][] groups = new int[this.map.length][data.size()];

		// Calculate cartesian distance
		double[][] d = new double[this.map.length][data.size()];
		double[] curr = new double[3];
		double[] vector = new double[3];
		for (int i = 0; i < this.map.length; i++) { // zeilen
			double[] ref = this.map[i];
			for (int j = 0; j < data.size(); j++) { // spalten

				curr[0] = data.elementAt(j).getX();
				curr[1] = data.elementAt(j).getY();
				curr[2] = data.elementAt(j).getZ();

				vector[0] = ref[0] - curr[0];
				vector[1] = ref[1] - curr[1];
				vector[2] = ref[2] - curr[2];
				d[i][j] = Math.sqrt((vector[0] * vector[0])
						+ (vector[1] * vector[1]) + (vector[2] * vector[2]));
				// Log.write(d[i][j] + "|");
			}
			// Log.write("");
		}

		// look, to which group a value belongs
		for (int j = 0; j < data.size(); j++) {
			double smallest = Double.MAX_VALUE;
			int row = 0;
			for (int i = 0; i < this.map.length; i++) {
				if (d[i][j] < smallest) {
					smallest = d[i][j];
					row = i;
				}
				groups[i][j] = 0;
			}
			groups[row][j] = 1; // guppe gesetzt
		}

		// Debug output
		/*
		 * for (int i = 0; i < groups.length; i++) { // zeilen for (int j = 0; j
		 * < groups[i].length; j++) { Log.write(groups[i][j] + "|"); }
		 * Log.write(""); }
		 */

		return groups;

	}

	/**
	 * With this method you can transform a gesture to a discrete symbol
	 * sequence with values between 0 and granularity (number of observations).
	 * 
	 * @param gesture
	 *            Gesture to get the observationsequence to.
	 */
	public int[] getObservationSequence(Gesture gesture) {
		int[][] groups = this.deriveGroups(gesture);
		Vector<Integer> sequence = new Vector<Integer>();

		// Log.write("Visible symbol sequence: ");

		for (int j = 0; j < groups[0].length; j++) { // spalten
			for (int i = 0; i < groups.length; i++) { // zeilen
				if (groups[i][j] == 1) {
					// Log.write(" "+ i);
					sequence.add(i);
					break;
				}
			}
		}

		// die sequenz darf nicht zu kurz sein... mindestens so lang
		// wie die anzahl der zustände. weil sonst die formeln nicht klappen.
		// english: this is very dirty! it have to be here because if not
		// too short sequences would cause an error. i've to think about a
		// better resolution than copying the old value a few time.
		while (sequence.size() < this.numStates) {
			sequence.add(sequence.elementAt(sequence.size() - 1));
			// Log.write(" "+sequence.elementAt(sequence.size()-1));
		}

		// Log.write("");

		int[] out = new int[sequence.size()];
		for (int i = 0; i < sequence.size(); i++) {
			out[i] = sequence.elementAt(i);
		}

		return out;
	}

	/**
	 * Prints out the current centeroids-map. Its for debug or technical
	 * interests.
	 */
	public void printMap() {
		Log.write("Centeroids:");
		for (int i = 0; i < this.map.length; i++) {
			Log.write(i + ". :" + this.map[i][0] + ":"
					+ this.map[i][1] + ":" + this.map[i][2]);
		}
	}

	/**
	 * Function to deepcopy an array.
	 */
	private int[][] copyarray(int[][] alt) {
		int[][] neu = new int[alt.length][alt[0].length];
		for (int i = 0; i < alt.length; i++) {
			for (int j = 0; j < alt[i].length; j++) {
				neu[i][j] = alt[i][j];
			}
		}
		return neu;
	}

	/**
	 * Function to look if the two arrays containing the same values.
	 */
	private boolean equalarrays(int[][] one, int[][] two) {
		for (int i = 0; i < one.length; i++) {
			for (int j = 0; j < one[i].length; j++) {
				if (!(one[i][j] == two[i][j])) {
					return false;
				}
			}
		}
		return true;
	}

	public double getRadius() {
		return this.radius;
	}

	public double[][] getHashMap() {
		return this.map;
	}

	public void setUpManually(double[][] map, double radius) {
		this.map = map;
		this.radius = radius;
	}
}
