package com.yyh.service;

import com.yyh.domain.Law;
import com.yyh.repository.LawRepository;
import com.yyh.repository.search.LawSearchRepository;
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
 * Service Implementation for managing Law.
 */
@Service
@Transactional
public class LawService {

    private final Logger log = LoggerFactory.getLogger(LawService.class);
    
    @Inject
    private LawRepository lawRepository;

    @Inject
    private LawSearchRepository lawSearchRepository;

    /**
     * Save a law.
     *
     * @param law the entity to save
     * @return the persisted entity
     */
    public Law save(Law law) {
        log.debug("Request to save Law : {}", law);
        Law result = lawRepository.save(law);
        lawSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the laws.
     *  
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public List<Law> findAll() {
        log.debug("Request to get all Laws");
        List<Law> result = lawRepository.findAll();

        return result;
    }

    /**
     *  Get one law by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Law findOne(Long id) {
        log.debug("Request to get Law : {}", id);
        Law law = lawRepository.findOne(id);
        return law;
    }

    /**
     *  Delete the  law by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Law : {}", id);
        lawRepository.delete(id);
        lawSearchRepository.delete(id);
    }

    /**
     * Search for the law corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Law> search(String query) {
        log.debug("Request to search Laws for query {}", query);
        return StreamSupport
            .stream(lawSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
