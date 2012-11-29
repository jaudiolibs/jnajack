/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Neil C Smith.
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
 * Implement this interface and set on client to be informed if the buffersize
 * changes.
 *
 * @author Neil C Smith
 */
public interface JackBufferSizeCallback {

    /**
     * Method invoked whenever the JACK engine buffer size changes.  Although
     * this function is called in the JACK process thread, the normal process
     * cycle is suspended during its operation, causing a gap in the audio flow.
     * So, it is OK to perform operations in this method that are not realtime
     * 'safe'.
     * 
     * @param client
     * @param buffersize
     */
    public void buffersizeChanged(JackClient client, int buffersize);

}
