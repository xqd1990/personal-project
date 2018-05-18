package uk.ac.le.qx16.pp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
	
	private static final String SENDER_ADDRESS = "qiangde_xiang@163.com";
	private static final String SENDER_ACCOUNT = "qiangde_xiang@163.com";
	private static final String SENDER_PWD = "woai520520";
	private static final String HOST = "smtp.163.com";
	private static final Session SESSION;
	private static final Transport TRANSPORT;
	static{
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.transport.protocol", "smtp");
		properties.setProperty("mail.smtp.host", HOST);
		Session session = Session.getInstance(properties);
		Transport transport = null;
		try {
			transport = session.getTransport();
			transport.connect(SENDER_ACCOUNT, SENDER_PWD);
			System.out.println("Connect Successfully!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Connect Error!");
		}
		SESSION = session;
		TRANSPORT = transport;
	}
	public static void sentMail(String to, String password) throws AddressException, MessagingException{
		MimeMessage message = new MimeMessage(SESSION);
		message.setFrom(new InternetAddress(SENDER_ADDRESS));
		message.setRecipient(MimeMessage.RecipientType.TO,new InternetAddress(to));
		message.setSubject("Twitter Data: Get Password Back", "UTF-8");
		message.setContent("Your password is "+password, "text/html;charset=UTF-8");
		message.setSentDate(new Date());
		TRANSPORT.sendMessage(message, message.getAllRecipients());
	}
	
	public static void main(String[] args) throws Exception{
		MimeMessage message = new MimeMessage(SESSION);
		message.setFrom(new InternetAddress(SENDER_ADDRESS));
		message.setRecipient(MimeMessage.RecipientType.TO,new InternetAddress("qiangde_xiang@163.com"));
		message.setSubject("Twitter Data: Get Password Back", "UTF-8");
		message.setContent("IMPORTANT", "text/html;charset=UTF-8");
		message.setSentDate(new Date());
		TRANSPORT.sendMessage(message, message.getAllRecipients());
	}
}
