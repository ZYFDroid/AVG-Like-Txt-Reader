package com.sbsoft.br;

import android.widget.*;
import android.view.*;
import android.app.*;
import android.os.*;
import android.content.*;
import android.media.*;
import java.io.*;
import android.content.res.*;
import android.view.Display.*;
import java.util.*;

public final class Ut
{
public static void tw(Context ctx,Object text)
{
Toast.makeText(ctx,text.toString(),Toast.LENGTH_LONG).show();
}
	public static void savestate(Context ctx,String key,String value){
		ctx.getSharedPreferences("preference",ctx.MODE_PRIVATE).edit().putString(key,value).commit();
		
	}
	public static String loadstate(Context ctx,String key,String defval){
		return ctx.getSharedPreferences("preference",ctx.MODE_PRIVATE).getString(key,defval);
	}
static String t;
static Activity v;
	public static void err(Activity ctx,String text){
		t=text;
		v=ctx;
		ctx.runOnUiThread(new Runnable(){

				@Override
				public void run()
				{
					// TODO: Implement this method
		AlertDialog.Builder adb=new AlertDialog.Builder(v);
		adb.setTitle("错误");
		adb.setMessage(t);
					adb.setPositiveButton("确定", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								// TODO: Implement this method
								v.startActivity(new Intent(v,MainActivity.class));
								v.finish();
								v=null;
							}
						});
						adb.setCancelable(false);
		adb.create().show();
		}});
	}
	public static void copyFileUsingFileStreams(File source, File dest)
	throws IOException {    
		InputStream input = null;    
		OutputStream output = null;    
		try {
			input = new FileInputStream(source);
			output = new FileOutputStream(dest);        
			byte[] buf = new byte[1024];        
			int bytesRead;        
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} finally {
			input.close();
			output.close();
		}
	}
	
	public Set<String> getStringArray(Context ctx,String nam){
		return ctx.getSharedPreferences("preference",ctx.MODE_PRIVATE).getStringSet(nam,null);
	}
	public void setStringArray(Context ctx,String nam,Set<String> data){
		ctx.getSharedPreferences("preference",ctx.MODE_PRIVATE).edit().putStringSet(nam,data).commit();
	}
}
