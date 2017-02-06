package com.yyh.repository;

import com.yyh.domain.LawEntry;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the LawEntry entity.
 */
@SuppressWarnings("unused")
public interface LawEntryRepository extends JpaRepository<LawEntry,Long> {

}
