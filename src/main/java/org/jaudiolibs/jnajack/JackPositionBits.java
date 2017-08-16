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

public enum JackPositionBits {
	
	JackPositionBBT(JackLibrary.jack_position_bits_t.JackPositionBBT),
	JackPositionTimecode(JackLibrary.jack_position_bits_t.JackPositionTimecode),
	JackBBTFrameOffset(JackLibrary.jack_position_bits_t.JackBBTFrameOffset),
	JackAudioVideoRatio(JackLibrary.jack_position_bits_t.JackAudioVideoRatio),
	JackVideoFrameOffset(JackLibrary.jack_position_bits_t.JackVideoFrameOffset);
	
	
	int val;
	
	JackPositionBits(int v) {
		val = v;
	}
	
	public int getIntVal() {
		return val;
	}

}
