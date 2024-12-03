/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.search.actions.json;

import com.atlassian.confluence.search.actions.json.ContentNameMatch;
import com.atlassian.confluence.search.contentnames.QueryToken;
import com.atlassian.confluence.search.contentnames.ResultTemplate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.builder.ToStringBuilder;

@XmlRootElement
public class ContentNameSearchResult
implements Serializable {
    private String statusMessage;
    private List<String> queryTokens;
    private List<List<ContentNameMatch>> matches = new ArrayList<List<ContentNameMatch>>(ResultTemplate.DEFAULT.getMaximumResults());
    private String query;
    private int totalSize;

    public ContentNameSearchResult(String query) {
        this.query = query;
    }

    @XmlElement
    public List<String> getQueryTokens() {
        return this.queryTokens;
    }

    public void setQueryTokens(List<QueryToken> queryTokens) {
        this.queryTokens = queryTokens.stream().map(QueryToken::getText).collect(Collectors.toList());
    }

    @XmlElement
    public String getStatusMessage() {
        return this.statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public void addMatchGroup(List<ContentNameMatch> matchGroup) {
        this.matches.add(matchGroup);
        this.totalSize += matchGroup.size();
    }

    @XmlElement
    public List<List<ContentNameMatch>> getContentNameMatches() {
        return this.matches;
    }

    @XmlElement
    public int getTotalSize() {
        return this.totalSize;
    }

    @XmlElement
    public String getQuery() {
        return this.query;
    }

    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(null);
        builder.append("status", (Object)this.statusMessage);
        builder.append("queryTokens", this.queryTokens);
        builder.append("matches", this.matches);
        return builder.toString();
    }
}

