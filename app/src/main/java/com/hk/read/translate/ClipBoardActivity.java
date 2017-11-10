package com.hk.read.translate;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.hk.read.R;
import com.hk.read.base.BaseActivity;

public class ClipBoardActivity extends BaseActivity implements OnClickListener {

	private TextView mResultTextView;

	private Button mStart;

	private Button mStop;

	private Button mBind;

	private Button mUnBind;

	private Context mContext;

	private ClipBoardReceiver mBoardReceiver;

	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.e(this.getClass().getSimpleName(), "onServiceDisconnected");
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.e(this.getClass().getSimpleName(), "onServiceConnected");
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mContext = this;

		mBoardReceiver = new ClipBoardReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.cybertron.dict.ClipBoardReceiver");
		registerReceiver(mBoardReceiver, filter);
		
		mStart = (Button) findViewById(R.id.start);
		mStop = (Button) findViewById(R.id.stop);
		mBind = (Button) findViewById(R.id.bind);
		mUnBind = (Button) findViewById(R.id.unbind);

		mStart.setOnClickListener(this);
		mStop.setOnClickListener(this);
		mBind.setOnClickListener(this);
		mUnBind.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent mIntent = new Intent();
		switch (v.getId()) {
		case R.id.start:
			mIntent.setClass(ClipBoardActivity.this, ClipBoardService.class);
			mContext.startService(mIntent);
			break;
		case R.id.stop:
			mIntent.setClass(ClipBoardActivity.this, ClipBoardService.class);
			mContext.stopService(mIntent);
			break;
		case R.id.bind:
			Intent show = new Intent(this, FloatingWindowService.class);
			show.putExtra(FloatingWindowService.OPERATION,FloatingWindowService.OPERATION_SHOW);
			startService(show);
			break;
		case R.id.unbind:
			Intent hide = new Intent(this, FloatingWindowService.class);
			hide.putExtra(FloatingWindowService.OPERATION,FloatingWindowService.OPERATION_HIDE);
			startService(hide);
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(mBoardReceiver);
	}
	
	class ClipBoardReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			if(bundle != null){
				String value = (String) bundle.get("clipboardvalue");
				Intent show = new Intent(ClipBoardActivity.this, FloatingWindowService.class);
				show.putExtra(FloatingWindowService.OPERATION,FloatingWindowService.OPERATION_SHOW);
				show.putExtra("copyValue", value);
				ClipBoardActivity.this.startService(show);
			}
		}		
	}
	
}
