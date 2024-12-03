/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonSubTypes
 *  org.codehaus.jackson.annotate.JsonSubTypes$Type
 *  org.codehaus.jackson.annotate.JsonTypeInfo
 *  org.codehaus.jackson.annotate.JsonTypeInfo$As
 *  org.codehaus.jackson.annotate.JsonTypeInfo$Id
 */
package com.atlassian.confluence.api.model.people;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.people.Anonymous;
import com.atlassian.confluence.api.model.people.Group;
import com.atlassian.confluence.api.model.people.KnownUser;
import com.atlassian.confluence.api.model.people.SubjectType;
import com.atlassian.confluence.api.model.people.UnknownUser;
import com.atlassian.confluence.api.model.people.User;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@ExperimentalApi
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes(value={@JsonSubTypes.Type(value=User.class, name="user"), @JsonSubTypes.Type(value=UnknownUser.class, name="unknown"), @JsonSubTypes.Type(value=KnownUser.class, name="known"), @JsonSubTypes.Type(value=Anonymous.class, name="anonymous"), @JsonSubTypes.Type(value=Group.class, name="group")})
@JsonIgnoreProperties(ignoreUnknown=true)
public interface Subject {
    public String getDisplayName();

    @JsonIgnore
    public SubjectType getSubjectType();
}

