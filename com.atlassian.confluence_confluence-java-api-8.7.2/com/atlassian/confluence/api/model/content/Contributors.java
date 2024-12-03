/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 */
package com.atlassian.confluence.api.model.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.ContributorUsers;
import com.atlassian.confluence.api.model.reference.ExpandedReference;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@ExperimentalApi
@RestEnrichable
@JsonIgnoreProperties(ignoreUnknown=true)
public class Contributors {
    @JsonDeserialize(as=ExpandedReference.class, contentAs=ContributorUsers.class)
    @JsonProperty
    private final Reference<ContributorUsers> publishers;

    @JsonCreator
    private Contributors() {
        this(Contributors.builder());
    }

    private Contributors(Builder builder) {
        this.publishers = builder.publishers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Reference<ContributorUsers> getPublishers() {
        return this.publishers;
    }

    public static class Expansions {
        public static final String PUBLISHERS = "publishers";
    }

    public static class Builder {
        private Reference<ContributorUsers> publishers;

        private Builder() {
        }

        public Builder publishers(Reference<ContributorUsers> publishers) {
            this.publishers = publishers;
            return this;
        }

        public Contributors build() {
            return new Contributors(this);
        }
    }
}

