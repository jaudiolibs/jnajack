
package org.jaudiolibs.jnajack.examples;

import java.nio.FloatBuffer;
import org.jaudiolibs.jnajack.util.SimpleAudioClient;

/**
 *
 * @author Neil C Smith
 */
public class PassThrough implements SimpleAudioClient.Processor {


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        SimpleAudioClient client = SimpleAudioClient.create("pass-through", new String[] {"input-L", "input-R"},
                new String[]{"output-L", "output-R"}, true, true, new PassThrough());
        client.activate();
        while (true) {
            Thread.sleep(1000);
        }
    }

    public void setup(float samplerate, int buffersize) {

    }

    public void process(FloatBuffer[] inputs, FloatBuffer[] outputs) {
        outputs[0].put(inputs[0]);
        outputs[1].put(inputs[1]);

    }

    public void shutdown() {
        System.out.println("Pass Through shutdown");
    }
}
