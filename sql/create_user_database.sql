show databases;

create database armorcode;
use armorcode;

ALTER TABLE Ticket
ADD COLUMN org_id INT;


select * from Ticket;

select * from Ticket where finding_id = "182581";
select * from Ticket;
ALTER TABLE Ticket
ADD finding_id BIGINT;

delete from Ticket;

SET SQL_SAFE_UPDATES = 0;


create table Organization(
org_id INT AUTO_INCREMENT PRIMARY KEY,
org_name VARCHAR(255) NOT NULL
);

insert into Organization values
(1,"ArmorCode"),
(2,"Google"),
(3,"Apple");

select * from Organization;

drop table Organization;

select * from Ticket;

UPDATE Ticket SET org_id = 1 WHERE finding_id = 552213;


CREATE TABLE Employee (
    email VARCHAR(255) PRIMARY KEY NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    role VARCHAR(255),
    org_id INT,
    password VARCHAR(255) NOT NULL,
    FOREIGN KEY (org_id) REFERENCES Organization(org_id)
);

ALTER TABLE Employee
ADD photo VARCHAR(255);


-- drop table Employee;

select * from Employee;
select * from Organization;


INSERT INTO Employee (email, first_name, last_name, role, org_id, password) VALUES
("praveen.kumar@armorcode.io", "Praveen", "Kumar", "Admin", 1, "8622f0f69c91819119a8acf60a248d7b36fdb7ccf857ba8f85cf7f2767ff8265"),
("kumarp222909@gmail.com", "Prince", "Kumar", "Developer", 1, "8622f0f69c91819119a8acf60a248d7b36fdb7ccf857ba8f85cf7f2767ff8265"),
("2020ucp1110@mnit.ac.in", "Ayush", "Singh", "User", 1, "8622f0f69c91819119a8acf60a248d7b36fdb7ccf857ba8f85cf7f2767ff8265");

UPDATE Employee SET photo = "praveen.jpeg" WHERE email = "praveen.kumar@armorcode.io";
UPDATE Employee SET photo = "default.jpg" WHERE email = "kumarp222909@gmail.com";
UPDATE Employee SET photo = "default.jpg" WHERE email = "2020ucp1110@mnit.ac.in";


CREATE TABLE Feature(
    feature_id INT AUTO_INCREMENT PRIMARY KEY,
    feature_name VARCHAR(255)
);


CREATE TABLE Privilege(
privilege_id INT AUTO_INCREMENT PRIMARY KEY,
privilege_name VARCHAR(255)
);

select f.feature_name,p.privilege_name,fp.employee_email from Feature f,Privilege p,Feature_Privilege fp where fp.employee_email = "2020ucp1110@mnit.ac.in" and
(f.feature_id = fp.feature_id) and (p.privilege_id = fp.privilege_id);




select count(feature_privilege_id) from Feature_Privilege where employee_email = "2020ucp1110@mnit.ac.in" and feature_id=(select feature_id from Feature where feature_name = "finding") and privilege_id=(select privilege_id from Privilege where privilege_name = "read write");


CREATE TABLE Feature_Privilege (
    feature_privilege_id INT AUTO_INCREMENT PRIMARY KEY,
    feature_id INT,
    privilege_id INT,
    employee_email VARCHAR(255),  -- Foreign key referencing Employee table
    FOREIGN KEY (feature_id) REFERENCES Feature(feature_id),
    FOREIGN KEY (privilege_id) REFERENCES Privilege(privilege_id),
    FOREIGN KEY (employee_email) REFERENCES Employee(email)
);

select * from Privilege;
select * from Feature;

select * from Feature_Privilege;

INSERT INTO Privilege (privilege_name) VALUES
("read"),
("write"),
("scan"),
("close");



select * from Privilege;


INSERT INTO Feature VALUES 
(1,"finding"),
(2,"ticket"),
(3,"runbook"),
(4,"dashboard");
insert into Feature_Privilege (feature_id, privilege_id, employee_email) VALUES 
(1,7,"praveen.kumar@armorcode.io"),
(1,8,"praveen.kumar@armorcode.io"),
(1,9,"praveen.kumar@armorcode.io"),
(1,10,"praveen.kumar@armorcode.io"),
(1,7,"kumarp222909@gmail.com"),
(1,8,"kumarp222909@gmail.com"),
(1,10,"kumarp222909@gmail.com"),
(1,7,"2020ucp1110@mnit.ac.in"),

(2,7,"praveen.kumar@armorcode.io"),
(2,7,"kumarp222909@gmail.com"),
(2,7,"2020ucp1110@mnit.ac.in"),

(3,7,"praveen.kumar@armorcode.io"),
(3,8,"praveen.kumar@armorcode.io"),
(3,7,"kumarp222909@gmail.com"),
(3,8,"kumarp222909@gmail.com"),

(4,7,"praveen.kumar@armorcode.io"),
(4,7,"kumarp222909@gmail.com"),
(4,7,"2020ucp1110@mnit.ac.in");


select * from Ticket;
delete from Ticket;

ALTER TABLE Ticket
modify column updated_at datetime;


































