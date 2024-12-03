/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.HibernateException;

public class UnknownProfileException
extends HibernateException {
    private final String name;

    public UnknownProfileException(String name) {
        super("Unknown fetch profile [" + name + "]");
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

