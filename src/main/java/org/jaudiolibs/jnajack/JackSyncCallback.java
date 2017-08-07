/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (c) 2017 Matthew MacLeod
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version.
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this work; if not, see http://www.gnu.org/licenses/
 */
package org.jaudiolibs.jnajack;

/**
 * Call back for JACK 'slow-sync clients'
 * 
 */
public interface JackSyncCallback {

	/**
	 * When the client is active, this callback is invoked just before process() in the same thread. This occurs once
	 * after registration, then subsequently whenever some client requests a new position, or the transport enters the
	 * JackTransportStarting state. This realtime function must not wait.
	 * 
	 * @param client
	 * @param state 
	 */
	public void slowSync(JackClient client, int state);

}
