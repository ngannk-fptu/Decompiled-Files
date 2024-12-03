/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.macros.dashboard.recentupdates.rest.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class RecentlyUpdatedContentResourceRequestDto {
    @JsonProperty(value="tab")
    private String tabKey;
    @JsonProperty(value="maxResults")
    int maxResults;
    @JsonProperty(value="showProfilePic")
    private String showProfilePic;
    @JsonProperty(value="labels")
    private String labelsFilter;
    @JsonProperty(value="spaces")
    private String spacesFilter;
    @JsonProperty(value="users")
    private String usersFilter;
    @JsonProperty(value="types")
    private String typesFilter;
    @JsonProperty(value="category")
    private String category;
    @JsonProperty(value="spaceKey")
    private String spaceKey;

    public String getTabKey() {
        return this.tabKey;
    }

    public int getMaxResults() {
        return this.maxResults;
    }

    public String getShowProfilePic() {
        return this.showProfilePic;
    }

    public String getLabelsFilter() {
        return this.labelsFilter;
    }

    public String getSpacesFilter() {
        return this.spacesFilter;
    }

    public String getUsersFilter() {
        return this.usersFilter;
    }

    public String getTypesFilter() {
        return this.typesFilter;
    }

    public String getCategory() {
        return this.category;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }
}

