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

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Device representation for Android Mobile Phones, like HTC Magic. Since
 * the API for Sensor values changed within the last Android SDKs this
 * should only compile for Android SDKs >= 1.5.
 *
 * @author Maarten 'MrSnowflake' Krijn
 * @author zl25drexel
 * @author Benjamin 'BePo' Poppinga
 *
 */
public class AndroidDevice extends Device implements SensorEventListener {
	static final String TAG = "AndroidDevice";

    private float x0, y0, z0, x1, y1, z1;
    private SensorManager sensorManager;

    public AndroidDevice(Context context) {
        super(true);
        // 'Calibrate' values
        this.x0 = 0;
        this.y0 = -SensorManager.STANDARD_GRAVITY;
        this.z0 = 0;
        this.x1 = SensorManager.STANDARD_GRAVITY;
        this.y1 = 0;
        this.z1 = SensorManager.STANDARD_GRAVITY;

        // request acceleration updates
        this.sensorManager = (SensorManager)
                context.getSystemService(Context.SENSOR_SERVICE);
        Sensor accSensor = this.sensorManager.
                getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.sensorManager.
                registerListener(this, accSensor,
                SensorManager.SENSOR_DELAY_GAME);
        
//        Log.i(TAG, "initialized");
    }

    public void close() {
        if(this.sensorManager!=null)
            this.sensorManager.unregisterListener(this);
    }

    public void onSensorChanged(SensorEvent sevent) {
        Sensor sensor = sevent.sensor;
        float[] values = sevent.values;

        if (this.accelerationEnabled && sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            double x, y, z;
            float xraw, yraw, zraw;
                /*
                 * calculation of acceleration vectors starts here. further
                 * information about normation exist in the public papers or
                 * the various www-sources.
                 *
                 */
                xraw = values[SensorManager.DATA_X];
                yraw = values[SensorManager.DATA_Y];
                zraw = values[SensorManager.DATA_Z];

                x = (double) (xraw - x0) / (double) (x1 - x0);
                y = (double) (yraw - y0) / (double) (y1 - y0);
                z = (double) (zraw - z0) / (double) (z1 - z0);
                
                this.fireAccelerationEvent(new double[] {x, y, z});
//                Log.i(TAG, "accel ("+x +","+y +","+z);
        }
    }

    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // Nothing to do.
    }

}
