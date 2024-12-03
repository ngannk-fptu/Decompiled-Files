/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.service;

import org.hibernate.HibernateException;

public class NullServiceException
extends HibernateException {
    public final Class serviceRole;

    public NullServiceException(Class serviceRole) {
        super("Unknown service requested [" + serviceRole.getName() + "]");
        this.serviceRole = serviceRole;
    }

    public Class getServiceRole() {
        return this.serviceRole;
    }
}

