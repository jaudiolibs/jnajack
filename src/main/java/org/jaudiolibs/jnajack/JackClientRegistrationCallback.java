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
 *   A JACK client registration callback which is called when a JACK client is
 * registered or unregistered.
 *
 *   @author Chuck Ritola
 *
 */
public interface JackClientRegistrationCallback {

    /**
     *   Called when a JACK client is registered.
     *
     *   @param	invokingClient	The JACK client which is invoking this callback.
     * This is <b>NOT</b> the client being registered.
     *  @param clientName name of the client being registered
     *   @since Jul 23, 2012
     */
    public void clientRegistered(JackClient invokingClient, String clientName);

    /**
     *   Called when a JACK client is unregistered.
     *
     * * @param invokingClient	The JACK client which is invoking this callback.
     * This is <b>NOT</b> the client being registered.
     *   @param clientName name of the client being unregistered
     *   @since Jul 23, 2012
     */
    public void clientUnregistered(JackClient invokingClient, String clientName);
}
