
package org.jaudiolibs.jnajack.examples;

import org.jaudiolibs.jnajack.Jack;
import org.jaudiolibs.jnajack.JackBufferSizeCallback;
import org.jaudiolibs.jnajack.JackClient;
import org.jaudiolibs.jnajack.JackException;
import org.jaudiolibs.jnajack.JackMidi;
import org.jaudiolibs.jnajack.JackOptions;
import org.jaudiolibs.jnajack.JackPort;
import org.jaudiolibs.jnajack.JackPortConnectCallback;
import org.jaudiolibs.jnajack.JackPortFlags;
import org.jaudiolibs.jnajack.JackPortType;
import org.jaudiolibs.jnajack.JackProcessCallback;
import org.jaudiolibs.jnajack.JackStatus;

import java.util.EnumSet;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;

/**
 * On Linux, install and start qsynth for this to work. You also need to
 * configure it to use JACK MIDI driver because it uses ALSA MIDI by default.
 *
 */
public class MidiSource implements JackProcessCallback, JackBufferSizeCallback, JackPortConnectCallback {

    public static final String JACK_CLIENT_NAME = "__JNATEST2__";
    private JackPort[] inputPorts;
    private JackPort[] outputPorts;
    private final CountDownLatch connected = new CountDownLatch(1);
    private final CountDownLatch shutdownRequested = new CountDownLatch(1);
    private final ConcurrentLinkedDeque<MidiEvent> midiEvents = new ConcurrentLinkedDeque<MidiEvent>();
    private volatile long totalFrames = 0l;
    private volatile double samplerate;

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) throws Exception {
        new MidiSource().run();
    }

    public void run() throws Exception {
        midiEvents.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 48, 120), 0));
        midiEvents.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 48, 120), 1000));
        midiEvents.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 49, 120), 2000));
        midiEvents.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 49, 120), 3000));
        midiEvents.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 50, 120), 4000));
        midiEvents.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 50, 120), 5000));
        midiEvents.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 50, 120), 6000));
        midiEvents.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 50, 120), 7000));
        midiEvents.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 50, 120), 8000));
        midiEvents.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 50, 120), 9000));
        midiEvents.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 50, 120), 10000));
        midiEvents.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 50, 120), 11000));

        Jack jack = Jack.getInstance();
        JackClient client = jack.openClient(JACK_CLIENT_NAME,
            EnumSet.of(JackOptions.JackNoStartServer),
            EnumSet.noneOf(JackStatus.class));

        String[] inputs = new String[0];
        inputPorts = new JackPort[inputs.length];
        EnumSet<JackPortFlags> flags = EnumSet.of(JackPortFlags.JackPortIsInput);
        for (int i = 0; i < inputs.length; i++) {
            inputPorts[i] = client.registerPort(inputs[i], JackPortType.MIDI, flags);
        }

        String[] outputs = new String[] { "output-midi" };
        outputPorts = new JackPort[outputs.length];
        flags = EnumSet.of(JackPortFlags.JackPortIsOutput);
        for (int i = 0; i < outputs.length; i++) {
            outputPorts[i] = client.registerPort(outputs[i], JackPortType.MIDI, flags);
        }

        samplerate = client.getSampleRate();
        System.out.println("Sample rate = " + samplerate);
        int buffersize = client.getBufferSize();
        System.out.println("Buffersize = " + buffersize);

        client.setProcessCallback(this);
        client.setBuffersizeCallback(this);
        client.setPortConnectCallback(this);
        client.activate();

        String[] physical = jack.getPorts(client, null, JackPortType.MIDI,
            EnumSet.of(JackPortFlags.JackPortIsInput));
        System.out.println(physical.length);
        int count = Math.min(outputPorts.length, physical.length);
        for (int i = 0; i < count; i++) {
            jack.connect(client, outputPorts[i].getName(), physical[i]);
        }
        physical = jack.getPorts(client, null, JackPortType.MIDI,
            EnumSet.of(JackPortFlags.JackPortIsOutput));
        count = Math.min(inputPorts.length, physical.length);
        for (int i = 0; i < count; i++) {
            jack.connect(client, physical[i], inputPorts[i].getName());
        }

        connected.await();

        shutdownRequested.await(2, TimeUnit.SECONDS);
    }

    /**
     * Observations:
     * <ul>
     * <li> you are not allowed to sleep inside the process callback, not even when sending midi events.
     * <li> JackMidi.clearBuffer() is *always* needed.
     * </ul>
     */
    @Override
    public boolean process(JackClient client, int nframes) {
        try {
            if (connected.getCount() != 0) {
                return true;
            }

            double currentSecs = totalFrames * 1d / samplerate;
            totalFrames += nframes;
            double nextSecs = totalFrames * 1d / samplerate;

            JackPort p = outputPorts[0];
            JackMidi.clearBuffer(p);

            MidiEvent event = midiEvents.peek();

            // all midi events processed?
            if (event == null) {
                shutdownRequested.countDown();
                return false;
            }

            // nothing to process yet?
            if (event.getTick() >= nextSecs * 1000d) {
                return true;
            }

            midiEvents.pop();

            int framePos = Math.max(0,
                (int) Math.floor(nframes * (event.getTick() / 1000d - currentSecs) / (nextSecs - currentSecs)));
            JackMidi.eventWrite(p, framePos, event.getMessage().getMessage(), event.getMessage().getMessage().length);
            System.out
                .println(String.format(Locale.ROOT, "midi event at %.3f seconds + %d frames", currentSecs, framePos));
        } catch (JackException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void buffersizeChanged(JackClient client, int buffersize) {
        System.out.println("buffersizeChanged: " + buffersize);
    }

    @Override
    public void portsConnected(JackClient client, String portName1, String portName2) {
        connected.countDown();
    }

    @Override
    public void portsDisconnected(JackClient client, String portName1, String portName2) {
        shutdownRequested.countDown();
    }
}
