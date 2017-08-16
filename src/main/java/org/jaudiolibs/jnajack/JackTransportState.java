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

import org.jaudiolibs.jnajack.lowlevel.JackLibrary;

/**
 * An enum mapping JACK transport states.
 *
 *
 */
public enum JackTransportState {
	/**
	 * Transport halted
	 */
	JackTransportStopped(JackLibrary.jack_transport_state_t.JackTransportStopped),
	
	/**
	 * Transport playing
	 */
	JackTransportRolling(JackLibrary.jack_transport_state_t.JackTransportRolling),
	
	/**
	 * For OLD_TRANSPORT, now ignored
	 */
	JackTransportLooping(JackLibrary.jack_transport_state_t.JackTransportLooping),
	
	/**
	 * Waiting for sync ready
	 */
	JackTransportStarting(JackLibrary.jack_transport_state_t.JackTransportStarting),
	
	/**
	 * This doesn't appear in the API, but it's included anyway
	 */
	JackTransportNetStarting(JackLibrary.jack_transport_state_t.JackTransportNetStarting);
	
	int val;
	
	JackTransportState(int v) {
		val = v;
	}
	
	public int getIntVal() {
		return val;
	}
	
	/**
	 * Find the appropriate {@code JackTransportState} for the supplied {@code int} value.
	 *  
	 * @param value The value to test
	 * @return The corresponding {@code JackTransportState} if one exists, or null otherwise
	 */
	static JackTransportState forVal(int value) {
		for (JackTransportState state: JackTransportState.values()) {
			if (state.getIntVal() == value) {
				return state;
			}
		}
		
		return null;
	}

}
