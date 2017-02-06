package com.yyh.service;

import com.yyh.domain.LawEntry;
import com.yyh.repository.LawEntryRepository;
import com.yyh.repository.search.LawEntrySearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing LawEntry.
 */
@Service
@Transactional
public class LawEntryService {

    private final Logger log = LoggerFactory.getLogger(LawEntryService.class);
    
    @Inject
    private LawEntryRepository lawEntryRepository;

    @Inject
    private LawEntrySearchRepository lawEntrySearchRepository;

    /**
     * Save a lawEntry.
     *
     * @param lawEntry the entity to save
     * @return the persisted entity
     */
    public LawEntry save(LawEntry lawEntry) {
        log.debug("Request to save LawEntry : {}", lawEntry);
        LawEntry result = lawEntryRepository.save(lawEntry);
        lawEntrySearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the lawEntries.
     *  
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public List<LawEntry> findAll() {
        log.debug("Request to get all LawEntries");
        List<LawEntry> result = lawEntryRepository.findAll();

        return result;
    }

    /**
     *  Get one lawEntry by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public LawEntry findOne(Long id) {
        log.debug("Request to get LawEntry : {}", id);
        LawEntry lawEntry = lawEntryRepository.findOne(id);
        return lawEntry;
    }

    /**
     *  Delete the  lawEntry by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete LawEntry : {}", id);
        lawEntryRepository.delete(id);
        lawEntrySearchRepository.delete(id);
    }

    /**
     * Search for the lawEntry corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<LawEntry> search(String query) {
        log.debug("Request to search LawEntries for query {}", query);
        return StreamSupport
            .stream(lawEntrySearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
