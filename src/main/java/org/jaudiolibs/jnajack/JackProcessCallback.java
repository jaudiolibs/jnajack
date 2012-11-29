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
 * Implement this interface and set on client to be called on every process cycle.
 *
 * @author Neil C Smith
 */
public interface JackProcessCallback {

    /**
     * Called on every process cycle.
     * @param client 
     * @param nframes number of frames to process.
     * @return true if all OK. Returning false or throwing an Exception during
     * this method call will remove the client from the JACK graph.
     */
    public boolean process(JackClient client, int nframes);

}
