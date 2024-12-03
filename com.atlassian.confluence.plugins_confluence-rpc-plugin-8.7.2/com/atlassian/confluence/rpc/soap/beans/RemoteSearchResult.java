/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Handle
 *  com.atlassian.confluence.core.Addressable
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.persistence.hibernate.HibernateHandle
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.util.GeneralUtil
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.rpc.soap.beans;

import com.atlassian.bonnie.Handle;
import com.atlassian.confluence.core.Addressable;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.util.GeneralUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class RemoteSearchResult {
    private static final boolean HIGHLIGHT = Boolean.parseBoolean(System.getProperty("confluence.soap.search.highlight.enable", "true"));
    private String type;
    private long id = -1L;
    private String title;
    private String url;
    private String excerpt;
    public static final String __PARANAMER_DATA = "<init> com.atlassian.confluence.search.v2.SearchResult searchResult \n<init> com.atlassian.confluence.search.v2.SearchResult,java.lang.String searchResult,query \n<init> com.atlassian.confluence.core.Addressable addressable \nsetExcerpt java.lang.String excerpt \nsetId long id \nsetTitle java.lang.String title \nsetType java.lang.String type \nsetUrl java.lang.String url \n";

    public RemoteSearchResult() {
    }

    public RemoteSearchResult(SearchResult searchResult) {
        this(searchResult, null);
    }

    public RemoteSearchResult(SearchResult searchResult, String query) {
        this.setExcerpt(GeneralUtil.makeFlatSummary((String)searchResult.getContent(), (String)(HIGHLIGHT ? query : null)));
        this.setType(searchResult.getType());
        this.setTitle(searchResult.getDisplayTitle());
        this.setUrl(GeneralUtil.getGlobalSettings().getBaseUrl() + searchResult.getUrlPath());
        Handle handle = searchResult.getHandle();
        if (handle instanceof HibernateHandle) {
            this.setId(((HibernateHandle)searchResult.getHandle()).getId());
        }
    }

    public RemoteSearchResult(Addressable addressable) {
        this.setId(addressable.getId());
        this.setType(addressable.getType());
        this.setTitle(addressable.getDisplayTitle());
        if (addressable instanceof Attachment) {
            this.setUrl(GeneralUtil.getGlobalSettings().getBaseUrl() + ((Attachment)addressable).getDownloadPath());
        } else {
            this.setUrl(GeneralUtil.getGlobalSettings().getBaseUrl() + addressable.getUrlPath());
        }
        if (addressable instanceof ContentEntityObject) {
            this.setExcerpt(((ContentEntityObject)addressable).getExcerpt());
        }
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getType() {
        return this.type;
    }

    public long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getUrl() {
        return this.url;
    }

    public String getExcerpt() {
        return this.excerpt;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this);
    }
}

