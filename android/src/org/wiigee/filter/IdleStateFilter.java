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

package org.wiigee.filter;

/**
 * Filters if the wiimote is not moved in any way. Be careful in using
 * this filter together with a HighPassFilter.
 *
 * @author Benjamin 'BePo' Poppinga
 */
public class IdleStateFilter extends Filter {
	
	private double sensivity;
	
	/**
	 * Since an acceleration sensor usually provides information even
	 * if it doesn't move, this filter removes the data if it's in the
	 * idle state.
	 */
	public IdleStateFilter() {
		super();
		this.sensivity = 0.1;
	}

    @Override
    public void reset() {
        // not needed
    }

    @Override
	public double[] filterAlgorithm(double[] vector) {
		// calculate values needed for filtering:
		// absolute value
		double absvalue = Math.sqrt((vector[0]*vector[0])+
				(vector[1]*vector[1])+(vector[2]*vector[2]));
		
		// filter formulaes and return values
		if(absvalue > 1+this.sensivity ||
		   absvalue < 1-this.sensivity) {
			return vector;
		} else {
			return null;	
		}
	}
	
	/**
	 * Defines the absolute value when the wiimote should react to acceleration.
	 * This is a parameter for the first of the two filters: idle state
	 * filter. For example: sensivity=0.2 makes the wiimote react to acceleration
	 * where the absolute value is equal or greater than 1.2g. The default value 0.1
	 * should work well. Only change if you are sure what you're doing.
	 * 
	 * @param sensivity
	 * 		acceleration data values smaller than this value wouldn't be detected.
	 */ 
	public void setSensivity(double sensivity) {
		this.sensivity = sensivity;
	}
	
	public double getSensivity() {
		return this.sensivity;
	}

}
