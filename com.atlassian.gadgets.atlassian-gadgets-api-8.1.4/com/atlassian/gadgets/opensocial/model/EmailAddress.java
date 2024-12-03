/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.atlassian.gadgets.opensocial.model;

import net.jcip.annotations.Immutable;

@Immutable
public final class EmailAddress {
    private final String emailAddress;

    public EmailAddress(String emailAddress) {
        if (emailAddress == null) {
            throw new NullPointerException("emailAddress parameter to EmailAddress must not be null");
        }
        this.emailAddress = emailAddress.intern();
    }

    public String value() {
        return this.emailAddress;
    }

    public String toString() {
        return this.emailAddress;
    }

    public static EmailAddress valueOf(String emailAddress) {
        return new EmailAddress(emailAddress);
    }

    public boolean equals(Object obj) {
        return obj instanceof EmailAddress && this.emailAddress.equals(((EmailAddress)obj).value());
    }

    public int hashCode() {
        return this.emailAddress.hashCode();
    }
}

