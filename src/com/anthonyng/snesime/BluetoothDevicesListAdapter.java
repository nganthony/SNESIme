package com.anthonyng.snesime;

import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BluetoothDevicesListAdapter extends ArrayAdapter<BluetoothDevice> {

	Context context;
	int rowResourceId;
	List<BluetoothDevice> bluetoothDevices = null;
	
	public BluetoothDevicesListAdapter(Context context, int rowResourceId,
			List<BluetoothDevice> bluetoothDevices) {
		super(context, rowResourceId, bluetoothDevices);
		
		this.context = context;
		this.rowResourceId = rowResourceId;
		this.bluetoothDevices = bluetoothDevices;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){

		View row = convertView;

		BluetoothDeviceHolder holder = null;
		
		if (row == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(rowResourceId, null);
			
			holder = new BluetoothDeviceHolder();
			holder.deviceNameTextView = (TextView)row.findViewById(R.id.device_name);
			row.setTag(holder);
		}
		else {
			holder = (BluetoothDeviceHolder)row.getTag();
		}

		BluetoothDevice bluetoothDevice = bluetoothDevices.get(position);
		holder.deviceNameTextView.setText(bluetoothDevice.getName());
		
		return row;

	}
	
	static class BluetoothDeviceHolder {
		TextView deviceNameTextView;
	}
}
