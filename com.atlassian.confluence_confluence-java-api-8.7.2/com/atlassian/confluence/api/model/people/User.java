/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.people;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.people.KnownUser;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.relations.Relatable;
import com.atlassian.confluence.api.model.web.Icon;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import java.util.Objects;
import java.util.Optional;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
public class User
extends Person
implements Relatable {
    @JsonProperty
    protected final String username;
    @JsonProperty
    private final String userKey;
    @Deprecated
    public static final Function<User, String> mapUserToUsername = user -> Objects.requireNonNull(user).getUsername();
    @Deprecated
    public static final Predicate<User> isUserKnown = user -> user instanceof KnownUser;

    @JsonCreator
    private User() {
        super(null, "");
        this.username = null;
        this.userKey = null;
    }

    public User(Icon profilePicture, String username, String displayName, String userKey) {
        super(profilePicture, displayName);
        this.username = username;
        this.userKey = userKey != null ? userKey : "";
    }

    public User(Icon profilePicture, String username, String displayName, UserKey userKey) {
        this(profilePicture, username, displayName, userKey != null ? userKey.getStringValue() : "");
    }

    @Deprecated
    public User(Icon profilePicture, String username) {
        super(profilePicture);
        this.username = username;
        this.userKey = null;
    }

    public String getUsername() {
        return this.username;
    }

    @Override
    public Optional<String> optionalUsername() {
        if (this.getUsername() == null || this.getUsername().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(this.getUsername());
    }

    @Override
    @JsonIgnore
    public Optional<UserKey> optionalUserKey() {
        if (this.userKey == null || this.userKey.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new UserKey(this.userKey));
    }

    public String toString() {
        return "User{username='" + this.username + '\'' + ", userKey='" + this.userKey + '\'' + '}';
    }

    public static User fromUsername(String username) {
        return new User(null, username);
    }

    public static User fromUserkey(UserKey key) {
        return new User(null, null, null, key);
    }

    public static class Expansions {
        public static final String STATUS = "status";
    }
}

