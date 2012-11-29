/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Neil C Smith
 * Some methods copyright 2012 Chuck Ritola
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jaudiolibs.jnajack.lowlevel.JackLibrary;
import org.jaudiolibs.jnajack.lowlevel.JackLibrary._jack_port;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;

/**
 *   Wraps a native Jack client.
 *
 *   There is no public constructor - use
 *  <code>Jack.getInstance().openClient(...)</code>
 *
 *   @author Neil C Smith
 */
public class JackClient {

    private final static Logger LOG = Logger.getLogger(JackClient.class.getName());
    private final static String CALL_ERROR_MSG = "Error calling native lib";
    private final static int FRAME_SIZE = 4;
    JackLibrary._jack_client clientPtr; // package private
    private ProcessCallbackWrapper processCallback; // reference kept - is in use!
    private BufferSizeCallbackWrapper buffersizeCallback;
    private SampleRateCallbackWrapper samplerateCallback;
    private ClientRegistrationCallbackWrapper clientRegistrationCallback;
    private GraphOrderCallbackWrapper graphOrderChangeCallback;
    private PortRegistrationCallbackWrapper portRegistrationCallback;
    private PortConnectCallbackWrapper portConnectCallback;
    private ShutdownCallback shutdownCallback;
    private JackShutdownCallback userShutdownCallback;
    private JackPort[] ports;
    private Jack jack;
    private JackLibrary jackLib;
    private String name;

    JackClient(String name, Jack jack, JackLibrary._jack_client client) {
        this.name = name;
        this.jack = jack;
        this.jackLib = jack.jackLib;
        this.clientPtr = client;
        shutdownCallback = new ShutdownCallback();
        try {
            jackLib.jack_on_shutdown(client, shutdownCallback, null);
        } catch (Throwable e) {
            LOG.log(Level.SEVERE, CALL_ERROR_MSG, e);
        }
        this.ports = new JackPort[0];
    }

    /**
     *   Create a new port for the client. This is an object used for moving data
     * of any type in or out of the client. Ports may be connected in various
     * ways. Each port has a short name. The port's full name contains the name
     * of the client concatenated with a colon (:) followed by its short name.
     *
     *   The jack_port_name_size() is the maximum length of this full name.
     * Exceeding that will cause the port registration to fail and return NULL.
     * <em>Checking name size is not currently done in JNAJack</em>
     *
     *   @param name
     *   @param type
     *   @param flags
     *   @return JackPort
     *   @throws JackException
     */
    public JackPort registerPort(String name, JackPortType type, EnumSet<JackPortFlags> flags)
            throws JackException {
        int fl = 0;
        for (JackPortFlags flag : flags) {
            fl |= flag.getIntValue();

        }
        String typeString = type.getTypeString();
        NativeLong bufferSize = new NativeLong(type.getBufferSize());
        NativeLong nativeFlags = new NativeLong(fl);
        JackLibrary._jack_port portPtr = null;
        try {
            portPtr = jackLib.jack_port_register(
                    clientPtr, name, typeString, nativeFlags, bufferSize);
        } catch (Throwable e) {
            LOG.log(Level.SEVERE, CALL_ERROR_MSG, e);
            portPtr = null;
        }
        if (portPtr == null) {
            throw new JackException("Could not register port");
        }
        JackPort port = new JackPort(name, this, portPtr);
        addToPortArray(port);
        return port;

    }

    /**
     *   Convenience method for calling other registerPort - most port creation
     * only requires one flag so this removes the need to create an EnumSet
     *
     *   @param name
     *   @param type
     *   @param flag
     *   @return JackPort
     *   @throws JackException
     */
    public JackPort registerPort(String name, JackPortType type, JackPortFlags flag)
            throws JackException {
        return registerPort(name, type, EnumSet.of(flag));
    }

    private void addToPortArray(JackPort port) {
        JackPort[] pts = ports;
        List<JackPort> portList = new ArrayList<JackPort>(Arrays.asList(pts));
        portList.add(port);
        pts = portList.toArray(new JackPort[portList.size()]);
        ports = pts;
    }

    /**
     *   Tell the Jack server to call the JackProcessCallback whenever there is
     * work be done. The code in the supplied function must be suitable for
     * real-time execution. That means that it cannot call functions that might
     * block for a long time.
     *
     *   @param callback
     *   @throws net.neilcsmith.jnajack.JackException
     */
    public void setProcessCallback(JackProcessCallback callback) throws JackException {
        if (callback == null) {
            throw new NullPointerException();
        }
        ProcessCallbackWrapper wrapper = new ProcessCallbackWrapper(callback);
        int ret = -1;
        try {
            ret = jackLib.jack_set_process_callback(clientPtr, wrapper, null);
        } catch (Throwable e) {
            LOG.log(Level.SEVERE, CALL_ERROR_MSG, e);
            throw new JackException(e);
        }
        if (ret == 0) {
            processCallback = wrapper;
        } else {
            throw new JackException();
        }

    }

    /**
     * Tell the JACK server to call the supplied JackGraphOrderCallback whenever the
     * processing graph is reordered.
     * * All "notification events" are received in a separated non RT thread,
     * the code in the supplied function does not need to be
     * suitable for real-time execution.
     * This method cannot be called while the client is activated
     * (after activate() has been called.)
     *
     *   @param callback
     *   @throws JackException
     *   @since Jul 23, 2012
     */
    //cjritola 2012
    public void setGraphOrderCallback(JackGraphOrderCallback callback)
            throws JackException {
        if (callback == null) {
            throw new NullPointerException("Passed callback is null.");
        }

        try {
            jackLib.jack_set_graph_order_callback(
                    clientPtr, graphOrderChangeCallback = new GraphOrderCallbackWrapper(callback), null);
        } catch (Throwable e) {
            LOG.log(Level.SEVERE, CALL_ERROR_MSG, e);
        }
    }

    /**
     *  Tell the JACK server to call the supplied JackClientRegistrationCallback 
     * whenever a client is registered or unregistered.
     * 
     * All "notification events" are received in a separated non RT thread,
     * the code in the supplied function does not need to be
     * suitable for real-time execution.
     * 
     * NOTE: this method cannot be called while the client is activated
     * (after activate() has been called.)
     *
     *   @param callback
     *   @throws JackException
     *   @since Jul 23, 2012
     */
    //cjritola 2012
    public void setClientRegistrationCallback(JackClientRegistrationCallback callback)
            throws JackException {
        if (callback == null) {
            throw new NullPointerException("Passed callback is null.");
        }
        try {
            jackLib.jack_set_client_registration_callback(
                    clientPtr, clientRegistrationCallback = new ClientRegistrationCallbackWrapper(callback), null);
        } catch (Throwable e) {
            LOG.log(Level.SEVERE, CALL_ERROR_MSG, e);
        }
    }

    /**
     *  Tell the JACK server to call the supplied JackPortConnectCallback 
     * whenever a port is connected or disconnected.
     * 
     * All "notification events" are received in a separated non RT thread,
     * the code in the supplied function does not need to be
     * suitable for real-time execution.
     * 
     * NOTE: this method cannot be called while the client is activated
     * (after activate() has been called.)
     *
     *   @param callback
     *   @throws JackException
     *   @since Jul 23, 2012
     */
    //cjritola 2012
    public void setPortConnectCallback(JackPortConnectCallback callback)
            throws JackException {
        if (callback == null) {
            throw new NullPointerException("Passed callback is null.");
        }
        try {
            jackLib.jack_set_port_connect_callback(
                    clientPtr, portConnectCallback = new PortConnectCallbackWrapper(callback), null);
        } catch (Throwable e) {
            LOG.log(Level.SEVERE, CALL_ERROR_MSG, e);
        }
    }

    /**
     *  Tell the JACK server to call the supplied JackPortRegistrationCallback 
     * whenever a port is registered or unregistered.
     * 
     * All "notification events" are received in a separated non RT thread,
     * the code in the supplied function does not need to be
     * suitable for real-time execution.
     * 
     * NOTE: this method cannot be called while the client is activated
     * (after activate() has been called.)
     *
     *   @param callback
     *   @throws JackException
     *   @since Jul 23, 2012
     */
    //cjritola 2012
    public void setPortRegistrationCallback(JackPortRegistrationCallback callback)
            throws JackException {
        if (callback == null) {
            throw new NullPointerException("Passed callback is null.");
        }
        try {
            jackLib.jack_set_port_registration_callback(
                    clientPtr, portRegistrationCallback = new PortRegistrationCallbackWrapper(callback), null);
        } catch (Throwable e) {
            LOG.log(Level.SEVERE, CALL_ERROR_MSG, e);
        }
    }

    /**
     *   Set interface to be called if buffer size changes
     *
     *   @param callback
     *   @throws net.neilcsmith.jnajack.JackException
     */
    public void setBuffersizeCallback(JackBufferSizeCallback callback)
            throws JackException {
        if (callback == null) {
            throw new NullPointerException();
        }
        BufferSizeCallbackWrapper wrapper = new BufferSizeCallbackWrapper(callback);
        int ret = -1;
        try {
            ret = jackLib.jack_set_buffer_size_callback(clientPtr, wrapper, null);
        } catch (Throwable e) {
            LOG.log(Level.SEVERE, CALL_ERROR_MSG, e);
            throw new JackException(e);
        }
        if (ret == 0) {
            buffersizeCallback = wrapper;
        } else {
            throw new JackException();
        }
    }

    /**
     *   Set interface to be called if sample rate changes.
     *
     *   @param callback
     *   @throws net.neilcsmith.jnajack.JackException
     */
    public void setSampleRateCallback(JackSampleRateCallback callback)
            throws JackException {
        if (callback == null) {
            throw new NullPointerException();
        }
        SampleRateCallbackWrapper wrapper = new SampleRateCallbackWrapper(callback);
        int ret = -1;
        try {
            ret = jackLib.jack_set_sample_rate_callback(clientPtr, wrapper, null);
        } catch (Throwable e) {
            LOG.log(Level.SEVERE, CALL_ERROR_MSG, e);
            throw new JackException(e);
        }
        if (ret == 0) {
            samplerateCallback = wrapper;
        } else {
            throw new JackException();
        }
    }

    /**
     *   Register a function (and argument) to be called if and when the JACK
     * server shuts down the client thread. The function is not called on the
     * process thread --- use only async-safe functions, and remember that it is
     * executed from another thread. A typical function might set a flag or
     * write to a pipe so that the rest of the application knows that the JACK
     * client thread has shut down. NOTE: clients do not need to call this. It
     * exists only to help more complex clients understand what is going on. It
     * should be called before activate().
     *
     *   @param callback
     *   @throws net.neilcsmith.jnajack.JackException
     */
    public void onShutdown(JackShutdownCallback callback) throws JackException {
        userShutdownCallback = callback;
    }

    /**
     *   Tell the Jack server that the program is ready to start processing audio.
     *
     *   @throws JackException if client could not be activated.
     */
    public void activate() throws JackException {
        int ret = -1;
        try {
            ret = jackLib.jack_activate(clientPtr);
        } catch (Throwable e) {
            LOG.log(Level.SEVERE, CALL_ERROR_MSG, e);
            throw new JackException(e);
        }
        if (ret != 0) {
            throw new JackException();
        }
    }

    /**
     *   Tell the JACK server to remove this client from the process graph. Also,
     * disconnect all ports belonging to it, since inactive clients have no port
     * connections.
     */
    public void deactivate() {
        try {
            jackLib.jack_deactivate(clientPtr);
        } catch (Throwable e) {
            LOG.log(Level.SEVERE, CALL_ERROR_MSG, e);
        }
    }

    /**
     *   Disconnects this client from the JACK server.
     */
    public void close() {
        try {
            if (clientPtr != null) {
                jackLib.jack_client_close(clientPtr);
            }

        } catch (Throwable e) {
            LOG.log(Level.SEVERE, CALL_ERROR_MSG, e);
        } finally {
            clientPtr = null;
        }
    }

    public String getName() {
        return name;
    }

    /**
     *   Get the sample rate of the jack system, as set by the user when jackd was
     * started.
     *
     *   @return sample rate
     *   @throws net.neilcsmith.jnajack.JackException
     */
    public int getSampleRate() throws JackException {
        try {
            return jackLib.jack_get_sample_rate(clientPtr);
        } catch (Throwable e) {
            LOG.log(Level.SEVERE, CALL_ERROR_MSG, e);
            throw new JackException(e);
        }
    }

    /**
     *   The current maximum size that will ever be passed to the
     * JackProcessCallback. It should only be used *before* the client has been
     * activated. This size may change, clients that depend on it must register
     * a JackBuffersizeCallback so they will be notified if it does.
     *
     *   @return int maximum buffersize.
     *   @throws net.neilcsmith.jnajack.JackException
     */
    public int getBufferSize() throws JackException {
        try {
            return jackLib.jack_get_buffer_size(clientPtr);
        } catch (Throwable e) {
            LOG.log(Level.SEVERE, CALL_ERROR_MSG, e);
            throw new JackException(e);
        }
    }

    private void processShutdown() {
        clientPtr = null;
        if (userShutdownCallback != null) {
            userShutdownCallback.clientShutdown(this);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    private class ProcessCallbackWrapper implements JackLibrary.JackProcessCallback {

        JackProcessCallback callback;

        ProcessCallbackWrapper(JackProcessCallback callback) {
            this.callback = callback;
            jack.setupCTI(this);
        }

        public int invoke(int nframes) {
            int ret = 1;
            try {
                JackPort[] pts = ports;

                int nbyteframes = nframes * FRAME_SIZE;

                for (JackPort port : pts) {
                    Pointer buffer = jackLib.jack_port_get_buffer(
                            port.portPtr, nframes);
                    if (buffer == port.buffer && port.byteBuffer.capacity() == nbyteframes) {
                        port.byteBuffer.rewind();
                        port.floatBuffer.rewind();
                    } else {
                        port.buffer = buffer;
                        port.byteBuffer = buffer.getByteBuffer(0, nbyteframes);
                        port.floatBuffer = port.byteBuffer.asFloatBuffer();
                    }
                }
                if (callback.process(JackClient.this, nframes)) {
                    ret = 0;
                }
            } catch (Throwable ex) {
                LOG.log(Level.SEVERE, "Error in process callback", ex);
                ret = 1;
            }
            if (ret != 0) {
                jack.forceThreadDetach();
            }
            return ret;
        }
    }

    private class ShutdownCallback implements JackLibrary.JackShutdownCallback {

        public void invoke(Pointer arg) {
            processShutdown();
            jack.forceThreadDetach();
        }
    }

    private class BufferSizeCallbackWrapper implements JackLibrary.JackBufferSizeCallback {

        JackBufferSizeCallback callback;

        BufferSizeCallbackWrapper(JackBufferSizeCallback callback) {
            this.callback = callback;
        }

        public int invoke(int nframes, Pointer arg) {
            int ret = -1;
            try {
                callback.buffersizeChanged(JackClient.this, nframes);
                ret = 0;
            } catch (Throwable e) {
                LOG.log(Level.SEVERE, "Error in buffersize callback", e);
                ret = -1;
            }
            return ret;
        }
    }

    //cjritola 2012
    private class PortConnectCallbackWrapper implements JackLibrary.JackPortConnectCallback {

        JackPortConnectCallback callback;

        PortConnectCallbackWrapper(JackPortConnectCallback callback) {
            this.callback = callback;
        }

        @Override
        public void invoke(int a, int b, int connect, Pointer arg) {
            try {
                _jack_port pA = jackLib.jack_port_by_id(clientPtr, a);
                _jack_port pB = jackLib.jack_port_by_id(clientPtr, b);

                String portNameA = jackLib.jack_port_name(pA);
                String portNameB = jackLib.jack_port_name(pB);

                if (connect != 0) {
                    callback.portsConnected(JackClient.this, portNameA, portNameB);
                } else {
                    callback.portsDisconnected(JackClient.this, portNameA, portNameB);
                }
            } catch (Throwable e) {
                LOG.log(Level.SEVERE, "Error in port connection callback", e);
            }
        }
    }

    //cjritola 2012
    private class ClientRegistrationCallbackWrapper implements JackLibrary.JackClientRegistrationCallback {

        JackClientRegistrationCallback callback;

        ClientRegistrationCallbackWrapper(JackClientRegistrationCallback callback) {
            this.callback = callback;
        }

        @Override
        public void invoke(ByteByReference name, int register, Pointer arg) {
            try {
                String nameString = name.getPointer().getString(0);
                if (register != 0) {
                    callback.clientRegistered(JackClient.this, nameString);
                } else {
                    callback.clientUnregistered(JackClient.this, nameString);
                }
            } catch (Throwable e) {
                LOG.log(Level.SEVERE, "Error in client registration callback", e);
            }
        }
    }

    //cjritola 2012
    private class PortRegistrationCallbackWrapper implements JackLibrary.JackPortRegistrationCallback {

        JackPortRegistrationCallback callback;

        PortRegistrationCallbackWrapper(JackPortRegistrationCallback callback) {
            this.callback = callback;
        }

        @Override
        public void invoke(int port, int int1, Pointer arg) {
            try {

                _jack_port p = jackLib.jack_port_by_id(clientPtr, port);
                String portName = jackLib.jack_port_name(p);
                if (int1 != 0) {
                    callback.portRegistered(JackClient.this, portName);
                } else {
                    callback.portUnregistered(JackClient.this, portName);
                }
            } catch (Throwable e) {
                LOG.log(Level.SEVERE, "Error in port registration callback", e);
            }
        }
    }

    //cjritola 2012
    private class GraphOrderCallbackWrapper implements JackLibrary.JackGraphOrderCallback {

        JackGraphOrderCallback callback;

        GraphOrderCallbackWrapper(JackGraphOrderCallback callback) {
            this.callback = callback;
        }

        @Override
        public int invoke(Pointer arg) {
            callback.graphOrderChanged(JackClient.this);
            return 0;
        }
    }

    private class SampleRateCallbackWrapper implements JackLibrary.JackSampleRateCallback {

        JackSampleRateCallback callback;

        SampleRateCallbackWrapper(JackSampleRateCallback callback) {
            this.callback = callback;
        }

        public int invoke(int nframes, Pointer arg) {
            int ret = -1;
            try {
                callback.sampleRateChanged(JackClient.this, nframes);
                ret = 0;
            } catch (Throwable e) {
                LOG.log(Level.SEVERE, "Error in samplerate callback", e);
                ret = -1;
            }
            return ret;
        }
    }
}