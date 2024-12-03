/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Page
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.createcontent.rest.entities;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.createcontent.services.model.BlueprintPage;
import org.codehaus.jackson.annotate.JsonProperty;

public class PageEntity {
    @JsonProperty
    private long pageId;
    @JsonProperty
    private String title;
    @JsonProperty
    private String spaceKey;
    @JsonProperty
    private String url;
    @JsonProperty
    private PageEntity indexPage;
    @JsonProperty
    private String createSuccessRedirectUrl;

    public PageEntity() {
    }

    private PageEntity(Page page, String baseUrl) {
        this.pageId = page.getId();
        this.spaceKey = page.getSpaceKey();
        this.title = page.getTitle();
        this.url = baseUrl + page.getUrlPath();
        this.createSuccessRedirectUrl = this.url;
    }

    public PageEntity(BlueprintPage blueprintPage, String baseUrl) {
        this(blueprintPage.getPage(), baseUrl);
        if (blueprintPage.getIndexPage() != null) {
            this.indexPage = new PageEntity(blueprintPage.getIndexPage(), baseUrl);
        }
    }

    public long getPageId() {
        return this.pageId;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public String getTitle() {
        return this.title;
    }

    public String getUrl() {
        return this.url;
    }

    public PageEntity getIndexPage() {
        return this.indexPage;
    }

    public String getCreateSuccessRedirectUrl() {
        return this.createSuccessRedirectUrl;
    }

    public void setCreateSuccessRedirectUrl(String createSuccessRedirectUrl) {
        this.createSuccessRedirectUrl = createSuccessRedirectUrl;
    }
}

