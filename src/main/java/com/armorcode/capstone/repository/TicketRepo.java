package com.armorcode.capstone.repository;

import com.armorcode.capstone.entity.Findings;
import com.armorcode.capstone.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepo extends JpaRepository<Ticket,String> {

    Page<Ticket> findByorgID(Pageable pageable,int orgID);
    Ticket findByfindingsID(String findingsID);
}
