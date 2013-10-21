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

import java.util.EventListener;

/**
 * This interface has to be implemented if the application should react
 * to button press/releases.
 *
 * @author Benjamin 'BePo' Poppinga
 */
public interface ButtonListener extends EventListener {


	/**
	 * This method would be called if a Device button has been pressed.
	 *
	 * @param event The button representation as an event.
	 */
	public abstract void buttonPressReceived(ButtonPressedEvent event);

	/**
	 * This method would be called if a Device button has been released.
	 *
	 * @param event This is actually a meta-event NOT containing which button
	 * has been released.
	 */
	public abstract void buttonReleaseReceived(ButtonReleasedEvent event);


}
