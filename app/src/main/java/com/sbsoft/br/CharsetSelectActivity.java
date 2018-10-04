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

public class CharsetSelectActivity extends Activity
{
	TextView charsetText;
String curCharset="GBK";
	public void initUi(){
		charsetText=(TextView)findViewById(R.id.charsetText);
	}
	public void crt(View p1){
		//TODO:Implements this method.
		curCharset=((Button)p1).getText().toString();
		loadText();
	}

	public void conf(View p1){
		//TODO:Implements this method.
		Ut.savestate(this,"charset",curCharset);
		startActivity(new Intent(this,MainActivity.class));
		finish();
	}


	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.charset);
		initUi();
		loadText();
	}
	String getFileCharset(){return curCharset;}
	void loadText(){
StringBuilder s=new StringBuilder("");
		try
		{
			char[] c=new char[1];
			FileInputStream fis=new FileInputStream(ReadActivity.readPath);
			InputStreamReader isr=new InputStreamReader(fis,getFileCharset());
			BufferedReader bfr=new BufferedReader(isr);
			for(int i=0;i<=500;i++){
				bfr.read(c);
				s.append(c[0]);
			}
			fis.close();
			isr.close();
			bfr.close();
		}
		catch (FileNotFoundException e)
		{}
		catch (UnsupportedEncodingException e)
		{}
		catch (IOException e)
		{}
charsetText.setText(s.toString());
	}
}
