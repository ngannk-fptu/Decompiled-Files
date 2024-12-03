/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.confluence.plugins.confluence_kb_space_blueprint.rest.request;

import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@XmlRootElement
@XmlAccessorType(value=XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown=true)
public class SearchRequest {
    public String user;
    public String query;
    public int startIndex = 0;
    public int pageSize = 10;
    public String type;
    public String where;
    public String lastModified;
    public String contributor;
    public String contributorUsername;
    public boolean includeArchivedSpaces;
    public String sessionUuid;
    public Set<String> labels;
    public boolean highlight = true;
    public Set<String> spaceKeys;

    public void setUser(String user) {
        this.user = user;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public void setContributorUsername(String contributorUsername) {
        this.contributorUsername = contributorUsername;
    }

    public void setIncludeArchivedSpaces(boolean includeArchivedSpaces) {
        this.includeArchivedSpaces = includeArchivedSpaces;
    }

    public void setSessionUuid(String sessionUuid) {
        this.sessionUuid = sessionUuid;
    }

    public void setLabels(Set<String> labels) {
        this.labels = labels;
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }
}

