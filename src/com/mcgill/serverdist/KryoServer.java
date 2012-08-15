package com.mcgill.serverdist;

import java.io.IOException;

import android.os.Handler;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mcgill.serverdist.Network.Signal;

public class KryoServer {
	private Handler handler; //handler to send data to the UI
	private ServerDistActivity activity; //object to connect to the main activity
	Server server;
	
	public KryoServer(ServerDistActivity parent) {
    	activity = parent;
        handler = new Handler();
		server = new Server();
		Network.register(server);

		server.addListener(new Listener() {
			public void received (Connection connection, Object object) {
				if (object instanceof Signal) {
					Signal signal = (Signal)object;
					System.out.println("Received from '" + signal.id + "': " + signal.heard + ". Volume: " + signal.volume);
				}
			}
		});
		server.start();
	}
	
	public void close() {
		server.close();
		server.stop();
		postConnectionStatus(Network.DISCONNECTED);
	}
	
	public void bind() {
		try {
			server.bind(Network.TCPPort);
			postConnectionStatus(Network.CONNECTED);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Server couldn't be opened.");
			postConnectionStatus(Network.DISCONNECTED);
		}
	}
	
	public void sendToAll(int volume) {
		Signal signal = new Signal();
		signal.volume = volume;
		signal.timems = System.currentTimeMillis();
		
		signal.heard = false;
		signal.id = "";
		
		server.sendToAllTCP(signal);
	}

	private void postConnectionStatus(final int data) {
		handler.post(new Runnable() {
			public void run() {
				activity.receiveConnectionStatus(data);
			}
		});
    }
}

