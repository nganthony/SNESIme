package com.anthonyng.snesime;

import java.io.IOException;
import java.io.InputStream;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class BluetoothConnectedThread extends Thread {
    public static final char START_FLAG = 18;
    public static final char ACK_FLAG = 19;
    
	public final static int MESSAGE_READ = 1;
	
	private final BluetoothSocket mmSocket;
	private final InputStream mmInStream;
	private Context m_context;
	
	public BluetoothConnectedThread(BluetoothSocket socket, Context context) {
		mmSocket = socket;
		InputStream tmpIn = null;
		m_context = context;
		
		try {
			tmpIn = socket.getInputStream();
		} catch (IOException e) { }

		mmInStream = tmpIn;
	}
	
	public void run() {
		//Buffer store for stream
		byte[] buffer = new byte[1024]; 
		int bytes;
		String readMessage;
		StringBuffer forwardBuffer = new StringBuffer();
		char c;
		
		// Keep listening to the InputStream until an exception occurs
		while (true) {
			
			try {
				//Read from the InputStream
				bytes = mmInStream.read(buffer);
				
                readMessage = new String(buffer, 0, (bytes != -1) ? bytes : 0 );
                
                for(int i = 0; i < readMessage.length(); i++) {
                	c = readMessage.charAt(i);
                	
                	if(c == START_FLAG) {
                		//Don't do anything with start flag
                	}
                	else if(c == ACK_FLAG) {
                        Log.w("forwardbuffer", forwardBuffer.toString());
                		forwardBuffer = new StringBuffer();
                	}
                	else {
                		forwardBuffer.append(c);
                	}
                }
				
                //Create an key press intent
                //Intent intent = new Intent("");
                //intent.putExtra(key, value);
                //Broadcast it
                //m_context.sendBroadcast(intent);
			} catch (IOException e) {
				break;
			}
		}
	}

	/**
	 * Shuts down the socket connection
	 */
	public void cancel() {
		try {
			mmSocket.close();
		} catch (IOException e) { }
	}
}
