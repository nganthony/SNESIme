package com.anthonyng.snesime;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.text.InputFilter.AllCaps;
import android.util.Log;

public class ImeSettingsActivity extends PreferenceActivity {

	//Request code call back for requesting Bluetooth to be enabled
	private static final int REQUEST_ENABLE_BT = 1;

	BluetoothAdapter bluetoothAdapter = null;
	
	/**
	 * All preferences from preference_layout.xml
	 */
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
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initializeActivateBtPreference();
		intializePairControllerPreference();
	}
	
	/**
	 * Initializes activate preference checkbox
	 */
	private void initializeActivateBtPreference() {
		//Set check state depending on whether Bluetooth is enabled on device
		activateBluetoothPreference.setChecked(bluetoothAdapter.isEnabled());
		
		//Disable click when Bluetooth is activated
		activateBluetoothPreference.setSelectable(!bluetoothAdapter.isEnabled());
		
		//Create on click listener
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
	
	/**
	 * Initializes pair with controller preference
	 */
	private void intializePairControllerPreference() {
		
		//List of available bluetooth devices
		List<String> deviceList = new ArrayList<String>();
		
		Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		    	deviceList.add(device.getName());
		    }
		    deviceList.add(getResources().getString(R.string.scan_for_controller));
		}
		
		//Convert to charsequence array for dialog 
		final CharSequence[] deviceNames = deviceList.toArray(new CharSequence[deviceList.size()]);
		
		pairControllerPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				//Open dialog showing list of already paired devices
				AlertDialog.Builder builder = new AlertDialog.Builder(ImeSettingsActivity.this);
			    builder.setTitle(R.string.select_controller)
			           .setItems(deviceNames, new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int which) {
			               // The 'which' argument contains the index position
			               // of the selected item
			           }
			    });
			    
			    AlertDialog selectControllerDialog = builder.create();
			    selectControllerDialog.show();
			    
				return false;
			}
		});
	}
}
