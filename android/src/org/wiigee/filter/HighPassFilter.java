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
 *
 * This filter removes every acceleration that happens slowly or
 * steadily (like e.g. gravity). Remember: It _passes_ acceleration
 * with a big variety.
 *
 * @author Benjamin 'BePo' Poppinga
 */
public class HighPassFilter extends Filter {

    private double factor;
    private double[] prevAcc;

    public HighPassFilter() {
        super();
        this.factor = 0.1;
        this.reset();
    }

    public HighPassFilter(double factor) {
        super();
        this.factor = factor;
        this.reset();
    }

    @Override
    public void reset() {
        this.prevAcc = new double[] {0.0, 0.0, 0.0};
    }

    @Override
    public double[] filterAlgorithm(double[] vector) {
        double[] retVal = new double[3];
        prevAcc[0] = vector[0] * this.factor + this.prevAcc[0] * (1.0 - this.factor);
        prevAcc[1] = vector[1] * this.factor + this.prevAcc[1] * (1.0 - this.factor);
        prevAcc[2] = vector[2] * this.factor + this.prevAcc[2] * (1.0 - this.factor);

        retVal[0] = vector[0] - prevAcc[0];
        retVal[1] = vector[1] - prevAcc[1];
        retVal[2] = vector[2] - prevAcc[2];
        
        return retVal;
    }

}
