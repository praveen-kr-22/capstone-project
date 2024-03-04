package com.armorcode.capstone.service;


import com.armorcode.capstone.entity.Organization;
import com.armorcode.capstone.repository.OrganizationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {

    @Autowired
    OrganizationRepo organizationRepo;

    public Organization getOrganizationDetail(int id){
        return organizationRepo.findByid(id);
    }
}
