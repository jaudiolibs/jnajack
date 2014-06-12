/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Neil C Smith
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
import org.jaudiolibs.jnajack.lowlevel.JackLibrary;

/**
 * Static utility functions for working with MIDI data in JACK port buffers.
 * 
 * @author Neil C Smith, with thanks to Salvatore Isaja
 */
public class JackMidi {

    private JackMidi() {
    }

    /**
     * Get the number of MIDI events in the port buffer.
     *
     * @param port
     * @return Number of events
     * @throws org.jaudiolibs.jnajack.JackException
     */
    public static int getEventCount(JackPort port) throws JackException {
        try {
            return port.jackLib.jack_midi_get_event_count(port.bufferPtr);
        } catch (Throwable e) {
            throw new JackException(e);
        }
    }

    /**
     * Get a MIDI event from the port buffer.
     *
     * Jack MIDI is normalised, and the MIDI event returned by this function is
     * guaranteed to be a complete MIDI event (the status byte will always be
     * present, and no realtime events will be interspersed with the event).
     *
     * @param event wrapper to native structure to store retrieved event in
     * @param port JackPort with buffer to retrieve event from
     * @param index index of event to retrieve ( 0 -> getEventCount() )
     * @throws JackException
     */
    public static void eventGet(Event event, JackPort port, int index) throws JackException {
        try {
            JackLibrary.jack_midi_event_t nativeEvent = event.nativeEvent;
            int result = port.jackLib.jack_midi_event_get(nativeEvent, port.bufferPtr, index);
            if (result == 0) {
                return;
            }
        } catch (Throwable e) {
            throw new JackException(e);
        }
        throw new JackException("ENOBUF");
    }

    /**
     * Clear a MIDI event buffer.
     * 
     * This should be called at the beginning of each process cycle before
     * calling eventWrite().
     * 
     * This function may not be called on an input port.
     *
     * @param port
     * @throws org.jaudiolibs.jnajack.JackException
     */
    public static void clearBuffer(JackPort port) throws JackException {
        try {
            port.jackLib.jack_midi_clear_buffer(port.bufferPtr);
        } catch (Throwable e) {
            throw new JackException(e);
        }
    }

    /**
     * Get the size of the largest event that can be stored by the port. This
     * function returns the current space available, taking into account events
     * already stored in the port.
     *
     * @param port
     * @return The size in bytes of the largest event that can be stored by the
     * port.
     * @throws org.jaudiolibs.jnajack.JackException
     */
    public static int maxEventSize(JackPort port) throws JackException {
        try {
            return port.jackLib.jack_midi_max_event_size(port.bufferPtr);
        } catch (Throwable e) {
            throw new JackException(e);
        }
    }

    /**
     * Write an event into a JackPort's buffer.
     *
     * Clients must not write more than data_size bytes into this buffer.
     * Clients must write normalised MIDI data to the port - no running status
     * and no (1-byte) realtime messages interspersed with other messages
     * (realtime messages are fine when they occur on their own, like other
     * messages).
     *
     * Events must be written in order, sorted by their sample offsets. JACK
     * will not sort the events for you, and will refuse to store out-of-order
     * events.
     *
     * @param port JackPort with buffer to write event to
     * @param time Sample offset of event
     * @param data Data to be written. Array should be at least data_size in
     * length
     * @param data_size Length of data to be written
     * @throws JackException
     */
    public static void eventWrite(JackPort port, int time, byte[] data, int data_size) throws JackException {
        try {
            port.jackLib.jack_midi_event_write(port.bufferPtr, time, data, data_size);
        } catch (Throwable e) {
            throw new JackException(e);
        }
    }

    /**
     * Get the number of events that could not be written to the port buffer.
     * 
     * This function returning a non-zero value implies the port buffer is full.
     * Currently the only way this can happen is if events are lost on port
     * mixdown.
     *
     * @param port
     * @return Number of events that could not be written to the event buffer.
     * @throws org.jaudiolibs.jnajack.JackException
     */
    public static int getLostEventCount(JackPort port) throws JackException {
        try {
            return port.jackLib.jack_midi_get_lost_event_count(port.bufferPtr);
        } catch (Throwable e) {
            throw new JackException(e);
        }
    }

    /**
     * Wrapper for the native struct that Jack uses to return a MIDI event in
     * eventGet(). It is recommended to use a single instance of Event and pass
     * it repeatedly into eventGet().
     */
    public static class Event {

        private final JackLibrary.jack_midi_event_t nativeEvent;

        /**
         * Create a JackMidi.Event. Also creates the underlying native struct.
         */
        public Event() {
            this.nativeEvent = new JackLibrary.jack_midi_event_t();
        }

        /**
         * Get the sample time from the start of the current frame of this
         * event.
         *
         * @return int time in samples
         */
        public int time() {
            return nativeEvent.time;
        }

        /**
         * Get the size of the MIDI data encapsulated by this event.
         *
         * @return size in bytes
         */
        public int size() {
            return nativeEvent.size.intValue();
        }

        /**
         * Read the MIDI data from native memory into the provided byte array.
         * The capacity of the provided array must be at least equal to size()
         *
         * @param data array to read native data into
         * @throws JackException
         */
        public void read(byte[] data) throws JackException {
            try {
                nativeEvent.buffer.read(0, data, 0, size());
            } catch (Throwable e) {
                throw new JackException(e);
            }
        }

    }

}
