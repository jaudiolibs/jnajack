package org.jaudiolibs.jnajack;

import org.jaudiolibs.jnajack.lowlevel.JackLibrary;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 *
 */
public class JackClientTest {

    public JackClientTest() {
    }

    @Test
    public void testIsRealtime() throws JackException {
        JackLibrary lib = mock(JackLibrary.class);
        when(lib.jack_client_open(any(), anyInt(), any()))
                .thenReturn(new JackLibrary._jack_client());
        when(lib.jack_is_realtime(any())).thenReturn(0, 1);

        Jack jack = new Jack(lib);
        JackClient client = jack.openClient("TestRealtime", null, null);
        assertEquals(false, client.isRealtime());
        assertEquals(true, client.isRealtime());
    }

}
