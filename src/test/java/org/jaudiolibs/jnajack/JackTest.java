package org.jaudiolibs.jnajack;

import com.sun.jna.NativeLong;
import com.sun.jna.ptr.IntByReference;
import java.util.EnumSet;
import org.jaudiolibs.jnajack.lowlevel.JackLibrary;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 *
 */
public class JackTest {

    public JackTest() {
    }

    @Test
    public void testGetTime() throws JackException {
        JackLibrary lib = mock(JackLibrary.class);
        long value = 1010101L;
        when(lib.jack_get_time()).thenReturn(new NativeLong(value));
        Jack jack = new Jack(lib);
        assertEquals(value, jack.getTime());
    }

    @Test
    public void testOpenClient() throws JackException {
        JackLibrary lib = mock(JackLibrary.class);
        int optionsFlag = JackLibrary.JackOptions.JackNoStartServer
                | JackLibrary.JackOptions.JackUseExactName;
        int statusFlag = JackLibrary.JackStatus.JackServerStarted
                | JackLibrary.JackStatus.JackNameNotUnique;
        when(lib.jack_client_open(anyString(), anyInt(), any()))
                .thenAnswer(i -> {
                    i.getArgument(2, IntByReference.class).setValue(statusFlag);
                    return new JackLibrary._jack_client();
                });
        when(lib.jack_get_client_name(any())).thenReturn("BAR");

        Jack jack = new Jack(lib);
        EnumSet<JackStatus> status = EnumSet.noneOf(JackStatus.class);
        JackClient client = jack.openClient("FOO",
                EnumSet.of(JackOptions.JackNoStartServer, JackOptions.JackUseExactName),
                status);
        verify(lib).jack_client_open(eq("FOO"), eq(optionsFlag), any(IntByReference.class));
        assertEquals("BAR", client.getName(), "Client name not updated");
        assertEquals(EnumSet.of(JackStatus.JackServerStarted, JackStatus.JackNameNotUnique),
                status,
                "Output status not converted from flags correctly");

    }

}
