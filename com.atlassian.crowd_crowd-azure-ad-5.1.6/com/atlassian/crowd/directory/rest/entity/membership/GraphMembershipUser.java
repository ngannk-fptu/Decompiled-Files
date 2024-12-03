/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.directory.rest.entity.membership;

import com.atlassian.crowd.directory.rest.entity.membership.DirectoryObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class GraphMembershipUser
extends DirectoryObject {
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

    protected GraphMembershipUser() {
        super(null, null);
        this.givenName = null;
        this.mail = null;
        this.surname = null;
        this.userPrincipalName = null;
        this.accountEnabled = null;
    }

    public GraphMembershipUser(String id, String displayName, String givenName, String mail, String surname, String userPrincipalName, Boolean accountEnabled) {
        super(displayName, id);
        this.givenName = givenName;
        this.mail = mail;
        this.surname = surname;
        this.userPrincipalName = userPrincipalName;
        this.accountEnabled = accountEnabled;
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

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public String getId() {
        return this.id;
    }
}

