package uk.ac.le.qx16.pp.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public final class MyUtil implements ServletContextListener{
	
	public static boolean judgeEmptyString(String test){
		boolean flag = false;
		if(test==null||"".equals(test.trim()))
			flag = true;
		return flag;
	}
	
	public static String preprocessText(String text){
		
/*		int a;
		a = 0;*/
		return "";
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
		//System.out.println(current_path);
	}
	public static String getCurrentPath(){
		return current_path;
	}
}
