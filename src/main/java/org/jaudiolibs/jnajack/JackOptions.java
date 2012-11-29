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

import org.jaudiolibs.jnajack.lowlevel.JackLibrary;

/**
 *
 * @author Neil C Smith
 */
public enum JackOptions {

    /**
     * Null value to use when no option bits are needed.
     * Not strictly needed but here for completeness.
     */
    JackNullOption(JackLibrary.JackOptions.JackNullOption),
    /**
     *  Do not automatically start the JACK server when it is not
     * already running.  This option is always selected if
     * \$JACK_NO_START_SERVER is defined in the calling process
     * environment.
     */
    JackNoStartServer(JackLibrary.JackOptions.JackNoStartServer),
    /**
     * Use the exact client name requested.  Otherwise, JACK
     * automatically generates a unique one, if needed.
     */
    JackUseExactName(JackLibrary.JackOptions.JackUseExactName),
    /**
     * Load internal client from optional <em>(char *)
     * load_name</em>.  Otherwise use the @a client_name.
     *
     * Not currently supported by JNAJack
     */
    JackLoadName(JackLibrary.JackOptions.JackLoadName),
    /**
     * Pass optional <em>(char *) load_init</em> string to the
     * jack_initialize() entry point of an internal client.
     *
     * Not currently supported by JNAJack
     */
    JackLoadInit(JackLibrary.JackOptions.JackLoadInit);
    int val;

    JackOptions(int val) {
        this.val = val;
    }

    public int getIntValue() {
        return val;
    }

}
