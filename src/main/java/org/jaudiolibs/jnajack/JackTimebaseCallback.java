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

/**
 * Callback for timebase changes
 * 
 */
public interface JackTimebaseCallback {

	/**
	 * Called when there is a time change (position, BPM, state, etc.)
	 * 
	 * @param invokingClient
	 * @param state The current transport state (see {@link JackTransportState})
	 * @param nframes The number of frames in current period
	 * @param position The {@link JackPosition} object for the next cycle; {@code position.getFrame()} will be its frame
	 *        number.
	 *        If {@code newPosition} is
	 *        {@code false}, this object contains extended position information from the current cycle. If {@code true},
	 *        it contains
	 *        whatever was set by the requester. The timebase_callback's task is to update the extended information
	 *        here.
	 * @param newPosition {@code true} for a newly requested position, or for the first cycle after the
	 *        timebase_callback is defined.
	 */
	public void timebaseChanged(JackClient invokingClient, int state, int nframes, JackPosition position,
			boolean newPosition);

}
