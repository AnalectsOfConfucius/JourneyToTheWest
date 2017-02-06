package com.yyh.repository.search;

import com.yyh.domain.LawEntry;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the LawEntry entity.
 */
public interface LawEntrySearchRepository extends ElasticsearchRepository<LawEntry, Long> {
}
