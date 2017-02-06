package com.yyh.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Law.
 */
@Entity
@Table(name = "law")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "law")
public class Law implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 256)
    @Column(name = "law_title", length = 256, nullable = false)
    private String lawTitle;

    @Size(max = 32)
    @Column(name = "law_publish_date", length = 32)
    private String lawPublishDate;

    @Size(max = 32)
    @Column(name = "law_publish_department", length = 32)
    private String lawPublishDepartment;

    @Size(max = 32)
    @Column(name = "law_effective_date", length = 32)
    private String lawEffectiveDate;

    @OneToMany(mappedBy = "law")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<LawEntry> lawEntries = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLawTitle() {
        return lawTitle;
    }

    public Law lawTitle(String lawTitle) {
        this.lawTitle = lawTitle;
        return this;
    }

    public void setLawTitle(String lawTitle) {
        this.lawTitle = lawTitle;
    }

    public String getLawPublishDate() {
        return lawPublishDate;
    }

    public Law lawPublishDate(String lawPublishDate) {
        this.lawPublishDate = lawPublishDate;
        return this;
    }

    public void setLawPublishDate(String lawPublishDate) {
        this.lawPublishDate = lawPublishDate;
    }

    public String getLawPublishDepartment() {
        return lawPublishDepartment;
    }

    public Law lawPublishDepartment(String lawPublishDepartment) {
        this.lawPublishDepartment = lawPublishDepartment;
        return this;
    }

    public void setLawPublishDepartment(String lawPublishDepartment) {
        this.lawPublishDepartment = lawPublishDepartment;
    }

    public String getLawEffectiveDate() {
        return lawEffectiveDate;
    }

    public Law lawEffectiveDate(String lawEffectiveDate) {
        this.lawEffectiveDate = lawEffectiveDate;
        return this;
    }

    public void setLawEffectiveDate(String lawEffectiveDate) {
        this.lawEffectiveDate = lawEffectiveDate;
    }

    public Set<LawEntry> getLawEntries() {
        return lawEntries;
    }

    public Law lawEntries(Set<LawEntry> lawEntries) {
        this.lawEntries = lawEntries;
        return this;
    }

    public Law addLawEntry(LawEntry lawEntry) {
        lawEntries.add(lawEntry);
        lawEntry.setLaw(this);
        return this;
    }

    public Law removeLawEntry(LawEntry lawEntry) {
        lawEntries.remove(lawEntry);
        lawEntry.setLaw(null);
        return this;
    }

    public void setLawEntries(Set<LawEntry> lawEntries) {
        this.lawEntries = lawEntries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Law law = (Law) o;
        if (law.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, law.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Law{" +
            "id=" + id +
            ", lawTitle='" + lawTitle + "'" +
            ", lawPublishDate='" + lawPublishDate + "'" +
            ", lawPublishDepartment='" + lawPublishDepartment + "'" +
            ", lawEffectiveDate='" + lawEffectiveDate + "'" +
            '}';
    }
}
