/*
 * Decompiled with CFR 0.152.
 */
package com.onelogin.saml2.model;

public class Contact {
    private final String contactType;
    private final String givenName;
    private final String emailAddress;

    public Contact(String contactType, String givenName, String emailAddress) {
        this.contactType = contactType != null ? contactType : "";
        this.givenName = givenName != null ? givenName : "";
        this.emailAddress = emailAddress != null ? emailAddress : "";
    }

    public final String getContactType() {
        return this.contactType;
    }

    public final String getEmailAddress() {
        return this.emailAddress;
    }

    public final String getGivenName() {
        return this.givenName;
    }
}

