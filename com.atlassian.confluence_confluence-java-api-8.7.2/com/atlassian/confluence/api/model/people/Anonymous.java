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
package com.atlassian.confluence.api.model.people;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.web.Icon;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
@RestEnrichable
public class Anonymous
extends Person {
    private static final String DISPLAY_NAME = "Anonymous";
    public static final Anonymous ANONYMOUS_USER = new Anonymous(null, "Anonymous");
    @JsonProperty
    private final String type = "anonymous";

    @Deprecated
    public Anonymous(Icon profilePicture) {
        this(profilePicture, DISPLAY_NAME);
    }

    @JsonCreator
    public Anonymous(@JsonProperty(value="profilePicture") Icon profilePicture, @JsonProperty(value="displayName") String displayName) {
        super(profilePicture, displayName);
    }

    @Override
    public Optional<String> optionalUsername() {
        return Optional.empty();
    }

    @Override
    public Optional<UserKey> optionalUserKey() {
        return Optional.empty();
    }
}

