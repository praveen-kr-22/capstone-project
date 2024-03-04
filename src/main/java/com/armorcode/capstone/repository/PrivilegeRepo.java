package com.armorcode.capstone.repository;

import com.armorcode.capstone.entity.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PrivilegeRepo extends JpaRepository<Privilege, Long> {
}
