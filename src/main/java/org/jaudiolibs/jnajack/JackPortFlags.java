/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Neil C Smith.
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

import org.jaudiolibs.jnajack.lowlevel.JackLibrary;

/**
 *  A port has a set of flags that are formed from the list below.
 * The flags "JackPortIsInput" and "JackPortIsOutput" are mutually exclusive
 * and it is an error to use them both.
 * @author Neil C Smith
 */
public enum JackPortFlags {


    /**
     * if JackPortIsInput is set, then the port can receive
     * data.
     */
    JackPortIsInput(JackLibrary.JackPortFlags.JackPortIsInput),
    /**
     * if JackPortIsOutput is set, then data can be read from
     * the port.
     */
    JackPortIsOutput(JackLibrary.JackPortFlags.JackPortIsOutput),
    /**
     * if JackPortIsPhysical is set, then the port corresponds
     * to some kind of physical I/O connector.
     */
    JackPortIsPhysical(JackLibrary.JackPortFlags.JackPortIsPhysical),
    /**
     * if JackPortCanMonitor is set, then a call to
     * jack_port_request_monitor() makes sense.
     * * Precisely what this means is dependent on the client. A typical
     * result of it being called with TRUE as the second argument is
     * that data that would be available from an output port (with
     * JackPortIsPhysical set) is sent to a physical output connector
     * as well, so that it can be heard/seen/whatever.
     * * Clients that do not control physical interfaces
     * should never create ports with this bit set.
     */
    JackPortCanMonitor(JackLibrary.JackPortFlags.JackPortCanMonitor),
    /**
     * JackPortIsTerminal means:
     * *	for an input port: the data received by the port
     *                    will not be passed on or made
     * 	           available at any other port
     * * for an output port: the data available at the port
     *                    does not originate from any other port
     * * Audio synthesizers, I/O hardware interface clients, HDR
     * systems are examples of clients that would set this flag for
     * their ports.
     */
    JackPortIsTerminal(JackLibrary.JackPortFlags.JackPortIsTerminal);
    
    int val;

    JackPortFlags(int val) {
        this.val = val;
    }

    public int getIntValue() {
        return val;
    }

}
