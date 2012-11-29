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

/**
 *  A JACK callback which is invoked when two ports and connected or
 * disconnected.
 *
 *  @author Chuck Ritola
 *
 */
public interface JackPortConnectCallback {

    /**
     *  Invoked when two ports are connected. Full names are of the format
     * <code>Client:Port</code>
     *
     *  @param client	The JACK client which is invoking this callback.
     *  @param portName1
     *  @param portName2
     *
     *  @since Jul 23, 2012
     */
    public void portsConnected(JackClient client, String portName1, String portName2);

    /**
     *  Invoked when two ports are disconnected. Full names are of the format
     * <code>Client:Port</code>
     *
     *  @param client	The JACK client which is invoking this callback.
     *  @param portName1
     *  @param portName2
     *
     *  @since Jul 23, 2012
     */
    public void portsDisconnected(JackClient client, String portName1, String portName2);
}
