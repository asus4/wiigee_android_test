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

package org.wiigee.control;

import org.wiigee.util.Log;

/**
 * The mother of all classes. :-) It's just used as parent class
 * to print version information and later on maybe dynamic configuring
 * of the whole wiimote system... detecting plugins and devices automatically
 * maybe. :)
 *
 * @author Benjamin 'BePo' Poppinga
 */
public class Wiigee {

    protected static String version = "1.5.5 alpha";
    protected static String releasedate = "20090714";

    protected Wiigee() {
        Log.write("This is wiigee version "+version+" ("+releasedate+")");
    }
    
}
