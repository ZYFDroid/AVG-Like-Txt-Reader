package com.sbsoft.br;
import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import android.widget.AdapterView.*;
import android.content.*;
public class AddActivity extends Activity
{
	ListView addListView;
	EditText addEditText;
	FrameLayout loading;
ArrayList<String> bookTitle;
ArrayList<String> bookPath;
	public void initUi(){
		addListView=(ListView)findViewById(R.id.addListView);
		addEditText=(EditText)findViewById(R.id.addEditText);
		loading=(FrameLayout)findViewById(R.id.loading);
	}
	EditText edt;
	public void onConfirm(View p1){
		if(new File(addEditText.getText().toString()).exists()){
		//TODO:Implements this method.
		AlertDialog.Builder adb=new AlertDialog.Builder(this);
		adb.setTitle("请输入电子书实际标题");
	    edt=new EditText(this);
		edt.setText(new File(addEditText.getText().toString()).getName().replace(".txt","").replace(".TXT",""));
		adb.setView(edt);
		adb.setNegativeButton("取消",null);
			adb.setPositiveButton("就决定是它了", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						String internalFileName=getFilesDir().getPath()+"/book.txt";
						
						try
						{
							Ut.copyFileUsingFileStreams(new File(addEditText.getText().toString()), new File(internalFileName));
						}
						catch (IOException e)
						{
							Ut.tw(AddActivity. this,"无法读取电子书:"+e.getMessage());
						}

						Ut.savestate(AddActivity.this,"book",internalFileName);
						Ut.savestate(AddActivity.this,"bookname",edt.getText().toString());
						cleansave();
						ReadActivity.readPath=internalFileName;
						startActivity(new Intent(AddActivity.this,CharsetSelectActivity.class));
						finish();
					}
				});
				adb.create().show();
		}else{
			Ut.tw(this,"电子书路径无效");
		}
	}
	public static int readslot=0;
	public void cleansave(){
		saveint("readPos" +readslot,0);
		Ut.savestate(this,"readedText"+readslot,"");
		Ut.savestate(this,"remainText"+readslot,"");
		Ut.savestate(this,"chapters","");
	}
	

	void saveint(String k,int v){
		Ut.savestate(this,k,String.valueOf(v));
	}
Thread t;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add);
		initUi();
		loading.setVisibility(View.VISIBLE);
		setTitle("选择TXT");
		(t = new Thread(new Runnable(){

				@Override
				public void run()
				{
					bookPath=new ArrayList<String>();
					bookTitle=new ArrayList<String>();
					ScanFolder(android.os.Environment.getExternalStorageDirectory().getAbsolutePath());
					runOnUiThread(new Runnable(){

							@Override
							public void run()
							{
								ArrayAdapter<Object> ard=new ArrayAdapter<Object>(AddActivity.this,android.R.layout.simple_list_item_1,bookTitle.toArray());
								addListView.setAdapter(ard);
								loading.setVisibility(View.GONE);
								addListView.setOnItemClickListener(new OnItemClickListener(){

										@Override
										public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
										{
											// TODO: Implement this method
											addEditText.setText(bookPath.get((int)p4));
										}
									});
							}
						});
				}
				
				public void ScanFolder(String path){
					File f=new File(path);
					if(f.isDirectory()){
						try{
							String[] l=f.list();
							for(int i=0;i<l.length;i++){
								if(new File(f.getAbsolutePath()+f.separator+l[i]).isDirectory()){

									//System.out.println(f.getAbsolutePath());
									//System.out.println(f.getAbsolutePath()+f.separator+(f.list())[i]);
									if(l[i].toUpperCase().contentEquals("ANDROID/DATA")){continue;}
									if(l[i].toUpperCase().contentEquals("XASH")){continue;}
									if(l[i].toUpperCase().contentEquals("MOBILEQQ")){continue;}
									if(l[i].toUpperCase().contentEquals("MICROMSG")){continue;}
									if(l[i].toUpperCase().contentEquals("ALIPAY")){continue;}
									if(l[i].toUpperCase().contains("CACHE")){continue;}
									if(l[i].toUpperCase().contains("THUMB")){continue;}
									if(l[i].toUpperCase().contains("TEMP")){continue;}
									if(l[i].toUpperCase().contains("TMP")){continue;}
									if(l[i].toUpperCase().contains("SDK")){continue;}
									if(l[i].toUpperCase().contains("PUSH")){continue;}
									if(l[i].toUpperCase().contains("LOG")){continue;}
									if(l[i].toUpperCase().contains("CSTRIKE")){continue;}
									if(l[i].toUpperCase().contains("VALVE")){continue;}
									if(l[i].toUpperCase().contains("DRAWABLE")){continue;}
									if(l[i].toUpperCase().contains("MIPMAP")){continue;}
									if(l[i].toUpperCase().contains("VALUES")){continue;}
									if(l[i].toUpperCase().contains("META-INF")){continue;}
									if(l[i].startsWith(".")){continue;}
									ScanFolder(f.getAbsolutePath()+f.separator+(l[i]));
								}else{
									if(l[i].toUpperCase().endsWith(".TXT")){
										if(new File(f.getAbsolutePath()+f.separator+(l[i])).length()>2048)
										AddtoLib(f.getAbsolutePath()+f.separator+(l[i]));
									}
								}
							}}catch(NullPointerException e){ return;}
					}
				}
				public void AddtoLib(String path){
					bookPath.add(path);
					bookTitle.add(new File(path).getName().replace(".txt","").replace(".TXT",""));
				}
			})).start();
	}
	
	
}
