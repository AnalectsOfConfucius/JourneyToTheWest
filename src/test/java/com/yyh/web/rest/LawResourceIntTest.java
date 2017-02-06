package com.yyh.web.rest;

import com.yyh.JourneyToTheWestApp;

import com.yyh.domain.Law;
import com.yyh.repository.LawRepository;
import com.yyh.service.LawService;
import com.yyh.repository.search.LawSearchRepository;

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
 * Test class for the LawResource REST controller.
 *
 * @see LawResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JourneyToTheWestApp.class)
public class LawResourceIntTest {

    private static final String DEFAULT_LAW_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_LAW_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_LAW_PUBLISH_DATE = "AAAAAAAAAA";
    private static final String UPDATED_LAW_PUBLISH_DATE = "BBBBBBBBBB";

    private static final String DEFAULT_LAW_PUBLISH_DEPARTMENT = "AAAAAAAAAA";
    private static final String UPDATED_LAW_PUBLISH_DEPARTMENT = "BBBBBBBBBB";

    private static final String DEFAULT_LAW_EFFECTIVE_DATE = "AAAAAAAAAA";
    private static final String UPDATED_LAW_EFFECTIVE_DATE = "BBBBBBBBBB";

    @Inject
    private LawRepository lawRepository;

    @Inject
    private LawService lawService;

    @Inject
    private LawSearchRepository lawSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restLawMockMvc;

    private Law law;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        LawResource lawResource = new LawResource();
        ReflectionTestUtils.setField(lawResource, "lawService", lawService);
        this.restLawMockMvc = MockMvcBuilders.standaloneSetup(lawResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Law createEntity(EntityManager em) {
        Law law = new Law()
                .lawTitle(DEFAULT_LAW_TITLE)
                .lawPublishDate(DEFAULT_LAW_PUBLISH_DATE)
                .lawPublishDepartment(DEFAULT_LAW_PUBLISH_DEPARTMENT)
                .lawEffectiveDate(DEFAULT_LAW_EFFECTIVE_DATE);
        return law;
    }

    @Before
    public void initTest() {
        lawSearchRepository.deleteAll();
        law = createEntity(em);
    }

    @Test
    @Transactional
    public void createLaw() throws Exception {
        int databaseSizeBeforeCreate = lawRepository.findAll().size();

        // Create the Law

        restLawMockMvc.perform(post("/api/laws")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(law)))
            .andExpect(status().isCreated());

        // Validate the Law in the database
        List<Law> lawList = lawRepository.findAll();
        assertThat(lawList).hasSize(databaseSizeBeforeCreate + 1);
        Law testLaw = lawList.get(lawList.size() - 1);
        assertThat(testLaw.getLawTitle()).isEqualTo(DEFAULT_LAW_TITLE);
        assertThat(testLaw.getLawPublishDate()).isEqualTo(DEFAULT_LAW_PUBLISH_DATE);
        assertThat(testLaw.getLawPublishDepartment()).isEqualTo(DEFAULT_LAW_PUBLISH_DEPARTMENT);
        assertThat(testLaw.getLawEffectiveDate()).isEqualTo(DEFAULT_LAW_EFFECTIVE_DATE);

        // Validate the Law in ElasticSearch
        Law lawEs = lawSearchRepository.findOne(testLaw.getId());
        assertThat(lawEs).isEqualToComparingFieldByField(testLaw);
    }

    @Test
    @Transactional
    public void createLawWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = lawRepository.findAll().size();

        // Create the Law with an existing ID
        Law existingLaw = new Law();
        existingLaw.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLawMockMvc.perform(post("/api/laws")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingLaw)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Law> lawList = lawRepository.findAll();
        assertThat(lawList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkLawTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = lawRepository.findAll().size();
        // set the field null
        law.setLawTitle(null);

        // Create the Law, which fails.

        restLawMockMvc.perform(post("/api/laws")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(law)))
            .andExpect(status().isBadRequest());

        List<Law> lawList = lawRepository.findAll();
        assertThat(lawList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLaws() throws Exception {
        // Initialize the database
        lawRepository.saveAndFlush(law);

        // Get all the lawList
        restLawMockMvc.perform(get("/api/laws?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(law.getId().intValue())))
            .andExpect(jsonPath("$.[*].lawTitle").value(hasItem(DEFAULT_LAW_TITLE.toString())))
            .andExpect(jsonPath("$.[*].lawPublishDate").value(hasItem(DEFAULT_LAW_PUBLISH_DATE.toString())))
            .andExpect(jsonPath("$.[*].lawPublishDepartment").value(hasItem(DEFAULT_LAW_PUBLISH_DEPARTMENT.toString())))
            .andExpect(jsonPath("$.[*].lawEffectiveDate").value(hasItem(DEFAULT_LAW_EFFECTIVE_DATE.toString())));
    }

    @Test
    @Transactional
    public void getLaw() throws Exception {
        // Initialize the database
        lawRepository.saveAndFlush(law);

        // Get the law
        restLawMockMvc.perform(get("/api/laws/{id}", law.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(law.getId().intValue()))
            .andExpect(jsonPath("$.lawTitle").value(DEFAULT_LAW_TITLE.toString()))
            .andExpect(jsonPath("$.lawPublishDate").value(DEFAULT_LAW_PUBLISH_DATE.toString()))
            .andExpect(jsonPath("$.lawPublishDepartment").value(DEFAULT_LAW_PUBLISH_DEPARTMENT.toString()))
            .andExpect(jsonPath("$.lawEffectiveDate").value(DEFAULT_LAW_EFFECTIVE_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingLaw() throws Exception {
        // Get the law
        restLawMockMvc.perform(get("/api/laws/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLaw() throws Exception {
        // Initialize the database
        lawService.save(law);

        int databaseSizeBeforeUpdate = lawRepository.findAll().size();

        // Update the law
        Law updatedLaw = lawRepository.findOne(law.getId());
        updatedLaw
                .lawTitle(UPDATED_LAW_TITLE)
                .lawPublishDate(UPDATED_LAW_PUBLISH_DATE)
                .lawPublishDepartment(UPDATED_LAW_PUBLISH_DEPARTMENT)
                .lawEffectiveDate(UPDATED_LAW_EFFECTIVE_DATE);

        restLawMockMvc.perform(put("/api/laws")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedLaw)))
            .andExpect(status().isOk());

        // Validate the Law in the database
        List<Law> lawList = lawRepository.findAll();
        assertThat(lawList).hasSize(databaseSizeBeforeUpdate);
        Law testLaw = lawList.get(lawList.size() - 1);
        assertThat(testLaw.getLawTitle()).isEqualTo(UPDATED_LAW_TITLE);
        assertThat(testLaw.getLawPublishDate()).isEqualTo(UPDATED_LAW_PUBLISH_DATE);
        assertThat(testLaw.getLawPublishDepartment()).isEqualTo(UPDATED_LAW_PUBLISH_DEPARTMENT);
        assertThat(testLaw.getLawEffectiveDate()).isEqualTo(UPDATED_LAW_EFFECTIVE_DATE);

        // Validate the Law in ElasticSearch
        Law lawEs = lawSearchRepository.findOne(testLaw.getId());
        assertThat(lawEs).isEqualToComparingFieldByField(testLaw);
    }

    @Test
    @Transactional
    public void updateNonExistingLaw() throws Exception {
        int databaseSizeBeforeUpdate = lawRepository.findAll().size();

        // Create the Law

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restLawMockMvc.perform(put("/api/laws")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(law)))
            .andExpect(status().isCreated());

        // Validate the Law in the database
        List<Law> lawList = lawRepository.findAll();
        assertThat(lawList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteLaw() throws Exception {
        // Initialize the database
        lawService.save(law);

        int databaseSizeBeforeDelete = lawRepository.findAll().size();

        // Get the law
        restLawMockMvc.perform(delete("/api/laws/{id}", law.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean lawExistsInEs = lawSearchRepository.exists(law.getId());
        assertThat(lawExistsInEs).isFalse();

        // Validate the database is empty
        List<Law> lawList = lawRepository.findAll();
        assertThat(lawList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchLaw() throws Exception {
        // Initialize the database
        lawService.save(law);

        // Search the law
        restLawMockMvc.perform(get("/api/_search/laws?query=id:" + law.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(law.getId().intValue())))
            .andExpect(jsonPath("$.[*].lawTitle").value(hasItem(DEFAULT_LAW_TITLE.toString())))
            .andExpect(jsonPath("$.[*].lawPublishDate").value(hasItem(DEFAULT_LAW_PUBLISH_DATE.toString())))
            .andExpect(jsonPath("$.[*].lawPublishDepartment").value(hasItem(DEFAULT_LAW_PUBLISH_DEPARTMENT.toString())))
            .andExpect(jsonPath("$.[*].lawEffectiveDate").value(hasItem(DEFAULT_LAW_EFFECTIVE_DATE.toString())));
    }
}
