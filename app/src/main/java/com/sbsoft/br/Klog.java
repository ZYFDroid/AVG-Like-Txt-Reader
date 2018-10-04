package com.sbsoft.br;

public class Klog
{
	public static String log="";
	public static void v(Object ... s){
		String sb="";
		if(s.length!=0){
			for(int i=0;i<s.length;i++){
				sb=sb+"\n"+s[i].toString();
			}
		log=log+sb.trim()+"\n";
		}
		if(log.length()>1600){log=log.subSequence(log.length()-1600,log.length()).toString();}
	}
}
