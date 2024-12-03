/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize$Inclusion
 */
package com.atlassian.confluence.plugins.conluenceview.rest.params;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class PagesSearchParam {
    List<Long> pageIds;
    String cacheToken;
    Integer limit;
    Integer start;
    String searchString;

    public String getCacheToken() {
        return this.cacheToken;
    }

    public List<Long> getPageIds() {
        return this.pageIds;
    }

    public Integer getLimit() {
        return this.limit;
    }

    public Integer getStart() {
        return this.start;
    }

    public String getSearchString() {
        return this.searchString;
    }
}

