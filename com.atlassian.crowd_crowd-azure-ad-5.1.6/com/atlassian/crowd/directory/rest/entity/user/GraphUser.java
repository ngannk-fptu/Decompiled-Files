/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.directory.rest.entity.user;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class GraphUser {
    @JsonProperty(value="id")
    private final String id;
    @JsonProperty(value="displayName")
    private final String displayName;
    @JsonProperty(value="givenName")
    private final String givenName;
    @JsonProperty(value="mail")
    private final String mail;
    @JsonProperty(value="surname")
    private final String surname;
    @JsonProperty(value="userPrincipalName")
    private final String userPrincipalName;
    @JsonProperty(value="accountEnabled")
    private final Boolean accountEnabled;

    public GraphUser() {
        this.id = null;
        this.displayName = null;
        this.givenName = null;
        this.mail = null;
        this.surname = null;
        this.userPrincipalName = null;
        this.accountEnabled = null;
    }

    public GraphUser(String userPrincipalName) {
        this.id = null;
        this.displayName = null;
        this.givenName = null;
        this.mail = null;
        this.surname = null;
        this.accountEnabled = null;
        this.userPrincipalName = userPrincipalName;
    }

    public GraphUser(String id, String displayName, String givenName, String mail, String surname, String userPrincipalName, Boolean accountEnabled) {
        this.id = id;
        this.displayName = displayName;
        this.givenName = givenName;
        this.mail = mail;
        this.surname = surname;
        this.userPrincipalName = userPrincipalName;
        this.accountEnabled = accountEnabled;
    }

    public String getId() {
        return this.id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getGivenName() {
        return this.givenName;
    }

    public String getMail() {
        return this.mail;
    }

    public String getSurname() {
        return this.surname;
    }

    public String getUserPrincipalName() {
        return this.userPrincipalName;
    }

    public Boolean getAccountEnabled() {
        return this.accountEnabled;
    }
}

