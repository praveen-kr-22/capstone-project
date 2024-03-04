package com.armorcode.capstone.repository;

import com.armorcode.capstone.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepo extends JpaRepository<Organization, Integer> {

    Organization findByid(int id);

}
