package com.armorcode.capstone.service;


import com.armorcode.capstone.entity.Employee;
import com.armorcode.capstone.repository.EmployeeRepo;
import com.armorcode.capstone.util.GenerateSHA256Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    EmployeeRepo employeeRepo;

    public Employee getEmployee(String email){


        return employeeRepo.findByemail(email);
    }
}
