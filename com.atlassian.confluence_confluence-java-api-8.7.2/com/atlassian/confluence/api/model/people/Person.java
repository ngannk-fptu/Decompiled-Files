/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.fugue.Option
 *  com.atlassian.graphql.annotations.GraphQLTypeName
 *  com.atlassian.sal.api.user.UserKey
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.annotate.JsonSubTypes
 *  org.codehaus.jackson.annotate.JsonSubTypes$Type
 *  org.codehaus.jackson.annotate.JsonTypeInfo
 *  org.codehaus.jackson.annotate.JsonTypeInfo$As
 *  org.codehaus.jackson.annotate.JsonTypeInfo$Id
 */
package com.atlassian.confluence.api.model.people;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.people.Anonymous;
import com.atlassian.confluence.api.model.people.KnownUser;
import com.atlassian.confluence.api.model.people.Subject;
import com.atlassian.confluence.api.model.people.SubjectType;
import com.atlassian.confluence.api.model.people.UnknownUser;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.web.Icon;
import com.atlassian.confluence.api.util.FugueConversionUtil;
import com.atlassian.fugue.Option;
import com.atlassian.graphql.annotations.GraphQLTypeName;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@ExperimentalApi
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes(value={@JsonSubTypes.Type(value=User.class, name="user"), @JsonSubTypes.Type(value=UnknownUser.class, name="unknown"), @JsonSubTypes.Type(value=KnownUser.class, name="known"), @JsonSubTypes.Type(value=Anonymous.class, name="anonymous")})
@JsonIgnoreProperties(ignoreUnknown=true)
@GraphQLTypeName(value="User")
public abstract class Person
implements Subject {
    protected static final String KNOWN_USER_TYPE = "known";
    protected static final String ANONYMOUS_USER_TYPE = "anonymous";
    @JsonProperty
    private final Icon profilePicture;
    @JsonProperty
    private final String displayName;

    protected Person(Icon profilePicture, String displayName) {
        this.profilePicture = profilePicture;
        this.displayName = displayName;
    }

    @Deprecated
    protected Person(Icon profilePicture) {
        this(profilePicture, "");
    }

    public Icon getProfilePicture() {
        return this.profilePicture;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Deprecated
    @JsonIgnore
    public Option<String> getOptionalUsername() {
        return FugueConversionUtil.toComOption(this.optionalUsername());
    }

    @JsonIgnore
    public abstract Optional<String> optionalUsername();

    @Deprecated
    @JsonIgnore
    public Option<UserKey> getUserKey() {
        return FugueConversionUtil.toComOption(this.optionalUserKey());
    }

    @JsonIgnore
    public abstract Optional<UserKey> optionalUserKey();

    @Override
    @JsonIgnore
    public SubjectType getSubjectType() {
        return SubjectType.USER;
    }
}

