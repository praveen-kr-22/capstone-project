package com.armorcode.capstone.repository;

import com.armorcode.capstone.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureRepos extends JpaRepository<Feature, Long> {
}
