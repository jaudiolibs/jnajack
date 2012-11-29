
package org.jaudiolibs.jnajack.examples;

import java.nio.FloatBuffer;
import org.jaudiolibs.jnajack.util.SimpleAudioClient;

/**
 *
 * @author Neil C Smith
 */
public class NoiseAudioSource implements SimpleAudioClient.Processor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        SimpleAudioClient client = SimpleAudioClient.create("noise", new String[0],
                new String[]{"output1", "output2"}, true, true, new NoiseAudioSource());
        client.activate();
        while (true) {
            Thread.sleep(1000);
        }
    }

    public void setup(float samplerate, int buffersize) {

    }

    public void process(FloatBuffer[] inputs, FloatBuffer[] outputs) {
        for (FloatBuffer buf : outputs) {
            int size = buf.capacity();
            for (int i=0; i < size; i++) {
                buf.put(i, (float) Math.random() - 0.5f);
            }
        }
    }

    public void shutdown() {
        System.out.println("Noise Audio Source shutdown");
    }
}
