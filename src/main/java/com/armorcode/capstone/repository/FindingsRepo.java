package com.armorcode.capstone.repository;

import com.armorcode.capstone.entity.Findings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FindingsRepo extends ElasticsearchRepository<Findings, Long> {
    Page<Findings> findByorgID(int orgID, Pageable pageable);

    Iterable<Findings> findByorgID(int orgID);

    Findings findByid(long id);

}
