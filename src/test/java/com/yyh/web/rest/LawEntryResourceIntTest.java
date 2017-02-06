package com.yyh.web.rest;

import com.yyh.JourneyToTheWestApp;

import com.yyh.domain.LawEntry;
import com.yyh.repository.LawEntryRepository;
import com.yyh.service.LawEntryService;
import com.yyh.repository.search.LawEntrySearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the LawEntryResource REST controller.
 *
 * @see LawEntryResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JourneyToTheWestApp.class)
public class LawEntryResourceIntTest {

    private static final String DEFAULT_LAW_ENTRY_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_LAW_ENTRY_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_LAW_ENTRY_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_LAW_ENTRY_CONTENT = "BBBBBBBBBB";

    @Inject
    private LawEntryRepository lawEntryRepository;

    @Inject
    private LawEntryService lawEntryService;

    @Inject
    private LawEntrySearchRepository lawEntrySearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restLawEntryMockMvc;

    private LawEntry lawEntry;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        LawEntryResource lawEntryResource = new LawEntryResource();
        ReflectionTestUtils.setField(lawEntryResource, "lawEntryService", lawEntryService);
        this.restLawEntryMockMvc = MockMvcBuilders.standaloneSetup(lawEntryResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LawEntry createEntity(EntityManager em) {
        LawEntry lawEntry = new LawEntry()
                .lawEntryTitle(DEFAULT_LAW_ENTRY_TITLE)
                .lawEntryContent(DEFAULT_LAW_ENTRY_CONTENT);
        return lawEntry;
    }

    @Before
    public void initTest() {
        lawEntrySearchRepository.deleteAll();
        lawEntry = createEntity(em);
    }

    @Test
    @Transactional
    public void createLawEntry() throws Exception {
        int databaseSizeBeforeCreate = lawEntryRepository.findAll().size();

        // Create the LawEntry

        restLawEntryMockMvc.perform(post("/api/law-entries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lawEntry)))
            .andExpect(status().isCreated());

        // Validate the LawEntry in the database
        List<LawEntry> lawEntryList = lawEntryRepository.findAll();
        assertThat(lawEntryList).hasSize(databaseSizeBeforeCreate + 1);
        LawEntry testLawEntry = lawEntryList.get(lawEntryList.size() - 1);
        assertThat(testLawEntry.getLawEntryTitle()).isEqualTo(DEFAULT_LAW_ENTRY_TITLE);
        assertThat(testLawEntry.getLawEntryContent()).isEqualTo(DEFAULT_LAW_ENTRY_CONTENT);

        // Validate the LawEntry in ElasticSearch
        LawEntry lawEntryEs = lawEntrySearchRepository.findOne(testLawEntry.getId());
        assertThat(lawEntryEs).isEqualToComparingFieldByField(testLawEntry);
    }

    @Test
    @Transactional
    public void createLawEntryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = lawEntryRepository.findAll().size();

        // Create the LawEntry with an existing ID
        LawEntry existingLawEntry = new LawEntry();
        existingLawEntry.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLawEntryMockMvc.perform(post("/api/law-entries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingLawEntry)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<LawEntry> lawEntryList = lawEntryRepository.findAll();
        assertThat(lawEntryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkLawEntryTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = lawEntryRepository.findAll().size();
        // set the field null
        lawEntry.setLawEntryTitle(null);

        // Create the LawEntry, which fails.

        restLawEntryMockMvc.perform(post("/api/law-entries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lawEntry)))
            .andExpect(status().isBadRequest());

        List<LawEntry> lawEntryList = lawEntryRepository.findAll();
        assertThat(lawEntryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLawEntryContentIsRequired() throws Exception {
        int databaseSizeBeforeTest = lawEntryRepository.findAll().size();
        // set the field null
        lawEntry.setLawEntryContent(null);

        // Create the LawEntry, which fails.

        restLawEntryMockMvc.perform(post("/api/law-entries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lawEntry)))
            .andExpect(status().isBadRequest());

        List<LawEntry> lawEntryList = lawEntryRepository.findAll();
        assertThat(lawEntryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLawEntries() throws Exception {
        // Initialize the database
        lawEntryRepository.saveAndFlush(lawEntry);

        // Get all the lawEntryList
        restLawEntryMockMvc.perform(get("/api/law-entries?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lawEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].lawEntryTitle").value(hasItem(DEFAULT_LAW_ENTRY_TITLE.toString())))
            .andExpect(jsonPath("$.[*].lawEntryContent").value(hasItem(DEFAULT_LAW_ENTRY_CONTENT.toString())));
    }

    @Test
    @Transactional
    public void getLawEntry() throws Exception {
        // Initialize the database
        lawEntryRepository.saveAndFlush(lawEntry);

        // Get the lawEntry
        restLawEntryMockMvc.perform(get("/api/law-entries/{id}", lawEntry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(lawEntry.getId().intValue()))
            .andExpect(jsonPath("$.lawEntryTitle").value(DEFAULT_LAW_ENTRY_TITLE.toString()))
            .andExpect(jsonPath("$.lawEntryContent").value(DEFAULT_LAW_ENTRY_CONTENT.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingLawEntry() throws Exception {
        // Get the lawEntry
        restLawEntryMockMvc.perform(get("/api/law-entries/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLawEntry() throws Exception {
        // Initialize the database
        lawEntryService.save(lawEntry);

        int databaseSizeBeforeUpdate = lawEntryRepository.findAll().size();

        // Update the lawEntry
        LawEntry updatedLawEntry = lawEntryRepository.findOne(lawEntry.getId());
        updatedLawEntry
                .lawEntryTitle(UPDATED_LAW_ENTRY_TITLE)
                .lawEntryContent(UPDATED_LAW_ENTRY_CONTENT);

        restLawEntryMockMvc.perform(put("/api/law-entries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedLawEntry)))
            .andExpect(status().isOk());

        // Validate the LawEntry in the database
        List<LawEntry> lawEntryList = lawEntryRepository.findAll();
        assertThat(lawEntryList).hasSize(databaseSizeBeforeUpdate);
        LawEntry testLawEntry = lawEntryList.get(lawEntryList.size() - 1);
        assertThat(testLawEntry.getLawEntryTitle()).isEqualTo(UPDATED_LAW_ENTRY_TITLE);
        assertThat(testLawEntry.getLawEntryContent()).isEqualTo(UPDATED_LAW_ENTRY_CONTENT);

        // Validate the LawEntry in ElasticSearch
        LawEntry lawEntryEs = lawEntrySearchRepository.findOne(testLawEntry.getId());
        assertThat(lawEntryEs).isEqualToComparingFieldByField(testLawEntry);
    }

    @Test
    @Transactional
    public void updateNonExistingLawEntry() throws Exception {
        int databaseSizeBeforeUpdate = lawEntryRepository.findAll().size();

        // Create the LawEntry

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restLawEntryMockMvc.perform(put("/api/law-entries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lawEntry)))
            .andExpect(status().isCreated());

        // Validate the LawEntry in the database
        List<LawEntry> lawEntryList = lawEntryRepository.findAll();
        assertThat(lawEntryList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteLawEntry() throws Exception {
        // Initialize the database
        lawEntryService.save(lawEntry);

        int databaseSizeBeforeDelete = lawEntryRepository.findAll().size();

        // Get the lawEntry
        restLawEntryMockMvc.perform(delete("/api/law-entries/{id}", lawEntry.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean lawEntryExistsInEs = lawEntrySearchRepository.exists(lawEntry.getId());
        assertThat(lawEntryExistsInEs).isFalse();

        // Validate the database is empty
        List<LawEntry> lawEntryList = lawEntryRepository.findAll();
        assertThat(lawEntryList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchLawEntry() throws Exception {
        // Initialize the database
        lawEntryService.save(lawEntry);

        // Search the lawEntry
        restLawEntryMockMvc.perform(get("/api/_search/law-entries?query=id:" + lawEntry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lawEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].lawEntryTitle").value(hasItem(DEFAULT_LAW_ENTRY_TITLE.toString())))
            .andExpect(jsonPath("$.[*].lawEntryContent").value(hasItem(DEFAULT_LAW_ENTRY_CONTENT.toString())));
    }
}
