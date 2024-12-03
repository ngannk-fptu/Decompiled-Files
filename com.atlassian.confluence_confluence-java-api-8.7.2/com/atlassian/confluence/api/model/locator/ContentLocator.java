/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.joda.time.LocalDate
 */
package com.atlassian.confluence.api.model.locator;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.locator.ContentLocatorBuilder;
import com.atlassian.confluence.api.util.JodaTimeUtils;
import java.time.LocalDate;
import java.util.Arrays;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
public final class ContentLocator {
    @JsonProperty
    private final String title;
    @JsonProperty
    private final String spaceKey;
    @JsonProperty
    private final org.joda.time.LocalDate postingDay;
    @JsonProperty
    private final ContentType[] contentTypes;

    public static ContentLocatorBuilder builder() {
        return new ContentLocatorBuilder();
    }

    @JsonCreator
    private ContentLocator() {
        this.title = null;
        this.spaceKey = null;
        this.postingDay = null;
        this.contentTypes = null;
    }

    ContentLocator(String title, String spaceKey, LocalDate postingDay, ContentType ... contentTypes) {
        this.title = title;
        this.spaceKey = spaceKey;
        this.postingDay = JodaTimeUtils.convert(postingDay);
        this.contentTypes = contentTypes;
    }

    @Deprecated
    ContentLocator(String title, String spaceKey, org.joda.time.LocalDate postingDay, ContentType ... contentTypes) {
        this.title = title;
        this.spaceKey = spaceKey;
        this.postingDay = postingDay;
        this.contentTypes = contentTypes;
    }

    public String getTitle() {
        return this.title;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    @JsonIgnore
    public LocalDate getPostingDate() {
        return JodaTimeUtils.convert(this.postingDay);
    }

    @Deprecated
    public org.joda.time.LocalDate getPostingDay() {
        return this.postingDay;
    }

    public ContentType[] getContentTypes() {
        return this.contentTypes == null ? null : Arrays.copyOf(this.contentTypes, this.contentTypes.length);
    }

    public boolean isForContent(ContentType contentType) {
        return this.contentTypes != null && this.contentTypes.length == 1 && this.contentTypes[0].equals(contentType);
    }
}

