package com.jd.qbo;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jd.qbo.CircleSeekBar.OnCircleSeekBarChangeListener;

/**
 * 主页面
 * @author piguangming
 *
 */
public class MainActivity extends Activity {

	/** 调试标签：与类同名**/
	private static final String TAG = "MainActivity";
	/** 调试开关**/
	private static final boolean D = true;
	/** 标志位：是否支持波特率 **/
	private static final boolean BOTELV_FLAG = false;
	/** 标志位：数据包发送频率 **/
	private static final int FREQUENCY = 50;
	
	/** 全向行走方向：左后225°方向**/
	private static final String LEFT_BACK = "225";
	/** 全向行走方向：右后135°方向**/
	private static final String RIGHT_BACK = "135";
	/** 全向行走方向：左前315°方向**/
	private static final String LEFT_FORWARD = "315";
	/** 全向行走方向：右前45°方向**/
	private static final String RIGHT_FORWARD = "45";
	/** 全向行走方向：右90°方向**/
	private static final String RIGHT = "90";
	/** 全向行走方向：左270°方向**/
	private static final String LEFT = "270";
	/** 全向行走方向：后180°方向**/
	private static final String BACK = "180";
	/** 全向行走方向：前0°方向**/
	private static final String FORWARD = "0";

	/** 运动模式：正常行走**/
	public final static String MOVE_MODE_NORMAL = "正常行走";
	/** 运动模式：全向行走**/
	public final static String MOVE_MODE_FULL = "全向行走";
	/** 运动模式：原地旋转**/
	public final static String MOVE_MODE_STILL = "原地旋转";
	/** 运动模式：零位移点**/
	public final static String MOVE_MODE_ZERO = "零位移点";
	/** 控制模式：休眠**/
	public final static String CONTROL_MODE_SLEEP = "休眠";
	/** 控制模式：自动**/
	public final static String CONTROL_MODE_AUTO = "自动";
	/** 控制模式：保护**/
	public final static String CONTROL_MODE_PROTECT = "保护";
	
	/** 表示唤醒手机锁屏 **/
	WakeLock wakeLock;
	/** 表示本地手机蓝牙设备 **/
	private BluetoothAdapter localBlueTooth_;
	/** 表示远程蓝牙设备 **/
	public BluetoothService remoteBlueTooth_ = null;
	/** 系统Activity回调请求码：蓝牙设备连接 **/
	private static final int REQ_CODE_CONN_DEVICE = 1;
	/** 系统Activity回调请求码：打开本地蓝牙 **/
	private static final int REQ_CODE_ENABLE_BLUETOOTH = 2;
	/** 远程蓝牙设备返回的状态**/
	public static final int REMOTE_BLUETOOTH_STATE = 3;
	/** 远程蓝牙设备返回的名称 **/
	public static final int DEVICE_NAME = 4;
	/** 远程蓝牙设备返回的数据 **/
	public static final int Message = 1;
	/** 远程蓝牙设备返回的UI消息 **/
	public static final int UImsg = 2;
	/** 存取远程蓝牙设备返回的名称  **/
	private String mDeviceName;
	/** 作为Intent的key，用于前端与后端交互数据：设备名  **/
	public static final String BT_DEVICE_NAME = null;
	/** 作为Intent的key，用于前端与后端交互数据：消息框  **/
	public static final String BT_TOAST = null;
	
	/** 文本：显示连接状态 **/
	private TextView tvConnStatus_;
	/** 文本：显示发送数据 **/
	private TextView tvSentData_;
	/** 文本：显示接收数据 **/
	private TextView tvReceiveData_;
	/** 按钮：连接蓝牙设备 **/
	private Button btnBlueToothConn_;
	/** 按钮：断开蓝牙设备 **/
	private Button btnBlueToothDisconn_;
	/** 下拉框：选择运动模式 **/
    private Spinner spinMoveMode_; 
    /** 下拉框：选择控制模式 **/
    private Spinner spinControlMode_; 
    /** 适配器：填充运动模式下拉框选项 **/
    private ArrayAdapter adapterMoveMode_; 
    /** 适配器：填充控制模式下拉框选项 **/
    private ArrayAdapter adapterControlMode; 
    /** 文本框：线速度或角速度 **/
    private EditText edtSpeed_;
    /** 文本框：转弯角度 **/
    private EditText edtTurn_;
    /** 垂直进度杆：线速度活角速度 **/
    private VerticalSeekBar seekBarSpeed_;
    /** 圆形进度杆：转弯角度 **/
	private CircleSeekBar seekBarTurn_;
	/** 图片按钮：向前 **/
	private ImageButton mButtonF_;
	/** 图片按钮：向后 **/
	private ImageButton mButtonB_;
	/** 图片按钮：向左 **/
	private ImageButton mButtonL_;
	/** 图片按钮：向右 **/
	private ImageButton mButtonR_;
	/** 图片按钮：向右 **/
//	private ImageButton mButtonS;
	/** 图片按钮：右前 **/
	private ImageButton mButtonFR_;
	/** 图片按钮：右后 **/
	private ImageButton mButtonBR_;
	/** 图片按钮：左前 **/
	private ImageButton mButtonFL_;
	/** 图片按钮：左后 **/
	private ImageButton mButtonBL_;
	
	private LinearLayout linearLayoutWheel_;
	/** 标志位：是否允许发送数据包 **/
	private boolean flagSentPackage_;
	private Timer myTimer;
	private static int packagcounts = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// 保持高亮，不被锁定
		PowerManager manager = ((PowerManager) getSystemService(POWER_SERVICE));
		wakeLock = manager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
				
		// 确保本地蓝牙可用，否则打印消息：蓝牙设备不可用
		localBlueTooth_ = BluetoothAdapter.getDefaultAdapter();
		if (localBlueTooth_ == null) {
			Toast.makeText(this, R.string.bt_not_available, Toast.LENGTH_LONG).show();
			return;
		}
		
		// 确保本地蓝牙设备打开，否则调用系统设置，打开蓝牙。
		if (!localBlueTooth_.isEnabled()) {
			Intent btintent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(btintent, REQ_CODE_ENABLE_BLUETOOTH);
		}
		
		// 设置计时器，每隔若干秒（由FREQUENCY指定）执行任务TimerMethod。
		myTimer = new Timer();
		myTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				TimerMethod();
			}

		}, 0, FREQUENCY);
	}
	
	/**
	 * 按照Android官方文档，此种属于框架类方法。即计时器会定期执行该项任务，最重要的是，此项任务会在UI线程中运行，而非单起一额外线程。
	 */
	private void TimerMethod() {
		this.runOnUiThread(Timer_Tick);
	}

	/**
	 * 呼应前面的计时器，每隔若干秒（由FREQUENCY指定），发送数据给远程蓝牙。
	 */
	private Runnable Timer_Tick = new Runnable() {
		// 此方法运行于UI线程。
		public void run() {
			// 蓝牙设备，处于连接状态，且允许发送数据，满足以上条件，每隔若干秒（由FREQUENCY指定），发送数据给远程蓝牙。
			if (flagSentPackage_ && remoteBlueTooth_!=null && remoteBlueTooth_.getState() == BluetoothService.STATE_CONNECTED) {
				byte[] sent_package = calculateSentPackage();
				if (BOTELV_FLAG) {//波特率标志
					// 考虑到发送频度,每隔0.01秒按字节发送
					for(int i = 0; i < sent_package.length; i++) {
						try {
							Thread.sleep(0, 100000);//0.01秒
							remoteBlueTooth_.write(sent_package[i]);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} else {
					// 整包发送
					remoteBlueTooth_.write(sent_package);
				}
				tvSentData_.setText("发包(频率:" + FREQUENCY + "ms, "  + (packagcounts++) + ")..." + "：0x" + Converter.bytesToHexString(sent_package));
			} else {
				// 不具备发送数据的条件
				tvSentData_.setText("发送数据包:" );
				tvReceiveData_.setText("接收数据包:");
			}
		}
	};
	
	/**
	 * 页面出现前的操作，确保程序后台蓝牙服务准备就绪
	 */
	@Override
	public void onStart() {
		super.onStart();
		
		if (!localBlueTooth_.isEnabled()) {
			// 如果本地蓝牙未打开，调用系统设置，提示用户打开蓝牙设置
			Intent btintent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(btintent, REQ_CODE_ENABLE_BLUETOOTH);
		} else if (remoteBlueTooth_ == null) {
			// 本地蓝牙打开，但未连接远程蓝牙，则准备构建UI。
			control();
		}
	}
	
	/**
	 * 当该应用失去焦点后，通后切换，重新获取焦点，那么该方法被调用,确保远程蓝牙处于监听状态
	 */
	@Override
	public synchronized void onResume() {
		super.onResume();
		wakeLock.acquire();// 与onPause()方法中的wakeLock.release();对称使用
		if (remoteBlueTooth_ != null) {
			if (remoteBlueTooth_.getState() == BluetoothService.STATE_NONE) {
				remoteBlueTooth_.start();
			}
		}
	}
	
	/**
	 * 失去焦点，当前应用切换到，是否继续发送？
	 */
	@Override
	protected void onPause() {
		super.onPause();
		wakeLock.release();// 与onResume()方法中的wakeLock.acquire();对称使用
//		if (localBlueTooth_.isEnabled()) {
//			flagSentPackage_ = false;
//		}
	}

	/**
	 * UI界面构造
	 */
	private void control() {
		// 初始化操作。
		init();
		/**************************************************
		 * 蓝牙连接按钮。1，确保本地蓝牙启动；2，确保连接对方蓝牙
		 ***************************************************/
		btnBlueToothConn_.setText(R.string.bt_conn);
		btnBlueToothConn_.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if (!localBlueTooth_.isEnabled()) {
					Intent btintent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(btintent, REQ_CODE_ENABLE_BLUETOOTH);
				}
				Intent serverIntent = new Intent(MainActivity.this,
						DeviceList.class);
				startActivityForResult(serverIntent, REQ_CODE_CONN_DEVICE);
				
				//允许发送数据
				flagSentPackage_ = true;
			}
		} );

		/**************************************************
		 * 蓝牙断开按钮。只有双方处于连接状态，才有必要断开
		 ***************************************************/
		btnBlueToothDisconn_.setText(R.string.bt_disconn);
		btnBlueToothDisconn_.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (localBlueTooth_.isEnabled()) {
					remoteBlueTooth_.stop();
					//停止发送数据
					flagSentPackage_ = false;
					packagcounts = 0;
				}
			}
		});
		
		/**************************************************
		 * 运动模式下拉框。运动模式分为：正常、全向、原地（默认）
		 ***************************************************/
		adapterMoveMode_ = ArrayAdapter.createFromResource(this, R.array.move_mode, android.R.layout.simple_spinner_item);   
		adapterMoveMode_.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinMoveMode_.setAdapter(adapterMoveMode_);
		spinMoveMode_.setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,       
	                long arg3) {       
				String moveMode = adapterMoveMode_.getItem(arg2).toString();
	            Toast.makeText(MainActivity.this, "运动模式：" + moveMode, Toast.LENGTH_SHORT).show();       
	            if (moveMode.equalsIgnoreCase(MOVE_MODE_FULL)) {
	            	linearLayoutWheel_.setVisibility(View.VISIBLE);
	            	seekBarTurn_.setVisibility(View.GONE);
	            	seekBarSpeed_.setProgress(128);
	            	edtTurn_.setText(FORWARD);
	            } else if (moveMode.equalsIgnoreCase(MOVE_MODE_NORMAL) || moveMode.equalsIgnoreCase(MOVE_MODE_STILL)) {
	            	linearLayoutWheel_.setVisibility(View.GONE);
	            	seekBarTurn_.setVisibility(View.VISIBLE);
	            	seekBarSpeed_.setProgress(128);
	            	seekBarTurn_.initValue();
	            	edtTurn_.setText(FORWARD);
	            }
	        } 
			 public void onNothingSelected(AdapterView<?> arg0) {       
                 
		        }       
			});   
		spinMoveMode_.setVisibility(View.VISIBLE);  
			
		/**************************************************
		 * 控制模式下拉框。运动模式分为：保护（默认）、自动、休眠
		 ***************************************************/
		adapterControlMode = ArrayAdapter.createFromResource(this, R.array.control_mode, android.R.layout.simple_spinner_item);   
		adapterControlMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinControlMode_.setAdapter(adapterControlMode);
		spinControlMode_.setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,       
	                long arg3) {       
//	            Toast.makeText(MainActivity.this, "控制模式：" + adapterControlMode.getItem(arg2), Toast.LENGTH_SHORT).show();       
	        }       
	      
	        public void onNothingSelected(AdapterView<?> arg0) {       
	                   
	        }       
		});   
		spinControlMode_.setVisibility(View.VISIBLE); 
		
		/**************************************************
		 * 速度进度条
		 ***************************************************/
		seekBarSpeed_.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
				edtSpeed_.setText("" + (int) (progress * 1.28)); //进度杆的值为0-100，这里乘上系数1.28，表示0-128
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		/**************************************************
		 * 转弯进度条
		 ***************************************************/
		seekBarTurn_.setOnSeekBarChangeListener(new OnCircleSeekBarChangeListener(){
			public void onProgressChanged(CircleSeekBar seekBar, int progress,
					boolean fromUser) {
				edtTurn_.setText(progress + "");
			}
		});
		
		/**************************************************
		 * 全向行走 - 方向盘 （前后左右，外加4个转弯）
		 ***************************************************/
		// 前
		mButtonF_.setOnTouchListener(new Button.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
//					sendMessage(FORWARD);
					edtTurn_.setText(FORWARD);
					mButtonF_.setImageResource(R.drawable.forward1);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
//					sendMessage(STOP);
					mButtonF_.setImageResource(R.drawable.forward);
				}
				return false;
			}
		});
		// 后
		mButtonB_.setOnTouchListener(new Button.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
//					sendMessage(BACK);
					edtTurn_.setText(BACK);
					mButtonB_.setImageResource(R.drawable.back2);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
//					sendMessage(STOP);
					mButtonB_.setImageResource(R.drawable.back1);
				}
				return false;
			}
		});
		// 左
		mButtonL_.setOnTouchListener(new Button.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
//					sendMessage(LEFT);
					edtTurn_.setText(LEFT);
					mButtonL_.setImageResource(R.drawable.left1);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
//					sendMessage(STOP);
					mButtonL_.setImageResource(R.drawable.left);
				}
				return false;
			}
		});
		// 右
		mButtonR_.setOnTouchListener(new Button.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
//					sendMessage(RIGHT);
					edtTurn_.setText(RIGHT);
					mButtonR_.setImageResource(R.drawable.right1);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
//					sendMessage(STOP);
					mButtonR_.setImageResource(R.drawable.right);
				}
				return false;
			}
		});
		// 右前
		mButtonFR_.setOnTouchListener(new Button.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
//					sendMessage(RIGHT_FORWARD);
					edtTurn_.setText(RIGHT_FORWARD);
					mButtonFR_.setImageResource(R.drawable.lf);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
//					sendMessage(STOP);
					mButtonFR_.setImageResource(R.drawable.rf);
				}
				return false;
			}
		});
		// 左前
		mButtonFL_.setOnTouchListener(new Button.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
//					sendMessage(LEFT_FORWARD);
					edtTurn_.setText(LEFT_FORWARD);
					mButtonFL_.setImageResource(R.drawable.lf);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
//					sendMessage(STOP);
					mButtonFL_.setImageResource(R.drawable.rf);
				}
				return false;
			}
		});
		// 右后
		mButtonBR_.setOnTouchListener(new Button.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
//					sendMessage(RIGHT_BACK);
					edtTurn_.setText(RIGHT_BACK);
					mButtonBR_.setImageResource(R.drawable.lf);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
//					sendMessage(STOP);
					mButtonBR_.setImageResource(R.drawable.rf);
				}
				return false;
			}
		});
		//左后
		mButtonBL_.setOnTouchListener(new Button.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
//					sendMessage(LEFT_BACK);
					edtTurn_.setText(LEFT_BACK);
					mButtonBL_.setImageResource(R.drawable.lf);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
//					sendMessage(STOP);
					mButtonBL_.setImageResource(R.drawable.rf);
				}
				return false;
			}
		});
		// 初始化 蓝牙后台服务类BluetoothService
		remoteBlueTooth_ = new BluetoothService(this, remoteBlueToothHandler);
	}
	
	/**
	 * 初始化页面控件
	 */
	private void init() {
		tvConnStatus_ = (TextView) findViewById(R.id.textview_connection_status);
		btnBlueToothConn_ = (Button)findViewById(R.id.bt_conn);
		btnBlueToothDisconn_ = (Button)findViewById(R.id.bt_disconn);
		edtSpeed_ = (EditText) this.findViewById(R.id.editview_input_speed);
		edtSpeed_.setText(FORWARD);
		edtTurn_ = (EditText) this.findViewById(R.id.editview_input_amount_of_turn);
		edtTurn_.setText(FORWARD);
		spinMoveMode_ = (Spinner) findViewById(R.id.spinner_move_mode); 
		spinControlMode_ = (Spinner) findViewById(R.id.spinner_control_mode); 
		linearLayoutWheel_ = (LinearLayout) MainActivity.this.findViewById(R.id.linearLayoutWheel);
    	seekBarTurn_ = (CircleSeekBar) MainActivity.this.findViewById(R.id.circleSeekBar);
    	seekBarSpeed_ = (VerticalSeekBar) MainActivity.this.findViewById(R.id.verticalSeekBar);
    	mButtonF_ = (ImageButton) findViewById(R.id.forward);
		mButtonB_ = (ImageButton) findViewById(R.id.back);
		mButtonL_ = (ImageButton) findViewById(R.id.left);
		mButtonR_ = (ImageButton) findViewById(R.id.right);
		mButtonFR_ = (ImageButton) findViewById(R.id.right_F);
		mButtonFL_ = (ImageButton) findViewById(R.id.left_F);
		mButtonBR_ = (ImageButton) findViewById(R.id.right_B);
		mButtonBL_ = (ImageButton) findViewById(R.id.left_B);
		tvSentData_ = (TextView)findViewById(R.id.textview_sent_data);
		tvReceiveData_ = (TextView)MainActivity.this.findViewById(R.id.textview_receive_data);
	}
		
	
	/**
	 * Handler的作用是连通前、后台。所谓前台，指的是Activity，后台之的是与蓝牙相关的服务类——BluetoothService
	 * 当用户前台点击蓝牙设备连接按钮时，后台服务类负责蓝牙连接操作，并将相应的状态，以信息的方式发送给Handler。
	 * 一旦Handler作为监听器收到，立即触发相应的Activity操作。比如连接完成后，显示设备名称。
	 */
	private final Handler remoteBlueToothHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REMOTE_BLUETOOTH_STATE:
				switch (msg.arg1) {
				case BluetoothService.STATE_CONNECTED:
					tvConnStatus_.setText(R.string.connected_to);
					tvConnStatus_.append(mDeviceName);//device.append("JD");//device.append(mDeviceName);
					break;
				case BluetoothService.STATE_CONNECTING:
					tvConnStatus_.setText(R.string.connecting);
					break;
				case BluetoothService.STATE_LISTEN:
				case BluetoothService.STATE_NONE:
					tvConnStatus_.setText(R.string.not_connected);
					break;
				}
				break;
			case Message:
				//接收数据包
				String s = (String)msg.obj;
				String message = s.substring(s.indexOf("ff") + 2, s.lastIndexOf("fe"));
//				
//				// 解析数据
//				String x = ParseProtocol.parseX(message); //X
//				String y = ParseProtocol.parseY(message); //Y
//				String xiansudu = ParseProtocol.parseLineSpeed(message); //线速度
//				String jiaosudu = ParseProtocol.parseAngleSpeed(message);//角速度
//				String zhuanwanjiaodu = ParseProtocol.parseAngle(message);//转弯角度
//				String zhangaiwujuli = ParseProtocol.parseDistance(message);//障碍物距离
//				String weiyijiaodu = ParseProtocol.parseDegree(message);//位移角度
				
//				TextView textViewX =  (TextView)MainActivity.this.findViewById(R.id.textview_position_status_x);
//				TextView textViewY =  (TextView)MainActivity.this.findViewById(R.id.textview_position_status_y);
//				TextView textViewXianSuDu =  (TextView)MainActivity.this.findViewById(R.id.textview_position_realtime_linear_velocity);
//				TextView textViewJiaoSuDu =  (TextView)MainActivity.this.findViewById(R.id.textview_position_realtime_angular_velocity);
//				TextView textViewZhuanwanjiaodu =  (TextView)MainActivity.this.findViewById(R.id.textview_amount_of_turn);
//				TextView textViewZhangaiwujuli =  (TextView)MainActivity.this.findViewById(R.id.textview_distance_of_obstacle);
//				TextView textViewWeiyijiaodu =  (TextView)MainActivity.this.findViewById(R.id.textview_position_status_angle);
//				textViewX.setText("0");//屏蔽
//				textViewY.setText("0");
//				textViewXianSuDu.setText(xiansudu);
//				textViewJiaoSuDu.setText(jiaosudu);
//				textViewZhuanwanjiaodu.setText(zhuanwanjiaodu);
//				textViewZhangaiwujuli.setText(zhangaiwujuli);
//				textViewWeiyijiaodu.setText(weiyijiaodu);
				
				tvReceiveData_ = (TextView)MainActivity.this.findViewById(R.id.textview_receive_data);
				tvReceiveData_.setText("实时采集数据：" + message);
				break;
			case DEVICE_NAME:
				mDeviceName = msg.getData().getString(BT_DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"The name of the device is: " + mDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case UImsg:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(BT_TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQ_CODE_CONN_DEVICE:
			// 当用户选中目标蓝牙设备，则连接，并提示设备Mac地址
			if (resultCode == Activity.RESULT_OK) {
				remoteBlueTooth_.connect(localBlueTooth_.getRemoteDevice(data.getExtras().getString(
						DeviceList.EXTRA_DEVICE_ADDRESS)));
				Toast.makeText(this, data.getExtras().getString(
						DeviceList.EXTRA_DEVICE_ADDRESS), Toast.LENGTH_LONG).show();
			}
			break;

		case REQ_CODE_ENABLE_BLUETOOTH:
			// 开启蓝牙设置
			if (resultCode == Activity.RESULT_OK) {
				control();
			} else {
				Toast.makeText(this, R.string.bt_not_enable, Toast.LENGTH_SHORT).show();
				return;
			}

		}
	}
	

	/**
	 * 根据协议计算数据包字段格式
	 * @return
	 */
	private byte[] calculateSentPackage() {
		/**
		 * 构造发送package，涉及字段包括：运动模式、控制模式、速度、转弯角度等
		 */
		//发送数据包共计十个字节
		byte[] sent_package = new byte[10];
		//字节0 - 开始（0xFF）
		sent_package[0] = (byte) 0xFF;
		//字节1 - 恒定为0
		sent_package[1] = 0;
		//字节2 - 恒定为5
		sent_package[2] = 5;
		//字节3,4,5,6 - 运动模式 (角速度，线速度，角度)
			//			正常行走	0x33
			//			全向行走	0x55
			//			原地旋转	0x66
			//			设置零位移点	0xaa
		String move_mode = spinMoveMode_.getSelectedItem().toString();
		byte moveMode;
		if (move_mode.equalsIgnoreCase(MainActivity.MOVE_MODE_FULL)){
			//全向行走
			moveMode = 0x55;
			String line_speed = edtSpeed_.getText().toString();
			int lineSpeed = Converter.strToIntNoException(line_speed);
			//线速度  (0 - 128)
			sent_package[4] = (byte) lineSpeed;
			String turn_degree = edtTurn_.getText().toString();
			int turnDegree = Converter.strToIntNoException(turn_degree);
			//转弯角度 (0 - 360)
			if (turnDegree <= 255) {
				sent_package[5] = 0x00;
				sent_package[6] = (byte) turnDegree;
			} else {
				byte[] b = Converter.toByteArray(turnDegree, 2);
				sent_package[6] = b[0];
				sent_package[5] = b[1];
			}
			
		} else if (move_mode.equalsIgnoreCase(MainActivity.MOVE_MODE_ZERO)){
			//设置零位移点
			moveMode = (byte) 0xaa;
		} else if (move_mode.equalsIgnoreCase(MainActivity.MOVE_MODE_STILL)){
			//角速度
			moveMode = 0x66;
			String angle_speed = edtSpeed_.getText().toString();
			int angleSpeed = Converter.strToIntNoException(angle_speed);
			//线速度  (0 - 128)
			sent_package[4] = (byte) angleSpeed;
		} else {
			moveMode = 0x33;
			String line_speed = edtSpeed_.getText().toString();
			int lineSpeed = Converter.strToIntNoException(line_speed);
			//线速度  (0 - 128)
			sent_package[4] = (byte) lineSpeed;
			String turn_degree = edtTurn_.getText().toString();
			int turnDegree = Converter.strToIntNoException(turn_degree);
			//转弯角度 (0 - 360)
			if (turnDegree <= 255) {
				sent_package[5] = 0x00;
				sent_package[6] = (byte) turnDegree;
			} else {
				byte[] b = Converter.toByteArray(turnDegree, 2);
				sent_package[6] = b[0];
				sent_package[5] = b[1];
			}
		}
		sent_package[3] = moveMode;
		
		String control_mode = spinControlMode_.getSelectedItem().toString();
		byte controlMode;
		if (control_mode.equalsIgnoreCase(MainActivity.CONTROL_MODE_SLEEP)){
			controlMode = 0x11;
		} else if (control_mode.equalsIgnoreCase(MainActivity.CONTROL_MODE_AUTO)){
			controlMode = 0x55;
		} else {
			controlMode = 0x33;
		}
		sent_package[7] = controlMode;//自动
		
		byte[] key = { sent_package[1], sent_package[2], sent_package[3], sent_package[4], sent_package[5], sent_package[6], sent_package[7] };
		sent_package[8] = PearsonHash.pearson(key);
		sent_package[9] = (byte) 0xFE;
		
		return sent_package;
	}
	
}
