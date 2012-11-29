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
 *
 * @author Neil C Smith
 */
public enum JackStatus {

    /**
     * Overall operation failed.
     */
    JackFailure(JackLibrary.JackStatus.JackFailure),
    /**
     * The operation contained an invalid or unsupported option.
     */
    JackInvalidOption(JackLibrary.JackStatus.JackInvalidOption),
    /**
     * The desired client name was not unique.  With the @ref
     * JackUseExactName option this situation is fatal.  Otherwise,
     * the name was modified by appending a dash and a two-digit
     * number in the range "-01" to "-99".  The
     * jack_get_client_name() function will return the exact string
     * that was used.  If the specified @a client_name plus these
     * extra characters would be too long, the open fails instead.
     */
    JackNameNotUnique(JackLibrary.JackStatus.JackNameNotUnique),
    /**
     * The JACK server was started as a result of this operation.
     * Otherwise, it was running already.  In either case the caller
     * is now connected to jackd, so there is no race condition.
     * When the server shuts down, the client will find out.
     */
    JackServerStarted(JackLibrary.JackStatus.JackServerStarted),

    /**
     * Unable to connect to the JACK server.
     */
    JackServerFailed(JackLibrary.JackStatus.JackServerFailed),

    /**
     * Communication error with the JACK server.
     */
    JackServerError(JackLibrary.JackStatus.JackServerError),

    /**
     * Requested client does not exist.
     */
    JackNoSuchClient(JackLibrary.JackStatus.JackNoSuchClient),

    /**
     * Unable to load internal client.
     */
    JackLoadFailure(JackLibrary.JackStatus.JackLoadFailure),

    /**
     * Unable to initialize client
     */
    JackInitFailure(JackLibrary.JackStatus.JackInitFailure),

    /**
     * Unable to access shared memory
     */
    JackShmFailure(JackLibrary.JackStatus.JackShmFailure),

    /**
     * Client's protocol version does not match
     */
    JackVersionError(JackLibrary.JackStatus.JackVersionError);

    int val;

    JackStatus(int val) {
        this.val = val;
    }

    public int getIntValue() {
        return val;
    }

}
