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
//import info.monitorenter.cpdetector.io.*;
public class MainActivity extends Activity
{
	public static final String chapterMatchRegExp=".*((第(\\s|一|二|三|四|五|六|七|八|九|十|百|千|万|1|2|3|4|5|6|7|8|9|0)*(章|节|集|季|回|话))|(正文)|(后话)|(后记)|(尾声)|(前言)|(大结局)).*";
	public ArrayList<ChapterInfo> chapters;
	TextView titleText;
	TextView tapScreenText;
	LinearLayout mainTwoBtn;
	LinearLayout clistPannel;
	ListView chapterList;
Button selBook;
	public void initUi(){
		titleText=(TextView)findViewById(R.id.titleText);
		tapScreenText=(TextView)findViewById(R.id.tapScreenText);
		mainTwoBtn=(LinearLayout)findViewById(R.id.mainTwoBtn);
		clistPannel=(LinearLayout)findViewById(R.id.clistPannel);
		chapterList=(ListView)findViewById(R.id.chapterList);
		selBook=(Button)findViewById(R.id.swbook);
		selBook.setVisibility(View.GONE);
	}
	public void onContinueClick(View p1){
		//TODO:Implements this method.
		startActivity(new Intent(this,ReadActivity.class));
		finish();
	}

	public void onChapterSelectClick(View p1){
		//TODO:Implements this method.
		if(chapters.size()>0){
			mainTwoBtn.setVisibility(View.GONE);
			clistPannel.setVisibility(View.VISIBLE);
		}
	}

	public void closeChapter(View p1){
		//TODO:Implements this method.
		onBackPressed();
		
	}

	public void chooseBook(View p1){
		//TODO:Implements this method.
		startActivity(new Intent(this,AddActivity.class));
		finish();
	}

public void onTap(View p1){
	if(tapScreenText.getVisibility()==View.VISIBLE){
		tapScreenText.setVisibility(View.GONE);
		selBook.setVisibility(View.GONE);
		mainTwoBtn.setVisibility(View.VISIBLE);
	}
}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		atyStarted=false;
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		onHide();
		
		setContentView(R.layout.main);
		initUi();
		if(android.os.Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
			int i = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
			             // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
			             if ((i != PackageManager.PERMISSION_GRANTED)||Ut.loadstate(this,"sdperm","").isEmpty()) {
			requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},233);
			}else{createActivity();}
		}
		else{
			createActivity();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		// TODO: Implement this method
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(requestCode==233){
			if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
				Ut.savestate(this,"sdperm","true");
				createActivity();
			}else{
				Ut.tw(this,"存储读取失败");
			}
		}
	}
		
		
		
		
		
		
		public void createActivity(){
		
		if(Ut.loadstate(this,"book","").isEmpty()||!new File(Ut.loadstate(this,"book","")).exists()){
			startActivity(new Intent(this,AddActivity.class));
			finish();
			return;
		}
		ReadActivity.readPath=Ut.loadstate(this,"book","");
		
		View readingBg=findViewById(R.id.mainBg);
		//findViewById(R.id.mainBg).setBackgroundResource((Integer.valueOf(Ut.loadstate(this,"bg",String.valueOf(R.drawable.read_bg_1)))));
		try{
			readingBg.setBackgroundResource(Integer.valueOf(Ut.loadstate(this,"bg",String.valueOf(R.drawable.read_bg_1))));
		}catch(NumberFormatException e){
			String path="";
			if(new File(path=Ut.loadstate(this,"bg","")).exists()){
				readingBg.setBackgroundDrawable(Drawable.createFromPath(path));
			}else{
				saveint("bg",R.drawable.read_bg_1);
				readingBg.setBackgroundResource(R.drawable.read_bg_1);
			}
		}
		
		
		
		titleText.setText(Ut.loadstate(this,"bookname","Undefinded"));
		titleText.setVisibility(View.VISIBLE);
	}
public void onUiPrepared(){
	new Thread(new Runnable(){

			@Override
			public void run()
			{
				// TODO: Implement this method
				ReadActivity.dataArr=new ArrayList<String>();
				try
				{
					BufferedReader bfr=new BufferedReader(new InputStreamReader(new FileInputStream(ReadActivity.readPath),getFileCharset(new File(ReadActivity.readPath))));
					String s="";
					while((s=bfr.readLine())!=null){
						if(!s.trim().isEmpty())
							ReadActivity.dataArr.add(s.trim());
					}
				}
				catch (Exception e)
				{runOnUiThread(new Runnable(){
							@Override
							public void run()
							{
								startActivity(new Intent(MainActivity.this,AddActivity.class));
								finish();
							}});}
					//loadChapters		
					/*
					PrintStream prt=System.out;
				try
				{
					prt=new PrintStream("/storage/emulated/0/cha.txt");
				}
				catch (FileNotFoundException e)
				{}*/
				chapters = new ArrayList<ChapterInfo>();
				chapters.add(new ChapterInfo(0,"重新开始"));
							for(int i=0;i<ReadActivity.dataArr.size();i++){
								if(ReadActivity.dataArr.get(i).length()<50){
									String s1=ReadActivity.dataArr.get(i);
									if(s1.matches(chapterMatchRegExp)){
										ChapterInfo CPI=(new ChapterInfo(i,s1));
											chapters.add(CPI);
											//prt.println(CPI);
									}
								}
							}
		//
		
				Runnable r=new Runnable(){

					@Override
					public void run()
					{
						// TODO: Implement this method
						titleText.setTextColor(local_bgalpha*0x1000000+0xffffff);
						titleText.setBackgroundColor(local_bgalpha*0x1000000);
						if(local_bgalpha==0){
							titleText.setVisibility(View.GONE);
							tapScreenText.setVisibility(View.VISIBLE);
							ArrayAdapter<Object> arp=new ArrayAdapter<Object>(MainActivity.this,R.layout.apd,R.id.emmm,chapters.toArray());
							chapterList.setAdapter(arp);
							selBook.setVisibility(View.VISIBLE);
							chapterList.setOnItemLongClickListener(new OnItemLongClickListener(){

									@Override
									public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4)
									{
										// TODO: Implement this method
										savefromChapter(chapters.get((int)p4));
										startActivity(new Intent(MainActivity.this,ReadActivity.class));
										finish();
										return true;
									}
								});
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
					local_bgalpha=i;
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
	if(tapScreenText.getVisibility()==View.VISIBLE){
		finish();
	}
	if(mainTwoBtn.getVisibility()==View.VISIBLE){
		mainTwoBtn.setVisibility(View.GONE);
		tapScreenText.setVisibility(View.VISIBLE);
		selBook.setVisibility(View.VISIBLE);
	}
	if(clistPannel.getVisibility()==View.VISIBLE){
		clistPannel.setVisibility(View.GONE);
		mainTwoBtn.setVisibility(View.VISIBLE);
	}
}




	public static int readslot=0;
	public void cleansave(){

		saveint("readPos" +readslot,0);
		Ut.savestate(this,"readedText"+readslot,"");
		Ut.savestate(this,"remainText"+readslot,"");
	}


	void saveint(String k,int v){
		Ut.savestate(this,k,String.valueOf(v));
	}
	public void savefromChapter(ChapterInfo cpi){
if(cpi.getChapterIndex()==1){cleansave();return;}
		saveint("readPos" +readslot,cpi.chapterPosition);
		Ut.savestate(this,"readedText"+readslot,"");
		Ut.savestate(this,"remainText"+readslot,"");
	}


boolean atyStarted=false;
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		// TODO: Implement this method
		super.onWindowFocusChanged(hasFocus);
		if(!atyStarted){atyStarted=true;onUiPrepared();}
		if(hasFocus){
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

	
	public void onHide() {
		//4.1及以上通用flags组合
		int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;


		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().getDecorView().setSystemUiVisibility(
                flags | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
			);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			getWindow().getDecorView().setSystemUiVisibility(flags);
		}
	}
	
	
	public static String toHex(byte[] byteArray) {

		int i;

		StringBuffer buf = new StringBuffer("");

		int len = byteArray.length;

		for (int offset = 0; offset < len; offset++) {

			i = byteArray[offset];

			if (i < 0)

				i += 256;

			if (i < 16)

				buf.append("0");

			buf.append(Integer.toHexString(i).replace("0x",""));

		}

		return buf.toString().toUpperCase();

	}

	public String getFileCharset(File fileName){
return Ut.loadstate(this,"charset","GBK");

	}
	
	
	
}
class ChapterInfo{
	int chapterPosition;
	String ChapterTitle;
public ChapterInfo(int pos,String tit){
	ChapterTitle=tit;
	chapterPosition=pos;
}
public int getChapterIndex(){
	try{
	String s=ChapterTitle.split("第")[1].split("(章|节|集|季|回|话)")[0].trim();
	int i;
	try{
		i=Integer.valueOf(s);
	}catch(NumberFormatException e){
		i=chineseNumber2Int(s);
	}
	return i;
	}catch(Exception e){
		return -1;
	}
}
	@Override
	public String toString()
	{
		// TODO: Implement this method
		return ChapterTitle;
	}
	@SuppressWarnings("unused")
    private static int chineseNumber2Int(String chineseNumber){
        int result = 0;
        int temp = 1;//存放一个单位的数字如：十万
        int count = 0;//判断是否有chArr
		char lastChar=' ';
        char[] cnArr = new char[]{'一','二','三','四','五','六','七','八','九'};
        char[] chArr = new char[]{'十','百','千','万','亿'};
        for (int i = 0; i < chineseNumber.length(); i++) {
            boolean b = true;//判断是否是chArr
            char c = chineseNumber.charAt(i);
            for (int j = 0; j < cnArr.length; j++) {//非单位，即数字
                if (c == cnArr[j]) {
                    if(0 != count){//添加下一个单位之前，先把上一个单位值添加到结果中
                        result += temp;
                        temp = 1;
                        count = 0;
                    }
                    // 下标+1，就是对应的值
                    temp = j + 1;
                    b = false;
                    break;
                }
            }
            if(b){//单位{'十','百','千','万','亿'}
                for (int j = 0; j < chArr.length; j++) {
                    if (c == chArr[j]) {
                        switch (j) {
							case 0:

								if(lastChar!='百'&&lastChar!='千'&&lastChar!='万'&&lastChar!='亿')
								{
									temp *= 10;
								}else{result+=10;}
								//temp *= 10;
								break;
							case 1:

								if(lastChar!='千'&&lastChar!='万'&&lastChar!='亿')
								{
									temp *= 100;
								}else{result+=100;}
								//temp *= 100;
								break;
							case 2:

								if(lastChar!='万'&&lastChar!='亿')
								{
									temp *= 1000;
								}else{result+=1000;}
								//temp *= 1000;
								break;
							case 3:


								temp *= 10000;
								break;
							case 4:
								temp *= 100000000;
								break;
							default:
								break;
                        }
                        count++;
                    }
                }
            }
			lastChar=c;
            if (i == chineseNumber.length() - 1) {//遍历到最后一个字符
                result += temp;
            }
        }
        return result;
    }
}
