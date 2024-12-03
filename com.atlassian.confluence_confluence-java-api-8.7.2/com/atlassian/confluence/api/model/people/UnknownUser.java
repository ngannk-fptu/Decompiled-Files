/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.people;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.web.Icon;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
public class UnknownUser
extends User {
    public UnknownUser(Icon profilePicture, String username, String userKey) {
        super(profilePicture, username, UnknownUser.getDisplayName(username), userKey);
    }

    @JsonCreator
    public UnknownUser(@JsonProperty(value="profilePicture") Icon profilePicture, @JsonProperty(value="username") String username, @JsonProperty(value="displayName") String displayName, @JsonProperty(value="userKey") String userKey) {
        super(profilePicture, username, displayName, userKey);
    }

    public UnknownUser(Icon profilePicture, String username) {
        this(profilePicture, username, null);
    }

    private static String getDisplayName(String username) {
        return String.format("Unknown User (%s)", username);
    }
}

