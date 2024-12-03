/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.rest.entities;

import com.atlassian.confluence.plugins.rest.entities.SearchResultEntity;
import com.atlassian.confluence.plugins.rest.entities.SearchResultGroupEntity;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="results")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class SearchResultEntityList {
    @XmlElement(name="result")
    private List<SearchResultEntity> results = Lists.newArrayListWithExpectedSize((int)0);
    @XmlElement(name="group")
    private List<SearchResultGroupEntity> groups = Lists.newArrayListWithExpectedSize((int)0);
    @XmlElement(name="totalSize")
    private int totalSize = 0;

    public List<SearchResultEntity> getResults() {
        return this.results;
    }

    public void setResults(List<SearchResultEntity> results) {
        this.results = results;
    }

    public List<SearchResultGroupEntity> getGroups() {
        return this.groups;
    }

    public void setGroups(List<SearchResultGroupEntity> groups) {
        this.groups = groups;
    }

    public int getTotalSize() {
        return this.totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public String toString() {
        return new StringJoiner(", ", SearchResultEntityList.class.getSimpleName() + "[", "]").add("results=" + this.results).add("groups=" + this.groups).add("totalSize=" + this.totalSize).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SearchResultEntityList)) {
            return false;
        }
        SearchResultEntityList that = (SearchResultEntityList)o;
        return this.totalSize == that.totalSize && Objects.equals(this.results, that.results) && Objects.equals(this.groups, that.groups);
    }

    public int hashCode() {
        return Objects.hash(this.totalSize, this.results, this.groups);
    }
}

