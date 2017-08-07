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
 * Enum mapping optional fields of {@link JackPosition}.
 * 
 */
public enum JackTransportBits {
	/**
	 * Bar, Beat, Tick 
	 */
	JackTransportState(JackLibrary.jack_transport_bits_t.JackTransportState),
	/**
	 * External timecode 
	 */
	JackTransportPosition(JackLibrary.jack_transport_bits_t.JackTransportPosition),
	/**
	 * Frame offset of BBT information
	 */
	JackTransportLoop(JackLibrary.jack_transport_bits_t.JackTransportLoop),
	/**
	 * Frame offset of BBT information
	 */
	JackTransportSMPTE(JackLibrary.jack_transport_bits_t.JackTransportSMPTE),
	/**
	 * frame offset of first video frame
	 */
	JackTransportBBT(JackLibrary.jack_transport_bits_t.JackTransportBBT);

	int val;

	JackTransportBits(int v) {
		val = v;
	}

	public int getIntVal() {
		return val;
	}

}
