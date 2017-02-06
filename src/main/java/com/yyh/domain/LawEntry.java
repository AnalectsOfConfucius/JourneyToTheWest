package com.yyh.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A LawEntry.
 */
@Entity
@Table(name = "law_entry")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "lawentry")
public class LawEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 128)
    @Column(name = "law_entry_title", length = 128, nullable = false)
    private String lawEntryTitle;

    @NotNull
    @Size(max = 2048)
    @Column(name = "law_entry_content", length = 2048, nullable = false)
    private String lawEntryContent;

    @ManyToOne
    private Law law;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLawEntryTitle() {
        return lawEntryTitle;
    }

    public LawEntry lawEntryTitle(String lawEntryTitle) {
        this.lawEntryTitle = lawEntryTitle;
        return this;
    }

    public void setLawEntryTitle(String lawEntryTitle) {
        this.lawEntryTitle = lawEntryTitle;
    }

    public String getLawEntryContent() {
        return lawEntryContent;
    }

    public LawEntry lawEntryContent(String lawEntryContent) {
        this.lawEntryContent = lawEntryContent;
        return this;
    }

    public void setLawEntryContent(String lawEntryContent) {
        this.lawEntryContent = lawEntryContent;
    }

    public Law getLaw() {
        return law;
    }

    public LawEntry law(Law law) {
        this.law = law;
        return this;
    }

    public void setLaw(Law law) {
        this.law = law;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LawEntry lawEntry = (LawEntry) o;
        if (lawEntry.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, lawEntry.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "LawEntry{" +
            "id=" + id +
            ", lawEntryTitle='" + lawEntryTitle + "'" +
            ", lawEntryContent='" + lawEntryContent + "'" +
            '}';
    }
}
