/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.rest.entities;

import com.atlassian.confluence.plugins.rest.entities.SearchResultEntity;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="group")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class SearchResultGroupEntity {
    @XmlElement(name="result")
    private List<SearchResultEntity> results;
    @XmlAttribute(name="name")
    private String name;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SearchResultEntity> getResults() {
        return this.results;
    }

    public void setResults(List<SearchResultEntity> results) {
        this.results = results;
    }

    public String toString() {
        return new StringJoiner(", ", SearchResultGroupEntity.class.getSimpleName() + "[", "]").add("results=" + this.results).add("name='" + this.name + "'").toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SearchResultGroupEntity)) {
            return false;
        }
        SearchResultGroupEntity that = (SearchResultGroupEntity)o;
        return Objects.equals(this.results, that.results) && Objects.equals(this.name, that.name);
    }

    public int hashCode() {
        return Objects.hash(this.name, this.results);
    }
}

