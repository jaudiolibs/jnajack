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
package org.jaudiolibs.jnajack.util;

import org.jaudiolibs.jnajack.*;
import java.nio.FloatBuffer;
import java.util.EnumSet;

/**
 * A simple example audio client.
 *
 * Implement SimpleAudioClient.Processor to process audio (see examples).
 *
 * This client doesn't currently implement callbacks for sample rate or buffer
 * size changes so may not work correctly if these change while running.
 *
 * @author Neil C Smith
 */
@Deprecated
public class SimpleAudioClient {

    private JackClient client;
    private Processor processor;
    private Callback callback;
    private ShutDownHook shutDownHook;
    private JackPort[] inputPorts;
    private JackPort[] outputPorts;
    private FloatBuffer[] inputBuffers;
    private FloatBuffer[] outputBuffers;
    private float samplerate;
    private int buffersize;
    private boolean autoconnect;
    private volatile boolean active;

    private SimpleAudioClient(JackClient client,
            JackPort[] inputPorts,
            JackPort[] outputPorts,
            boolean autoconnect,
            Processor processor) throws JackException {
        this.client = client;
        this.inputPorts = inputPorts;
        this.outputPorts = outputPorts;
        this.inputBuffers = new FloatBuffer[inputPorts.length];
        this.outputBuffers = new FloatBuffer[outputPorts.length];
        this.autoconnect = autoconnect;
        this.processor = processor;
        this.callback = new Callback();
        this.shutDownHook = new ShutDownHook();
        client.onShutdown(shutDownHook);
    }

    public void activate() throws JackException {
        try {
            samplerate = client.getSampleRate();
            System.out.println("Sample rate = " + samplerate);
            buffersize = client.getBufferSize();
            System.out.println("Buffersize = " + buffersize);
            processor.setup(samplerate, buffersize);
            active = true;
            client.setProcessCallback(callback);
            client.activate();
            if (autoconnect) {
                doAutoconnect();
            }
        } catch (Exception ex) {
            active = false;
            throw new JackException("Could not activate Jack client");
        }
    }
    
    private void doAutoconnect() throws JackException {
        Jack jack = Jack.getInstance();
        String[] physical = jack.getPorts(client, null, JackPortType.AUDIO,
                EnumSet.of(JackPortFlags.JackPortIsInput, JackPortFlags.JackPortIsPhysical));
        int count = Math.min(outputPorts.length, physical.length);
        for (int i=0; i<count; i++) {
            jack.connect(client, outputPorts[i].getName(), physical[i]);
        }
        physical = jack.getPorts(client, null, JackPortType.AUDIO,
                EnumSet.of(JackPortFlags.JackPortIsOutput, JackPortFlags.JackPortIsPhysical));
        count = Math.min(inputPorts.length, physical.length);
        for (int i=0; i<count; i++) {
            jack.connect(client, physical[i], inputPorts[i].getName());
        }
    }

    public void shutdown() {
        active = false;
        client.deactivate();
        client.close();
    }

    private void processBuffers(int nframes) {
        for (int i = 0; i < inputPorts.length; i++) {
            inputBuffers[i] = inputPorts[i].getFloatBuffer();
        }
        for (int i = 0; i < outputPorts.length; i++) {
            outputBuffers[i] = outputPorts[i].getFloatBuffer();
        }
        processor.process(inputBuffers, outputBuffers);
    }

    private class Callback implements JackProcessCallback {

        public boolean process(JackClient client, int nframes) {
            if (!active) {
                return false;
            } else {
                try {
                    processBuffers(nframes);
                    return true;
                } catch (Exception ex) {
                    System.out.println("ERROR : " + ex);
                    active = false;
                    return false;
                }

            }
        }
    }

    private class ShutDownHook implements JackShutdownCallback {

        public void clientShutdown(JackClient client) {
            active = false;
            processor.shutdown();
        }
    }

    public static interface Processor {

        public void setup(float samplerate, int buffersize);

        public void process(FloatBuffer[] inputs, FloatBuffer[] outputs);

        public void shutdown();
    }

    /**
     * Create a SimpleAudioClient.
     * 
     * The Jack server is not started if not already running, and ports are not
     * automatically connected.
     * 
     * @param name
     * @param inputs array of input port names, may be null.
     * @param outputs array of output port names, may be null.
     * @param processor
     * @return client
     * @throws net.neilcsmith.jnajack.JackException
     */
    public static SimpleAudioClient create(
            String name,
            String[] inputs,
            String[] outputs,
            Processor processor) throws JackException {
        return create(name, inputs, outputs, false, false, processor);
    }

    /**
     * Create a SimpleAudioClient.
     * @param name
     * @param inputs array of input port names, may be null.
     * @param outputs array of output port names, may be null.
     * @param autoconnect whether to autoconnect to system input and output ports
     * @param startServer whether to force the Jack server to start if it isn't running -
     * see the Jack documentation for .jackdrc to find out how to set the parameters the server
     * is started with
     * @param processor
     * @return client
     * @throws net.neilcsmith.jnajack.JackException
     */
    public static SimpleAudioClient create(
            String name,
            String[] inputs,
            String[] outputs,
            boolean autoconnect,
            boolean startServer,
            Processor processor) throws JackException {
        if (name == null || processor == null) {
            throw new NullPointerException();
        }
        if (inputs == null) {
            inputs = new String[0];
        }
        if (outputs == null) {
            outputs = new String[0];
        }
        Jack jack = Jack.getInstance();
        EnumSet<JackOptions> options =
                startServer ? EnumSet.noneOf(JackOptions.class)
                : EnumSet.of(JackOptions.JackNoStartServer);

        EnumSet<JackStatus> status = EnumSet.noneOf(JackStatus.class);
        JackClient client;
        try {
            client = jack.openClient(name, options, status);
        } catch (JackException ex) {
            System.out.println("ERROR : Status : " + status);
            throw ex;
        }

        JackPort[] inputPorts = new JackPort[inputs.length];
        EnumSet<JackPortFlags> flags = EnumSet.of(JackPortFlags.JackPortIsInput);
        for (int i = 0; i < inputs.length; i++) {
            inputPorts[i] = client.registerPort(inputs[i], JackPortType.AUDIO, flags);
        }

        JackPort[] outputPorts = new JackPort[outputs.length];
        flags = EnumSet.of(JackPortFlags.JackPortIsOutput);
        for (int i = 0; i < outputs.length; i++) {
            outputPorts[i] = client.registerPort(outputs[i], JackPortType.AUDIO, flags);
        }

        return new SimpleAudioClient(client, inputPorts, outputPorts, autoconnect, processor);
    }
}
