package com.example;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;



@Controller
@SpringBootApplication
public class Main {

	@Value("${spring.datasource.url}")
	private String dbUrl;

	@Autowired
	private DataSource dataSource;

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }
  
  @RequestMapping(value = "login", method = RequestMethod.POST) 
  public @ResponseBody User login(HttpServletRequest request, HttpServletResponse res) throws IOException {
      try (Connection connection = dataSource.getConnection()) {
          Statement stmt = connection.createStatement();
          String ID = request.getParameter("UserID");
          String PW = request.getParameter("Password");
          ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE userid=" + ID + " AND password=" + PW);         
          User output =  null;
          while (rs.next()) {
            output=new User(rs.getString("UserID"), rs.getString("Name"), rs.getString("Email"),rs.getString("Password"), rs.getBoolean("Admin"));
          }       
          return output;
      }catch (Exception e) {
    	  return null;
      }
  }

  @RequestMapping(value = "getvotes", method = RequestMethod.POST)
  public @ResponseBody ArrayList<Voting> getvotes(HttpServletRequest request, HttpServletResponse res) throws IOException {
      try (Connection connection = dataSource.getConnection()) {
          Statement stmt = connection.createStatement();               
          String ID = request.getParameter("UserID");
          ArrayList<Voting> output = new ArrayList<Voting>();          
          ResultSet rs = stmt.executeQuery("SELECT * FROM voting WHERE votenum IN(SELECT votenum FROM allowedtovote WHERE userid=" + ID +"AND voted=false)");                               
          while (rs.next()) {
    	  	output.add(new Voting(rs.getInt("votenum"), rs.getString("start"), rs.getString("finish"), rs.getString("votename"), rs.getString("votedescription")));	               	  
          }
          return output;
      }catch (Exception e) {
    	  return null;
      }
  }
    
  
  @RequestMapping(value = "getcandidates", method = RequestMethod.POST)
  public @ResponseBody ArrayList<Candidate> getcandidates(HttpServletRequest request, HttpServletResponse res) throws IOException {
      try (Connection connection = dataSource.getConnection()) {
          Statement stmt = connection.createStatement();               
          String VoteNum = request.getParameter("VoteNum");
          ArrayList<Candidate> output = new ArrayList<Candidate>();          
          ResultSet rs = stmt.executeQuery("SELECT * FROM candidate WHERE votenum=" + VoteNum);                               
          while (rs.next()) {
    	  	output.add(new Candidate(rs.getInt("votenum"), rs.getInt("candidateid"), rs.getString("candidatename")));	               	  
          }
          return output;
      }catch (Exception e) {
    	  return null;
      }
  }
  
  @RequestMapping(value = "getcandidates2", method = RequestMethod.POST)
  public @ResponseBody ArrayList<Candidate> getcandidates2(HttpServletRequest request, HttpServletResponse res) throws IOException {
      try (Connection connection = dataSource.getConnection()) {
          Statement stmt = connection.createStatement();               
          String VoteNum = request.getParameter("VoteNum");
          ArrayList<Candidate> output = new ArrayList<Candidate>();          
          ResultSet rs = stmt.executeQuery("SELECT * FROM candidate WHERE votenum=" + VoteNum);                               
          while (rs.next()) {
    	  	output.add(new Candidate(rs.getInt("votenum"), rs.getInt("candidateid"), rs.getString("candidatename")));	               	  
          }
          return output;
      }catch (Exception e) {
    	  return null;
      }
  }
  
  
  @RequestMapping(value = "uservoted", method = RequestMethod.POST)
  public @ResponseBody boolean uservoted(HttpServletRequest request, HttpServletResponse res) throws IOException {
      try (Connection connection = dataSource.getConnection()) {
          Statement stmt = connection.createStatement();               
          String ID = request.getParameter("UserID");
          String votenum = request.getParameter("VoteNum");
          int count = stmt.executeUpdate("UPDATE allowedtovote SET voted=true WHERE userid=" + ID + "AND votenum=" + votenum +" AND voted=false");         
          if(count > 0)          	  
          {        	    
        	Statement stmt2 = connection.createStatement();
	        ResultSet rs = stmt2.executeQuery("SELECT * FROM users WHERE userid=" + ID);         
	        String Email = null;
	        String Name = null;
	        while (rs.next()) {
	        	Email = rs.getString("Email");
	        	Name = rs.getString("Name");
	        }  
	        if(Email != null)
	        {
	      	  Email EmailObj = new Email();
	      	  EmailObj.send(Name, Email);
	      	  return true;
	        }   	 
          }
        	  
          return false;
      }catch (Exception e) {
    	  return false;
      }

  }
  
  @RequestMapping(value = "uservotedreset", method = RequestMethod.POST)
  public @ResponseBody boolean uservotedreset(HttpServletRequest request, HttpServletResponse res) throws IOException {
      try (Connection connection = dataSource.getConnection()) {
          Statement stmt = connection.createStatement(); 
          String ID = request.getParameter("UserID");
          String votenum = request.getParameter("VoteNum");
          int count = stmt.executeUpdate("UPDATE allowedtovote SET voted=false WHERE userid=" + ID + "AND votenum=" + votenum +" AND voted=true");                       	          
          if(count > 0)
        	  return true;
          return false;
      }catch (Exception e) {
    	  return false;
      }

  }
  
  @RequestMapping(value = "sendballot", method = RequestMethod.POST)
  public @ResponseBody boolean sendballot(HttpServletRequest request, HttpServletResponse res) throws IOException {
      try (Connection connection = dataSource.getConnection()) {
          Statement stmt = connection.createStatement();               
          String Candidate_ID = request.getParameter("CandidateID");
          String votenum = request.getParameter("VoteNum");
          String key = request.getParameter("VoteKey");
         
          int count = stmt.executeUpdate("INSERT INTO ballot VALUES (" + votenum + ", " + key + ", " + Candidate_ID + ")");         
          if(count > 0)
        	  return true;
          return false;
      }catch (Exception e) {
    	  return false;
      }
  }
  
  @RequestMapping(value = "adduser", method = RequestMethod.POST)
  public @ResponseBody boolean adduser(HttpServletRequest request, HttpServletResponse res) throws IOException {
      try (Connection connection = dataSource.getConnection()) {
          Statement stmt = connection.createStatement();  
             
          String UserID = request.getParameter("UserID");
          String Name = request.getParameter("Name");
          String Email = request.getParameter("Email");
          String Password = request.getParameter("Password");     
         
          int count = stmt.executeUpdate("INSERT INTO users VALUES (" + UserID + ", " + Email + ", " + Password + ", false, " + Name +")");         
          if(count > 0)
        	  return true;
          return false;
      }catch (Exception e) {
    	  return false;
      }
  }
  

  
  @RequestMapping(value = "getallvotes", method = RequestMethod.POST)
  public @ResponseBody ArrayList<Voting> getallvotes(HttpServletRequest request, HttpServletResponse res) throws IOException {
      try (Connection connection = dataSource.getConnection()) {
    	  
          Statement stmt = connection.createStatement();
          String ID = request.getParameter("UserID");
          ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE userid=" + ID);    
    	  boolean Admin = false;
          while (rs.next()) {
    	  	Admin = rs.getBoolean("Admin");            	  
          }
          if(Admin == false)
        	  return null;
          Statement stmt2 = connection.createStatement();               
          ArrayList<Voting> output = new ArrayList<Voting>();          
          ResultSet rs2 = stmt2.executeQuery("SELECT * FROM voting");                               
          while (rs2.next()) {
    	  	output.add(new Voting(rs2.getInt("votenum"), rs2.getString("start"), rs2.getString("finish"), rs2.getString("votename"), rs2.getString("votedescription")));	               	  
          }
          return output;
      }catch (Exception e) {
    	  return null;
      }
  }
  
  
  @RequestMapping(value = "results", method = RequestMethod.POST)
  public @ResponseBody ArrayList<Result> results(HttpServletRequest request, HttpServletResponse res) throws IOException {
      try (Connection connection = dataSource.getConnection()) {
          Statement stmt = connection.createStatement();     
          String VoteNum = request.getParameter("VoteNum");
          ArrayList<Result> output = new ArrayList<Result>();
          ResultSet rs = stmt.executeQuery("SELECT b.candidatename, COUNT(a.candidateid) "
          		+ "FROM ballot a, candidate b "
          		+ "WHERE a.votenum=" + VoteNum + "AND a.votenum=b.votenum AND a.candidateid=b.candidateid "
          		+ "GROUP BY a.candidateid,b.candidatename");  
          
          while (rs.next()) {
        	  output.add(new Result(rs.getString("candidatename"), String.valueOf(rs.getInt("count"))));	
          }
          return output;
      }catch (Exception e) {
    	  return null;
      }
  }
  
 
  @RequestMapping(value = "removeuser", method = RequestMethod.POST)
  public @ResponseBody boolean removeuser(HttpServletRequest request, HttpServletResponse res) throws IOException {
      try (Connection connection = dataSource.getConnection()) {
          Statement stmt = connection.createStatement();  
             
          String UserID = request.getParameter("UserID");     
                               
          stmt.executeUpdate("DELETE FROM allowedtovote WHERE UserID=" + UserID); 
          stmt = connection.createStatement();  
          int count = stmt.executeUpdate("DELETE FROM users WHERE UserID=" + UserID);         
          if(count > 0)
        	  return true;
          return false;
      }catch (Exception e) {
    	  return false;
      }
  }
  
  @RequestMapping(value = "removevoting", method = RequestMethod.POST)
  public @ResponseBody boolean removevoting(HttpServletRequest request, HttpServletResponse res) throws IOException {
      try (Connection connection = dataSource.getConnection()) {
          String VoteNum = request.getParameter("VoteNum");  
          
          Statement stmt = connection.createStatement();                       
          stmt.executeUpdate("DELETE FROM allowedtovote WHERE VoteNum=" + VoteNum); 
          
          stmt = connection.createStatement();                       
          stmt.executeUpdate("DELETE FROM candidate WHERE VoteNum=" + VoteNum); 
          
          stmt = connection.createStatement();                       
          stmt.executeUpdate("DELETE FROM ballot WHERE VoteNum=" + VoteNum); 
          
          stmt = connection.createStatement();        
          int count = stmt.executeUpdate("DELETE FROM voting WHERE VoteNum=" + VoteNum);            
          if(count > 0)
        	  return true;
          return false;
      }catch (Exception e) {
    	  return false;
      }
  }
  

  @RequestMapping(value = "addcandidate", method = RequestMethod.POST)
  public @ResponseBody boolean addcandidate(HttpServletRequest request, HttpServletResponse res) throws IOException {
      try (Connection connection = dataSource.getConnection()) {
          Statement stmt = connection.createStatement();  
             
          String VoteNum = request.getParameter("VoteNum");
          String CandidateID = request.getParameter("CandidateID");
          String CandidateName = request.getParameter("CandidateName");
          
          int count = stmt.executeUpdate("INSERT INTO candidate VALUES (" + VoteNum + ", " + CandidateID + ", " + CandidateName + ")");         
          if(count > 0)
        	  return true;
          return false;
      }catch (Exception e) {
    	  return false;
      }
  }
  
  @RequestMapping(value = "addvoting", method = RequestMethod.POST)
  public @ResponseBody String addvoting(HttpServletRequest request, HttpServletResponse res) throws IOException {
      try (Connection connection = dataSource.getConnection()) {
    	  
          Statement get_votenum = connection.createStatement();
          ResultSet result = get_votenum.executeQuery("SELECT MAX(votenum) FROM voting");
          int Num = 0;
          while (result.next()) 
          {
        	  Num = result.getInt("max");
          }
          Num++;
          
          String VoteNum = String.valueOf(Num);
          String Start = request.getParameter("Start");
          String Finish = request.getParameter("Finish");
          String VoteName = request.getParameter("VoteName");
          String VoteDescription = request.getParameter("VoteDescription");
          
          Statement stmt = connection.createStatement();  
          
          int count = stmt.executeUpdate("INSERT INTO voting VALUES (" + VoteNum + ", " + Start + ", " + Finish + ", " + VoteName + ", "+ VoteDescription + ")");         
          if(count <= 0)
        	  return "false 1";
          
          Statement stmt2 = connection.createStatement();                      
          ResultSet rs = stmt2.executeQuery("SELECT * FROM users");
          while (rs.next()) {
        	  String UserID = rs.getString("UserID");
        	  Statement temp = connection.createStatement();  
        	  int count2 = temp.executeUpdate("INSERT INTO allowedtovote VALUES (" + UserID + ", " + VoteNum +", false )");
              if(count2 <= 0)
            	  return "false 2";
            } 
             
        	  return VoteNum;
       
      }catch (Exception e) {
    	  return e.getMessage();
      }
  }


  
  @RequestMapping("/db")
  String db(Map<String, Object> model) {
      try (Connection connection = dataSource.getConnection()) {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
        stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
        ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

        ArrayList<String> output = new ArrayList<String>();
        while (rs.next()) {
          output.add("Read from DB: " + rs.getTimestamp("tick"));
        }

        model.put("records", output);
        return "db";
      } catch (Exception e) {
        model.put("message", e.getMessage());
        return "error";
      }
  }

@RequestMapping(value = "logine", method = RequestMethod.POST) 
  public @ResponseBody User logine(HttpServletRequest request, HttpServletResponse res) throws IOException {
	  
	  
      try (Connection connection = dataSource.getConnection()) {
          Statement stmt = connection.createStatement();
          String ID = AES.decrypt(request.getParameter("UserID"), "");
          String PW = AES.decrypt(request.getParameter("Password"), "");
          ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE userid=" + ID + " AND password=" + PW);         
          User output =  null;
          while (rs.next()) {
            output=new User(rs.getString("UserID"), rs.getString("Name"), rs.getString("Email"),rs.getString("Password"), rs.getBoolean("Admin"));
          }       
          return output;
      }catch (Exception e) {
    	  return null;
      }
  }
  

 

  //********************************************** BASIC FUNCTIONS **********************************************


  @RequestMapping("/")
  String index() {
    return "index";
  }

  @Bean
  public DataSource dataSource() throws SQLException {
      if (dbUrl == null || dbUrl.isEmpty()) {
        return new HikariDataSource();
      } else {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        return new HikariDataSource(config);
      }
  }
  
}
