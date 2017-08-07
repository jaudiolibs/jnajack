/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (c) 2017 Matthew MacLeod
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version.
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this work; if not, see http://www.gnu.org/licenses/
 */
package org.jaudiolibs.jnajack.examples;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jaudiolibs.jnajack.Jack;
import org.jaudiolibs.jnajack.JackClient;
import org.jaudiolibs.jnajack.JackException;
import org.jaudiolibs.jnajack.JackOptions;
import org.jaudiolibs.jnajack.JackPosition;
import org.jaudiolibs.jnajack.JackPositionBits;
import org.jaudiolibs.jnajack.JackShutdownCallback;
import org.jaudiolibs.jnajack.JackStatus;
import org.jaudiolibs.jnajack.JackTimebaseCallback;
import org.jaudiolibs.jnajack.JackTransportStates;

/**
 * This example is a port of transport.c from the example clients in the jack2 source distribution
 * (see {@link https://github.com/jackaudio/jack2/blob/master/example-clients/transport.c}). It
 * demonstrates how to work with the JACK transport system.
 * 
 */
public class Transport implements JackShutdownCallback, JackTimebaseCallback {

	private boolean done;
	private JackClient client;
	private Map<String, Command> commandMap;
	private List<String> commandList;

	/*
	 * Time and tempo variables. These are global to the entire,
	 * transport timeline. There is no attempt to keep a true tempo map.
	 * The default time signature is: "march time", 4/4, 120bpm
	 */
	private float beatsPerBar = 4.0f;
	private float beatType = 4.0f;
	private double ticksPerBeat = 1920.0;
	private double bpm = 120.0;
	private volatile boolean timeReset = true; /* true when time values change */

	public static void main(String[] args) {
		try {
			Transport transport = new Transport();
			transport.commandLoop();

		} catch (JackException e) {
			Logger.getLogger(Transport.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	public Transport() throws JackException {
		done = false;
		commandMap = createCommands();
		commandList = createCommandList();
		EnumSet<JackStatus> status = EnumSet.noneOf(JackStatus.class);

		try {
			Jack jack = Jack.getInstance();
			client = jack.openClient("TransportJava", EnumSet.of(JackOptions.JackNoStartServer), status);
			if (!status.isEmpty()) {
				System.out.println("JACK client status : " + status);
			}

		} catch (JackException ex) {
			if (!status.isEmpty()) {
				System.out.println("JACK exception client status : " + status);
			}
			throw ex;
		}
	}

	private void commandLoop() {
		Scanner scanner = new Scanner(System.in);

		while (!done) {
			System.out.print("transport> ");
			String input = scanner.nextLine();
			input.trim();

			if (input.isEmpty()) {
				System.err.println("\nNo command entered. Try again.\n");
				continue;
			}
			String[] parts = input.split("\\s+");

			if (parts.length > 2) {
				System.err.println("\nToo many arguments. Try again.\n");
				continue;
			}

			String cmd = parts[0];
			cmd.trim();
			Command command = commandMap.get(cmd);

			if (command == null) {
				System.err.println("\nUnknown command " + cmd + ". Try one of these:\n");
				displayHelp(null);

			} else if (parts.length == 1) {
				command.execute(null);
			} else {
				command.execute(parts[1]);
			}

		}

		scanner.close();
		clientShutdown(client);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jaudiolibs.jnajack.JackShutdownCallback#clientShutdown(org.jaudiolibs.jnajack.JackClient)
	 */
	@Override
	public void clientShutdown(JackClient client) {
		client.deactivate();
		client.close();

	}

	/*
	 * (non-Javadoc)
	 * @see org.jaudiolibs.jnajack.JackTimebaseCallback#timebaseChanged(org.jaudiolibs.jnajack.JackClient, int, int,
	 * org.jaudiolibs.jnajack.JackPosition, boolean)
	 */
	@Override
	public void timebaseChanged(JackClient client, int state, int nframes, JackPosition position, boolean newPosition) {

		double min; /* minutes since frame 0 */
		long absTick; /* ticks since frame 0 */
		long absBeat; /* beats since frame 0 */

		if (newPosition || timeReset) {
			position.setValid(JackPositionBits.JackPositionBBT.getIntVal());
			position.setBeatsPerBar(beatsPerBar);
			position.setBeatType(beatType);
			position.setTicksPerBeat(ticksPerBeat);
			position.setBeatsPerMinute(bpm);

			timeReset = false;

			/*
			 * Compute BBT info from frame number. This is relatively
			 * simple here, but would become complex if we supported tempo
			 * or time signature changes at specific locations in the
			 * transport timeline.
			 */
			min = position.getFrame() / (position.getFrameRate() * 60.0);
			absTick = (long) (min * position.getBeatsPerMinute() * position.getTicksPerBeat());
			absBeat = absTick / (long) position.getTicksPerBeat();

			position.setBar((int) (absBeat / position.getBeatsPerBar()));
			position.setBeat((int) (absBeat - (position.getBar() * position.getBeatsPerBar()) + 1));
			position.setTick((int) (absTick - (absBeat * position.getTicksPerBeat())));
			position.setBarStartTick(position.getBar() * position.getBeatsPerBar() * position.getBeatsPerBar());
			position.incrementBar();

		} else {
			/* Compute BBT info based on previous period. */

			position.addToTick((int) (nframes * position.getTicksPerBeat() * position.getBeatsPerMinute()
					/ (position.getFrameRate() * 60.0)));

			while (position.getTick() >= position.getTicksPerBeat()) {
				position.subtractFromTick((int) position.getTicksPerBeat());
				position.incrementBeat();
				if (position.getBeat() > position.getBeatsPerBar()) {
					position.setBeat(1);
					position.incrementBar();
					position.addToBarStartTick(position.getBeatsPerBar() * position.getTicksPerBeat());
				}
			}
		}

	}

	private void transportPlay() {
		try {
			Jack.getInstance().transportStart(client);
		} catch (JackException e) {
			System.err.println("Unable to start transport: " + e.getMessage());
		}
	}

	private void transportStop() {
		try {
			Jack.getInstance().transportStop(client);
		} catch (JackException e) {
			System.err.println("Unable to stop transport: " + e.getMessage());
		}
	}

	private void displayHelp(Object arg) {
		if (arg != null) {
			String cmd = (String) arg;
			cmd.trim();
			Command command = commandMap.get(cmd);

			if (command != null) {
				System.out.println(cmd + "\t\t\t" + command.doc);
			}
			return;
		}

		for (String cmd : commandList) {
			if (cmd.equals("default"))
				continue;

			Command command = commandMap.get(cmd);
			System.out.println(cmd + "\t\t\t" + command.doc);
		}
		System.out.println();

	}

	private Map<String, Command> createCommands() {
		Map<String, Command> cmdMap = new HashMap<>();

		cmdMap.put("activate", new Command("activate", "Call jack_activate()") {
			@Override
			public void execute(String arg) {
				try {
					client.activate();
				} catch (JackException e) {
					System.err.println("Unable to activate client. ");
				}
			}
		});
		cmdMap.put("exit", new Command("exit", "Exit transport program") {
			@Override
			public void execute(String arg) {
				done = true;
			}
		});
		cmdMap.put("deactivate", new Command("deactivate", "Call jack_deactivate()") {
			@Override
			public void execute(String arg) {
				client.deactivate();
			}
		});
		cmdMap.put("help", new Command("help", "Display help text [<command>]") {
			@Override
			public void execute(String arg) {
				displayHelp(arg);
			}
		});
		cmdMap.put("locate", new Command("locate", "Locate to frame <position>") {
			@Override
			public void execute(String arg) {
				if (arg == null) {
					System.out.println("locate command requires an integer argument");
					return;
				}
				int frame = Integer.parseInt(arg);

				try {
					Jack.getInstance().transportLocate(client, frame);
				} catch (JackException e) {
					System.err.println("Unable to execute locate: " + e.getMessage());
				}
			}
		});
		cmdMap.put("master", new Command("master", "Become timebase master [<conditionally>]") {
			@Override
			public void execute(String arg) {
				int conditional = 0;
				if (arg != null) {
					conditional = Integer.parseInt(arg);
				}
				try {

					client.setTimebaseCallback(Transport.this, conditional);
				} catch (JackException e) {
					System.err.println("Unable to become timebase master: " + e.getMessage());
				}
			}
		});
		cmdMap.put("play", new Command("play", "Start transport rolling") {
			@Override
			public void execute(String arg) {
				transportPlay();
			}
		});
		cmdMap.put("quit", new Command("quit", "Synonym for `exit'") {
			@Override
			public void execute(String arg) {
				done = true;
			}
		});
		cmdMap.put("release", new Command("release", "Release timebase") {
			@Override
			public void execute(String arg) {
				client.releaseTimebase();
			}
		});
		cmdMap.put("stop", new Command("stop", "Stop transport") {
			@Override
			public void execute(String arg) {
				transportStop();
			}
		});
		cmdMap.put("tempo", new Command("tempo", "Set beat tempo <beats_per_min>") {
			@Override
			public void execute(String arg) {
				float tempo = 120.0f;
				if (arg != null) {
					tempo = Float.parseFloat(arg);
				}
				bpm = tempo;
				timeReset = true;
			}
		});
		cmdMap.put("timeout", new Command("timeout", "Set sync timeout in <seconds>") {
			@Override
			public void execute(String arg) {
				long timeout = 2l;
				if (arg != null) {
					timeout = Long.parseLong(arg);
				}
				try {
					Jack.getInstance().setSyncTimeout(client, timeout);
				} catch (JackException e) {
					System.err.println("Unable to set sync timeout: " + e.getMessage());
				}
			}
		});
		cmdMap.put("toggle", new Command("toggle", "Toggle transport rolling") {
			@Override
			public void execute(String arg) {
				JackPosition position = client.getPosition();
				int transportState = -1;
				try {
					transportState = Jack.getInstance().transportQuery(client, position);
					JackTransportStates.JackTransportStopped.getIntVal();

					if (transportState == JackTransportStates.JackTransportStopped.getIntVal()) {
						transportPlay();
					} else if (transportState == JackTransportStates.JackTransportRolling.getIntVal()) {
						transportStop();
					} else if (transportState == JackTransportStates.JackTransportStarting.getIntVal()) {
						System.out.println("state: Starting - no transport toggling");
					} else {
						System.out.println("unexpected state: no transport toggling");
					}
				} catch (JackException e) {
					System.err.println("Unable to toggle transport state: " + e.getMessage());
				}

			}
		});
		cmdMap.put("?", new Command("?", "Synonym for `help'") {
			@Override
			public void execute(String arg) {
				displayHelp(arg);
			}
		});
		cmdMap.put("default", new Command("default", "") {
			@Override
			public void execute(String arg) {
				System.err.println("Unkown command " + arg);
				displayHelp(null);
			}
		});

		return cmdMap;
	}

	private List<String> createCommandList() {
		List<String> cmdList = new ArrayList<>();

		Object[] l = commandMap.keySet().toArray();

		for (Object obj : l) {
			String s = (String) obj;
			cmdList.add(s);
		}

		cmdList.sort(null);

		return cmdList;
	}

	private abstract class Command {
		String name;
		String doc;

		public Command(String n, String d) {
			name = n;
			doc = d;
		}

		public abstract void execute(String arg);
	}

}
