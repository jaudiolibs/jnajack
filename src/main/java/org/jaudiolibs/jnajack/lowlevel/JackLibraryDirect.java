package org.jaudiolibs.jnajack.lowlevel;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;

/**
 *
 * @author nsigma
 */
public class JackLibraryDirect implements JackLibrary {

	static {
		if (Platform.is64Bit()) {
			try {
				Native.register("jack64");
			} catch (UnsatisfiedLinkError e) {
				Native.register("jack");
			}
		} else {
			Native.register("jack");
		}
	}

	@Override
	public native _jack_client jack_client_open(String client_name, int options, IntByReference status);

	@Override
	public native int jack_client_close(_jack_client client);

	@Override
	public native int jack_client_name_size();

	@Override
	public native String jack_get_client_name(_jack_client client);

	@Override
	public native int jack_is_realtime(_jack_client client);

	@Override
	public native void jack_on_shutdown(_jack_client client, JackShutdownCallback shutdown_callback, Pointer arg);

	@Override
	public native int jack_set_process_callback(_jack_client client, JackProcessCallback process_callback, Pointer arg);

	@Override
	public native int jack_cycle_wait(_jack_client client);

	@Override
	public native void jack_cycle_signal(_jack_client client, int status);

	@Override
	public native int jack_set_process_thread(_jack_client client, JackThreadCallback thread_callback, Pointer arg);

	@Override
	public native int jack_set_thread_init_callback(_jack_client client, JackThreadInitCallback thread_init_callback,
			Pointer arg);

	@Override
	public native int jack_set_freewheel_callback(_jack_client client, JackFreewheelCallback freewheel_callback,
			Pointer arg);

	@Override
	public native int jack_set_freewheel(_jack_client client, int onoff);

	@Override
	public native int jack_set_buffer_size(_jack_client client, int nframes);

	@Override
	public native int jack_set_buffer_size_callback(_jack_client client, JackBufferSizeCallback bufsize_callback,
			Pointer arg);

	@Override
	public native int jack_set_sample_rate_callback(_jack_client client, JackSampleRateCallback srate_callback,
			Pointer arg);

	@Override
	public native int jack_set_client_registration_callback(_jack_client jack_client_tPtr1,
			JackClientRegistrationCallback registration_callback, Pointer arg);

	@Override
	public native int jack_set_port_registration_callback(_jack_client jack_client_tPtr1,
			JackPortRegistrationCallback registration_callback, Pointer arg);

	@Override
	public native int jack_set_port_connect_callback(_jack_client jack_client_tPtr1,
			JackPortConnectCallback connect_callback, Pointer arg);

//    public native int jack_set_port_rename_callback(_jack_client jack_client_tPtr1, JackPortRenameCallback rename_callback, Pointer arg);
	@Override
	public native int jack_set_graph_order_callback(_jack_client jack_client_tPtr1,
			JackGraphOrderCallback graph_callback, Pointer voidPtr1);

	@Override
	public native int jack_set_xrun_callback(_jack_client jack_client_tPtr1, JackXRunCallback xrun_callback,
			Pointer arg);

	@Override
	public native int jack_activate(_jack_client client);

	@Override
	public native int jack_deactivate(_jack_client client);

	@Override
	public native _jack_port jack_port_register(_jack_client client, String port_name, String port_type,
			NativeLong flags, NativeLong buffer_size);

	@Override
	public native int jack_port_unregister(_jack_client jack_client_tPtr1, _jack_port jack_port_tPtr1);

	@Override
	public native Pointer jack_port_get_buffer(_jack_port jack_port_tPtr1, int nframes);

	@Override
	public native String jack_port_name(_jack_port port);

	@Override
	public native ByteByReference jack_port_short_name(_jack_port port);

	@Override
	public native int jack_port_flags(_jack_port port);

	@Override
	public native ByteByReference jack_port_type(_jack_port port);

//    public native int jack_port_type_id(_jack_port port);
	@Override
	public native int jack_port_is_mine(_jack_client jack_client_tPtr1, _jack_port port);

	@Override
	public native int jack_port_connected(_jack_port port);

	@Override
	public native int jack_port_connected_to(_jack_port port, String port_name);

	@Override
	public native Pointer jack_port_get_connections(_jack_port port);

	@Override
	public native Pointer jack_port_get_all_connections(_jack_client client, _jack_port port);

	@Override
	public native int jack_port_tie(_jack_port src, _jack_port dst);

	@Override
	public native int jack_port_untie(_jack_port port);

	@Override
	public native int jack_port_get_latency(_jack_port port);

	@Override
	public native int jack_port_get_total_latency(_jack_client jack_client_tPtr1, _jack_port port);

	@Override
	public native void jack_port_set_latency(_jack_port jack_port_tPtr1);

	@Override
	public native int jack_recompute_total_latency(_jack_client jack_client_tPtr1, _jack_port port);

	@Override
	public native int jack_recompute_total_latencies(_jack_client jack_client_tPtr1);

	@Override
	public native int jack_port_set_name(_jack_port port, String port_name);

	@Override
	public native int jack_port_set_alias(_jack_port port, String alias);

	@Override
	public native int jack_port_unset_alias(_jack_port port, String alias);

//    public native int jack_port_get_aliases(_jack_port port, ByteBuffer[] aliases);
	@Override
	public native int jack_port_request_monitor(_jack_port port, int onoff);

	@Override
	public native int jack_port_request_monitor_by_name(_jack_client client, String port_name, int onoff);

	@Override
	public native int jack_port_ensure_monitor(_jack_port port, int onoff);

	@Override
	public native int jack_port_monitoring_input(_jack_port port);

	@Override
	public native int jack_connect(_jack_client jack_client_tPtr1, String source_port, String destination_port);

	@Override
	public native int jack_disconnect(_jack_client jack_client_tPtr1, String source_port, String destination_port);

	@Override
	public native int jack_port_disconnect(_jack_client jack_client_tPtr1, _jack_port jack_port_tPtr1);

	@Override
	public native int jack_port_name_size();

	@Override
	public native int jack_port_type_size();

	@Override
	public native void jack_port_get_latency_range(_jack_port port, int mode, jack_latency_range_t range);

	@Override
	public native int jack_get_sample_rate(_jack_client jack_client_tPtr1);

	@Override
	public native int jack_get_buffer_size(_jack_client jack_client_tPtr1);

	@Override
	public native Pointer jack_get_ports(_jack_client jack_client_tPtr1, String port_name_pattern,
			String type_name_pattern, NativeLong flags);

	@Override
	public native _jack_port jack_port_by_name(_jack_client jack_client_tPtr1, String port_name);

	@Override
	public native _jack_port jack_port_by_id(_jack_client client, int port_id);

	@Override
	public native int jack_frames_since_cycle_start(_jack_client jack_client_tPtr1);

	@Override
	public native int jack_frame_time(_jack_client jack_client_tPtr1);

	@Override
	public native int jack_last_frame_time(_jack_client client);

	@Override
	public native int jack_time_to_frames(_jack_client client);

	@Override
	public native float jack_cpu_load(_jack_client client);

	@Override
	public native void jack_set_error_function(func arg1);

	@Override
	public native void jack_set_info_function(func arg1);

	@Override
	public native NativeLong jack_get_time();

//    public native void free(Pointer ptr);
	@Override
	public native void jack_free(Pointer ptr);

	// MIDI functions //////////////////////////////////////////////////////////

	@Override
	public native void jack_midi_clear_buffer(Pointer port_buffer);

	@Override
	public native int jack_midi_event_get(jack_midi_event_t event, Pointer port_buffer, int event_index);

	@Override
	public native Pointer jack_midi_event_reserve(Pointer port_buffer, int time, int data_size);

	@Override
	public native int jack_midi_event_write(Pointer port_buffer, int time, Pointer data, int data_size);

	@Override
	public native int jack_midi_event_write(Pointer port_buffer, int time, byte[] data, int data_size);

	@Override
	public native int jack_midi_get_event_count(Pointer port_buffer);

	@Override
	public native int jack_midi_get_lost_event_count(Pointer port_buffer);

	@Override
	public native int jack_midi_max_event_size(Pointer port_buffer);

	/// Transport
	@Override
	public native int jack_release_timebase(_jack_client client);

	@Override
	public native int jack_get_current_transport_frame(_jack_client client);

	@Override
	public native int jack_set_sync_callback(_jack_client client, JackSyncCallback sync_callback, Pointer arg);

	@Override
	public native int jack_set_sync_timeout(_jack_client client, long timeout);

	@Override
	public native int jack_set_timebase_callback(_jack_client client, int conditional, JackTimebaseCallback timebase_callback,
			Pointer arg);

	@Override
	public native int jack_transport_locate(_jack_client client, int frame);

	@Override
	public native int jack_transport_query(_jack_client client, jack_position_t pos);

	@Override
	public native int jack_transport_reposition(_jack_client client, jack_position_t pos);

	@Override
	public native void jack_transport_start(_jack_client client);

	@Override
	public native void jack_transport_stop(_jack_client client);

}
