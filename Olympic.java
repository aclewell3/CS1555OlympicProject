import java.io.*;
import java.util.*;
import java.util.Date;
import java.text.*;
import java.sql.*;

public class Olympic {
    private static final String username = "alc261";
    private static final String password = "4117524";
    private static final String url = "jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass";
	Connection connection;
	String usernameEntry;
	
	public Olympic(){
		connection = null;
        try {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        } catch (Exception e) {
            System.out.println(
                    "Error connecting to database. Printing stack trace: ");
            e.printStackTrace();
        }
	}	

	public void login(int roleID, String usernameEntry, String passwordEntry){
		String loginQuery = "SELECT * FROM user_account WHERE username= '" + usernameEntry + "' and passkey='" + passwordEntry + "' and role_id='" + roleID + "'";
	
		try{
			Statement st = connection.createStatement();
			ResultSet rs = st.executeQuery(loginQuery);
			
			if(rs.next()){
				System.out.println("Login successful");
				this.usernameEntry = usernameEntry;
			}
			else{
				System.out.println("Login unsuccessful");
			}
		}
		catch(SQLException e){
			System.out.println(e.toString());
		}
	}
	
	public void createUser(String newUserName, String newPassword, int newRoleID){
		String curDate = "dd-MMM-yyyy";
		String dateAsString = new SimpleDateFormat(curDate).format(new Date());
	
		try {
			Statement st = connection.createStatement();
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			connection.setAutoCommit(false);
			st.executeUpdate("INSERT INTO USER_ACCOUNT(user_id,username,passkey,role_id,last_login) values(SEQ_USER_ACCOUNT.NEXTVAL,'" + newUserName + "','" + newPassword + "'," + newRoleID + ",'" + dateAsString + "')");
			connection.commit();
			System.out.println("User successfully created");
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		} catch (SQLException e1) {
			System.out.println(e1.toString());
			try {
				connection.rollback();
			} catch (SQLException e2) {
				System.out.println(e2.toString());
			}
		}
			
	}
	
	public void dropUser(int userIDtoDrop){
		try {
			Statement st = connection.createStatement();
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			connection.setAutoCommit(false);
			st.executeUpdate("DELETE FROM USER_ACCOUNT WHERE user_id=" + userIDtoDrop);
			connection.commit();
			System.out.println("User successfully deleted");
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		} catch (SQLException e1) {
			System.out.println(e1.toString());
			try {
				connection.rollback();
			} catch (SQLException e2) {
				System.out.println(e2.toString());
			}
		}
	}
	
	public void dropUser(String usernameToDrop){
		try {
			Statement st = connection.createStatement();
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);									
			connection.setAutoCommit(false);
			st.executeUpdate("DELETE FROM USER_ACCOUNT WHERE username='" + usernameToDrop + "'");
			connection.commit();
			System.out.println("User successfully deleted");
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		} catch (SQLException e1) {
			System.out.println(e1.toString());
			try {
				connection.rollback();
			} catch (SQLException e2) {
				System.out.println(e2.toString());
			}
		}
	}
	
	public void dropUser(){
		try {
			Statement st = connection.createStatement();
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);	
			connection.setAutoCommit(false);
			st.executeUpdate("DELETE FROM USER_ACCOUNT WHERE username='" + this.usernameEntry + "'");
			connection.commit();
			System.out.println("User successfully deleted");
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		} catch (SQLException e1) {
			System.out.println(e1.toString());
			try {
				connection.rollback();
			} catch (SQLException e2) {
				System.out.println(e2.toString());
			}
		}
		
	}
	
	public void createEvent(int sportID, int venueID, String genderSelection, String date){
		try {
			Statement st = connection.createStatement();
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);									
			connection.setAutoCommit(false);
			st.executeUpdate("INSERT INTO EVENT(event_id,sport_id,venue_id,gender,event_time) values(SEQ_EVENT.NEXTVAL," + sportID + "," + venueID + ",'" + genderSelection + "','" + date + "')");
			connection.commit();
			System.out.println("Event successfully created");
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		} catch (SQLException e1) {
			System.out.println(e1.toString());
			try {
				connection.rollback();
			} catch (SQLException e2) {
				System.out.println(e2.toString());
			}
		}
	}
	
	public void addEventOutcome(int olympicID, int eventID, int teamID, int participantID, int position){																
		try {
			Statement st = connection.createStatement();
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);		
			connection.setAutoCommit(false);
			st.executeUpdate("INSERT INTO SCOREBOARD(olympics_id,event_id,team_id,participant_id,position,medal_id) values(" + olympicID + "," + eventID + "," + teamID + "," + participantID + "," + position + ",null)");
			connection.commit();
			System.out.println("Event outcome successfully added");
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		} catch (SQLException e1) {
			System.out.println(e1.toString());
			try {
				connection.rollback();
			} catch (SQLException e2) {
				System.out.println(e2.toString());
			}
		}
	}
	
	public void createTeam(String olympicCity, String olympicYear, String sport, String country, String teamname){
		boolean noInsert = false;
		int curUserID = 0;
		
		String[] nameSplit = usernameEntry.split(" ");
		String fname = null;
		String lname = null;
		if(nameSplit.length == 2){
			fname = nameSplit[0];
			lname = nameSplit[1];
		}
		else{
			noInsert = true;
		}
		
		try{
			Statement st = connection.createStatement();
			String findCoachID = "SELECT participant_id FROM PARTICIPANT WHERE fname='" + fname + "' and lname='" + lname + "'";
			ResultSet res1 = st.executeQuery(findCoachID);
			while(res1.next()){
				curUserID = res1.getInt("participant_id");
			}
		}catch(SQLException e){
			System.out.println(e.toString());
		}
		if(curUserID == 0){
			noInsert = true;
			System.out.println("Current user is not a coach in the system");
		}
				
		int olympicID = 0;
		int sportID = 0;
		int countryID = 0;
		
		try{
			Statement st = connection.createStatement();
			String findOlympicIDquery = "SELECT olympic_id FROM OLYMPICS where host_city='" + olympicCity + "' and opening_date between '01-JAN-" + olympicYear + "' and '31-DEC-" + olympicYear + "'";
			ResultSet res1 = st.executeQuery(findOlympicIDquery);
			olympicID = 0;
			while (res1.next()) {
				olympicID = res1.getInt("olympic_id");
			}
		}catch(SQLException e){
			System.out.println(e.toString());
			noInsert = true;
		}

		if(!noInsert){
			try{
				Statement st = connection.createStatement();
				String findSportIDquery = "SELECT sport_id FROM SPORT where sport_name='" + sport + "'";
				ResultSet res1 = st.executeQuery(findSportIDquery);
				sportID = 0;
				while (res1.next()) {
					sportID = res1.getInt("sport_id");
				}

				st = connection.createStatement();
				String findCountryIDquery = "SELECT country_id FROM COUNTRY where country_code='" + country + "'";
				res1 = st.executeQuery(findCountryIDquery);
				countryID = 0;
				while (res1.next()) {
					countryID = res1.getInt("country_id");
				}
			}catch(SQLException e){
				System.out.println(e.toString());
			}
		}	
		
		if(!noInsert){																										
			try {
				Statement st = connection.createStatement();
				connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);	
				connection.setAutoCommit(false);
				st.executeUpdate("INSERT INTO TEAM(team_id,olympics_id,team_name,country_id,sport_id,coach_id) values(SEQ_TEAM.NEXTVAL," + olympicID + ",'" + teamname + "'," + countryID + "," + sportID + "," + curUserID + ")");
				connection.commit();
				System.out.println("Team successfully created");
				connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);	
			} catch (SQLException e1) {
				System.out.println(e1.toString());
				try {
					connection.rollback();
				} catch (SQLException e2) {
					System.out.println(e2.toString());
				}
			}																																			
		}					
	}
	
	public void registerTeam(int teamID, int eventID){																																			
		try {
			Statement st = connection.createStatement();
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);	
			connection.setAutoCommit(false);
			st.executeUpdate("INSERT INTO EVENT_PARTICIPATION(event_id,team_id,status) values(" + eventID + "," + teamID + ",'e')");
			connection.commit();
			System.out.println("Team successfully added to event");
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);	
		} catch (SQLException e1) {
			System.out.println(e1.toString());
			try {
				connection.rollback();
			} catch (SQLException e2) {
				System.out.println(e2.toString());
			}
		}	
	}
	
	public void addParticipant(String firstName, String lastName, String nationality, String birthPlace, String dob){																																											
		try {
			Statement st = connection.createStatement();
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);	
			connection.setAutoCommit(false);
			st.executeUpdate("INSERT INTO PARTICIPANT(participant_id, fname, lname, nationality, birth_place, dob) values(SEQ_PARTICIPANT.NEXTVAL,'" + firstName + "','" + lastName + "','" + nationality + "','" + birthPlace + "','" + dob + "')");
			connection.commit();
			System.out.println("Participant successfully added");
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		} catch (SQLException e1) {
			System.out.println(e1.toString());
			try {
				connection.rollback();
			} catch (SQLException e2) {
				System.out.println(e2.toString());
			}
		}
	}
	
	public void addTeamMember(int teamID, int participantID){																																									
		try {
			Statement st = connection.createStatement();
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);		
			connection.setAutoCommit(false);
			st.executeUpdate("INSERT INTO TEAM_MEMBER(team_id, participant_id) values(" + teamID + "," + participantID + ")");
			connection.commit();
			System.out.println("Team member successfully added");
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		} catch (SQLException e1) {
			System.out.println(e1.toString());
			try {
				connection.rollback();
			} catch (SQLException e2) {
				System.out.println(e2.toString());
			}
		}	
	}
	
	public void dropTeamMember(int participantID){
		int partExist = 0;
		
		try{
			Statement st = connection.createStatement();
			String findParticipantIDQuery = "SELECT participant_id FROM PARTICIPANT where participant_id=" + participantID;
			ResultSet res1 = st.executeQuery(findParticipantIDQuery);
			while (res1.next()) {
				partExist = res1.getInt("participant_id");
			}
		}catch(SQLException e){
			System.out.println(e.toString());
		}
		
		if(partExist != 0){																																																			
			try {
				Statement st = connection.createStatement();
				connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);	
				connection.setAutoCommit(false);
				st.executeUpdate("DELETE FROM PARTICIPANT WHERE participant_id=" + participantID);
				connection.commit();
				System.out.println("Participant successfully deleted");
				connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			} catch (SQLException e1) {
				System.out.println(e1.toString());
				try {
					connection.rollback();
				} catch (SQLException e2) {
					System.out.println(e2.toString());
				}
			}																																																															
		}
		else{
			System.out.println("Participant ID entered is not registered in system");
		}
	}
	
	public void displaySport(String sportName){
		try{
			Statement st = connection.createStatement();
			String displaySportQuery = "SELECT s.dob, (sport_name || ' ' || sc.event_id) As EventName, e.gender, (p.fname || ' ' || p.lname) As Name, country_code, medal_title FROM (((((SPORT s join EVENT e on s.sport_id = e.sport_id) join SCOREBOARD sc on e.event_id = sc.event_id) join PARTICIPANT p on sc.participant_id = p.participant_id) join MEDAL m on sc.medal_id = m.medal_id) join COUNTRY c on p.nationality = c.country) where sport_name = '" + sportName + "' order by position asc, EventName asc";
			ResultSet res1 = st.executeQuery(displaySportQuery);
			String dob, EventName, gender, participantName, country_code, medal_title;
			if(res1.next() == false){
				System.out.println("No results match this query");
			}
			else{
				System.out.printf("%-15s %-25s %-10s %-30s %-15s %-15s%n", "Date Created", "Event Name", "Gender", "Participant Name", "Country Code", "Medal");
				 do{
					dob = res1.getString("dob");
					dob = dob.substring(0, 10);
					EventName = res1.getString("EventName");
					medal_title = res1.getString("medal_title");
					gender = res1.getString("gender");
					participantName = res1.getString("Name");
					country_code = res1.getString("country_code");
					System.out.printf("%-15s %-25s %-10s %-30s %-15s %-15s%n", dob, EventName, gender, participantName, country_code, medal_title);
				}while (res1.next());
			}
		}catch(SQLException e){
			System.out.println(e.toString());
		}
	}
	
	public void displayEvent(String hostCity, String year, int eventID){
		boolean noQuery = false;
		int olympicID = 0;

		try{
			Statement st = connection.createStatement();
			String findOlympicIDquery = "SELECT olympic_id FROM OLYMPICS where host_city='" + hostCity + "' and opening_date between '01-JAN-" + year + "' and '31-DEC-" + year + "'";
			ResultSet res1 = st.executeQuery(findOlympicIDquery);
			while (res1.next()) {
				olympicID = res1.getInt("olympic_id");
			}
		}catch(SQLException e){
			System.out.println(e.toString());
			noQuery = true;
		}
								
		if(!noQuery){
			try{
				Statement st = connection.createStatement();
				String displayEventQuery = "SELECT (host_city || ' ' || EXTRACT(YEAR from opening_date)) As OlympicGame, (sport_name || ' ' || s.event_id) As EventName, participant_id, position, medal_title FROM ((((SCOREBOARD s join EVENT e on s.event_id = e.event_id) join OLYMPICS o on s.olympics_id = o.olympic_id) join MEDAL m on s.medal_id = m.medal_id) join SPORT sp on e.sport_id = sp.sport_id) WHERE olympics_id = " + olympicID + " and s.event_id = " + eventID + " order by position asc";
				ResultSet res1 = st.executeQuery(displayEventQuery);
				String OlympicGame, EventName, medal;
				int participantID, position;
				if(res1.next() == false){
					System.out.println("No results match this query");
				}
				else{
					System.out.printf("%-15s %-25s %-15s %-10s %-10s%n", "Olympic Game", "Event Name", "Participant ID", "Position", "Medal");
					do {
						OlympicGame = res1.getString("OlympicGame");
						EventName = res1.getString("EventName");
						medal = res1.getString("medal_title");
						participantID = res1.getInt("participant_id");
						position = res1.getInt("position");
						System.out.printf("%-15s %-25s %-15s %-10s %-10s%n", OlympicGame, EventName, participantID, position, medal);
					}while (res1.next());
				}
			}catch(SQLException e){
				System.out.println(e.toString());
			}
		}
	}
	
	public void countryRanking(int olympicID){
		try{
			Statement st = connection.createStatement();
			String countryRankQuery = "SELECT country, Gold, Silver, Bronze, RANK() OVER (order by (Gold*3+Silver*2+Bronze) desc) As Rank, FirstYear from ((SELECT country, count(case when points=3 then 1 else null end) As Gold,count(case when points=2 then 1 else null end) As Silver, count(case when points=1 then 1 else null end) As Bronze from(SELECT DISTINCT olympic_id, s.team_id, country, points FROM (((((OLYMPICS o join SCOREBOARD s on o.olympic_id = s.olympics_id) join TEAM t on s.team_id = t.team_id) join COUNTRY c on t.country_id = c.country_id) join MEDAL m on s.medal_id = m.medal_id)) where s.olympics_id = " + olympicID + ")group by country) natural join (SELECT country, EXTRACT(YEAR from opening_date) As FirstYear from (SELECT country, min(olympics_id) As minOlympics FROM (TEAM t join COUNTRY c on t.country_id = c.country_id) group by country) join OLYMPICS o on minOlympics = o.olympic_id)) order by (Gold*3+Silver*2+Bronze) desc";
			ResultSet res1 = st.executeQuery(countryRankQuery);
			String country, firstyear;
			int gold, silver, bronze, rank;
			if(res1.next() == false){
				System.out.println("No results match this query");
			}
			else{
				System.out.printf("%-25s %-10s %-10s %-10s %-10s %-15s%n", "Country", "Gold", "Silver", "Bronze", "Rank", "First Year Competing");
				do {
					country = res1.getString("country");
					firstyear = res1.getString("FirstYear");
					gold = res1.getInt("gold");
					silver = res1.getInt("silver");
					bronze = res1.getInt("bronze");
					rank = res1.getInt("rank");
					System.out.printf("%-25s %-10s %-10s %-10s %-10s %-15s%n", country, gold, silver, bronze, rank, firstyear);
				}while (res1.next());
			}
		}catch(SQLException e){
			System.out.println(e.toString());
		}
	}

	public void topKAthletes(int olympicID, int k){
		try{
			Statement st = connection.createStatement();
			String topKAthletesQuery = "SELECT participantName, Gold, Silver, Bronze, RANK() OVER (order by (Gold*3+Silver*2+Bronze) desc) As Rank FROM (SELECT (fname || ' ' || lname) As ParticipantName, count(case when points=3 then 1 else null end) As Gold,count(case when points=2 then 1 else null end) As Silver, count(case when points=1 then 1 else null end) As Bronze FROM ((SCOREBOARD s join PARTICIPANT p on s.participant_id = p.participant_id) join MEDAL m on s.medal_id = m.medal_id)where s.olympics_id = " + olympicID + " group by fname, lname order by (Gold*3+Silver*2+Bronze) desc, ParticipantName asc fetch first " + k + " rows only)";
			ResultSet res1 = st.executeQuery(topKAthletesQuery);
			String participantName;
			int gold, silver, bronze;
			int rank;
			if(res1.next() == false){
				System.out.println("No results match this query");
			}
			else{
				System.out.printf("%-10s %-30s %-10s %-10s %-10s%n", "Rank", "Participant Name", "Gold", "Silver", "Bronze");
				do {
					participantName = res1.getString("ParticipantName");
					gold = res1.getInt("gold");
					silver = res1.getInt("silver");
					bronze = res1.getInt("bronze");
					rank = res1.getInt("rank");
					System.out.printf("%-10s %-30s %-10s %-10s %-10s%n", rank, participantName, gold, silver, bronze);
				}while (res1.next());
			}
		}catch(SQLException e){
			System.out.println(e.toString());
		}
	}
	
	public void connectedAthletes(int participantID, int olympicID, int n){
		try{
			Statement st = connection.createStatement();
			String participantNameQuery = "SELECT (fname || ' ' || lname) As Name FROM PARTICIPANT where participant_id=" + participantID;
			ResultSet res1 = st.executeQuery(participantNameQuery);
			String startConnectionName = null;
			while(res1.next()){
				startConnectionName = res1.getString("Name");
			}
			
			st = connection.createStatement();
			String connectedAthletesQuery = "SELECT (fname || ' ' || lname) As Name FROM PARTICIPANT natural join (SELECT DISTINCT(participant_id) FROM SCOREBOARD natural join (SELECT participant_id FROM SCOREBOARD where event_id = (SELECT event_id FROM SCOREBOARD where olympics_id =" + olympicID + " and participant_id =" + participantID + ")) where olympics_id =" + (olympicID-n) + "and participant_id !=" + participantID + ")";
			res1 = st.executeQuery(connectedAthletesQuery);
			String participantName;
			if(res1.next() == false){
				System.out.println("No results match this query");
			}
			else{
				System.out.printf("%-30s %-30s\n", "Athlete Name", "Connected To");
				do {
					participantName = res1.getString("Name");
					System.out.printf("%-30s %-30s%n", startConnectionName, participantName);
				}while (res1.next());
			}
		}catch(SQLException e){
			System.out.println(e.toString());
		}
	}
	
	public void logout(){
		String curDate = "dd-MMM-yyyy";
		String dateAsString = new SimpleDateFormat(curDate).format(new Date());
		
		try {
			Statement st = connection.createStatement();
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			connection.setAutoCommit(false);
			st.executeUpdate("UPDATE USER_ACCOUNT set last_login='" + dateAsString + "' where username = '" + this.usernameEntry + "'");
			connection.commit();
			System.out.println("You have successfully logged out of the system");
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		} catch (SQLException e1) {
			System.out.println(e1.toString());
			try {
				connection.rollback();
			} catch (SQLException e2) {
				System.out.println(e2.toString());
			}
		}
		
	}
	
    public static void main(String args[]) throws SQLException {

        Connection connection = null;
        try {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        } catch (Exception e) {
            System.out.println(
                    "Error connecting to database. Printing stack trace: ");
            e.printStackTrace();
        }
		
		Scanner input = new Scanner(System.in);
		int userChoice;
		int roleID;
		int functionID;
		boolean keepGoing = true;
		
		String[] roles = new String[]{"Organizer", "Coach", "Guest", "Exit System"};
		String[] organizerFunc = new String[]{"Create User", "Drop User", "Create Event", "Add Event Outcome", "Display Sport", "Display Event", "Country Ranking", "Top k Athletes", "Connected Athletes", "Logout", "Exit"};
		String[] coachFunc = new String[]{"Create Team", "Register Team", "Add Participant", "Add Team Member", "Drop Team Member", "Display Sport", "Display Event", "Country Ranking", "Top k Athletes", "Connected Athletes", "Logout", "Exit"};
		String[] allFunc = new String[]{"Display Sport", "Display Event", "Country Ranking", "Top k Athletes", "Connected Athletes", "Logout", "Exit"};
		String[] deleteLlist = new String[]{"Drop by user ID", "Drop by username", "Drop this account"};
		
		while(keepGoing){
			System.out.println("What type of user would you like to login as? Type the corresponding number");
			for(int i = 0; i < roles.length; i++){
				System.out.println(i+1 + ": " + roles[i]);
			}
			System.out.print("Choice: ");
			try{
				userChoice = input.nextInt();
			} catch (InputMismatchException e){
				System.out.println("Invalid Input");
				input.nextLine();
				userChoice = 5;
			}
			roleID = userChoice;
			
			if(roleID <= 3){
				String usernameEntry = null;
				String passwordEntry = null;
				
				System.out.print("Enter your username: ");
				input.nextLine();
				usernameEntry = input.nextLine();
				System.out.print("Enger your password: ");
				passwordEntry = input.nextLine();
				if(passwordEntry.contains(" ")){
					System.out.println("Password may not contain spaces. Invalid input");
				}
				
				String loginQuery = "SELECT * FROM user_account WHERE username= '" + usernameEntry + "' and passkey='" + passwordEntry + "' and role_id='" + roleID + "'";

				Statement st = connection.createStatement();
				ResultSet rs = st.executeQuery(loginQuery);
				if (rs.next()) {
					System.out.println("Successfully logged in");
					
					if(roleID == 1){
						boolean organizerKeepGoing = true;
						while(organizerKeepGoing){
							System.out.println("What function would you like to carry out? Type the corresponding number");
							for(int i = 0; i < organizerFunc.length; i++){
								System.out.println(i+1 + ": " + organizerFunc[i]);
							}
							System.out.print("Choice: ");
							try{
								userChoice = input.nextInt();
							}
							catch(InputMismatchException e){
								System.out.println("Invalid Input");
								input.nextLine();
								userChoice = 0;
							}
							functionID = userChoice;
							
							if(functionID == 1){ //create user
								boolean noInsert = false;
								String newUserName = null;
								String newPassword = null;
								int newRoleID = 0;
								
								String curDate = "dd-MMM-yyyy";
								String dateAsString = new SimpleDateFormat(curDate).format(new Date());
								
								System.out.print("Enter new user's username: ");
								input.nextLine();
								newUserName = input.nextLine();
								System.out.print("Enter new user's passkey: ");
								newPassword = input.nextLine();
								if(newPassword.contains(" ")){
									System.out.println("Password cannot contain spaces");
									noInsert = true;
								}
								if(!noInsert){
									System.out.print("Enter new user's role ID: ");
									try{
										newRoleID = input.nextInt();
									}catch(InputMismatchException e){
										System.out.println("Invalid Input");
										input.nextLine();
										noInsert = true;
									}
								}
								
								if(!noInsert){
									st = connection.createStatement();
									connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
									try {
										connection.setAutoCommit(false);
										st.executeUpdate("INSERT INTO USER_ACCOUNT(user_id,username,passkey,role_id,last_login) values(SEQ_USER_ACCOUNT.NEXTVAL,'" + newUserName + "','" + newPassword + "'," + newRoleID + ",'" + dateAsString + "')");
										connection.commit();
										System.out.println("User successfully created");
									} catch (SQLException e1) {
										System.out.println(e1.toString());
										try {
											connection.rollback();
										} catch (SQLException e2) {
											System.out.println(e2.toString());
										}
									}
									connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);								
								}
							}
							else if(functionID == 2){ //drop user
								//get current user's user_id
								st = connection.createStatement();
								String findUserIDquery = "SELECT user_id FROM USER_ACCOUNT where username='" + usernameEntry + "'";
								ResultSet res1 = st.executeQuery(findUserIDquery);
								int curUserID = 0;
								while (res1.next()) {
									curUserID = res1.getInt("user_id");
								}
								
								System.out.println("Please select how to delete user");
								for(int i = 0; i < deleteLlist.length; i++){
									System.out.println(i+1 + ": " + deleteLlist[i]);
								}
								System.out.print("Choice: ");
								try{
									userChoice = input.nextInt();
								}
								catch(InputMismatchException e){
									System.out.println("Invalid Input");
									input.nextLine();
									userChoice = 0;
								}
								int deleteChoice = userChoice;
								
								if(deleteChoice == 1){ //delete by user id
									int userIDtoDrop;
									System.out.print("Enter user id to drop: ");
									try{
										userIDtoDrop = input.nextInt();
									}
									catch(InputMismatchException e){
										System.out.println("Invalid Input");
										input.nextLine();
										userIDtoDrop = 0;
									}
									
									if(userIDtoDrop != 0){
										st = connection.createStatement();
										connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
										try {
											connection.setAutoCommit(false);
											st.executeUpdate("DELETE FROM USER_ACCOUNT WHERE user_id=" + userIDtoDrop);
											connection.commit();
											System.out.println("User successfully deleted");
										} catch (SQLException e1) {
											System.out.println(e1.toString());
											try {
												connection.rollback();
											} catch (SQLException e2) {
												System.out.println(e2.toString());
											}
										}
										connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
										
										if(userIDtoDrop==curUserID){
											System.out.println("You have selected your own userID and are being logged out");
											String curDate = "dd-MMM-yyyy";
											String dateAsString = new SimpleDateFormat(curDate).format(new Date());
										
											st = connection.createStatement();
											connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
											try {
												connection.setAutoCommit(false);
												st.executeUpdate("UPDATE USER_ACCOUNT set last_login='" + dateAsString + "' where username = '" + usernameEntry + "'");
												connection.commit();
												organizerKeepGoing = false;
												System.out.println("You have successfully logged out of the system");
											} catch (SQLException e1) {
												System.out.println(e1.toString());
												try {
													connection.rollback();
												} catch (SQLException e2) {
													System.out.println(e2.toString());
												}
											}
											connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
										}
									}
								}
								else if(deleteChoice == 2){ //delete by username
									String usernameToDrop = null;
									System.out.print("Enter username to drop: ");
									input.nextLine();
									usernameToDrop = input.nextLine();
									
									st = connection.createStatement();
									connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);									
									try {
										connection.setAutoCommit(false);
										st.executeUpdate("DELETE FROM USER_ACCOUNT WHERE username='" + usernameToDrop + "'");
										connection.commit();
										System.out.println("User successfully deleted");
									} catch (SQLException e1) {
										try {
											connection.rollback();
										} catch (SQLException e2) {
											System.out.println(e2.toString());
										}
									}
									connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);									
									
									if(usernameToDrop.equals(usernameEntry)){
										System.out.println("You have selected your own username and are being logged out");
										String curDate = "dd-MMM-yyyy";
										String dateAsString = new SimpleDateFormat(curDate).format(new Date());
									
										st = connection.createStatement();
										connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
										try {
											connection.setAutoCommit(false);
											st.executeUpdate("UPDATE USER_ACCOUNT set last_login='" + dateAsString + "' where username = '" + usernameEntry + "'");
											connection.commit();
											organizerKeepGoing = false;
											System.out.println("You have successfully logged out of the system");
										} catch (SQLException e1) {
											System.out.println(e1.toString());
											try {
												connection.rollback();
											} catch (SQLException e2) {
												System.out.println(e2.toString());
											}
										}
										connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
									}
								}
								else if(deleteChoice == 3){ //delete self
									st = connection.createStatement();
									connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);									
									try {
										connection.setAutoCommit(false);
										st.executeUpdate("DELETE FROM USER_ACCOUNT WHERE user_id=" + curUserID);
										connection.commit();
										System.out.println("User successfully deleted");
									} catch (SQLException e1) {
										try {
											connection.rollback();
										} catch (SQLException e2) {
											System.out.println(e2.toString());
										}
									}
									connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);									
									
									System.out.println("You have successfully been dropped and are now being logged out");
									String curDate = "dd-MMM-yyyy";
									String dateAsString = new SimpleDateFormat(curDate).format(new Date());
								
									st = connection.createStatement();
									connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
									try {
										connection.setAutoCommit(false);
										st.executeUpdate("UPDATE USER_ACCOUNT set last_login='" + dateAsString + "' where username = '" + usernameEntry + "'");
										connection.commit();
										organizerKeepGoing = false;
										System.out.println("You have successfully logged out of the system");
									} catch (SQLException e1) {
										System.out.println(e1.toString());
										try {
											connection.rollback();
										} catch (SQLException e2) {
											System.out.println(e2.toString());
										}
									}
									connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
								}
							}
							else if(functionID == 3){ //create event
								boolean noInsert = false;
								int sportID = 0;
								int venueID = 0;
								String date = null;
								String genderSelection = null;
								
								System.out.print("Enter sport ID of event being created: ");
								try{
									sportID = input.nextInt();
								}catch(InputMismatchException e){
									System.out.println("Invalid input");
									input.nextLine();
									noInsert = true;
								}
								if(!noInsert){
									System.out.print("Enter venue ID of event being created: ");
									try{
										venueID = input.nextInt();
									}catch(InputMismatchException e){
										System.out.println("Invalid input");
										input.nextLine();
										noInsert = true;
									}
								}
								if(!noInsert){
									System.out.print("Enter date (DD-MMM-YYYY) of event being created: ");
									input.nextLine();
									date = input.nextLine();
									System.out.print("Enter gender for event, type 0 for men's and 1 for women's: ");
									int gender = 2;
									try{
										gender = input.nextInt();
									}catch(InputMismatchException e){
										System.out.println("Invalid input");
										input.nextLine();
										noInsert = true;	
									}
									if(gender == 0){
										genderSelection = "m";
									}
									else if(gender == 1){
										genderSelection = "f";
									}
									else{
										System.out.println("Invalid input");
										noInsert = true;
									}
								}						
								
								if(!noInsert){
									st = connection.createStatement();
									connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);									
									try {
										connection.setAutoCommit(false);
										st.executeUpdate("INSERT INTO EVENT(event_id,sport_id,venue_id,gender,event_time) values(SEQ_EVENT.NEXTVAL," + sportID + "," + venueID + ",'" + genderSelection + "','" + date + "')");
										connection.commit();
										System.out.println("Event successfully created");
									} catch (SQLException e1) {
										System.out.println(e1.toString());
										try {
											connection.rollback();
										} catch (SQLException e2) {
											System.out.println(e2.toString());
										}
									}
									connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);									
								}								
							}
							else if(functionID == 4){ //add event outcome
								boolean noInsert = false;
								int olympicID = 0;
								int eventID = 0;
								int teamID = 0;
								int participantID = 0;
								int position = 0;
								
								System.out.print("Enter olympic ID of event: ");
								try{
									olympicID = input.nextInt();
								}catch(InputMismatchException e){
									System.out.println("Invalid input");
									input.nextLine();
									noInsert = true;
								}
								if(!noInsert){
									System.out.print("Enter event ID of event: ");
									try{
										eventID = input.nextInt();
									}catch(InputMismatchException e){
										System.out.println("Invalid input");
										input.nextLine();
										noInsert = true;
									}
								}
								if(!noInsert){
									System.out.print("Enter team ID of team in event: ");
									try{	
										teamID = input.nextInt();
									}catch(InputMismatchException e){
										System.out.println("Invalid input");
										input.nextLine();
										noInsert = true;
									}
								}
								if(!noInsert){
									System.out.print("Enter participant ID of participant in event: ");
									try{
										participantID = input.nextInt();
									}catch(InputMismatchException e){
										System.out.println("Invalid input");
										input.nextLine();
										noInsert = true;
									}
								}
								if(!noInsert){
									System.out.print("Enter position placed in event: ");
									try{	
										position = input.nextInt();
									}catch(InputMismatchException e){
										System.out.println("Invalid input");
										input.nextLine();
										noInsert = true;
									}
								}	

								if(!noInsert){
									st = connection.createStatement();
									connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);																		
									try {
										connection.setAutoCommit(false);
										st.executeUpdate("INSERT INTO SCOREBOARD(olympics_id,event_id,team_id,participant_id,position,medal_id) values(" + olympicID + "," + eventID + "," + teamID + "," + participantID + "," + position + ",null)");
										connection.commit();
										System.out.println("Event outcome successfully added");
									} catch (SQLException e1) {
										System.out.println(e1.toString());
										try {
											connection.rollback();
										} catch (SQLException e2) {
											System.out.println(e2.toString());
										}
									}
									connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);																		
								}
							}
							else if(functionID == 5){ //display sport
								String sportName = null;
								System.out.print("Enter sport name of interest: ");
								input.nextLine();
								sportName = input.nextLine();
								
								st = connection.createStatement();
								String displaySportQuery = "SELECT s.dob, (sport_name || ' ' || sc.event_id) As EventName, e.gender, (p.fname || ' ' || p.lname) As Name, country_code, medal_title FROM (((((SPORT s join EVENT e on s.sport_id = e.sport_id) join SCOREBOARD sc on e.event_id = sc.event_id) join PARTICIPANT p on sc.participant_id = p.participant_id) join MEDAL m on sc.medal_id = m.medal_id) join COUNTRY c on p.nationality = c.country) where sport_name = '" + sportName + "' order by position asc, EventName asc";
								ResultSet res1 = st.executeQuery(displaySportQuery);
								String dob, EventName, gender, participantName, country_code, medal_title;
								if(res1.next() == false){
									System.out.println("No results match this query");
								}
								else{
									System.out.printf("%-15s %-25s %-10s %-30s %-15s %-15s%n", "Date Created", "Event Name", "Gender", "Participant Name", "Country Code", "Medal");
									 do{
										dob = res1.getString("dob");
										dob = dob.substring(0, 10);
										EventName = res1.getString("EventName");
										medal_title = res1.getString("medal_title");
										gender = res1.getString("gender");
										participantName = res1.getString("Name");
										country_code = res1.getString("country_code");
										System.out.printf("%-15s %-25s %-10s %-30s %-15s %-15s%n", dob, EventName, gender, participantName, country_code, medal_title);
									}while (res1.next());
								}
							}
							else if(functionID == 6){ //display event
								boolean noQuery = false;
								String hostCity = null;
								String year = null;
								int olympicID = 0;
								
								System.out.print("Enter olympic host city of event: ");
								input.nextLine();
								hostCity = input.nextLine();
								System.out.print("Enter year of event: ");
								year = input.nextLine();
								
								try{
									st = connection.createStatement();
									String findOlympicIDquery = "SELECT olympic_id FROM OLYMPICS where host_city='" + hostCity + "' and opening_date between '01-JAN-" + year + "' and '31-DEC-" + year + "'";
									ResultSet res1 = st.executeQuery(findOlympicIDquery);
									while (res1.next()) {
										olympicID = res1.getInt("olympic_id");
									}
								}catch(SQLException e){
									System.out.println(e.toString());
									noQuery = true;
								}
								
								int eventID = 0;
								
								if(!noQuery){
									System.out.print("Enter event ID of event: ");
									try{
										eventID = input.nextInt();
									}catch(InputMismatchException e){
											System.out.println("Invalid input");
											input.nextLine();
											noQuery = true;
									}
								}								
								if(!noQuery){
									st = connection.createStatement();
									String displayEventQuery = "SELECT (host_city || ' ' || EXTRACT(YEAR from opening_date)) As OlympicGame, (sport_name || ' ' || s.event_id) As EventName, participant_id, position, medal_title FROM ((((SCOREBOARD s join EVENT e on s.event_id = e.event_id) join OLYMPICS o on s.olympics_id = o.olympic_id) join MEDAL m on s.medal_id = m.medal_id) join SPORT sp on e.sport_id = sp.sport_id) WHERE olympics_id = " + olympicID + " and s.event_id = " + eventID + " order by position asc";
									ResultSet res1 = st.executeQuery(displayEventQuery);
									String OlympicGame, EventName, medal;
									int participantID, position;
									if(res1.next() == false){
										System.out.println("No results match this query");
									}
									else{
										System.out.printf("%-15s %-25s %-15s %-10s %-10s%n", "Olympic Game", "Event Name", "Participant ID", "Position", "Medal");
										do {
											OlympicGame = res1.getString("OlympicGame");
											EventName = res1.getString("EventName");
											medal = res1.getString("medal_title");
											participantID = res1.getInt("participant_id");
											position = res1.getInt("position");
											System.out.printf("%-15s %-25s %-15s %-10s %-10s%n", OlympicGame, EventName, participantID, position, medal);
										}while (res1.next());
									}
								}
							}
							else if(functionID == 7){ //country ranking
								int olympicID = 0;
								boolean noQuery = false;
								
								System.out.print("Enter olympic ID of olympics of interest: ");
								try{
									olympicID = input.nextInt();
								}catch(InputMismatchException e){
									System.out.println("Invalid input");
									input.nextLine();
									noQuery = true;
								}
									
								if(!noQuery){
									st = connection.createStatement();
									String countryRankQuery = "SELECT country, Gold, Silver, Bronze, RANK() OVER (order by (Gold*3+Silver*2+Bronze) desc) As Rank, FirstYear from ((SELECT country, count(case when points=3 then 1 else null end) As Gold,count(case when points=2 then 1 else null end) As Silver, count(case when points=1 then 1 else null end) As Bronze from(SELECT DISTINCT olympic_id, s.team_id, country, points FROM (((((OLYMPICS o join SCOREBOARD s on o.olympic_id = s.olympics_id) join TEAM t on s.team_id = t.team_id) join COUNTRY c on t.country_id = c.country_id) join MEDAL m on s.medal_id = m.medal_id)) where s.olympics_id = " + olympicID + ")group by country) natural join (SELECT country, EXTRACT(YEAR from opening_date) As FirstYear from (SELECT country, min(olympics_id) As minOlympics FROM (TEAM t join COUNTRY c on t.country_id = c.country_id) group by country) join OLYMPICS o on minOlympics = o.olympic_id)) order by (Gold*3+Silver*2+Bronze) desc";
									ResultSet res1 = st.executeQuery(countryRankQuery);
									String country, firstyear;
									int gold, silver, bronze, rank;
									if(res1.next() == false){
										System.out.println("No results match this query");
									}
									else{
										System.out.printf("%-25s %-10s %-10s %-10s %-10s %-15s%n", "Country", "Gold", "Silver", "Bronze", "Rank", "First Year Competing");
										do {
											country = res1.getString("country");
											firstyear = res1.getString("FirstYear");
											gold = res1.getInt("gold");
											silver = res1.getInt("silver");
											bronze = res1.getInt("bronze");
											rank = res1.getInt("rank");
											System.out.printf("%-25s %-10s %-10s %-10s %-10s %-15s%n", country, gold, silver, bronze, rank, firstyear);
										}while (res1.next());
									}
								}
							}
							else if(functionID == 8){ //top k athletes
								boolean noQuery = false;
								int olympicID = 0;
								int k = 0;
								
								System.out.print("Enter olympic ID of olympics of interest: ");
								try{
									olympicID = input.nextInt();
								}catch(InputMismatchException e){
									System.out.println("Invalid input");
									input.nextLine();
									noQuery = true;
								}
								
								if(!noQuery){
									System.out.print("Enter the number of athletes you want to see: ");
									try{
										k = input.nextInt();
									}catch(InputMismatchException e){
										System.out.println("Invalid input");
										input.nextLine();
										noQuery = true;
									}
								}	

								if(!noQuery){
									st = connection.createStatement();
									String topKAthletesQuery = "SELECT participantName, Gold, Silver, Bronze, RANK() OVER (order by (Gold*3+Silver*2+Bronze) desc) As Rank FROM (SELECT (fname || ' ' || lname) As ParticipantName, count(case when points=3 then 1 else null end) As Gold,count(case when points=2 then 1 else null end) As Silver, count(case when points=1 then 1 else null end) As Bronze FROM ((SCOREBOARD s join PARTICIPANT p on s.participant_id = p.participant_id) join MEDAL m on s.medal_id = m.medal_id)where s.olympics_id = " + olympicID + " group by fname, lname order by (Gold*3+Silver*2+Bronze) desc, ParticipantName asc fetch first " + k + " rows only)";
									ResultSet res1 = st.executeQuery(topKAthletesQuery);
									String participantName;
									int gold, silver, bronze;
									int rank;
									if(res1.next() == false){
										System.out.println("No results match this query");
									}
									else{
										System.out.printf("%-10s %-30s %-10s %-10s %-10s%n", "Rank", "Participant Name", "Gold", "Silver", "Bronze");
										do {
											participantName = res1.getString("ParticipantName");
											gold = res1.getInt("gold");
											silver = res1.getInt("silver");
											bronze = res1.getInt("bronze");
											rank = res1.getInt("rank");
											System.out.printf("%-10s %-30s %-10s %-10s %-10s%n", rank, participantName, gold, silver, bronze);
										}while (res1.next());
									}
								}
							}
							else if(functionID == 9){ //connected athletes
								boolean noQuery = false;
								int participantID = 0;
								int olympicID = 0;
								int n = 0;
								
								System.out.print("Enter participantID to search connections for: ");
								try{
									participantID = input.nextInt();
								}catch(InputMismatchException e){
									System.out.println("Invalid input");
									input.nextLine();
									noQuery = true;
								}
								
								if(!noQuery){
									System.out.print("Enter olympic ID to start search: ");
									try{
										olympicID = input.nextInt();
									}catch(InputMismatchException e){
										System.out.println("Invalid input");
										input.nextLine();
										noQuery = true;
									}
								}
								
								if(!noQuery){
									System.out.print("Enter hops to make: ");
									try{
										n = input.nextInt();
									}catch(InputMismatchException e){
										System.out.println("Invalid input");
										input.nextLine();
										noQuery = true;
									}
								}
								
								if(!noQuery){
									st = connection.createStatement();
									String participantNameQuery = "SELECT (fname || ' ' || lname) As Name FROM PARTICIPANT where participant_id=" + participantID;
									ResultSet res1 = st.executeQuery(participantNameQuery);
									String startConnectionName = null;
									while(res1.next()){
										startConnectionName = res1.getString("Name");
									}
									
									st = connection.createStatement();
									String connectedAthletesQuery = "SELECT (fname || ' ' || lname) As Name FROM PARTICIPANT natural join (SELECT DISTINCT(participant_id) FROM SCOREBOARD natural join (SELECT participant_id FROM SCOREBOARD where event_id = (SELECT event_id FROM SCOREBOARD where olympics_id =" + olympicID + " and participant_id =" + participantID + ")) where olympics_id =" + (olympicID-n) + "and participant_id !=" + participantID + ")";
									res1 = st.executeQuery(connectedAthletesQuery);
									String participantName;
									if(res1.next() == false){
										System.out.println("No results match this query");
									}
									else{
										System.out.printf("%-30s %-30s\n", "Athlete Name", "Connected To");
										do {
											participantName = res1.getString("Name");
											System.out.printf("%-30s %-30s%n", startConnectionName, participantName);
										}while (res1.next());
									}
								}
							}
							else if(functionID == 10){ //logout
								String curDate = "dd-MMM-yyyy";
								String dateAsString = new SimpleDateFormat(curDate).format(new Date());
							
								st = connection.createStatement();
								connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
								try {
									connection.setAutoCommit(false);
									st.executeUpdate("UPDATE USER_ACCOUNT set last_login='" + dateAsString + "' where username = '" + usernameEntry + "'");
									connection.commit();
									organizerKeepGoing = false;
									System.out.println("You have successfully logged out of the system");
								} catch (SQLException e1) {
									System.out.println(e1.toString());
									try {
										connection.rollback();
									} catch (SQLException e2) {
										System.out.println(e2.toString());
									}
								}
								connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
							}
							else if(functionID == 11){ //exit system
								organizerKeepGoing = false;
								keepGoing = false;
							}
						}
					}
					else if(roleID == 2){
						boolean coachKeepGoing = true;
						while(coachKeepGoing) {
							System.out.println("What function would you like to carry out? Type the corresponding number");
							for(int i = 0; i < coachFunc.length; i++){
								System.out.println(i+1 + ": " + coachFunc[i]);
							}
							System.out.print("Choice: ");
							try{
								userChoice = input.nextInt();
							}
							catch(InputMismatchException e){
								System.out.println("Invalid Input");
								input.nextLine();
								userChoice = 0;
							}
							functionID = userChoice;
							
							if(functionID == 1){ //create team
								//get current user's user_id
								boolean noInsert = false;
								
								String[] nameSplit = usernameEntry.split(" ");
								String fname = null;
								String lname = null;
								if(nameSplit.length == 2){
									fname = nameSplit[0];
									lname = nameSplit[1];
								}
								else{
									noInsert = true;
								}
								
								st = connection.createStatement();
								String findCoachID = "SELECT participant_id FROM PARTICIPANT WHERE fname='" + fname + "' and lname='" + lname + "'";
								ResultSet res1 = st.executeQuery(findCoachID);
								int curUserID = 0;
								while(res1.next()){
									curUserID = res1.getInt("participant_id");
								}
								if(curUserID == 0){
									noInsert = true;
									System.out.println("Current user is not a coach in the system");
								}
																
								String olympicCity = null;
								String olympicYear = null;
								String sport = null;
								String country = null;
								String teamname = null;
								int olympicID = 0;
								int sportID = 0;
								int countryID = 0;
								
								System.out.print("Enter olympic city team will be competing: ");
								input.nextLine();
								olympicCity = input.nextLine();
								System.out.print("Enter year team will be competing: ");
								olympicYear = input.nextLine();
								
								try{
									st = connection.createStatement();
									String findOlympicIDquery = "SELECT olympic_id FROM OLYMPICS where host_city='" + olympicCity + "' and opening_date between '01-JAN-" + olympicYear + "' and '31-DEC-" + olympicYear + "'";
									res1 = st.executeQuery(findOlympicIDquery);
									olympicID = 0;
									while (res1.next()) {
										olympicID = res1.getInt("olympic_id");
									}
								}catch(SQLException e){
									System.out.println(e.toString());
									noInsert = true;
								}

								if(!noInsert){
									System.out.print("Enter sport the team will be playing: ");
									sport = input.nextLine();
									
									st = connection.createStatement();
									String findSportIDquery = "SELECT sport_id FROM SPORT where sport_name='" + sport + "'";
									res1 = st.executeQuery(findSportIDquery);
									sportID = 0;
									while (res1.next()) {
										sportID = res1.getInt("sport_id");
									}
								
									System.out.print("Enter country code of the country the team is playing for: ");
									country = input.nextLine();
								
									st = connection.createStatement();
									String findCountryIDquery = "SELECT country_id FROM COUNTRY where country_code='" + country + "'";
									res1 = st.executeQuery(findCountryIDquery);
									countryID = 0;
									while (res1.next()) {
										countryID = res1.getInt("country_id");
									}
									
									System.out.print("Enter name of the team: ");
									teamname = input.nextLine();
								}	
								
								if(!noInsert){
									st = connection.createStatement();
									connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);																											
									try {
										connection.setAutoCommit(false);
										st.executeUpdate("INSERT INTO TEAM(team_id,olympics_id,team_name,country_id,sport_id,coach_id) values(SEQ_TEAM.NEXTVAL," + olympicID + ",'" + teamname + "'," + countryID + "," + sportID + "," + curUserID + ")");
										connection.commit();
										System.out.println("Team successfully created");
									} catch (SQLException e1) {
										System.out.println(e1.toString());
										try {
											connection.rollback();
										} catch (SQLException e2) {
											System.out.println(e2.toString());
										}
									}
									connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);																																				
								}								
							}
							else if(functionID == 2){ //register team
								boolean noInsert = false;
								int teamID = 0;
								int eventID = 0;
							
								System.out.print("Enter teamID for the team to register: ");
								try{
									teamID = input.nextInt();
								}catch(InputMismatchException e){
									System.out.println("Invalid input");
									input.nextLine();
									noInsert = true;
								}
								
								if(!noInsert){
									System.out.print("Enter eventID for event the team is registering for: ");
									try{
										eventID = input.nextInt();
									}catch(InputMismatchException e){
										System.out.println("Invalid input");
										input.nextLine();
										noInsert = true;
									}
								}				

								if(!noInsert){
									st = connection.createStatement();
									connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);																																				
									try {
										connection.setAutoCommit(false);
										st.executeUpdate("INSERT INTO EVENT_PARTICIPATION(event_id,team_id,status) values(" + eventID + "," + teamID + ",'e')");
										connection.commit();
										System.out.println("Team successfully added to event");
									} catch (SQLException e1) {
										System.out.println(e1.toString());
										try {
											connection.rollback();
										} catch (SQLException e2) {
											System.out.println(e2.toString());
										}
									}
									connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);																																													
								}
							}
							else if(functionID == 3){ //add participant
								String firstName = null;
								String lastName = null;
								String nationality = null;
								String birthPlace = null;
								String dob = null;
								
								System.out.print("Enter first name of participant to add: ");
								input.nextLine();
								firstName = input.nextLine();
								
								System.out.print("Enter last name of participant to add: ");
								lastName = input.nextLine();
								
								System.out.print("Enter nationality of participant to add: ");
								nationality = input.nextLine();
								
								System.out.print("Enter birth place of participant to add: ");
								birthPlace = input.nextLine();
								
								System.out.print("Enter date of birth (DD-MMM-YYYY) of participant to add: ");
								dob = input.nextLine();
								
								st = connection.createStatement();
								connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);																																												
								try {
									connection.setAutoCommit(false);
									st.executeUpdate("INSERT INTO PARTICIPANT(participant_id, fname, lname, nationality, birth_place, dob) values(SEQ_PARTICIPANT.NEXTVAL,'" + firstName + "','" + lastName + "','" + nationality + "','" + birthPlace + "','" + dob + "')");
									connection.commit();
									System.out.println("Participant successfully added");
								} catch (SQLException e1) {
									System.out.println(e1.toString());
									try {
										connection.rollback();
									} catch (SQLException e2) {
										System.out.println(e2.toString());
									}
								}
								connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);																																																					
							}
							else if(functionID == 4){ //add team member
								boolean noInsert = false;
								int teamID = 0;
								int participantID = 0;
							
								System.out.print("Enter team ID of team to add member: ");
								try{
									teamID = input.nextInt();
								}catch(InputMismatchException e){
									System.out.println("Invalid input");
									input.nextLine();
									noInsert = true;
								}
								
								if(!noInsert){
									System.out.print("Enter participant ID of member to add: ");
									try{
										participantID = input.nextInt();
									}catch(InputMismatchException e){
										System.out.println("Invalid input");
										input.nextLine();
										noInsert = true;
									}
								}
								
								if(!noInsert){
									st = connection.createStatement();
									connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);																																												
									try {
										connection.setAutoCommit(false);
										st.executeUpdate("INSERT INTO TEAM_MEMBER(team_id, participant_id) values(" + teamID + "," + participantID + ")");
										connection.commit();
										System.out.println("Team member successfully added");
									} catch (SQLException e1) {
										System.out.println(e1.toString());
										try {
											connection.rollback();
										} catch (SQLException e2) {
											System.out.println(e2.toString());
										}
									}
									connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);																																																														
								}
							}
							else if(functionID == 5){ //drop team member
								boolean noQuery = false;
								int participantID = 0;
								
								System.out.print("Enter the participant ID of participant to be removed: ");
								try{
									participantID = input.nextInt();
								}catch(InputMismatchException e){
									System.out.println("Invalid input");
									input.nextLine();
									noQuery = true;
								}
								
								if(!noQuery){
									st = connection.createStatement();
									String findParticipantIDQuery = "SELECT participant_id FROM PARTICIPANT where participant_id=" + participantID;
									ResultSet res1 = st.executeQuery(findParticipantIDQuery);
									int partExist = 0;
									while (res1.next()) {
										partExist = res1.getInt("participant_id");
									}
									
									if(partExist != 0){								
										st = connection.createStatement();
										connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);																																												
										try {
											connection.setAutoCommit(false);
											st.executeUpdate("DELETE FROM PARTICIPANT WHERE participant_id=" + participantID);
											connection.commit();
											System.out.println("Participant successfully deleted");
										} catch (SQLException e1) {
											System.out.println(e1.toString());
											try {
												connection.rollback();
											} catch (SQLException e2) {
												System.out.println(e2.toString());
											}
										}
										connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);																																																															
									}
									else{
										System.out.println("Participant ID entered is not registered in system");
									}
								}
							}
							else if(functionID == 6){ //display sport
								String sportName = null;
								System.out.print("Enter sport name of interest: ");
								input.nextLine();
								sportName = input.nextLine();
								
								st = connection.createStatement();
								String displaySportQuery = "SELECT s.dob, (sport_name || ' ' || sc.event_id) As EventName, e.gender, (p.fname || ' ' || p.lname) As Name, country_code, medal_title FROM (((((SPORT s join EVENT e on s.sport_id = e.sport_id) join SCOREBOARD sc on e.event_id = sc.event_id) join PARTICIPANT p on sc.participant_id = p.participant_id) join MEDAL m on sc.medal_id = m.medal_id) join COUNTRY c on p.nationality = c.country) where sport_name = '" + sportName + "' order by position asc, EventName asc";
								ResultSet res1 = st.executeQuery(displaySportQuery);
								String dob, EventName, gender, participantName, country_code, medal_title;
								if(res1.next() == false){
									System.out.println("No results match this query");
								}
								else{
									System.out.printf("%-15s %-25s %-10s %-30s %-15s %-15s%n", "Date Created", "Event Name", "Gender", "Participant Name", "Country Code", "Medal");
									 do{
										dob = res1.getString("dob");
										dob = dob.substring(0, 10);
										EventName = res1.getString("EventName");
										medal_title = res1.getString("medal_title");
										gender = res1.getString("gender");
										participantName = res1.getString("Name");
										country_code = res1.getString("country_code");
										System.out.printf("%-15s %-25s %-10s %-30s %-15s %-15s%n", dob, EventName, gender, participantName, country_code, medal_title);
									}while (res1.next());
								}
							}
							else if(functionID == 7){ //display event
								boolean noQuery = false;
								String hostCity = null;
								String year = null;
								int olympicID = 0;
								
								System.out.print("Enter olympic host city of event: ");
								input.nextLine();
								hostCity = input.nextLine();
								System.out.print("Enter year of event: ");
								year = input.nextLine();
								
								try{
									st = connection.createStatement();
									String findOlympicIDquery = "SELECT olympic_id FROM OLYMPICS where host_city='" + hostCity + "' and opening_date between '01-JAN-" + year + "' and '31-DEC-" + year + "'";
									ResultSet res1 = st.executeQuery(findOlympicIDquery);
									while (res1.next()) {
										olympicID = res1.getInt("olympic_id");
									}
								}catch(SQLException e){
									System.out.println(e.toString());
									noQuery = true;
								}
								
								int eventID = 0;
								
								if(!noQuery){
									System.out.print("Enter event ID of event: ");
									try{
										eventID = input.nextInt();
									}catch(InputMismatchException e){
											System.out.println("Invalid input");
											input.nextLine();
											noQuery = true;
									}
								}								
								if(!noQuery){
									st = connection.createStatement();
									String displayEventQuery = "SELECT (host_city || ' ' || EXTRACT(YEAR from opening_date)) As OlympicGame, (sport_name || ' ' || s.event_id) As EventName, participant_id, position, medal_title FROM ((((SCOREBOARD s join EVENT e on s.event_id = e.event_id) join OLYMPICS o on s.olympics_id = o.olympic_id) join MEDAL m on s.medal_id = m.medal_id) join SPORT sp on e.sport_id = sp.sport_id) WHERE olympics_id = " + olympicID + " and s.event_id = " + eventID + " order by position asc";
									ResultSet res1 = st.executeQuery(displayEventQuery);
									String OlympicGame, EventName, medal;
									int participantID, position;
									if(res1.next() == false){
										System.out.println("No results match this query");
									}
									else{
										System.out.printf("%-15s %-25s %-15s %-10s %-10s%n", "Olympic Game", "Event Name", "Participant ID", "Position", "Medal");
										do {
											OlympicGame = res1.getString("OlympicGame");
											EventName = res1.getString("EventName");
											medal = res1.getString("medal_title");
											participantID = res1.getInt("participant_id");
											position = res1.getInt("position");
											System.out.printf("%-15s %-25s %-15s %-10s %-10s%n", OlympicGame, EventName, participantID, position, medal);
										}while (res1.next());
									}
								}
							}
							else if(functionID == 8){ //country ranking
								int olympicID = 0;
								boolean noQuery = false;
								
								System.out.print("Enter olympic ID of olympics of interest: ");
								try{
									olympicID = input.nextInt();
								}catch(InputMismatchException e){
									System.out.println("Invalid input");
									input.nextLine();
									noQuery = true;
								}
									
								if(!noQuery){
									st = connection.createStatement();
									String countryRankQuery = "SELECT country, Gold, Silver, Bronze, RANK() OVER (order by (Gold*3+Silver*2+Bronze) desc) As Rank, FirstYear from ((SELECT country, count(case when points=3 then 1 else null end) As Gold,count(case when points=2 then 1 else null end) As Silver, count(case when points=1 then 1 else null end) As Bronze from(SELECT DISTINCT olympic_id, s.team_id, country, points FROM (((((OLYMPICS o join SCOREBOARD s on o.olympic_id = s.olympics_id) join TEAM t on s.team_id = t.team_id) join COUNTRY c on t.country_id = c.country_id) join MEDAL m on s.medal_id = m.medal_id)) where s.olympics_id = " + olympicID + ")group by country) natural join (SELECT country, EXTRACT(YEAR from opening_date) As FirstYear from (SELECT country, min(olympics_id) As minOlympics FROM (TEAM t join COUNTRY c on t.country_id = c.country_id) group by country) join OLYMPICS o on minOlympics = o.olympic_id)) order by (Gold*3+Silver*2+Bronze) desc";
									ResultSet res1 = st.executeQuery(countryRankQuery);
									String country, firstyear;
									int gold, silver, bronze, rank;
									if(res1.next() == false){
										System.out.println("No results match this query");
									}
									else{
										System.out.printf("%-25s %-10s %-10s %-10s %-10s %-15s%n", "Country", "Gold", "Silver", "Bronze", "Rank", "First Year Competing");
										do {
											country = res1.getString("country");
											firstyear = res1.getString("FirstYear");
											gold = res1.getInt("gold");
											silver = res1.getInt("silver");
											bronze = res1.getInt("bronze");
											rank = res1.getInt("rank");
											System.out.printf("%-25s %-10s %-10s %-10s %-10s %-15s%n", country, gold, silver, bronze, rank, firstyear);
										}while (res1.next());
									}
								}
							}
							else if(functionID == 9){ //top k athletes
								boolean noQuery = false;
								int olympicID = 0;
								int k = 0;
								
								System.out.print("Enter olympic ID of olympics of interest: ");
								try{
									olympicID = input.nextInt();
								}catch(InputMismatchException e){
									System.out.println("Invalid input");
									input.nextLine();
									noQuery = true;
								}
								
								if(!noQuery){
									System.out.print("Enter the number of athletes you want to see: ");
									try{
										k = input.nextInt();
									}catch(InputMismatchException e){
										System.out.println("Invalid input");
										input.nextLine();
										noQuery = true;
									}
								}	

								if(!noQuery){
									st = connection.createStatement();
									String topKAthletesQuery = "SELECT participantName, Gold, Silver, Bronze, RANK() OVER (order by (Gold*3+Silver*2+Bronze) desc) As Rank FROM (SELECT (fname || ' ' || lname) As ParticipantName, count(case when points=3 then 1 else null end) As Gold,count(case when points=2 then 1 else null end) As Silver, count(case when points=1 then 1 else null end) As Bronze FROM ((SCOREBOARD s join PARTICIPANT p on s.participant_id = p.participant_id) join MEDAL m on s.medal_id = m.medal_id)where s.olympics_id = " + olympicID + " group by fname, lname order by (Gold*3+Silver*2+Bronze) desc, ParticipantName asc fetch first " + k + " rows only)";
									ResultSet res1 = st.executeQuery(topKAthletesQuery);
									String participantName;
									int gold, silver, bronze;
									int rank;
									if(res1.next() == false){
										System.out.println("No results match this query");
									}
									else{
										System.out.printf("%-10s %-30s %-10s %-10s %-10s%n", "Rank", "Participant Name", "Gold", "Silver", "Bronze");
										do {
											participantName = res1.getString("ParticipantName");
											gold = res1.getInt("gold");
											silver = res1.getInt("silver");
											bronze = res1.getInt("bronze");
											rank = res1.getInt("rank");
											System.out.printf("%-10s %-30s %-10s %-10s %-10s%n", rank, participantName, gold, silver, bronze);
										}while (res1.next());
									}
								}
							}
							else if(functionID == 10){ //connected athletes
								boolean noQuery = false;
								int participantID = 0;
								int olympicID = 0;
								int n = 0;
								
								System.out.print("Enter participantID to search connections for: ");
								try{
									participantID = input.nextInt();
								}catch(InputMismatchException e){
									System.out.println("Invalid input");
									input.nextLine();
									noQuery = true;
								}
								
								if(!noQuery){
									System.out.print("Enter olympic ID to start search: ");
									try{
										olympicID = input.nextInt();
									}catch(InputMismatchException e){
										System.out.println("Invalid input");
										input.nextLine();
										noQuery = true;
									}
								}
								
								if(!noQuery){
									System.out.print("Enter hops to make: ");
									try{
										n = input.nextInt();
									}catch(InputMismatchException e){
										System.out.println("Invalid input");
										input.nextLine();
										noQuery = true;
									}
								}
								
								if(!noQuery){
									st = connection.createStatement();
									String participantNameQuery = "SELECT (fname || ' ' || lname) As Name FROM PARTICIPANT where participant_id=" + participantID;
									ResultSet res1 = st.executeQuery(participantNameQuery);
									String startConnectionName = null;
									while(res1.next()){
										startConnectionName = res1.getString("Name");
									}
									
									st = connection.createStatement();
									String connectedAthletesQuery = "SELECT (fname || ' ' || lname) As Name FROM PARTICIPANT natural join (SELECT DISTINCT(participant_id) FROM SCOREBOARD natural join (SELECT participant_id FROM SCOREBOARD where event_id = (SELECT event_id FROM SCOREBOARD where olympics_id =" + olympicID + " and participant_id =" + participantID + ")) where olympics_id =" + (olympicID-n) + "and participant_id !=" + participantID + ")";
									res1 = st.executeQuery(connectedAthletesQuery);
									String participantName;
									if(res1.next() == false){
										System.out.println("No results match this query");
									}
									else{
										System.out.printf("%-30s %-30s\n", "Athlete Name", "Connected To");
										do {
											participantName = res1.getString("Name");
											System.out.printf("%-30s %-30s%n", startConnectionName, participantName);
										}while (res1.next());
									}
								}
							}
							else if(functionID == 11){ //logout
								String curDate = "dd-MMM-yyyy";
								String dateAsString = new SimpleDateFormat(curDate).format(new Date());
							
								st = connection.createStatement();
								connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
								try {
									connection.setAutoCommit(false);
									st.executeUpdate("UPDATE USER_ACCOUNT set last_login='" + dateAsString + "' where username = '" + usernameEntry + "'");
									connection.commit();
									coachKeepGoing = false;
									System.out.println("You have successfully logged out of the system");
								} catch (SQLException e1) {
									System.out.println(e1.toString());
									try {
										connection.rollback();
									} catch (SQLException e2) {
										System.out.println(e2.toString());
									}
								}
								connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
							}
							else if(functionID == 12){ //exit system
								coachKeepGoing = false;
								keepGoing = false;
							}
						}
					}
					else if(roleID == 3){
						boolean guestKeepGoing = true;
						while(guestKeepGoing){
							System.out.println("What function would you like to carry out? Type the corresponding number");
							for(int i = 0; i < allFunc.length; i++){
								System.out.println(i+1 + ": " + allFunc[i]);
							}
							System.out.print("Choice: ");
							try{
								userChoice = input.nextInt();
							}
							catch(InputMismatchException e){
								System.out.println("Invalid Input");
								input.nextLine();
								userChoice = 0;
							}
							functionID = userChoice;
							
							if(functionID == 1){ //display sport
								String sportName = null;
								System.out.print("Enter sport name of interest: ");
								input.nextLine();
								sportName = input.nextLine();
								
								st = connection.createStatement();
								String displaySportQuery = "SELECT s.dob, (sport_name || ' ' || sc.event_id) As EventName, e.gender, (p.fname || ' ' || p.lname) As Name, country_code, medal_title FROM (((((SPORT s join EVENT e on s.sport_id = e.sport_id) join SCOREBOARD sc on e.event_id = sc.event_id) join PARTICIPANT p on sc.participant_id = p.participant_id) join MEDAL m on sc.medal_id = m.medal_id) join COUNTRY c on p.nationality = c.country) where sport_name = '" + sportName + "' order by position asc, EventName asc";
								ResultSet res1 = st.executeQuery(displaySportQuery);
								String dob, EventName, gender, participantName, country_code, medal_title;
								if(res1.next() == false){
									System.out.println("No results match this query");
								}
								else{
									System.out.printf("%-15s %-25s %-10s %-30s %-15s %-15s%n", "Date Created", "Event Name", "Gender", "Participant Name", "Country Code", "Medal");
									 do{
										dob = res1.getString("dob");
										dob = dob.substring(0, 10);
										EventName = res1.getString("EventName");
										medal_title = res1.getString("medal_title");
										gender = res1.getString("gender");
										participantName = res1.getString("Name");
										country_code = res1.getString("country_code");
										System.out.printf("%-15s %-25s %-10s %-30s %-15s %-15s%n", dob, EventName, gender, participantName, country_code, medal_title);
									}while (res1.next());
								}
							}
							else if(functionID == 2){ //display event
								boolean noQuery = false;
								String hostCity = null;
								String year = null;
								int olympicID = 0;
								
								System.out.print("Enter olympic host city of event: ");
								input.nextLine();
								hostCity = input.nextLine();
								System.out.print("Enter year of event: ");
								year = input.nextLine();
								
								try{
									st = connection.createStatement();
									String findOlympicIDquery = "SELECT olympic_id FROM OLYMPICS where host_city='" + hostCity + "' and opening_date between '01-JAN-" + year + "' and '31-DEC-" + year + "'";
									ResultSet res1 = st.executeQuery(findOlympicIDquery);
									while (res1.next()) {
										olympicID = res1.getInt("olympic_id");
									}
								}catch(SQLException e){
									System.out.println(e.toString());
									noQuery = true;
								}
								
								int eventID = 0;
								
								if(!noQuery){
									System.out.print("Enter event ID of event: ");
									try{
										eventID = input.nextInt();
									}catch(InputMismatchException e){
											System.out.println("Invalid input");
											input.nextLine();
											noQuery = true;
									}
								}								
								if(!noQuery){
									st = connection.createStatement();
									String displayEventQuery = "SELECT (host_city || ' ' || EXTRACT(YEAR from opening_date)) As OlympicGame, (sport_name || ' ' || s.event_id) As EventName, participant_id, position, medal_title FROM ((((SCOREBOARD s join EVENT e on s.event_id = e.event_id) join OLYMPICS o on s.olympics_id = o.olympic_id) join MEDAL m on s.medal_id = m.medal_id) join SPORT sp on e.sport_id = sp.sport_id) WHERE olympics_id = " + olympicID + " and s.event_id = " + eventID + " order by position asc";
									ResultSet res1 = st.executeQuery(displayEventQuery);
									String OlympicGame, EventName, medal;
									int participantID, position;
									if(res1.next() == false){
										System.out.println("No results match this query");
									}
									else{
										System.out.printf("%-15s %-25s %-15s %-10s %-10s%n", "Olympic Game", "Event Name", "Participant ID", "Position", "Medal");
										do {
											OlympicGame = res1.getString("OlympicGame");
											EventName = res1.getString("EventName");
											medal = res1.getString("medal_title");
											participantID = res1.getInt("participant_id");
											position = res1.getInt("position");
											System.out.printf("%-15s %-25s %-15s %-10s %-10s%n", OlympicGame, EventName, participantID, position, medal);
										}while (res1.next());
									}
								}
							}
							else if(functionID == 3){ //country ranking
								int olympicID = 0;
								boolean noQuery = false;
								
								System.out.print("Enter olympic ID of olympics of interest: ");
								try{
									olympicID = input.nextInt();
								}catch(InputMismatchException e){
									System.out.println("Invalid input");
									input.nextLine();
									noQuery = true;
								}
									
								if(!noQuery){
									st = connection.createStatement();
									String countryRankQuery = "SELECT country, Gold, Silver, Bronze, RANK() OVER (order by (Gold*3+Silver*2+Bronze) desc) As Rank, FirstYear from ((SELECT country, count(case when points=3 then 1 else null end) As Gold,count(case when points=2 then 1 else null end) As Silver, count(case when points=1 then 1 else null end) As Bronze from(SELECT DISTINCT olympic_id, s.team_id, country, points FROM (((((OLYMPICS o join SCOREBOARD s on o.olympic_id = s.olympics_id) join TEAM t on s.team_id = t.team_id) join COUNTRY c on t.country_id = c.country_id) join MEDAL m on s.medal_id = m.medal_id)) where s.olympics_id = " + olympicID + ")group by country) natural join (SELECT country, EXTRACT(YEAR from opening_date) As FirstYear from (SELECT country, min(olympics_id) As minOlympics FROM (TEAM t join COUNTRY c on t.country_id = c.country_id) group by country) join OLYMPICS o on minOlympics = o.olympic_id)) order by (Gold*3+Silver*2+Bronze) desc";
									ResultSet res1 = st.executeQuery(countryRankQuery);
									String country, firstyear;
									int gold, silver, bronze, rank;
									if(res1.next() == false){
										System.out.println("No results match this query");
									}
									else{
										System.out.printf("%-25s %-10s %-10s %-10s %-10s %-15s%n", "Country", "Gold", "Silver", "Bronze", "Rank", "First Year Competing");
										do {
											country = res1.getString("country");
											firstyear = res1.getString("FirstYear");
											gold = res1.getInt("gold");
											silver = res1.getInt("silver");
											bronze = res1.getInt("bronze");
											rank = res1.getInt("rank");
											System.out.printf("%-25s %-10s %-10s %-10s %-10s %-15s%n", country, gold, silver, bronze, rank, firstyear);
										}while (res1.next());
									}
								}
							}
							else if(functionID == 4){ //top k athletes
								boolean noQuery = false;
								int olympicID = 0;
								int k = 0;
								
								System.out.print("Enter olympic ID of olympics of interest: ");
								try{
									olympicID = input.nextInt();
								}catch(InputMismatchException e){
									System.out.println("Invalid input");
									input.nextLine();
									noQuery = true;
								}
								
								if(!noQuery){
									System.out.print("Enter the number of athletes you want to see: ");
									try{
										k = input.nextInt();
									}catch(InputMismatchException e){
										System.out.println("Invalid input");
										input.nextLine();
										noQuery = true;
									}
								}	

								if(!noQuery){
									st = connection.createStatement();
									String topKAthletesQuery = "SELECT participantName, Gold, Silver, Bronze, RANK() OVER (order by (Gold*3+Silver*2+Bronze) desc) As Rank FROM (SELECT (fname || ' ' || lname) As ParticipantName, count(case when points=3 then 1 else null end) As Gold,count(case when points=2 then 1 else null end) As Silver, count(case when points=1 then 1 else null end) As Bronze FROM ((SCOREBOARD s join PARTICIPANT p on s.participant_id = p.participant_id) join MEDAL m on s.medal_id = m.medal_id)where s.olympics_id = " + olympicID + " group by fname, lname order by (Gold*3+Silver*2+Bronze) desc, ParticipantName asc fetch first " + k + " rows only)";
									ResultSet res1 = st.executeQuery(topKAthletesQuery);
									String participantName;
									int gold, silver, bronze;
									int rank;
									if(res1.next() == false){
										System.out.println("No results match this query");
									}
									else{
										System.out.printf("%-10s %-30s %-10s %-10s %-10s%n", "Rank", "Participant Name", "Gold", "Silver", "Bronze");
										do {
											participantName = res1.getString("ParticipantName");
											gold = res1.getInt("gold");
											silver = res1.getInt("silver");
											bronze = res1.getInt("bronze");
											rank = res1.getInt("rank");
											System.out.printf("%-10s %-30s %-10s %-10s %-10s%n", rank, participantName, gold, silver, bronze);
										}while (res1.next());
									}
								}
							}
							else if(functionID == 5){ //connected athletes
								boolean noQuery = false;
								int participantID = 0;
								int olympicID = 0;
								int n = 0;
								
								System.out.print("Enter participantID to search connections for: ");
								try{
									participantID = input.nextInt();
								}catch(InputMismatchException e){
									System.out.println("Invalid input");
									input.nextLine();
									noQuery = true;
								}
								
								if(!noQuery){
									System.out.print("Enter olympic ID to start search: ");
									try{
										olympicID = input.nextInt();
									}catch(InputMismatchException e){
										System.out.println("Invalid input");
										input.nextLine();
										noQuery = true;
									}
								}
								
								if(!noQuery){
									System.out.print("Enter hops to make: ");
									try{
										n = input.nextInt();
									}catch(InputMismatchException e){
										System.out.println("Invalid input");
										input.nextLine();
										noQuery = true;
									}
								}
								
								if(!noQuery){
									st = connection.createStatement();
									String participantNameQuery = "SELECT (fname || ' ' || lname) As Name FROM PARTICIPANT where participant_id=" + participantID;
									ResultSet res1 = st.executeQuery(participantNameQuery);
									String startConnectionName = null;
									while(res1.next()){
										startConnectionName = res1.getString("Name");
									}
									
									st = connection.createStatement();
									String connectedAthletesQuery = "SELECT (fname || ' ' || lname) As Name FROM PARTICIPANT natural join (SELECT DISTINCT(participant_id) FROM SCOREBOARD natural join (SELECT participant_id FROM SCOREBOARD where event_id = (SELECT event_id FROM SCOREBOARD where olympics_id =" + olympicID + " and participant_id =" + participantID + ")) where olympics_id =" + (olympicID-n) + "and participant_id !=" + participantID + ")";
									res1 = st.executeQuery(connectedAthletesQuery);
									String participantName;
									if(res1.next() == false){
										System.out.println("No results match this query");
									}
									else{
										System.out.printf("%-30s %-30s\n", "Athlete Name", "Connected To");
										do {
											participantName = res1.getString("Name");
											System.out.printf("%-30s %-30s%n", startConnectionName, participantName);
										}while (res1.next());
									}
								}
							}
							else if(functionID == 6){ //logout
								String curDate = "dd-MMM-yyyy";
								String dateAsString = new SimpleDateFormat(curDate).format(new Date());
							
								st = connection.createStatement();
								connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
								try {
									connection.setAutoCommit(false);
									st.executeUpdate("UPDATE USER_ACCOUNT set last_login='" + dateAsString + "' where username = '" + usernameEntry + "'");
									connection.commit();
									guestKeepGoing = false;
									System.out.println("You have successfully logged out of the system");
								} catch (SQLException e1) {
									System.out.println(e1.toString());
									try {
										connection.rollback();
									} catch (SQLException e2) {
										System.out.println(e2.toString());
									}
								}
								connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
							}
							else if(functionID == 7){ //exit system
								guestKeepGoing = false;
								keepGoing = false;
							}
						}
					}
				} else {
					System.out.println("Username and/or password not recognized");
				}
			}
			else if(roleID == 4){
				keepGoing = false;
			}
		}
    }
}