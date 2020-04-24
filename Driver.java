import java.io.*;
import java.util.*;
import java.util.Date;
import java.text.*;
import java.sql.*;

public class Driver{
	
	public static void main(String[] args) throws SQLException{
	
		Olympic testOrganizer = new Olympic();
		Olympic testCoach = new Olympic();
		Olympic testGuest = new Olympic();
		
		System.out.println("This driver program will test the Olympic.java program");
		System.out.println("\nThe first method I will be testing is the login of a user");
		System.out.println("\nShowing successful logins for each user type; organizer, coach, guest");
		
		testOrganizer.login(1, "Hu Jintao", "Beijing");
		testCoach.login(2, "Ruben Magnano", "RM");
		testGuest.login(3, "guest", "GUEST");
		
		System.out.println("\nShowing unsuccessful login first with invalid role type and then invalid username/password");
	
		testGuest.login(4, "guest", "GUEST");
		testOrganizer.login(1, "Hu Jintao", "Beijing_China");
		
		System.out.println("\nI will now demonstrate the functionality of the organizer functions");
		System.out.println("\nSuccessful creation of a user");
	
		testOrganizer.createUser("test", "test", 3);
		
		System.out.println("\nUnsuccessful creation of a user due to invalid role type");
		
		testOrganizer.createUser("test", "test", 4);
		
		System.out.println("\nSuccessful dropping of user by participant ID, username, and dropping of self. Dropping of self will also log the user out");
		
		testOrganizer.dropUser(50);
		
		testOrganizer.dropUser("Larry Brown");
		
		testOrganizer.dropUser();
		
		System.out.println("\nNote that drop user can be called and executed on an input that is not in the system and will not affect the integrity of the database");
		
		System.out.println("\nLogging back in as different organizer since first was just dropped");
		
		testOrganizer.login(1, "Carlos Arthur Nuzman", "Rio");
		
		System.out.println("\nSuccessful creation of an event");
		
		testOrganizer.createEvent(1, 1, "m", "13-AUG-2004");
		
		System.out.println("\nUnsuccessful creation of event due to invalid date of event");
		
		testOrganizer.createEvent(1, 1, "m", "10-JAN-2004");
		
		System.out.println("\nSuccessful addition of event outcome");
		
		testOrganizer.addEventOutcome(2, 1, 12, 30, 4);
		
		System.out.println("\nUnsuccessful addition of event due to outcome already registered");
		
		testOrganizer.addEventOutcome(2, 18, 86, 226, 1);
		
		System.out.println("\nI will now demonstrate the functionality of the coach functions");
		System.out.println("\nSuccessful creation of team");
		
		testCoach.createTeam("Rio", "2016", "Basketball", "USA", "baloncesto");
		
		System.out.println("\nUnsuccessful creation of team due to not being a coach");
		
		testOrganizer.createTeam("Rio", "2016", "Basketball", "USA", "baloncesto");
		
		System.out.println("\nSuccessful registering of team");
		
		testCoach.registerTeam(12, 3);
		
		System.out.println("\nUnsuccessful registering of team due to event sport not matching team sport");
		
		testCoach.registerTeam(1, 6);
		
		System.out.println("\nSuccessful addition of participant");
		
		testCoach.addParticipant("Alex", "Clewell", "United States", "Pittsburgh", "04-MAY-1998");
		
		System.out.println("\nSuccessful addition of team member");
		
		testCoach.addTeamMember(15, 140);
		
		System.out.println("\nUnsuccessful addition of team member because invalid team ID");
		
		testCoach.addTeamMember(300, 141);
		
		System.out.println("\nSuccessful dropping of a team member");
		
		testCoach.dropTeamMember(100);
		
		System.out.println("\nUnsuccessful dropping of team member because participant not registered in system");
		
		testCoach.dropTeamMember(900);
		
		System.out.println("\nI will now demonstrate the functionality of the guest functions, which can also be called as an organizer or coach");
		System.out.println("\nSuccessful display of a sport");
		
		testGuest.displaySport("Archery");
		
		System.out.println("\nUnsuccessful display of sport because sport not in system");
		
		testGuest.displaySport("Hockey");
		
		System.out.println("\nSuccessful display of event");
		
		testGuest.displayEvent("Rio", "2016", 24);
		
		System.out.println("\nUnsuccessful display of event because event doesn't match olympic input");
		
		testGuest.displayEvent("Rio", "2016", 1);
		
		System.out.println("\nSuccessful display of country ranking");
		
		testGuest.countryRanking(3);
		
		System.out.println("\nUnsuccessful display of country ranking due to invalid olympic ID");
		
		testGuest.countryRanking(6);
		
		System.out.println("\nSuccessful display of top k athletes");
		
		testGuest.topKAthletes(3, 10);
		
		System.out.println("\nUnsuccessful display of top k athletes due to invalid olympic ID");
		
		testGuest.topKAthletes(6, 20);
		
		System.out.println("\nSuccessful display of connected athletes");
		
		testGuest.connectedAthletes(41, 2, 1);
		
		System.out.println("\nUnsuccessful display of connected athletes due to participant not being in olympic ID given");
		
		testGuest.connectedAthletes(168, 4, 1);
		
		System.out.println("\nSuccessful logout of each of the users");
		
		testOrganizer.logout();
		testCoach.logout();
		testGuest.logout();
	}
}