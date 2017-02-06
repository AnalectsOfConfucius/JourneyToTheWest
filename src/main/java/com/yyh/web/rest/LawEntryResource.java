package com.yyh.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.yyh.domain.LawEntry;
import com.yyh.service.LawEntryService;
import com.yyh.web.rest.util.HeaderUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing LawEntry.
 */
@RestController
@RequestMapping("/api")
public class LawEntryResource {

    private final Logger log = LoggerFactory.getLogger(LawEntryResource.class);
        
    @Inject
    private LawEntryService lawEntryService;

    /**
     * POST  /law-entries : Create a new lawEntry.
     *
     * @param lawEntry the lawEntry to create
     * @return the ResponseEntity with status 201 (Created) and with body the new lawEntry, or with status 400 (Bad Request) if the lawEntry has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/law-entries")
    @Timed
    public ResponseEntity<LawEntry> createLawEntry(@Valid @RequestBody LawEntry lawEntry) throws URISyntaxException {
        log.debug("REST request to save LawEntry : {}", lawEntry);
        if (lawEntry.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("lawEntry", "idexists", "A new lawEntry cannot already have an ID")).body(null);
        }
        LawEntry result = lawEntryService.save(lawEntry);
        return ResponseEntity.created(new URI("/api/law-entries/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("lawEntry", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /law-entries : Updates an existing lawEntry.
     *
     * @param lawEntry the lawEntry to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated lawEntry,
     * or with status 400 (Bad Request) if the lawEntry is not valid,
     * or with status 500 (Internal Server Error) if the lawEntry couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/law-entries")
    @Timed
    public ResponseEntity<LawEntry> updateLawEntry(@Valid @RequestBody LawEntry lawEntry) throws URISyntaxException {
        log.debug("REST request to update LawEntry : {}", lawEntry);
        if (lawEntry.getId() == null) {
            return createLawEntry(lawEntry);
        }
        LawEntry result = lawEntryService.save(lawEntry);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("lawEntry", lawEntry.getId().toString()))
            .body(result);
    }

    /**
     * GET  /law-entries : get all the lawEntries.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of lawEntries in body
     */
    @GetMapping("/law-entries")
    @Timed
    public List<LawEntry> getAllLawEntries() {
        log.debug("REST request to get all LawEntries");
        return lawEntryService.findAll();
    }

    /**
     * GET  /law-entries/:id : get the "id" lawEntry.
     *
     * @param id the id of the lawEntry to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the lawEntry, or with status 404 (Not Found)
     */
    @GetMapping("/law-entries/{id}")
    @Timed
    public ResponseEntity<LawEntry> getLawEntry(@PathVariable Long id) {
        log.debug("REST request to get LawEntry : {}", id);
        LawEntry lawEntry = lawEntryService.findOne(id);
        return Optional.ofNullable(lawEntry)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /law-entries/:id : delete the "id" lawEntry.
     *
     * @param id the id of the lawEntry to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/law-entries/{id}")
    @Timed
    public ResponseEntity<Void> deleteLawEntry(@PathVariable Long id) {
        log.debug("REST request to delete LawEntry : {}", id);
        lawEntryService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("lawEntry", id.toString())).build();
    }

    /**
     * SEARCH  /_search/law-entries?query=:query : search for the lawEntry corresponding
     * to the query.
     *
     * @param query the query of the lawEntry search 
     * @return the result of the search
     */
    @GetMapping("/_search/law-entries")
    @Timed
    public List<LawEntry> searchLawEntries(@RequestParam String query) {
        log.debug("REST request to search LawEntries for query {}", query);
        return lawEntryService.search(query);
    }


}
