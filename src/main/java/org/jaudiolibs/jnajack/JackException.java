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
 * General exception thrown if an operation using the JACK library is
 * unsucessful.
 * @author Neil C Smith
 */
public class JackException extends Exception {

    /**
     * Creates a new instance of <code>JackException</code> without detail message.
     */
    public JackException() {
    }


    /**
     * Constructs an instance of <code>JackException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public JackException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>JackException</code> linked to the given cause.
     * @param e the underlying cause of this exception.
     */
    public JackException(Throwable e) {
        super(e);
    }

    /**
     * Constructs an instance of <code>JackException</code> with the specified detail message
     * and underlying cause.
     * @param msg the detail message.
     * @param e the underlying cause of this exception.
     */
    public JackException(String msg, Throwable e) {
        super(msg, e);
    }
}
