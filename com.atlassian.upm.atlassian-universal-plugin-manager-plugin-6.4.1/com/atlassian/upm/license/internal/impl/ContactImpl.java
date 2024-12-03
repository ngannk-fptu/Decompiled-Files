/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Contact
 */
package com.atlassian.upm.license.internal.impl;

import com.atlassian.upm.api.license.entity.Contact;
import java.util.Objects;

public class ContactImpl
implements Contact {
    private final String name;
    private final String email;

    public ContactImpl(com.atlassian.extras.api.Contact contact) {
        this(Objects.requireNonNull(contact, "contact").getName(), contact.getEmail());
    }

    ContactImpl(String name, String email) {
        this.name = name;
        this.email = email;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    public String toString() {
        return "ContactImpl[" + this.getName() + "]";
    }
}

