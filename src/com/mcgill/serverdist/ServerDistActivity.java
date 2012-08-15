package com.mcgill.serverdist;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ServerDistActivity extends Activity {
	TextView textEdit;
	ToggleButton serverButton, soundButton;
	Button sendButton;
	boolean sendingSignal = false;
	
	PlaySound playSound;
	
	KryoServer server;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        textEdit = (TextView)findViewById(R.id.ipaddress);
        serverButton = (ToggleButton)findViewById(R.id.toggleButton);
        soundButton = (ToggleButton)findViewById(R.id.soundButton);
        sendButton = (Button)findViewById(R.id.button);
        
        sendButton.setVisibility(Button.INVISIBLE);
        soundButton.setVisibility(Button.INVISIBLE);
        
        playSound = new PlaySound(this);
        
        String ipAddress = getLocalIpAddress();
        if (ipAddress != null) {
        	textEdit.setText(ipAddress);
        }
        else
        	finish();
    }
    
    public void serverButtonAction(View view) {
    	if (serverButton.isChecked()) {
    		changeUI(Network.DISCONNECTED);
    		serverStart();
    	}
    	else
    		serverStop();
    }
    
    public void serverStart() {
    	server = new KryoServer(this);
    	server.bind();   
    }
    
    public void serverStop() {
    	server.close();
    }
    
    public void sendSignalAction(View view) {
    	if (!sendingSignal) {
    		sendingSignal = true;
    		new Thread(new Runnable() {
    			public void run() {
    				int i = 10;
    				while (i <= 100) {
    					playSound.setVolume(i);
    					try {
    						Thread.sleep(1500);
    					} catch (InterruptedException e) {
    						e.printStackTrace();
    					}

    					server.sendToAll(i);
    					if (soundButton.isChecked())
    						playSound.run();

    					i += 10;
    				}
    				
//    				server.sendToAll(100);
//    				new Thread(playSound).start();
    				
    				sendingSignal = false;
    			}
    		}).start();
    	}
    }
    
    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
        	System.out.println(ex.toString());
        }
        return null;
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		onPause();
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		serverStop();
	}
	
	protected void changeUI(final int status) {
		switch (status) {
		case Network.DISCONNECTED:
			serverButton.setChecked(false);
	        sendButton.setVisibility(Button.INVISIBLE);
	        soundButton.setVisibility(Button.INVISIBLE);
			break;
		case Network.CONNECTED:
			serverButton.setChecked(true);			
	        sendButton.setVisibility(Button.VISIBLE);
	        soundButton.setVisibility(Button.VISIBLE);
			break;
		}
	}
	
	public void receiveConnectionStatus(final int status) {
		changeUI(status);
	}
}
