package com.example;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
public class Email {
	
	   public void send(String name, String to) {    

	        final String username = "votingsystem.sce@gmail.com";
	        final String password = "Aa123456Aa";

	        Properties props = new Properties();
	        props.put("mail.smtp.starttls.enable", "true");
	        props.put("mail.smtp.auth", "true");
	        props.put("mail.smtp.host", "smtp.gmail.com");
	        props.put("mail.smtp.port", "587");

	        Session session = Session.getInstance(props,
	          new javax.mail.Authenticator() {
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(username, password);
	            }
	          });

	        try {

	            Message message = new MimeMessage(session);
	            message.setFrom(new InternetAddress("votingsystem.sce@gmail.com"));
	            message.setRecipients(Message.RecipientType.TO,
	                InternetAddress.parse(to));
	            message.setSubject("Thank you for your vote");
	            message.setText("Dear " + name + "\n your vote was successfully submitted \n thank you for voting");

	            Transport.send(message);

	            //System.out.println("Done");

	        } catch (MessagingException e) {
	            throw new RuntimeException(e);
	        }
	    }
	   
}




