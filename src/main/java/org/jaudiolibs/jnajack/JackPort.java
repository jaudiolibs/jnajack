/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Neil C Smith
 * 
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
import com.sun.jna.ptr.PointerByReference;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jaudiolibs.jnajack.lowlevel.JackLibrary;

/**
 * Wraps a native Jack port.
 *
 * @author Neil C Smith
 */
public class JackPort {

    JackLibrary._jack_port portPtr;
    Pointer buffer;
    ByteBuffer byteBuffer;
    FloatBuffer floatBuffer;
    private JackClient client;
    private String shortName;
    private final static Logger LOG = Logger.getLogger(JackPort.class.getName());
    private final static String CALL_ERROR_MSG = "Error calling native lib";

    JackPort(String shortName, JackClient client, JackLibrary._jack_port portPtr) {
        this.shortName = shortName;
        this.portPtr = portPtr;
        this.client = client;
    }

    /**
     * Get the ByteBuffer associated with this port. Do not cache this value
     * between process calls - this buffer reference is only valid inside the
     * process callback.
     *
     * The returned buffer is direct.
     *
     * For audio use
     * <code>getFloatBuffer()</code> as this will be more efficient.
     *
     * @return buffer associated with this port.
     */
    // @TODO should we create this lazily in call to client. MIDI ports won't require this.
    public ByteBuffer getBuffer() {
        return byteBuffer;
    }

    /**
     * Get the FloatBuffer associated with this port. Do not cache this value
     * between process calls - this buffer reference is only valid inside the
     * process callback.
     *
     * The returned buffer is direct.
     *
     * @return buffer associated with this port.
     */
    public FloatBuffer getFloatBuffer() {
        return floatBuffer;
    }

    /**
     * Get the full name for this port including the "client_name:" prefix.
     *
     * @return full name
     */
    public String getName() {
        return client.getName() + ":" + shortName;
    }

    /**
     * Get the short name for this port (without client name prefix).
     *
     * @return short name
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Returns the full names of the ports which are connected to this port. If
     * none, returns an empty array.
     *
     * @return Array of port names
     * @throws JackException
     * @since Jul 22, 2012
     */
    public String[] getConnections() throws JackException {
        try {
            JackLibrary jackLib = Jack.getInstance().jackLib;
            Pointer ptr = jackLib.jack_port_get_connections(portPtr);
            if (ptr == null) {
                return new String[0];
            } else {
                String[] res = ptr.getStringArray(0);
                jackLib.jack_free(ptr);
                return res;
            }
        } catch (Throwable e) {
            LOG.log(Level.SEVERE, CALL_ERROR_MSG, e);
            throw new JackException(e);
        }
    }
}