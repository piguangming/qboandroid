package com.jd.qbo;

import java.util.Set;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 设备选择对话框
 * @author piguangming
 *
 */
public class DeviceList extends Activity {
	/** 调试标签：与类同名**/
    private static final String TAG = "DeviceList";
    /** 调试开关**/
    private static final boolean D = true;
    /** 远程蓝牙设备MAC地址**/
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    /** 本地蓝牙**/
    private BluetoothAdapter mBtAdapter;
    /** 已配对蓝牙列表**/
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list);
        setResult(Activity.RESULT_CANCELED);

        //扫描按钮
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	// 扫描
                doDiscovery();
                //v.setVisibility(View.GONE);
            }
        });

        //已配对设备列表
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        //注册两个广播接收器机制，一个用于发现新设备，另一个用于扫描。新找的设备添加到列表中去；另一个完成扫描
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 对话框销毁前，需保证扫描结束
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        // 对话框销毁前，需保证广播通知一并销毁
        this.unregisterReceiver(mReceiver);
    }

    /**
     * 扫描蓝牙设备，并置进度条于加载状态
     */
    private void doDiscovery() {
        if (D) Log.d(TAG, "doDiscovery()");

        // 扫描蓝牙设备，并置进度条于加载状态
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        // 扫描前，需保证扫描结束
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        mBtAdapter.startDiscovery();
    }

    /**
     * 监听器，当某蓝牙设备被选定后，关闭弹出对话框，与此同时，将数据传回给主页面
     */
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            mBtAdapter.cancelDiscovery();

            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    /**
     * 广播接收器；
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
               // 发现新设备，将其添加到配对列表中
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            	// 扫描结束，通知进度条结束加载状态
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
            }
        }
    };

}
