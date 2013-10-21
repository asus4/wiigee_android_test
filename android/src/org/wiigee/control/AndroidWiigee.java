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

import android.content.Context;
import org.wiigee.device.AndroidDevice;
import org.wiigee.event.GestureListener;
import org.wiigee.filter.Filter;
import org.wiigee.util.Log;

/**
 * This is for using wiigee on Android Smartphones. This port has been
 * initiated by Maarten 'MrSnowflake' Krijn and updated by 'zl25drexel'
 * under the pseudonym 'Andgee'.
 *
 * It has been re-integrated into wiigee on 29th May of 2009.
 *
 * @author Maarten 'MrSnowflake' Krijn
 * @author zl25drexel
 * @author Benjamin 'BePo' Poppinga
 */
public class AndroidWiigee extends Wiigee {

    // Version information
    protected static String pluginversion = "1.0.3 alpha - untested!";
    protected static String pluginreleasedate = "20101011";

    // Device
    private AndroidDevice device;

    
    public AndroidWiigee(Context context) {
        super();
        Log.write("This is wiigee-plugin-android (Andgee) version "+pluginversion+" ("+pluginreleasedate+")");
        device = new AndroidDevice(context);
    }

    public void addGestureListener(GestureListener listener) {
            device.addGestureListener(listener);
    }

    public void addFilter(Filter filter) {
            device.addAccelerationFilter(filter);
    }


    public AndroidDevice getDevice() {
        return device;
    }


    /**
     * Sets the Trainbutton for all wiimotes;
     *
     * @param b Button encoding, see static Wiimote values
     */
    public void setTrainButton(int b) {
            device.setTrainButton(b);
    }

    /**
     * Sets the Recognitionbutton for all wiimotes;
     *
     * @param b Button encoding, see static Wiimote values
     */
    public void setRecognitionButton(int b) {
            device.setRecognitionButton(b);
    }

    /**
     * Sets the CloseGesturebutton for all wiimotes;
     *
     * @param b Button encoding, see static Wiimote values
     */
    public void setCloseGestureButton(int b) {
            device.setCloseGestureButton(b);
    }

}
