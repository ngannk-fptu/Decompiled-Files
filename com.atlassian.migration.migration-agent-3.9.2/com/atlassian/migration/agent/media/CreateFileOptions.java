/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize$Inclusion
 */
package com.atlassian.migration.agent.media;

import java.time.Instant;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class CreateFileOptions {
    @JsonProperty
    private Instant expireAfter;
    @JsonProperty
    private boolean skipConversions;
    @JsonProperty
    private String collection;

    @JsonCreator
    public CreateFileOptions(@JsonProperty(value="expireAfter") Instant expireAfter, @JsonProperty(value="skipConversions") boolean skipConversions, @JsonProperty(value="collections") String collection) {
        this.expireAfter = expireAfter;
        this.skipConversions = skipConversions;
        this.collection = collection;
    }

    public Instant getExpireAfter() {
        return this.expireAfter;
    }

    public boolean isSkipConversions() {
        return this.skipConversions;
    }

    public String getCollection() {
        return this.collection;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CreateFileOptions that = (CreateFileOptions)o;
        return this.skipConversions == that.skipConversions && Objects.equals(this.expireAfter, that.expireAfter) && Objects.equals(this.collection, that.collection);
    }

    public int hashCode() {
        return Objects.hash(this.expireAfter, this.skipConversions, this.collection);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Instant expireAfter;
        private boolean skipConversions;
        private String collection;

        private Builder() {
        }

        public Builder expireAfter(Instant expireAfter) {
            this.expireAfter = expireAfter;
            return this;
        }

        public Builder skipConversions(boolean skipConversions) {
            this.skipConversions = skipConversions;
            return this;
        }

        public Builder collection(String collection) {
            this.collection = collection;
            return this;
        }

        public CreateFileOptions build() {
            return new CreateFileOptions(this.expireAfter, this.skipConversions, this.collection);
        }
    }
}

