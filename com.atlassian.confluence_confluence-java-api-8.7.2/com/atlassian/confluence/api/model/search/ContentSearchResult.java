/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 */
package com.atlassian.confluence.api.model.search;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.reference.ExpandedReference;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.search.SearchResult;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@RestEnrichable
@Internal
public final class ContentSearchResult
extends SearchResult<Content> {
    static final String ENTITY_TYPE = "content";
    @JsonDeserialize(as=ExpandedReference.class, contentAs=Content.class)
    @JsonProperty
    private Reference<Content> content;

    @JsonCreator
    private ContentSearchResult() {
        super(ENTITY_TYPE);
    }

    ContentSearchResult(SearchResult.Builder<Content> contentSearchResultBuilder) {
        super(contentSearchResultBuilder, ENTITY_TYPE);
        this.content = contentSearchResultBuilder.getEntityRef();
    }

    @Override
    public Reference<Content> getEntityRef() {
        return this.content;
    }

    public Content getContent() {
        return (Content)this.getEntity();
    }
}

