package com.armorcode.capstone.repository;

import com.armorcode.capstone.entity.Employee;
import com.armorcode.capstone.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee,String> {
//    @Query("SELECT e.email, e.firstName, e.lastName, e.role, o.orgName FROM Employee e LEFT JOIN e.organization o WHERE e.email = ?1")
    Employee findByemail(String email);
}
