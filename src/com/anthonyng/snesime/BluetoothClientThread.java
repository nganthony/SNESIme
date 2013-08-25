package com.anthonyng.snesime;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Connects Android device with remote device through Bluetooth
 * @author Anthony
 *
 */
class BluetoothClientThread extends Thread {
	private static final String STANDARD_SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";

	private final BluetoothSocket mmSocket;
	private final BluetoothDevice mmDevice;

	private final Handler connectedHandler;
	private Context m_context;
	
	public BluetoothClientThread(BluetoothDevice device, 
			Handler connectedHandler, Context context) {
		// Use a temporary object that is later assigned to mmSocket,
		// because mmSocket is final
		BluetoothSocket tmp = null;
		mmDevice = device;
		m_context = context;

		// Get a BluetoothSocket to connect with the given BluetoothDevice
		try {
			tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(STANDARD_SPP_UUID));
		} catch (IOException e) { }
		mmSocket = tmp;
		this.connectedHandler = connectedHandler;
	}

	public void run() {
		// Cancel discovery because it will slow down the connection
		BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

		try {
			// Connect the device through the socket. This will block
			// until it succeeds or throws an exception
			mmSocket.connect();
			connectedHandler.sendEmptyMessage(0);
		} catch (IOException connectException) {
			// Unable to connect; close the socket and get out
			try {
				mmSocket.close();
			} catch (IOException closeException) { }
			return;
		}

		// Do work to manage the connection (in a separate thread)
		manageConnectedSocket(mmSocket);
	}

	/** 
	 * Will cancel an in-progress connection, and close the socket 
	 */
	public void cancel() {
		try {
			mmSocket.close();
		} catch (IOException e) { }
	}
	
	private void manageConnectedSocket(BluetoothSocket btSocket) {
		BluetoothConnectedThread btConnectedThread = new BluetoothConnectedThread(
				btSocket, m_context);
		btConnectedThread.start();
	}
}
