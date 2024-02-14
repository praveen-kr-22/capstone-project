package com.armorcode.capstone.repository;

import com.armorcode.capstone.entity.Findings;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IssuesRepo extends ElasticsearchRepository<Findings, Long> {
}
