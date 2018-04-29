package uk.ac.le.qx16.pp.util;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.json.JSONObject;

public final class MyUtil implements ServletContextListener{
	
	public static boolean judgeEmptyString(String test){
		boolean flag = false;
		if(test==null||"".equals(test.trim()))
			flag = true;
		return flag;
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	private static String current_path;
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		current_path = arg0.getServletContext().getRealPath("");
		//System.out.println(MyUtil.class.getClassLoader().getResource(""));
		//System.out.println(current_path);
	}
	public static String getCurrentPath(){
		return current_path;
	}
	public static JSONObject readerToJson(BufferedReader reader) throws IOException{
		StringBuffer content = new StringBuffer("");
		String line;
		while((line=reader.readLine())!=null) content.append(line);
		reader.close();
		return new JSONObject(content.toString());
	}
}
