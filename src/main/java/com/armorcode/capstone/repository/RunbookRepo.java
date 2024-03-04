package com.armorcode.capstone.repository;

import com.armorcode.capstone.entity.Runbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RunbookRepo extends JpaRepository<Runbook,Long> {
}
