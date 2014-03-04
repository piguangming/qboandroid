package com.jd.qbo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 负责蓝牙连接与通信
 * @author piguangming
 *
 */
public class BluetoothService {
	/** 调试标签：与类同名**/
	private static final String TAG = "BTService";
	/** 调试开关**/
	private static final boolean D = true;
	/** 蓝牙协议类型**/
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	/** 手机蓝牙**/
	private final BluetoothAdapter localBlueTooth_;
	/** 连接线程**/
	private ConnectThread mConnectThread;
	/** 数据发送线程（数据通信的基础是连接建立）**/
	private ConnectedThread mConnectedThread;

	/** UI句柄，与主线程通信**/
	private final Handler mHandler;
	/** 手机蓝牙状态**/
	private int blueToothState_;
	
	/** 蓝牙状态：未连接**/
	public static final int STATE_NONE = 1;
	/** 蓝牙状态：监听**/
	public static final int STATE_LISTEN = 2;
	/** 蓝牙状态：正在连接**/
	public static final int STATE_CONNECTING = 3;
	/** 蓝牙状态：已连接**/
	public static final int STATE_CONNECTED = 4;

	// Main method where everything is initiated
	/**
	 * 构造函数：初始化操作，包括获取本地蓝牙设备，状态，主线程通信的句柄
	 * @param context 主线程
	 * @param handler 主线程句柄
	 */
	public BluetoothService(Context context, Handler handler) {
		// Get a handle to the default local Bluetooth adapter.
		localBlueTooth_ = BluetoothAdapter.getDefaultAdapter();
		blueToothState_ = STATE_NONE;
		mHandler = handler;

	}

	/**
	 * 设置蓝牙状态，并且通知UI线程
	 * @param state 蓝牙状态
	 */
	private synchronized void setState(int state) {
		blueToothState_ = state;
		mHandler.obtainMessage(MainActivity.REMOTE_BLUETOOTH_STATE, state, -1)
				.sendToTarget();
	}

	/**
	 * 获取蓝牙状态
	 * @param state 蓝牙状态
	 */
	public synchronized int getState() {
		return blueToothState_;
	}

	/**
	 * 准备连接
	 */
	public synchronized void start() {
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		setState(STATE_LISTEN);
	}

	/**
	 * 正在建立远程蓝牙连接...
	 * @param device 远程蓝牙设备
	 */
	public synchronized void connect(BluetoothDevice device) {
		// 在连接之前，务必保证socket关闭
		if (blueToothState_ == STATE_CONNECTING) {
			if (mConnectThread != null) {
				mConnectThread.cancel();
				mConnectThread = null;
			}
		}
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		mConnectThread = new ConnectThread(device);
		mConnectThread.start();
		// 设置正在连接状态
		setState(STATE_CONNECTING);
	}

	/**
	 * 已经建立连接
	 * @param device 远程蓝牙设备
	 */
	public synchronized void connected(BluetoothSocket socket,
			BluetoothDevice device) {
		// 在连接之前，务必保证socket关闭
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();
		Message msg = mHandler.obtainMessage(MainActivity.DEVICE_NAME);
		Bundle bundle = new Bundle();
		bundle.putString(MainActivity.BT_DEVICE_NAME, device.getName());
		msg.setData(bundle);
		mHandler.sendMessage(msg);
		// 设置已连接状态
		setState(STATE_CONNECTED);
	}

	/**
	 * 停止连接
	 */
	public synchronized void stop() {
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		setState(STATE_NONE);
	}

	/**
	 * 发送数据，以字节为单位
	 * @param out - 字节
	 */
	public void write(byte out) {
		byte[] b = {out};
		write(b);
	}

	/**
	 * 发送数据，以字节数组为单位
	 * @param out - 字节数组
	 */
	public void write(byte[] out) {
		ConnectedThread Thread;
		synchronized (this) {
			if (blueToothState_ != STATE_CONNECTED) {
				Log.e(TAG, "byte array " + out + " fails to write due to bluetooth connection lost...");
				return;
			}
			Thread = mConnectedThread;
		}
		Thread.write(out);
	}

	/**
	 * 读取数据，以字节数组为单位
	 * @param in - 字节数组
	 */
	public void read(byte[] in) {
		ConnectedThread Thread;
		synchronized (this) {
			if (blueToothState_ != STATE_CONNECTED){
				Log.e(TAG, "byte array " + in + " fails to read due to bluetooth connection lost...");
				return;
			}
			Thread = mConnectedThread;
		}
		Thread.read(in);
	}

	/**
	 * 该线程ConnectThread专用于连接蓝牙 ConnectedThread负责数据通信
	 * @author piguangming
	 *
	 */
	private class ConnectThread extends Thread {
		private final BluetoothSocket socket_;
		private final BluetoothDevice device_;

		public ConnectThread(BluetoothDevice device) {
			device_ = device;
			BluetoothSocket tmp = null;
			try {
				//设置蓝牙连接协议
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
				// 错误信息写入调试器中
				Log.e(TAG, "create() failed", e);
			}
			socket_ = tmp;
		}

		public void run() {
			setName("ConnectThread");
			// 连接之前，务必确保手机蓝牙未处于搜寻状态。如果正在搜寻，将其取消，毕竟搜索状态非常耗时（12s）
			localBlueTooth_.cancelDiscovery();
			try {
				//建立蓝牙连接
				socket_.connect();
			} catch (IOException e) {
				//连接失败，则通知主页面
				connectionFailed();
				try {
					// 关闭端口，释放资源
					socket_.close();
				} catch (IOException e2) {
					Log.e(TAG,
							"unable to close()socket during connection failure",
							e2);
				}
				//重新初始化
				BluetoothService.this.start();
				return;
			}
			// 连接没有任何问题，则释放连接线程，开始进入连接后操作
			synchronized (BluetoothService.this) {
				mConnectThread = null;
			}
			//连接后线程被调用，通知主页面连接成功建立
			connected(socket_, device_);
		}

		/**
		 * 释放资源
		 */
		public void cancel() {
			try {
				socket_.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}

	/**
	 * 连接失败，通知主页面
	 */
	private void connectionFailed() {
		setState(STATE_NONE);
		Message msg = mHandler.obtainMessage(MainActivity.UImsg);
		Bundle bundle = new Bundle();
		bundle.putString(MainActivity.BT_TOAST, "未连接");
		msg.setData(bundle);
		mHandler.sendMessage(msg);

	}

	/**
	 * 连接丢失，通知主页面
	 */
	private void connectionLost() {
		setState(STATE_NONE);
		Message msg = mHandler.obtainMessage(MainActivity.UImsg);
		Bundle bundle = new Bundle();
		bundle.putString(MainActivity.BT_TOAST, "连接丢失");
		msg.setData(bundle);
		mHandler.sendMessage(msg);
		BluetoothService.this.start();
	}

	/**
	 * 该线程 ConnectedThread负责数据通信， ConnectThread专用于连接蓝牙
	 * @author piguangming
	 *
	 */
	private class ConnectedThread extends Thread {
		private static final int PACKAGE_SIZE = 100;
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			Log.d(TAG, "create ConnectedThread: ");
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG, "temp sockets not created", e);
			}
			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}
		
		/**
		 * 按字节读取，一边读，一边判断是否ff，如果是，放入buffer，如果否，扔掉，
		 */
		public void run() {
			boolean isStartByte = false, isEndByte = false;
			byte[] buffer = new byte[PACKAGE_SIZE];
			byte[] aBuffer = new byte[PACKAGE_SIZE];
			int offset = 0;
			try {
				while (true) {
					int aByte = mmInStream.read();
					while (true) {
						if(D) Log.d(TAG, "当前字节：" + aByte);
						if (isStartByte) {// 当前已经是包头，接下来判断包尾
							if(D) Log.d(TAG, "======已经遇到包头======");
							isEndByte = checkisEnd(aByte);
							byte b = (byte) aByte;
							buffer[offset++] = b;
							if (isEndByte) {
								if(D) Log.d(TAG, "======已经遇到包尾======");
								String packs = Converter.bytesToHexString(buffer);
								if(D) Log.d(TAG, "包数据：" + packs);
								// 遇到结尾，移除
								buffer = aBuffer;
								offset = 0;
								mHandler.obtainMessage(MainActivity.Message,
										buffer.length, -1, packs).sendToTarget();
							} else {
								// 未遇到结尾,直接放入
							}
						} else {// 没有包头，扔掉
							isStartByte = checkisStart(aByte);
							if (isStartByte) {
								byte b = (byte) aByte;
								buffer[offset++] = b;
							}
						}

						aByte = (byte) mmInStream.read();
					}
				}

			} catch (Exception e) {
				Log.e(TAG, "disconnected", e);
				connectionLost();
			}
		}

		/**
		 * 检查是否字节是否为开头——0xFF
		 * @param aByte 指定字节
		 * @return 如果为0xFF，返回真，否则返回假
		 */
		private boolean checkisStart(int aByte) {
			if (aByte == -1 || aByte == 255) {
				return true;
			} else {
				return false;
			}
		}

		/**
		 * 检查是否字节是否为开头——0xFE
		 * @param aByte 指定字节
		 * @return 如果为0xFE，返回真，否则返回假
		 */
		private boolean checkisEnd(int aByte) {
			if (aByte == -2 || aByte == 254) {
				return true;
			} else {
				return false;
			}
		}

		/**
		 * 发送数据
		 * @param buffer 要发送的数据
		 */
		public void write(byte[] buffer) {
			try {
				mmOutStream.write(buffer);
			} catch (IOException e) {
				Log.e(TAG, "Exception during write", e);
			}
		}

		/**
		 * 接收数据
		 * @param buffer 要接收的数据
		 */
		public void read(byte[] buffer) {
			try {
				mmInStream.read(buffer);
			} catch (IOException e) {
				Log.e(TAG, "Exception during write", e);
			}
		}

		/**
		 * 关闭连接
		 */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}

}
