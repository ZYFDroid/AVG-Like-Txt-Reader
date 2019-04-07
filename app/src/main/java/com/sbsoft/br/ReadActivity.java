package com.sbsoft.br;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import android.widget.SeekBar.*;
import android.util.*;
import android.content.res.*;
import android.net.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.provider.*;
import android.database.*;

public class ReadActivity extends Activity
{

	public boolean lock=true;
	public static String readPath="/storage/emulated/0/testbook.txt";
	public static int readslot=0;
	public static int readPos=-1;

	void loadSlot()
	{
		readPos = loadInt("readPos" + String.valueOf(readslot), -1);
		Klog.log = Ut.loadstate(this, "readedText" + readslot, "");
		tr.changeText(Ut.loadstate(this, "remainText" + readslot, ""));
		logTextView.setText(Klog.log);

	}

	int loadInt(String k, int df)
	{
		return Integer.parseInt(Ut.loadstate(this, k, String.valueOf(df)));
	}
	void saveint(String k, int v)
	{
		Ut.savestate(this, k, String.valueOf(v));
	}
	public static ArrayList<String> dataArr;
	RelativeLayout readingBg;
	LinearLayout readingFg;
	TextView readingText;
	ImageView menubtn;
	LinearLayout logpan;
	TextView nextHint;
	ImageView logbutton;
	LinearLayout menupannel;
	TextView logTextView;
	TextView loadingtext;
	SeekBar textSpeed;
	SeekBar textSizeBar;
	TextView threelinetext;
	Typer tr;
	BatteryReceiver receiver;

	public void initUi()
	{
		threelinetext = (TextView)findViewById(R.id.threeCharHeight);
		readingBg = (RelativeLayout)findViewById(R.id.readingBg);
		try
		{
			readingBg.setBackgroundResource(Integer.valueOf(Ut.loadstate(this, "bg", String.valueOf(R.drawable.read_bg_1))));
		}
		catch (NumberFormatException e)
		{
			String path="";
			if (new File(path = Ut.loadstate(this, "bg", "")).exists())
			{
				readingBg.setBackgroundDrawable(Drawable.createFromPath(path));
			}
			else
			{
				saveint("bg", R.drawable.read_bg_1);
				readingBg.setBackgroundResource(R.drawable.read_bg_1);
			}
		}
		readingFg = (LinearLayout)findViewById(R.id.readingFg);
		readingFg.setBackgroundResource(Integer.valueOf(Ut.loadstate(this, "fg", String.valueOf(R.drawable.readfg_1))));
		readingText = (TextView)findViewById(R.id.readingText);
		menubtn = (ImageView)findViewById(R.id.menubtn);
		nextHint = (TextView)findViewById(R.id.nextHint);
		logpan = (LinearLayout)findViewById(R.id.logpan);
		logbutton = (ImageView)findViewById(R.id.logbutton);
		menupannel = (LinearLayout)findViewById(R.id.menupannel);
		menupannel.setVisibility(View.GONE);

		logTextView = (TextView)findViewById(R.id.logTextView);
		loadingtext = (TextView)findViewById(R.id.loadingtext);
		textSizeBar = (SeekBar)findViewById(R.id.textSizeBar);
		textSizeBar.setMax(50 - 6);
		textSizeBar.setProgress(loadInt("textsize", 20));
		threelinetext.setTextSize(loadInt("textsize", 20));
		threelinetext.setText("l\nl\nl");
		readingText.setTextSize(loadInt("textsize", 20));

		textSizeBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

				@Override
				public void onProgressChanged(SeekBar p1, int p2, boolean p3)
				{
					// TODO: Implement this method
					threelinetext.setTextSize(p2);
					readingText.setTextSize(p2);

					matchHeight();

				}

				@Override
				public void onStartTrackingTouch(SeekBar p1)
				{
					// TODO: Implement this method
				}

				@Override
				public void onStopTrackingTouch(SeekBar p1)
				{
					// TODO: Implement this method
					matchHeight();
				}
			});
		textSpeed = (SeekBar)findViewById(R.id.textSpeed);
		textSpeed.setMax(195);
		textSpeed.setProgress(200 - loadInt("textSpeed", 100));
		textSpeed.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

				@Override
				public void onProgressChanged(SeekBar p1, int p2, boolean p3)
				{
					// TODO: Implement this method
					tr.delay = 200 - p2;
					saveint("textSpeed", 200 - p2);
				}

				@Override
				public void onStartTrackingTouch(SeekBar p1)
				{
					// TODO: Implement this method
				}

				@Override
				public void onStopTrackingTouch(SeekBar p1)
				{
					// TODO: Implement this method

				}
			});
		tr = new Typer(this, loadInt("textSpeed", 100), readingText, nextHint);
		logbutton.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View p1, MotionEvent p2)
				{
					// TODO: Implement this method
					if (p2.getAction() == p2.ACTION_DOWN)
					{
						logpan.setVisibility(View.VISIBLE);
						lock = true;
					}
					if (p2.getAction() == p2.ACTION_UP)
					{
						logpan.setVisibility(View.GONE);
						lock = false;
						//throw new AndroidRuntimeException("TestCrash");

					}
					return true;
				}
			});
		menubtn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
					openSetting();
				}
			});
		onHide();
		
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		receiver = new BatteryReceiver((TextView)findViewById(R.id.batteryView));
		registerReceiver(receiver, filter);
		
		readStatusSetting();
		
	}

	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		// TODO: Implement this method
		onHide();
		super.onConfigurationChanged(newConfig);
	}

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
		currentCtx = this;
		atystarted = false;
        super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);		
        setContentView(R.layout.reading);
		initUi();
		loadSlot();
    }
	boolean atystarted=false;
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		// TODO: Implement this method
		super.onWindowFocusChanged(hasFocus);
		if (!atystarted)
		{
			atystarted = true;
			start();
		}
		if (hasFocus)
		{
			onHide();}
	}
	int local_bgalpha;
	public void start()
	{
		loadingtext.setVisibility(View.VISIBLE);
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					Runnable r=new Runnable(){

						@Override
						public void run()
						{
							// TODO: Implement this method
							matchHeight();
							loadingtext.setTextColor(local_bgalpha * 0x1000000 + 0xffffff);
							loadingtext.setBackgroundColor(local_bgalpha * 0x1000000);
							if (local_bgalpha == 0)
							{
								loadingtext.setVisibility(View.INVISIBLE);

							}
						}
					};
					// TODO: Implement this method
					try
					{
						Thread.sleep(2500);
					}
					catch (InterruptedException e)
					{}
					if (readPos == -1)
					{readPos++;}
					for (int i=255;i >= 0;i--)
					{
						local_bgalpha = i;
						runOnUiThread(r);
						try
						{
							Thread.sleep(4);
						}
						catch (InterruptedException e)
						{}
					}
					lock = false;
					runOnUiThread(new Runnable(){

							@Override
							public void run()
							{
								// TODO: Implement this method
								onNextClick(null);
							}
						});
				}
			}).start();
	}

	@Override
	public void onBackPressed()
	{
		// TODO: Implement this method
		//super.onBackPressed();
		openSetting();
	}

	void openSetting()
	{


		if (menupannel.getVisibility() == View.VISIBLE)
		{


			menupannel.setVisibility(View.GONE);
			lock = false;
		}
		else
		{
			menupannel.setVisibility(View.VISIBLE);
			lock = true;

		}
	}

	public static ArrayList<Integer> nextKeycode=new ArrayList<>();
	public static ArrayList<Integer> leftKeycode=new ArrayList<>();
	static{
		nextKeycode.add(KeyEvent.KEYCODE_ENTER);
		nextKeycode.add(KeyEvent.KEYCODE_SPACE);
		nextKeycode.add(KeyEvent.KEYCODE_BUTTON_A);
		nextKeycode.add(KeyEvent.KEYCODE_BUTTON_R1);
		nextKeycode.add(KeyEvent.KEYCODE_BUTTON_R2);
		nextKeycode.add(KeyEvent.KEYCODE_DPAD_RIGHT);
		nextKeycode.add(KeyEvent.KEYCODE_DPAD_DOWN);
		nextKeycode.add(KeyEvent.KEYCODE_DPAD_CENTER);
		nextKeycode.add(KeyEvent.KEYCODE_PAGE_DOWN);
		
		leftKeycode.add(KeyEvent.KEYCODE_DEL);
		leftKeycode.add(KeyEvent.KEYCODE_FORWARD_DEL);
		leftKeycode.add(KeyEvent.KEYCODE_PAGE_UP);
		leftKeycode.add(KeyEvent.KEYCODE_SHIFT_RIGHT);
		leftKeycode.add(KeyEvent.KEYCODE_SHIFT_LEFT);
		leftKeycode.add(KeyEvent.KEYCODE_BUTTON_X);
		leftKeycode.add(KeyEvent.KEYCODE_BUTTON_L1);
		leftKeycode.add(KeyEvent.KEYCODE_BUTTON_L2);
		leftKeycode.add(KeyEvent.KEYCODE_DPAD_LEFT);
		leftKeycode.add(KeyEvent.KEYCODE_DPAD_UP);
		
		
	}
	

	@Override
	public boolean dispatchKeyEvent(KeyEvent event)
	{
		int keyCode=event.getKeyCode();
		if(event.getAction()==KeyEvent.ACTION_DOWN||event.getAction()==KeyEvent.ACTION_MULTIPLE){
			if(nextKeycode.contains(keyCode)){onNextClick(null);return true;}

			if(leftKeycode.contains(keyCode)){
				logpan.setVisibility(View.VISIBLE);
				lock = true;
				return true;
			}
		}
		if(event.getAction()==KeyEvent.ACTION_UP){
			if(leftKeycode.contains(keyCode)){
				logpan.setVisibility(View.GONE);
				lock = false;
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}
	
	
	
	

	public void matchHeight()
	{
		LinearLayout.LayoutParams llp=(LinearLayout.LayoutParams)((LinearLayout)findViewById(R.id.itn)).getLayoutParams();
		llp.height = threelinetext.getHeight();
		((LinearLayout)findViewById(R.id.itn)).setLayoutParams(llp);
	}


	

	public void onNextClick(View p)
	{
		//TODO: Implements this method

		if (!lock)
		{
			try
			{
				if (tr.needAppend())
				{tr.changeText(dataArr.get(readPos));readPos++;
				}if (tr.needclean)
				{Klog.v(readingText.getText());
					logTextView.setText(Klog.log.trim());
				}
				tr.type();
				
				loadProgressText();
				
				}
			catch (IndexOutOfBoundsException e)
			{
				loadingtext.setText("全文 完");
				loadingtext.setVisibility(View.VISIBLE);
				new Thread(new Runnable(){

						@Override
						public void run()
						{
							// TODO: Implement this method

							Runnable r=new Runnable(){

								@Override
								public void run()
								{
									// TODO: Implement this method
									loadingtext.setTextColor(local_bgalpha * 0x1000000 + 0xffffff);
									loadingtext.setBackgroundColor(local_bgalpha * 0x1000000);

									if (local_bgalpha == 255)
									{
										cleansave();

										startActivity(new Intent(ReadActivity.this, MainActivity.class));
										finish();
									}
								}
							};
							lock = true;

							for (int i=0;i <= 254;i++)
							{
								local_bgalpha = i;
								runOnUiThread(r);
								try
								{
									Thread.sleep(4);
								}
								catch (InterruptedException e)
								{}
							}

							try
							{
								Thread.sleep(2500);
							}
							catch (InterruptedException e)
							{}
							local_bgalpha = 255;
							runOnUiThread(r);

						}
					}).start();


			}}
	}
	public void saveandexit(View p1)
	{
		//TODO: Implements this method
		save(p1);
		startActivity(new Intent(this, MainActivity.class));
		finish();
	}


	public void gallery(View view)
	{
		// 激活系统图库，选择一张图片
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		// 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
		startActivityForResult(intent, 2);
	}
	@SuppressWarnings("unused")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO: Implement this method
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 2)
		{
			// 从相册返回的数据
			if (data != null)
			{
				// 得到图片的全路径
				Uri uri = data.getData();
				try
				{
					//Ut.tw(this,getFilePathFromContentUri(uri,getContentResolver()));
					readingBg.setBackgroundDrawable(Drawable.createFromPath(getFilePathFromContentUri(uri, getContentResolver())));
					Ut.savestate(this, "bg", getFilePathFromContentUri(uri, getContentResolver()));
				}
				catch (Exception e)
				{Ut.tw(this, "打开图片失败.");}
			}

		}


	}
	/**
     * Gets the corresponding path to a file from the given content:// URI
     * uri转绝对路径
     *
     * @param selectedVideoUri The content:// URI to find the file path from
     * @param contentResolver  The content resolver to use to perform the query.
     * @return the file path as a string
     */
    public static String getFilePathFromContentUri(Uri selectedVideoUri, ContentResolver contentResolver)
	{
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }


	public void cleansave()
	{

		saveint("readPos" + readslot, 0);
		Ut.savestate(this, "readedText" + readslot, "");
		Ut.savestate(this, "remainText" + readslot, "");
	}
	@Override
	protected void onResume()
	{
		// TODO: Implement this method
		onHide();
		super.onResume();
	}

	@Override
	protected void onRestart()
	{
		// TODO: Implement this method
		onHide();
		super.onRestart();
	}
	public void svc(View p1)
	{
		openSetting();
		save(p1);
		Toast t=Toast.makeText(this, "", Toast.LENGTH_LONG);
		TextView tv=new TextView(this);
		tv.setBackgroundResource(R.drawable.readfg_1);
		tv.setTextColor(0xffffffff);
		tv.setText("保存成功");
		t.setView(tv);
		t.show();
	}
	public void save(View p1)
	{

		saveint("readPos" + readslot, readPos);
		Ut.savestate(this, "readedText" + readslot, Klog.log);
		Ut.savestate(this, "remainText" + readslot, tr.remainText);
	}
	private static ReadActivity currentCtx;
	public static String forceSave()
	{
		try
		{
			currentCtx.save(null);
		}
		catch (Exception e)
		{
			return "存档保存失败";
		}
		return "存档成功保存";
	}
	public void sbg1(View p1)
	{
		//TODO: Implements this method
		saveint("bg", R.drawable.read_bg_1);
		readingBg.setBackgroundResource(R.drawable.read_bg_1);
		openSetting();
	}
	public void sbg2(View p1)
	{
		//TODO: Implements this method
		saveint("bg", R.drawable.read_bg_2);
		readingBg.setBackgroundResource(R.drawable.read_bg_2);
		openSetting();
	}
	public void sbg3(View p1)
	{
		//TODO: Implements this method
		saveint("bg", R.drawable.read_bg_3);
		readingBg.setBackgroundResource(R.drawable.read_bg_3);
		openSetting();
	}
	public void sfg1(View p1)
	{
		//TODO: Implements this method
		saveint("fg", R.drawable.readfg_1);
		readingFg.setBackgroundResource(R.drawable.readfg_1);
		openSetting();
	}
	public void sfg2(View p1)
	{
		//TODO: Implements this method
		saveint("fg", R.drawable.readfg_2);
		readingFg.setBackgroundResource(R.drawable.readfg_2);
		openSetting();
	}
	public void sfg3(View p1)
	{
		//TODO: Implements this method
		saveint("fg", R.drawable.readfg_3);
		readingFg.setBackgroundResource(R.drawable.readfg_3);
		openSetting();
	}
	public void sfg4(View p1)
	{
		//TODO: Implements this method
		saveint("fg", R.drawable.readfg_4);
		readingFg.setBackgroundResource(R.drawable.readfg_4);
		openSetting();
	}

	public void onHide()
	{
		//4.1及以上通用flags组合
		int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;


		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			getWindow().getDecorView().setSystemUiVisibility(
                flags | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
			);
		}
		else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
		{
			getWindow().getDecorView().setSystemUiVisibility(flags);
		}
	}

	public void loadProgressText(){
		float current=readPos*10000/dataArr.size();
		current=current/100f;
		((TextView)findViewById(R.id.progressView)).setText(current+"%");
	}
	
	public void onStatusVisibilityChanged(View p1){
		if(p1.getId()==R.id.showProg){
			((View)findViewById(R.id.progressView)).setVisibility(((CheckBox)p1).isChecked()?View.VISIBLE:View.GONE);
			saveint("show_prog",((CheckBox)p1).isChecked() ? 0 : 1);
		}
		if(p1.getId()==R.id.showTime){
			((View)findViewById(R.id.clockView)).setVisibility(((CheckBox)p1).isChecked()?View.VISIBLE:View.GONE);
			saveint("show_clock",((CheckBox)p1).isChecked() ? 0 : 1);
		}
		if(p1.getId()==R.id.showBatt){
			((View)findViewById(R.id.batteryView)).setVisibility(((CheckBox)p1).isChecked()?View.VISIBLE:View.GONE);
			saveint("show_battery",((CheckBox)p1).isChecked() ? 0 : 1);
		}
	}
	
	public void readStatusSetting(){
		CheckBox chk;
		chk=(CheckBox)findViewById(R.id.showBatt);
		chk.setChecked(loadInt("show_battery",1)==0);
		onStatusVisibilityChanged(chk);
		
		chk=(CheckBox)findViewById(R.id.showTime);
		chk.setChecked(loadInt("show_clock",0)==0);
		onStatusVisibilityChanged(chk);
		
		chk=(CheckBox)findViewById(R.id.showProg);
		chk.setChecked(loadInt("show_prog",1)==0);
		onStatusVisibilityChanged(chk);
		
	}
	
}


class Typer
{
	Activity ctx;
	public static final String marks="!~?:;.,，。？！：、…”；’～.—∶";
	boolean needclean=false;
	int delay;
	TextView textHandler;
	View switcher;
	String l=" ";
	String s=" ";
	String curText="";
	String remainText="";
	boolean isTyping=false;
	public Queue<String> msgque;
	public Runnable eventRun;
	public Typer(Activity nctx, int ndelay, TextView ntextHandler, View nswitcher)
	{

		delay = ndelay;
		msgque = new LinkedBlockingQueue<String>();
		if (delay < 1 || delay > 1000)
		{
			delay = 100;
		}
		textHandler = ntextHandler;
		switcher = nswitcher;
		ctx = nctx;
	}
	public boolean needAppend()
	{return msgque.isEmpty();}
	public void changeText(String text)
	{
		for (int i=0;i <= text.length() - 1;i++)
		{
			msgque.add(text.substring(i, i + 1));
		}
	}
	Thread t;
	public void type()
	{
		if (isTyping)
		{return;}
		switcher.setVisibility(View.INVISIBLE);
		t = new Thread(new Runnable(){

				@Override
				public void run()
				{
					isTyping = true;
					Runnable rb=new Runnable(){
						@Override
						public void run()
						{
							textHandler.setText(curText);

						}
					};
					if (needclean)
					{
						curText = "";needclean = false;
						setRemainText();}
					boolean needexit=false;
					boolean inMark=false;
					while (!msgque.isEmpty() && !needexit)
					{
						s = msgque.poll();
						if (marks.contains(s))
						{
							inMark = true;
						}
						curText = curText + s;
						ctx.runOnUiThread(rb);
						deley(delay);
						try
						{
							if (inMark && !marks.contains(msgque.peek()))
							{needexit = true;
								if (curText.contains(".") ||
									curText.contains("?") ||
									curText.contains("!") ||
								//curText.contains(":")||
									curText.contains(";") ||
									curText.contains("。") ||
									curText.contains("；") ||
								//curText.contains("：")||
									curText.contains("？") ||
									curText.contains("！")
									)
								{needclean = true;}}}
						catch (NullPointerException e)
						{needexit = true;needclean = true;}
						l = s;
					}
					if (msgque.isEmpty())
					{
						needclean = true;}
					ctx.runOnUiThread(new Runnable(){

							@Override
							public void run()
							{
								// TODO: Implement this method
								switcher.setVisibility(View.VISIBLE);
							}
						});
					isTyping = false;
				}
			});
		t.start();
	}

	void deley(long t)
	{
		try
		{
			Thread.sleep(t);
		}
		catch (InterruptedException e)
		{}
	}

	void setRemainText()
	{
		Object[] o=msgque.toArray();
		StringBuilder sb=new StringBuilder("");
		for (int i=0;i <= o.length - 1;i++)
		{
			sb.append(o[i]);
		}
		remainText = sb.toString();
	}

}


class BatteryReceiver extends BroadcastReceiver
{
	private TextView pow;

	public BatteryReceiver(TextView pow)
	{
		this.pow = pow;
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		int current = intent.getExtras().getInt("level");// 获得当前电量
		int total = intent.getExtras().getInt("scale");// 获得总电量
		int percent = current * 100 / total;
		pow.setText(percent + "%");
	}
}
