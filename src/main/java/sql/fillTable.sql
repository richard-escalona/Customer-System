BEGIN
    DECLARE personfirstNameId INT DEFAULT 0;
    DECLARE personLastnameId INT DEFAULT 0;
	DECLARE personDOB DATE DEFAULT '1920-03-0';
    DECLARE count INT DEFAULT 1;
    DECLARE personAge INT DEFAULT 0;
    DECLARE personFirstname VARCHAR(50) DEFAULT 'first';
    DECLARE personLastname VARCHAR(50) DEFAULT 'last'; 

    TRUNCATE TABLE people;

    setLoop : LOOP
        SET personfirstNameId = (SELECT 1 + ROUND(RAND() * 49, 0));
        SET personLastnameId = (SELECT 1 + ROUND(RAND() * 49, 0));
        SET personDOB = (SELECT date_format(from_unixtime(RAND() * (unix_timestamp('1950-01-01') - unix_timestamp('2000-01-01')) + unix_timestamp('2000-01-01')), '%Y-%m-%d'));
        SET personAge = (SELECT TIMESTAMPDIFF(YEAR, personDOB, CURDATE()));
        SET personFirstname = (SELECT firstnames.first_name FROM firstnames WHERE firstnames.id = personfirstNameId);
        SET personLastname = (SELECT lastnames.last_name FROM lastnames WHERE lastnames.id = personLastnameId);

        INSERT INTO people (first_name, last_name, age, birth_date) VALUES (personFirstname, personLastname, personAge, personDOB);

        SET count = count + 1;
        IF count <= 10000 THEN
            ITERATE setLoop;
        END IF;
        LEAVE setLoop;
    END LOOP setLoop;

END
