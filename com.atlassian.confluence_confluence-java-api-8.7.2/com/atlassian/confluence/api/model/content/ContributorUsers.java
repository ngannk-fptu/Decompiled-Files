/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.sal.api.user.UserKey
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import com.atlassian.sal.api.user.UserKey;
import java.util.List;
import java.util.stream.Collectors;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@RestEnrichable
@JsonIgnoreProperties(ignoreUnknown=true)
public class ContributorUsers {
    @JsonProperty
    private final List<Person> users;
    @JsonProperty
    private final List<String> userKeys;

    @JsonCreator
    private ContributorUsers() {
        this(ContributorUsers.builder());
    }

    private ContributorUsers(Builder builder) {
        this.users = builder.users;
        this.userKeys = builder.userKeys != null ? builder.userKeys.stream().map(UserKey::getStringValue).collect(Collectors.toList()) : null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<Person> getUsers() {
        return this.users;
    }

    public List<UserKey> getUserKeys() {
        return this.userKeys != null ? this.userKeys.stream().map(UserKey::new).collect(Collectors.toList()) : null;
    }

    public static class Expansions {
        public static final String USERS = "users";
    }

    public static class Builder {
        private List<Person> users;
        private List<UserKey> userKeys;

        private Builder() {
        }

        public Builder users(List<Person> users) {
            this.users = users;
            return this;
        }

        public Builder userKeys(List<UserKey> userKeys) {
            this.userKeys = userKeys;
            return this;
        }

        public ContributorUsers build() {
            return new ContributorUsers(this);
        }
    }
}

