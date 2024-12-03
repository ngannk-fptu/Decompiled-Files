/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.web.Icon
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.efi.rest.beans;

import com.atlassian.confluence.api.model.web.Icon;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class RelevantSpaceBean
implements Comparable<RelevantSpaceBean> {
    @JsonProperty
    private String key;
    @JsonProperty
    private String name;
    @JsonProperty
    private Integer spaceRank;
    @JsonProperty
    private Icon icon;

    public RelevantSpaceBean() {
    }

    public RelevantSpaceBean(String key, String name, int spaceRank, Icon icon) {
        this.key = key;
        this.name = name;
        this.spaceRank = spaceRank;
        this.icon = icon;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSpaceRank() {
        return this.spaceRank;
    }

    public void setSpaceRank(Integer spaceRank) {
        this.spaceRank = spaceRank;
    }

    @Override
    public int compareTo(RelevantSpaceBean relevantSpaceBean) {
        return -this.getSpaceRank().compareTo(relevantSpaceBean.getSpaceRank());
    }
}

