package com.sbsoft.br;
import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.graphics.drawable.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import java.io.*;
import java.util.*;
import android.text.*;
//import info.monitorenter.cpdetector.io.*;
public class MainActivity extends Activity
{
	//public static final String chapterMatchRegExp=".*((第(\\s|一|二|三|四|五|六|七|八|九|十|百|千|万|1|2|3|4|5|6|7|8|9|0)*(章|节|集|季|回|话))|(正文)|(后话)|(后记)|(尾声)|(前言)|(大结局)).*";
	public static final String chapterMatchRegExp="^(((第|卷){0,}[零〇一二三四五六七八九十白千万亿0-9]{0,}(章|节|集|季|回|话){0,})|[(正文)(后话)(后记)(尾声)(前言)(大结局)])((?![(。)(，”)(？”)(！”)(—”)]).){0,20}((?![。;\\.，\\,\\\"\\!\\?？！…～、—\\:：“”;；\\)\\>〉]).){1,1}$";//"^(((第){0,}[零〇一二三四五六七八九十白千万亿0-9]{1,}(章|节|集|季|回|话){0,})|[(正文)(后话)(后记)(尾声)(前言)(大结局)])((?![(。)(，”)(？”)(！”)(—”)]).){0,20}$";

	TextView titleText;
	TextView tapScreenText;
	LinearLayout mainTwoBtn;
	LinearLayout clistPannel;
	ChpAdapter mChapterAdapter;
	ListView chapterList;
	Button selBook;
	public void initUi()
	{
		titleText = (TextView)findViewById(R.id.titleText);
		tapScreenText = (TextView)findViewById(R.id.tapScreenText);
		mainTwoBtn = (LinearLayout)findViewById(R.id.mainTwoBtn);
		clistPannel = (LinearLayout)findViewById(R.id.clistPannel);
		chapterList = (ListView)findViewById(R.id.chapterList);
		selBook = (Button)findViewById(R.id.swbook);
		selBook.setVisibility(View.GONE);
	}
	public void onContinueClick(View p1)
	{
		//TODO:Implements this method.
		startActivity(new Intent(this, ReadActivity.class));
		finish();
	}

	public void onChapterSelectClick(View p1)
	{
		mainTwoBtn.setVisibility(View.GONE);
		clistPannel.setVisibility(View.VISIBLE);
	}

	public void closeChapter(View p1)
	{
		onBackPressed();

	}

	public void chooseBook(View p1)
	{
		startActivity(new Intent(this, AddActivity.class));
		finish();
	}

	public void onTap(View p1)
	{
		if (tapScreenText.getVisibility() == View.VISIBLE)
		{
			tapScreenText.setVisibility(View.GONE);
			selBook.setVisibility(View.GONE);
			mainTwoBtn.setVisibility(View.VISIBLE);
		}
	}



	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		atyStarted = false;
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		onHide();

		setContentView(R.layout.main);
		initUi();
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			int i = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
			// 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
			if ((i != PackageManager.PERMISSION_GRANTED) || Ut.loadstate(this, "sdperm", "").isEmpty())
			{
				requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 233);
			}
			else
			{createActivity();}
		}
		else
		{
			createActivity();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		// TODO: Implement this method
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 233)
		{
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
			{
				Ut.savestate(this, "sdperm", "true");
				createActivity();
			}
			else
			{
				Ut.tw(this, "存储读取失败");
			}
		}
	}






	public void createActivity()
	{

		if (Ut.loadstate(this, "book", "").isEmpty() || !new File(Ut.loadstate(this, "book", "")).exists())
		{
			startActivity(new Intent(this, AddActivity.class));
			finish();
			return;
		}
		ReadActivity.readPath = Ut.loadstate(this, "book", "");

		View readingBg=findViewById(R.id.mainBg);
		//findViewById(R.id.mainBg).setBackgroundResource((Integer.valueOf(Ut.loadstate(this,"bg",String.valueOf(R.drawable.read_bg_1)))));
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



		titleText.setText(Ut.loadstate(this, "bookname", "Undefinded"));
		titleText.setVisibility(View.VISIBLE);
	}
	public void onUiPrepared()
	{
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					// TODO: Implement this method
					ReadActivity.dataArr = new ArrayList<String>();
					try
					{
						BufferedReader bfr=new BufferedReader(new InputStreamReader(new FileInputStream(ReadActivity.readPath), getFileCharset(new File(ReadActivity.readPath))));
						String s="";
						while ((s = bfr.readLine()) != null)
						{
							if (!s.trim().isEmpty())
								ReadActivity.dataArr.add(s.trim());
						}
					}
					catch (Exception e)
					{runOnUiThread(new Runnable(){
								@Override
								public void run()
								{
									startActivity(new Intent(MainActivity.this, AddActivity.class));
									finish();
								}
							}
						);
					}
					
					String chapterData=Ut.loadstate(MainActivity.this,"chapters","");
					if(chapterData.isEmpty()){
						scanChapter(ReadActivity.dataArr);
						chapterData=Ut.loadstate(MainActivity.this,"chapters","");
						if(chapterData.isEmpty()){
							mChapterAdapter=new ChpAdapter(MainActivity.this,new String[0]);
						}
						else
						{
							mChapterAdapter=new ChpAdapter(MainActivity.this,chapterData.split("\\|"));
						}
					}
					else
					{
						mChapterAdapter=new ChpAdapter(MainActivity.this,chapterData.split("\\|"));
					}
					//

					Runnable r=new Runnable(){

						@Override
						public void run()
						{
							// TODO: Implement this method
							titleText.setTextColor(local_bgalpha * 0x1000000 + 0xffffff);
							titleText.setBackgroundColor(local_bgalpha * 0x1000000);
							if (local_bgalpha == 0)
							{
								titleText.setVisibility(View.GONE);
								tapScreenText.setVisibility(View.VISIBLE);
								chapterList.setAdapter(mChapterAdapter);
								selBook.setVisibility(View.VISIBLE);


							}
						}
					};
					try
					{
						Thread.sleep(1000);
					}

					catch (InterruptedException e)
					{}
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

				}
			}).start();
	}
	int local_bgalpha=255;
	@Override
	public void onBackPressed()
	{
		if (tapScreenText.getVisibility() == View.VISIBLE)
		{
			finish();
		}
		if (mainTwoBtn.getVisibility() == View.VISIBLE)
		{
			mainTwoBtn.setVisibility(View.GONE);
			tapScreenText.setVisibility(View.VISIBLE);
			selBook.setVisibility(View.VISIBLE);
		}
		if (clistPannel.getVisibility() == View.VISIBLE)
		{
			clistPannel.setVisibility(View.GONE);
			mainTwoBtn.setVisibility(View.VISIBLE);
		}
	}


	void scanChapter(ArrayList<String> contents)
	{
		ArrayList<ChapterInfo> chapters=new ArrayList<>();
		for (int i=0;i < ReadActivity.dataArr.size();i++)
		{
			if (ReadActivity.dataArr.get(i).length() < 50)
			{
				String s1=ReadActivity.dataArr.get(i).replace("　", "  ").trim();
				if (s1.matches(chapterMatchRegExp))
				{
					ChapterInfo CPI=(new ChapterInfo(i, s1));
					chapters.add(CPI);
				}
			}
		}
		saveChapterResult(chapters);
	}

	void saveChapterResult(ArrayList<ChapterInfo> chapters){
		StringBuilder chapterData=new StringBuilder();
		for (int i=0;i < chapters.size();i++)
		{
			chapterData.append(chapters.get(i).toString());
			if (i < chapters.size() - 1)
			{chapterData.append("|");}
		}
		Ut.savestate(this, "chapters", chapterData.toString());
	}
	
	
	public static int readslot=0;
	public void cleansave()
	{

		saveint("readPos" + readslot, 0);
		Ut.savestate(this, "readedText" + readslot, "");
		Ut.savestate(this, "remainText" + readslot, "");
	}


	void saveint(String k, int v)
	{
		Ut.savestate(this, k, String.valueOf(v));
	}
	public void savefromChapter(ChapterInfo cpi)
	{
		saveint("readPos" + readslot, cpi.chapterPosition);
		Ut.savestate(this, "readedText" + readslot, "");
		Ut.savestate(this, "remainText" + readslot, "");
	}
	


	boolean atyStarted=false;
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		// TODO: Implement this method
		super.onWindowFocusChanged(hasFocus);
		if (!atyStarted)
		{atyStarted = true;onUiPrepared();}
		if (hasFocus)
		{
			onHide();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		// TODO: Implement this method
		onHide();
		super.onConfigurationChanged(newConfig);
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


	public static String toHex_(byte[] byteArray)
	{

		int i;

		StringBuffer buf = new StringBuffer("");

		int len = byteArray.length;

		for (int offset = 0; offset < len; offset++)
		{

			i = byteArray[offset];

			if (i < 0)

				i += 256;

			if (i < 16)

				buf.append("0");

			buf.append(Integer.toHexString(i).replace("0x", ""));

		}

		return buf.toString().toUpperCase();

	}

	public String getFileCharset(File fileName)
	{
		return Ut.loadstate(this, "charset", "GBK");

	}
	
	public void rescanChapter(View p1){
		new ConfirmDialog(this, "是否重新扫描章节？", "重新扫描章节"){
			@Override
			void onConfirm()
			{
				String chapterData;
				scanChapter(ReadActivity.dataArr);
				chapterData=Ut.loadstate(MainActivity.this,"chapters","");
				if(chapterData.isEmpty()){
					mChapterAdapter=new ChpAdapter(MainActivity.this,new String[0]);
				}
				else
				{
					mChapterAdapter=new ChpAdapter(MainActivity.this,chapterData.split("\\|"));
				}
				chapterList.setAdapter(mChapterAdapter);
				Ut.tw(MainActivity.this,"扫描完成");
			}
		};
	}


	class ChpAdapter extends BaseAdapter
	{
		ChapterInfo head=new ChapterInfo(0,"从头开始");
		ArrayList<ChapterInfo> chapters=new ArrayList<>();
		Context ctx;
		@Override
		public int getCount()
		{
			return chapters.size()+1;
		}

		@Override
		public Object getItem(int p1)
		{
			return chapters.get(p1-1);
		}

		@Override
		public long getItemId(int p1)
		{
			// TODO: Implement this method
			return p1;
		}

		
		
		@Override
		public View getView(int p1, View p2, ViewGroup p3)
		{
			View v=getLayoutInflater().inflate(R.layout.apd, null, false);
			ChapterInfo info;
			if(p1==0){
				info=head;
			}else{
				info=chapters.get(p1-1);
			}
			if (p1 < 1)
			{v.findViewById(R.id.btnRemoveChapter).setVisibility(View.GONE);}
			((TextView)v.findViewById(R.id.emmm)).setText(info.ChapterTitle);
			((ImageButton)v.findViewById(R.id.btnRemoveChapter)).setOnClickListener(new ChapterDeleteOnClick(p1-1));
			((ImageButton)v.findViewById(R.id.btnStartHere)).setOnClickListener(new ChapterStartOnClick(info));
			return v;
		}

		public ChpAdapter(Context ctx, String[] datas)
		{
			this.ctx = ctx;
			for (int i=0;i < datas.length;i++)
			{
				chapters.add(ChapterInfo.fromString(datas[i]));
			}
		}

		class ChapterDeleteOnClick implements View.OnClickListener
		{
			final int deleteposition;
			public ChapterDeleteOnClick(int position)
			{
				this.deleteposition = position;
			}
			@Override
			public void onClick(View p1)
			{
				new ConfirmDialog(ctx, "是否删除此章节", "删除章节"){
					@Override
					void onConfirm()
					{
						chapters.remove(deleteposition);
						saveChapterResult(chapters);
						notifyDataSetChanged();
					}
				};
			}
		}
		class ChapterStartOnClick implements View.OnClickListener
		{

			ChapterInfo startupPosition;

			public ChapterStartOnClick(ChapterInfo startupPosition)
			{
				this.startupPosition = startupPosition;
			}
			@Override
			public void onClick(View p1)
			{
				new ConfirmDialog(ctx, "是否从选中的章节继续？\n这将会覆盖当前存档", "继续阅读"){
					@Override
					void onConfirm()
					{
						savefromChapter(startupPosition);
						startActivity(new Intent(MainActivity.this, ReadActivity.class));
						finish();
					}
				};
			}

		}

	}
}
class ChapterInfo
{
	int chapterPosition;
	String ChapterTitle;
	public ChapterInfo(int pos, String tit)
	{
		ChapterTitle = tit;
		chapterPosition = pos;
	}
	@Override
	public String toString()
	{
		// TODO: Implement this method
		return chapterPosition + ":" + ChapterTitle.replace('|', '｜');
	}
	public static ChapterInfo fromString(String from)
	{
		String[] meta=from.split(":", 2);
		return new ChapterInfo(Integer.parseInt(meta[0]), meta[1]);
	}

}



abstract class ConfirmDialog implements DialogInterface.OnClickListener
{
	public ConfirmDialog(Context ctx, String msg, String title)
	{
		AlertDialog.Builder adb=new AlertDialog.Builder(ctx);
		adb.setMessage(msg).setTitle(title);
		adb.setCancelable(false).setNegativeButton(android.R.string.no, null);
		adb.setPositiveButton(android.R.string.yes, this);
		adb.create().show();
	}

	@Override
	public void onClick(DialogInterface p1, int p2)
	{
		onConfirm();
	}
	abstract void onConfirm();
}
