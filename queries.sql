--Successful login of each user type
SELECT * FROM USER_ACCOUNT where username = 'Hu Jintao' and passkey = 'Beijing' and role_id = 1;
SELECT * FROM USER_ACCOUNT where username = 'Ruben Magnano' and passkey = 'RM' and role_id = 2;
SELECT * FROM USER_ACCOUNT where username = 'guest' and passkey = 'GUEST' and role_id = 3;

--Unsuccessful login first with invalid role type and then invalid username/password
SELECT * FROM USER_ACCOUNT where username = 'guest' and passkey = 'GUEST' and role_id = 4;
SELECT * FROM USER_ACCOUNT where username = 'Hu Jintao' and passkey = 'Beijing_China' and role_id = 1;

--Organizer functions
--Successful creation of user
SELECT * FROM USER_ACCOUNT where username = 'test' and passkey = 'test' and role_id = 3;
INSERT INTO USER_ACCOUNT(user_id,username,passkey,role_id,last_login) values(SEQ_USER_ACCOUNT.NEXTVAL,'test','test',3,sysdate);
SELECT * FROM USER_ACCOUNT where username = 'test' and passkey = 'test' and role_id = 3;

--Unsuccessful creation of user due to invalid role type
INSERT INTO USER_ACCOUNT(user_id,username,passkey,role_id,last_login) values(SEQ_USER_ACCOUNT.NEXTVAL,'test','test',4,sysdate);

--Successful dropping of user by participant ID, username
--Note dropping of self not simulated here because utilizes dropping by username
SELECT * FROM USER_ACCOUNT where user_id = 50 or username = 'Larry Brown';
DELETE FROM USER_ACCOUNT WHERE user_id = 50;
DELETE FROM USER_ACCOUNT where username = 'Larry Brown';
SELECT * FROM USER_ACCOUNT where user_id = 50 or username = 'Larry Brown';

--Since deleting self wasn't simulated neither will logging back in as a different organizer
--Successful creation of event
SELECT * FROM EVENT where sport_id = 1 and venue_id = 1 and gender = 'm';
INSERT INTO EVENT(event_id,sport_id,venue_id,gender,event_time) values(SEQ_EVENT.NEXTVAL,1,1,'m','13-AUG-2004');
SELECT * FROM EVENT where sport_id = 1 and venue_id = 1 and gender = 'm';

--Unsuccessful creation of event due to invalid date input
INSERT INTO EVENT(event_id,sport_id,venue_id,gender,event_time) values(SEQ_EVENT.NEXTVAL,1,1,'m','10-JAN-2004');

--Successful addition of event outcome
SELECT * FROM SCOREBOARD where olympics_id = 2 and event_id = 1 and team_id = 12 and participant_id = 30;
INSERT INTO SCOREBOARD(olympics_id,event_id,team_id,participant_id,position,medal_id) values(2, 1, 12, 30, 4,null);
SELECT * FROM SCOREBOARD where olympics_id = 2 and event_id = 1 and team_id = 12 and participant_id = 30;

--Unsuccessful addition of event outcome due to outcome already registered
INSERT INTO SCOREBOARD(olympics_id,event_id,team_id,participant_id,position,medal_id) values(2, 18, 86, 226, 1,null);

--Coach functions
--Successful creation of team
SELECT * FROM TEAM where olympics_id = 4 and country_id = 40 and sport_id = 1;
INSERT INTO TEAM(team_id,olympics_id,team_name,country_id,sport_id,coach_id) values(SEQ_TEAM.NEXTVAL,4,'baloncesto',40,1,260);
SELECT * FROM TEAM where olympics_id = 4 and country_id = 40 and sport_id = 1;

--Unsuccessful creation of team due to invalid olympic input
INSERT INTO TEAM(team_id,olympics_id,team_name,country_id,sport_id,coach_id) values(SEQ_TEAM.NEXTVAL,0,'baloncesto',40,1,0);

--Successful registering of team
SELECT * FROM EVENT_PARTICIPATION where event_id = 3 and team_id = 12;
INSERT INTO EVENT_PARTICIPATION(event_id,team_id,status) values(3,12,'e');
SELECT * FROM EVENT_PARTICIPATION where event_id = 3 and team_id = 12;

--Unsuccessful registering of team due to event sport not matching team sport
INSERT INTO EVENT_PARTICIPATION(event_id,team_id,status) values(6,1,'e');

--Successful addition of participant
SELECT * FROM PARTICIPANT where fname = 'Alex' and lname = 'Clewell';
INSERT INTO PARTICIPANT(participant_id, fname, lname, nationality, birth_place, dob) values(SEQ_PARTICIPANT.NEXTVAL,'Alex','Clewell','United States','Pittsburgh','04-MAY-1998');
SELECT * FROM PARTICIPANT where fname = 'Alex' and lname = 'Clewell';

--Successful addition of team member
SELECT * FROM TEAM_MEMBER where team_id = 15 and participant_id = 140;
INSERT INTO TEAM_MEMBER(team_id, participant_id) values(15,140);
SELECT * FROM TEAM_MEMBER where team_id = 15 and participant_id = 140;

--Unsuccessful addition of team member because invalid team ID
INSERT INTO TEAM_MEMBER(team_id, participant_id) values(300,141);

--Successful dropping of a team member
SELECT * FROM PARTICIPANT where participant_id = 100;
DELETE FROM PARTICIPANT WHERE participant_id = 100;
SELECT * FROM PARTICIPANT where participant_id = 100;

--Unsuccessful dropping of a team member because participant not registered in system
DELETE FROM PARTICIPANT WHERE participant_id = 900;

--Guest functions, also can be called as organizer or coach
--Successful display of sport
SELECT s.dob, (sport_name || ' ' || sc.event_id) As EventName, e.gender, (p.fname || ' ' || p.lname) As Name, country_code, medal_title FROM (((((SPORT s join EVENT e on s.sport_id = e.sport_id) join SCOREBOARD sc on e.event_id = sc.event_id) join PARTICIPANT p on sc.participant_id = p.participant_id) join MEDAL m on sc.medal_id = m.medal_id) join COUNTRY c on p.nationality = c.country) where sport_name = 'Archery' order by position asc, EventName asc;

--Unsuccessful display of sport because sport not in system
SELECT s.dob, (sport_name || ' ' || sc.event_id) As EventName, e.gender, (p.fname || ' ' || p.lname) As Name, country_code, medal_title FROM (((((SPORT s join EVENT e on s.sport_id = e.sport_id) join SCOREBOARD sc on e.event_id = sc.event_id) join PARTICIPANT p on sc.participant_id = p.participant_id) join MEDAL m on sc.medal_id = m.medal_id) join COUNTRY c on p.nationality = c.country) where sport_name = 'Hockey' order by position asc, EventName asc;

--Successful display of event
SELECT (host_city || ' ' || EXTRACT(YEAR from opening_date)) As OlympicGame, (sport_name || ' ' || s.event_id) As EventName, participant_id, position, medal_title FROM ((((SCOREBOARD s join EVENT e on s.event_id = e.event_id) join OLYMPICS o on s.olympics_id = o.olympic_id) join MEDAL m on s.medal_id = m.medal_id) join SPORT sp on e.sport_id = sp.sport_id) WHERE olympics_id = 4 and s.event_id = 24 order by position asc;

--Unsuccessful display of event because event doesn't match olympic input
SELECT (host_city || ' ' || EXTRACT(YEAR from opening_date)) As OlympicGame, (sport_name || ' ' || s.event_id) As EventName, participant_id, position, medal_title FROM ((((SCOREBOARD s join EVENT e on s.event_id = e.event_id) join OLYMPICS o on s.olympics_id = o.olympic_id) join MEDAL m on s.medal_id = m.medal_id) join SPORT sp on e.sport_id = sp.sport_id) WHERE olympics_id = 4 and s.event_id = 1 order by position asc;

--Successful display of country ranking
SELECT country, Gold, Silver, Bronze, RANK() OVER (order by (Gold*3+Silver*2+Bronze) desc) As Rank, FirstYear from ((SELECT country, count(case when points=3 then 1 else null end) As Gold,count(case when points=2 then 1 else null end) As Silver, count(case when points=1 then 1 else null end) As Bronze from(SELECT DISTINCT olympic_id, s.team_id, country, points FROM (((((OLYMPICS o join SCOREBOARD s on o.olympic_id = s.olympics_id) join TEAM t on s.team_id = t.team_id) join COUNTRY c on t.country_id = c.country_id) join MEDAL m on s.medal_id = m.medal_id)) where s.olympics_id = 3)group by country) natural join (SELECT country, EXTRACT(YEAR from opening_date) As FirstYear from (SELECT country, min(olympics_id) As minOlympics FROM (TEAM t join COUNTRY c on t.country_id = c.country_id) group by country) join OLYMPICS o on minOlympics = o.olympic_id)) order by (Gold*3+Silver*2+Bronze) desc;

--Unsuccessful display of country ranking due to invalid olympic ID
SELECT country, Gold, Silver, Bronze, RANK() OVER (order by (Gold*3+Silver*2+Bronze) desc) As Rank, FirstYear from ((SELECT country, count(case when points=3 then 1 else null end) As Gold,count(case when points=2 then 1 else null end) As Silver, count(case when points=1 then 1 else null end) As Bronze from(SELECT DISTINCT olympic_id, s.team_id, country, points FROM (((((OLYMPICS o join SCOREBOARD s on o.olympic_id = s.olympics_id) join TEAM t on s.team_id = t.team_id) join COUNTRY c on t.country_id = c.country_id) join MEDAL m on s.medal_id = m.medal_id)) where s.olympics_id = 6)group by country) natural join (SELECT country, EXTRACT(YEAR from opening_date) As FirstYear from (SELECT country, min(olympics_id) As minOlympics FROM (TEAM t join COUNTRY c on t.country_id = c.country_id) group by country) join OLYMPICS o on minOlympics = o.olympic_id)) order by (Gold*3+Silver*2+Bronze) desc;

--Successful display of top k athletes
SELECT participantName, Gold, Silver, Bronze, RANK() OVER (order by (Gold*3+Silver*2+Bronze) desc) As Rank FROM (SELECT (fname || ' ' || lname) As ParticipantName, count(case when points=3 then 1 else null end) As Gold,count(case when points=2 then 1 else null end) As Silver, count(case when points=1 then 1 else null end) As Bronze FROM ((SCOREBOARD s join PARTICIPANT p on s.participant_id = p.participant_id) join MEDAL m on s.medal_id = m.medal_id)where s.olympics_id = 3 group by fname, lname order by (Gold*3+Silver*2+Bronze) desc, ParticipantName asc fetch first 10 rows only);

--Unsuccessful display of top k athletes due to invalid olympic ID
SELECT participantName, Gold, Silver, Bronze, RANK() OVER (order by (Gold*3+Silver*2+Bronze) desc) As Rank FROM (SELECT (fname || ' ' || lname) As ParticipantName, count(case when points=3 then 1 else null end) As Gold,count(case when points=2 then 1 else null end) As Silver, count(case when points=1 then 1 else null end) As Bronze FROM ((SCOREBOARD s join PARTICIPANT p on s.participant_id = p.participant_id) join MEDAL m on s.medal_id = m.medal_id)where s.olympics_id = 6 group by fname, lname order by (Gold*3+Silver*2+Bronze) desc, ParticipantName asc fetch first 20 rows only);

--Successful display of connected athletes
--Note this will display the list of connected athletes and the output in java has a column of the starting athlete that is not shown here
SELECT (fname || ' ' || lname) As Name FROM PARTICIPANT natural join (SELECT DISTINCT(participant_id) FROM SCOREBOARD natural join (SELECT participant_id FROM SCOREBOARD where event_id = (SELECT event_id FROM SCOREBOARD where olympics_id = 2 and participant_id = 41)) where olympics_id = (2-1) and participant_id != 41);

--Unsuccessful display of connected athletes due to participant not being in olympic ID given
SELECT (fname || ' ' || lname) As Name FROM PARTICIPANT natural join (SELECT DISTINCT(participant_id) FROM SCOREBOARD natural join (SELECT participant_id FROM SCOREBOARD where event_id = (SELECT event_id FROM SCOREBOARD where olympics_id = 4 and participant_id = 168)) where olympics_id = (4-1) and participant_id != 168);

--Successful logout of each of the users
UPDATE USER_ACCOUNT set last_login = sysdate where username = 'Hu Jintao';
UPDATE USER_ACCOUNT set last_login = sysdate where username = 'Ruben Magnano';
UPDATE USER_ACCOUNT set last_login = sysdate where username = 'guest';

SELECT * FROM USER_ACCOUNT where username = 'Hu Jintao' or username = 'Ruben Magnano' or username = 'guest';