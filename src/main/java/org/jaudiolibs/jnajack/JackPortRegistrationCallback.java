/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Neil C Smith / Chuck Ritola
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this work; if not, see http://www.gnu.org/licenses/
 * 
 * 
 * Please visit http://neilcsmith.net if you need additional information or
 * have any questions.
 *
 */

package org.jaudiolibs.jnajack;

import com.sun.jna.Pointer;

/**
 *  A JACK Port registration callback which is invoked when a Port is registered
 * or unregistered.
 *
 *  @author Chuck Ritola
 *
 */
public interface JackPortRegistrationCallback {

    /**
     *  Called when a JACK port is being registered
     *
     * @param	client	The JACK client which is invoking this callback.
     *  @param portFullName	The full name of the port experiencing this transient
     * in the form clientName:portName
     *  
     *  @since Jul 23, 2012
     */
    public void portRegistered(JackClient client, String portFullName);

    /**
     *  Called when a JACK port is being unregistered
     *
     * @param	client	The JACK client which is invoking this callback.
     *  @param portFullName	The full name of the port experiencing this transient
     * in the form clientName:portName
     *  
     *  @since Jul 23, 2012
     */
    public void portUnregistered(JackClient client, String portFullName);
}
