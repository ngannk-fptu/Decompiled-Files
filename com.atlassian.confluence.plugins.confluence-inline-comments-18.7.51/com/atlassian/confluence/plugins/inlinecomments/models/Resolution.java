/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.serialization.RestEnrichable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.plugins.inlinecomments.models;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

@ExperimentalApi
@RestEnrichable
@JsonIgnoreProperties(ignoreUnknown=true)
public final class Resolution {
    @JsonProperty
    private final String status;
    @JsonProperty
    private final Person lastModifier;
    @JsonProperty
    private final DateTime lastModifiedDate;

    @JsonCreator
    private Resolution(Builder builder) {
        this.status = builder.status;
        this.lastModifier = builder.lastModifier;
        this.lastModifiedDate = builder.lastModifiedDate;
    }

    public String getStatus() {
        return this.status;
    }

    public Person getLastModifier() {
        return this.lastModifier;
    }

    public DateTime getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public static class Builder {
        private String status;
        private Person lastModifier;
        private DateTime lastModifiedDate;

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder setLastModifier(Person lastModifier) {
            this.lastModifier = lastModifier;
            return this;
        }

        public Builder setLastModifiedDate(DateTime lastModifiedDate) {
            this.lastModifiedDate = lastModifiedDate;
            return this;
        }

        public Resolution build() {
            return new Resolution(this);
        }
    }
}

