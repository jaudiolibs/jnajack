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
 * Represents a type of Port in JACK. Replaces the port type Strings in JACK.
 * @author Neil C Smith 
 */
public class JackPortType {
    
    /**
     * Audio port.
     */
    public final static JackPortType AUDIO =
            new JackPortType(JackLibrary.JACK_DEFAULT_AUDIO_TYPE, 0);
    /**
     * MIDI port. Not yet implemented in JNAJack.
     */
    public final static JackPortType MIDI =
            new JackPortType(JackLibrary.JACK_DEFAULT_MIDI_TYPE, 0);

    private String type;
    private int bufferSize;

    /**
     * Create a JackPortType.
     *
     * Buffersizes of inbuilt JACK types are passed as 0.
     *
     * @param type String passed as type String to JACK
     * @param bufferSize passed to JACK when creating a Port of this type
     */
    public JackPortType(String type, int bufferSize) {
        if (type == null) {
            throw new NullPointerException();
        }
        this.type = type;
        this.bufferSize = bufferSize;
    }

    /**
     * Get the Type String for this JackPortType
     * @return type
     */
    public String getTypeString() {
        return type;
    }

    /**
     * Get the buffer size for this JackPortType
     * @return buffer size
     */
    public int getBufferSize() {
        return bufferSize;
    }

}
