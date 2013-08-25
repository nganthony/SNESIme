package com.anthonyng.snesime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.prefs.Preferences;

import android.provider.Settings;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.text.InputFilter.AllCaps;
import android.util.Log;

public class ImeSettingsActivity extends PreferenceActivity {

	//Request code call back for requesting Bluetooth to be enabled
	private static final int REQUEST_ENABLE_BT = 1;
	private static final String STANDARD_SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	
	BluetoothAdapter bluetoothAdapter = null;
	
	CheckBoxPreference activateBluetoothPreference;
	Preference pairControllerPreference;
	Preference selectImePreference;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences_layout);
		
		//Initialize bluetooth adapter
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		Resources res = getResources();
		activateBluetoothPreference = (CheckBoxPreference)findPreference(res.getString(R.string.activate_bluetooth_checkbox_key));
		pairControllerPreference = findPreference(res.getString(R.string.pair_controller_preference_key));
		selectImePreference = findPreference(res.getString(R.string.select_ime_preference_key));
		
		initializeSelectImePreference();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initializeActivateBtPreference();
		intializePairControllerPreference();
	}
	
	private void initializeActivateBtPreference() {
		//Set check state depending on whether Bluetooth is enabled on device
		activateBluetoothPreference.setChecked(bluetoothAdapter.isEnabled());
		
		//Disable click when Bluetooth is activated
		activateBluetoothPreference.setSelectable(!bluetoothAdapter.isEnabled());
		
		activateBluetoothPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				
				if (!bluetoothAdapter.isEnabled()) {
					//Ask user for permission to enable bluetooth
					Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
				}
				
				return false;
			}
		});
	}
	
	private void intializePairControllerPreference() {
		
		//List of available bluetooth devices
		List<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();
		
		Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
		
		if (pairedDevices.size() > 0) {
		    for (BluetoothDevice device : pairedDevices) {
		    	deviceList.add(device);
		    }
		}
		
		final BluetoothDevicesListAdapter deviceAdapter = new BluetoothDevicesListAdapter(
				this, R.layout.bluetooth_device_item, deviceList);
		
		pairControllerPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				//Open dialog showing list of already paired devices
				AlertDialog.Builder builder = new AlertDialog.Builder(ImeSettingsActivity.this);
				builder.setTitle(R.string.select_controller);
				builder.setAdapter(deviceAdapter, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						final BluetoothDevice deviceSelected = deviceAdapter.getItem(which);
						
						Handler deviceConnectedHandler = new Handler() {
							@Override 
							public void handleMessage(Message message) {
								pairControllerPreference.setSummary(deviceSelected.getName() + " - "
										+ deviceSelected.getAddress());
							}
						};
						
						BluetoothClientThread btClientThread = new BluetoothClientThread(deviceSelected, 
								deviceConnectedHandler, getApplicationContext());
						btClientThread.start();
					}
				});
			           
			    
			    AlertDialog selectControllerDialog = builder.create();
			    selectControllerDialog.show();
			    
				return false;
			}
		});
	}
	
	private void initializeSelectImePreference() {
		selectImePreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent selectMethodIntent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
				startActivity(selectMethodIntent);
				return false;
			}
		});
	}
}
