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
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.reference.ExpandedReference;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.search.SearchResult;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@RestEnrichable
@Internal
public final class UserSearchResult
extends SearchResult<User> {
    static final String ENTITY_TYPE = "user";
    @JsonDeserialize(as=ExpandedReference.class, contentAs=User.class)
    @JsonProperty
    private Reference<User> user;

    @JsonCreator
    private UserSearchResult() {
        super(ENTITY_TYPE);
    }

    UserSearchResult(SearchResult.Builder<User> userBuilder) {
        super(userBuilder, ENTITY_TYPE);
        this.user = userBuilder.getEntityRef();
    }

    @Override
    public Reference<User> getEntityRef() {
        return this.user;
    }

    public User getUser() {
        return (User)this.getEntity();
    }
}

