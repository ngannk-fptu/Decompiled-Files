/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.search.api.model;

import java.util.Objects;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import org.apache.commons.lang3.StringUtils;

@XmlAccessorType(value=XmlAccessType.FIELD)
public class SearchResultContainer {
    private String name;
    private String url;
    private SearchResultContainer child;

    private SearchResultContainer() {
    }

    @Nullable
    public static SearchResultContainer create(String name, String url, SearchResultContainer child) {
        if (StringUtils.isNotBlank((CharSequence)name) && StringUtils.isNotBlank((CharSequence)url)) {
            return new SearchResultContainer(name, url, child);
        }
        return null;
    }

    public static SearchResultContainer create(String name, String url) {
        return SearchResultContainer.create(name, url, null);
    }

    public SearchResultContainer(String name, String url) {
        this(name, url, null);
    }

    public SearchResultContainer(String name, String url, SearchResultContainer child) {
        if (StringUtils.isBlank((CharSequence)name)) {
            throw new IllegalArgumentException("name cannot be null.");
        }
        if (StringUtils.isBlank((CharSequence)url)) {
            throw new IllegalArgumentException("url cannot be null.");
        }
        this.name = name;
        this.url = url;
        this.child = child;
    }

    public String getName() {
        return this.name;
    }

    public String getUrl() {
        return this.url;
    }

    public SearchResultContainer getChild() {
        return this.child;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SearchResultContainer)) {
            return false;
        }
        SearchResultContainer that = (SearchResultContainer)o;
        return Objects.equals(this.getName(), that.getName()) && Objects.equals(this.getUrl(), that.getUrl()) && Objects.equals(this.getChild(), that.getChild());
    }

    public int hashCode() {
        return Objects.hash(this.getName(), this.getUrl(), this.getChild());
    }
}

