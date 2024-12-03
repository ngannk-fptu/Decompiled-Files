/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.fugue.Option
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.confluence.api.model.search;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.util.FugueConversionUtil;
import com.atlassian.fugue.Option;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
public class SearchContext {
    public static final SearchContext EMPTY = SearchContext.builder().build();
    @JsonProperty
    private final String spaceKey;
    @JsonProperty
    private final ContentId contentId;
    @JsonProperty
    private final List<ContentStatus> contentStatuses;

    @JsonCreator
    private SearchContext() {
        this(SearchContext.builder());
    }

    private SearchContext(Builder builder) {
        this.spaceKey = builder.spaceKey;
        this.contentId = builder.contentId;
        this.contentStatuses = builder.contentStatuses;
    }

    @Deprecated
    @JsonIgnore
    public Option<ContentId> getContentId() {
        return FugueConversionUtil.toComOption(this.contentId());
    }

    @JsonIgnore
    public Optional<ContentId> contentId() {
        return Optional.ofNullable(this.contentId);
    }

    @Deprecated
    @JsonIgnore
    public Option<String> getSpaceKey() {
        return FugueConversionUtil.toComOption(this.spaceKey());
    }

    @JsonIgnore
    public Optional<String> spaceKey() {
        return Optional.ofNullable(this.spaceKey);
    }

    @Deprecated
    @JsonIgnore
    public Option<List<ContentStatus>> getContentStatuses() {
        return FugueConversionUtil.toComOption(this.contentStatuses());
    }

    @JsonIgnore
    public Optional<List<ContentStatus>> contentStatuses() {
        return Optional.ofNullable(this.contentStatuses);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SearchContext deserialize(String searchContextJson, ObjectMapper objectMapper) {
        if (searchContextJson == null || searchContextJson.isEmpty()) {
            return null;
        }
        try {
            return (SearchContext)objectMapper.readValue(searchContextJson, SearchContext.class);
        }
        catch (IOException ex) {
            throw new BadRequestException("Could not parse Search Context from cql context param " + searchContextJson, ex);
        }
    }

    public static class Builder {
        private String spaceKey;
        private ContentId contentId;
        private List<ContentStatus> contentStatuses;

        private Builder() {
        }

        public Builder spaceKey(String spaceKey) {
            this.spaceKey = spaceKey;
            return this;
        }

        public Builder contentId(ContentId contentId) {
            this.contentId = contentId;
            return this;
        }

        public Builder contentStatus(List<ContentStatus> contentStatuses) {
            this.contentStatuses = contentStatuses;
            return this;
        }

        public SearchContext build() {
            return new SearchContext(this);
        }
    }
}

