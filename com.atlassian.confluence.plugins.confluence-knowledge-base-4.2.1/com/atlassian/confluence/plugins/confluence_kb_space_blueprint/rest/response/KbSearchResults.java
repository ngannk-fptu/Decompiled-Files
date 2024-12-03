/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.confluence_kb_space_blueprint.rest.response;

import com.atlassian.confluence.plugins.confluence_kb_space_blueprint.rest.response.KbSearchResult;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement
@XmlAccessorType(value=XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown=true)
public class KbSearchResults {
    private final List<KbSearchResult> results;
    private final Integer total;

    @JsonCreator
    public KbSearchResults(@JsonProperty(value="results") List<KbSearchResult> results, @JsonProperty(value="total") Integer total) {
        this.results = results;
        this.total = total;
    }

    @JsonProperty(value="results")
    public List<KbSearchResult> getResults() {
        return this.results == null ? Collections.emptyList() : this.results;
    }

    @JsonProperty(value="total")
    public Integer getTotal() {
        return this.total;
    }
}

