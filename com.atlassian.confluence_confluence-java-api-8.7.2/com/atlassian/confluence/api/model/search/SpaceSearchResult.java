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
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.reference.ExpandedReference;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.search.SearchResult;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@RestEnrichable
@Internal
public final class SpaceSearchResult
extends SearchResult<Space> {
    static final String ENTITY_TYPE = "space";
    @JsonDeserialize(as=ExpandedReference.class, contentAs=Space.class)
    @JsonProperty
    private Reference<Space> space;

    @JsonCreator
    private SpaceSearchResult() {
        super(ENTITY_TYPE);
    }

    SpaceSearchResult(SearchResult.Builder<Space> spaceSearchResultBuilder) {
        super(spaceSearchResultBuilder, ENTITY_TYPE);
        this.space = spaceSearchResultBuilder.getEntityRef();
    }

    @Override
    public Reference<Space> getEntityRef() {
        return this.space;
    }

    public Space getSpace() {
        return (Space)this.getEntity();
    }
}

