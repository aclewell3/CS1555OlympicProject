--assign medal trigger
create or replace trigger ASSIGN_MEDAL
before insert or update 
on SCOREBOARD
for each row
BEGIN
    IF :new.position = 1 THEN
        :new.medal_id := 1;
    ELSIF :new.position = 2 THEN   
        :new.medal_id := 2;
    ELSIF :new.position = 3 THEN
        :new.medal_id := 3;
    ELSE
        :new.medal_id := null;
    END IF;
End;
/

--athlete dismissal trigger
create or replace trigger ATHLETE_DISMISSAL
before delete
on PARTICIPANT
for each row
declare
    badTeamID integer;
    badTeamSportID integer;
    badTeamSize integer;
BEGIN
    For counter IN (SELECT * from TEAM_MEMBER where participant_id = :old.participant_id)
    LOOP
        badTeamID := counter.team_id;
        SELECT sport_id into badTeamSportID from TEAM where team_id = badTeamID;
        SELECT team_size into badTeamSize from SPORT where sport_id = badTeamSportID;
        UPDATE EVENT_PARTICIPATION set status = 'n' where team_id = badTeamID;
        IF badTeamSize = 1 THEN
            DELETE FROM TEAM where team_id = badTeamID;
        END IF;
        DELETE FROM TEAM_MEMBER where team_id = badTeamID and participant_id = :old.participant_id;
        DELETE FROM SCOREBOARD where team_id = badTeamID;
    END LOOP;
END;
/ 

--enforce capacity trigger
create or replace trigger ENFORCE_CAPACITY
BEFORE INSERT
ON EVENT
for each row
declare
    venueCapacity integer;
    currentCapacity integer;
BEGIN
    dbms_output.put_line(:new.venue_id);
    SELECT capacity into venueCapacity from VENUE where venue_id = :new.venue_id;
    SELECT count(*) into currentCapacity from EVENT where venue_id = :new.venue_id and event_time = :new.event_time;
    dbms_output.put_line(venueCapacity);
    dbms_output.put_line(currentCapacity);    
    If(currentCapacity >= venueCapacity) THEN
        RAISE_APPLICATION_ERROR(-20001, 'Venue is already at maximum capacity, this event cannot be added');
    END IF;
END;
/

--event participation sport matches trigger
create or replace trigger SPORT_MATCHES
BEFORE INSERT 
ON EVENT_PARTICIPATION
for each row
declare
    eventSportID integer;
    teamSportID integer;
BEGIN
    SELECT sport_id into eventSportID FROM EVENT WHERE event_id = :new.event_id;
    SELECT sport_id into teamSportID FROM TEAM WHERE team_id = :new.team_id;
    IF(eventSportID != teamSportID) THEN
        RAISE_APPLICATION_ERROR(-20001, 'Event sport does not match team sport');
    END IF;
END;
/

--event data is within olympic date range
create or replace trigger EVENT_DATE_CHECK
BEFORE INSERT
ON EVENT
for each row
declare
    olympicID integer;
    olympicStartDate date;
    olympicEndDate date;
BEGIN
    SELECT olympics_id into olympicID FROM VENUE where venue_id = :new.venue_id;
    SELECT opening_date into olympicStartDate FROM OLYMPICS where olympic_id = olympicID;
    SELECT closing_date into olympicEndDate FROM OLYMPICS where olympic_id = olympicID;
    IF(:new.event_time not between olympicStartDate and olympicEndDate) THEN
        RAISE_APPLICATION_ERROR(-20001, 'Event time is not in olympic date range');
    END IF;
END;
/
