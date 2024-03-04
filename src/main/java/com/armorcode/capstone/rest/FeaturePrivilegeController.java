package com.armorcode.capstone.rest;


import com.armorcode.capstone.entity.Employee;
import com.armorcode.capstone.service.FeaturePrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feature privilege")
public class FeaturePrivilegeController {

    @Autowired
    FeaturePrivilegeService featurePrivilegeService;

    @GetMapping("/employee")
    public Long getEmployee(@RequestParam("email") String email,@RequestParam("featureName") String featureName,@RequestParam("privilegeName") String privilegeName){
        return featurePrivilegeService.findByEmployeeEmailAndFeatureNameAndPrivilegeName(email,privilegeName,featureName);
    }

}
