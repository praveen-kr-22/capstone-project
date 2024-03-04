package com.armorcode.capstone.service;

import com.armorcode.capstone.entity.Runbook;
import com.armorcode.capstone.repository.RunbookRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RunbookService {

    @Autowired
    private RunbookRepo runbookRepo;

    public List<Runbook> getAllRunbooks() {
        return runbookRepo.findAll();
    }

    public Optional<Runbook> getRunbookById(Long id) {
        return runbookRepo.findById(id);
    }

    public Runbook createRunbook(Runbook newRunbook) {
        return runbookRepo.save(newRunbook);
    }

    public Runbook updateRunbook(Long id, Runbook newRunbook) {
        if (runbookRepo.existsById(id)) {
            newRunbook.setId(id);
            return runbookRepo.save(newRunbook);
        }
        return null;
    }

    public void deleteRunbook(Long id) {
        runbookRepo.deleteById(id);
    }
}
