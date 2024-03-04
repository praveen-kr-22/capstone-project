package com.armorcode.capstone.service;


import com.armorcode.capstone.entity.Employee;
import com.armorcode.capstone.repository.FeaturePrivilegeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeaturePrivilegeService {

    @Autowired
    FeaturePrivilegeRepo featurePrivilegeRepo;

    public Long findByEmployeeEmailAndFeatureNameAndPrivilegeName(String email, String privilegeName, String featureName){
        return featurePrivilegeRepo.findByEmployeeEmailAndFeatureNameAndPrivilegeName(email,featureName,privilegeName);
    }

    public List<Object[]> findFeatureAndPrivilegeByEmail(String email){
        return featurePrivilegeRepo.findFeatureAndPrivilegeByEmployeeEmail(email);
    }

}
