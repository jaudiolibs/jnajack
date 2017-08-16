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

/**
 * Wrapper for the native jack_position_t struct.
 * 
 */
public class JackPosition {
	
	private JackLibrary.jack_position_t nativePosition;
	
	public JackPosition() {
		nativePosition = new JackLibrary.jack_position_t();
	}
	
	public JackPosition(JackLibrary.jack_position_t p) {
		nativePosition = p;
	}
	
	
	public final long getUnique_1() {
		return nativePosition.unique_1;
	}
	
	public final long getUsecs() {
		return nativePosition.usecs;
	}
	
	public final int getFrameRate() {
		return nativePosition.frame_rate;
	}
	
	public int getFrame() {
		return nativePosition.frame;
	}
	
	public void setValid(int v) {
		nativePosition.valid = v;
	}
	
	public int getValid() {
		return nativePosition.valid;
	}
	
	public void setBar(int b) {
		nativePosition.bar = b;
	}
	
	public int getBar() {
		return nativePosition.bar;
	}
	
	public void setBeat(int b) {
		nativePosition.beat = b;
	}
	
	public int getBeat() {
		return nativePosition.beat;
	}
	
	public void setTick(int t) {
		nativePosition.tick = t;
	}
	
	public int getTick() {
		return nativePosition.tick;
	}
	
	public void setBarStartTick(double bsp) {
		nativePosition.bar_start_tick = bsp;
	}
	
	public double getBarStartTick() {
		return nativePosition.bar_start_tick;
	}
	
	public void setBeatsPerBar(float bpb) {
		nativePosition.beats_per_bar = bpb;
	}
	
	public float getBeatsPerBar() {
		return nativePosition.beats_per_bar;
	}
	
	public void setBeatType(float bt) {
		nativePosition.beat_type = bt;
	}
	
	public float getBeatType() {
		return nativePosition.beat_type;
	}
	
	public void setTicksPerBeat(double tpb) {
		nativePosition.ticks_per_beat = tpb;
	}
	
	public double getTicksPerBeat() {
		return nativePosition.ticks_per_beat;
	}
	
	public void setBeatsPerMinute(double bpm) {
		nativePosition.beats_per_minute = bpm;
	}
	public double getBeatsPerMinute() {
		return nativePosition.beats_per_minute;
	}
	
	public double getFrameTime() {
		return nativePosition.frame_time;
	}
	
	public double getNextTime() {
		return nativePosition.next_time;
	}
	
	public int getBbtOffset() {
		return nativePosition.bbt_offset;
	}
	
	public float getAudioFramesPerVideoFrame() {
		return nativePosition.audio_frames_per_video_frame;
	}
	
	public int getVideoOffset() {
		return nativePosition.video_offset;
	}
	
	public long getUnique_2() {
		return nativePosition.unique_2;
	}
	
	JackLibrary.jack_position_t getNativePosition() {
		return nativePosition;
	}
	
	void setNativePosition(JackLibrary.jack_position_t np) {
		if (np == null)
			throw new NullPointerException();
		
		nativePosition = np;
	}
	
	// Some convenience methods
	public void incrementBar() {
		nativePosition.bar++;
	}
	
	public void incrementBeat() {
		nativePosition.beat++;
	}
	
	public void incrementTick() {
		nativePosition.tick++;
	}
	
	public void addToBar(int toAdd) {
		nativePosition.bar += toAdd;
	}
	
	public void addToBeat(int toAdd) {
		nativePosition.beat += toAdd;
	}
	
	public void addToTick(int toAdd) {
		nativePosition.tick += toAdd;
	}
	
	public void addToBarStartTick(double toAdd) {
		nativePosition.bar_start_tick += toAdd;
	}
	
	public void subtractFromBar(int toAdd) {
		nativePosition.bar -= toAdd;
	}
	
	public void subtractFromBeat(int toAdd) {
		nativePosition.beat -= toAdd;
	}
	
	public void subtractFromTick(int toAdd) {
		nativePosition.tick -= toAdd;
	}
	
	

}
