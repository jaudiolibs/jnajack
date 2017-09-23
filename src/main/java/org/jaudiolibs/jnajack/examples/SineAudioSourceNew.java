
package org.jaudiolibs.jnajack.examples;

import org.jaudiolibs.jnajack.Jack;
import org.jaudiolibs.jnajack.JackClient;
import org.jaudiolibs.jnajack.JackOptions;
import org.jaudiolibs.jnajack.JackPort;
import org.jaudiolibs.jnajack.JackPortFlags;
import org.jaudiolibs.jnajack.JackPortType;
import org.jaudiolibs.jnajack.JackProcessCallback;
import org.jaudiolibs.jnajack.JackStatus;

import java.nio.FloatBuffer;
import java.util.EnumSet;

public class SineAudioSourceNew implements JackProcessCallback {

    public static final String JACK_CLIENT_NAME = "__JNATEST2__";
    private float[] data = null;
    private final static int TABLE_SIZE = 200;
    private int left_phase = 0;
    private int right_phase = 0;
    private JackPort[] inputPorts;
    private JackPort[] outputPorts;
    private FloatBuffer[] inputBuffers;
    private FloatBuffer[] outputBuffers;

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) throws Exception {
        new SineAudioSourceNew().run();
    }

    public void run() throws Exception {
        Jack jack = Jack.getInstance();
        JackClient client = jack.openClient(JACK_CLIENT_NAME,
            EnumSet.of(JackOptions.JackNoStartServer),
            EnumSet.noneOf(JackStatus.class));

        String[] inputs = new String[0];
        inputPorts = new JackPort[inputs.length];
        EnumSet<JackPortFlags> flags = EnumSet.of(JackPortFlags.JackPortIsInput);
        for (int i = 0; i < inputs.length; i++) {
            inputPorts[i] = client.registerPort(inputs[i], JackPortType.AUDIO, flags);
        }

        String[] outputs = new String[] { "output-L", "output-R" };
        outputPorts = new JackPort[outputs.length];
        flags = EnumSet.of(JackPortFlags.JackPortIsOutput);
        for (int i = 0; i < outputs.length; i++) {
            outputPorts[i] = client.registerPort(outputs[i], JackPortType.AUDIO, flags);
        }

        this.inputBuffers = new FloatBuffer[inputPorts.length];
        this.outputBuffers = new FloatBuffer[outputPorts.length];

        int samplerate = client.getSampleRate();
        System.out.println("Sample rate = " + samplerate);
        int buffersize = client.getBufferSize();
        System.out.println("Buffersize = " + buffersize);
        setup(samplerate, buffersize);

        client.setProcessCallback(this);
        client.activate();

        String[] physical = jack.getPorts(client, null, JackPortType.AUDIO,
            EnumSet.of(JackPortFlags.JackPortIsInput, JackPortFlags.JackPortIsPhysical));
        int count = Math.min(outputPorts.length, physical.length);
        for (int i = 0; i < count; i++) {
            jack.connect(client, outputPorts[i].getName(), physical[i]);
        }
        physical = jack.getPorts(client, null, JackPortType.AUDIO,
            EnumSet.of(JackPortFlags.JackPortIsOutput, JackPortFlags.JackPortIsPhysical));
        count = Math.min(inputPorts.length, physical.length);
        for (int i = 0; i < count; i++) {
            jack.connect(client, physical[i], inputPorts[i].getName());
        }

    }

    public void setup(float samplerate, int buffersize) {
        data = new float[TABLE_SIZE];
        for (int i = 0; i < TABLE_SIZE; i++) {
            data[i] = (float) (0.2 * Math.sin(((double) i / (double) TABLE_SIZE) * Math.PI * 2.0));
        }
    }

    @Override
    public boolean process(JackClient client, int nframes) {
        for (int i = 0; i < inputPorts.length; i++) {
            inputBuffers[i] = inputPorts[i].getFloatBuffer();
        }
        for (int i = 0; i < outputPorts.length; i++) {
            outputBuffers[i] = outputPorts[i].getFloatBuffer();
        }
        FloatBuffer left = outputBuffers[0];
        FloatBuffer right = outputBuffers[1];
        int size = left.capacity();
        for (int i = 0; i < size; i++) {
            left.put(i, data[left_phase]);
            right.put(i, data[right_phase]);
            left_phase += 2;
            right_phase += 3;
            if (left_phase >= TABLE_SIZE) {
                left_phase -= TABLE_SIZE;
            }
            if (right_phase >= TABLE_SIZE) {
                right_phase -= TABLE_SIZE;
            }
        }
        return true;
    }
}
