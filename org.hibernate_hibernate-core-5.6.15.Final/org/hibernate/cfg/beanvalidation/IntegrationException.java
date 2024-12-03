/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cfg.beanvalidation;

import org.hibernate.HibernateException;

public class IntegrationException
extends HibernateException {
    public IntegrationException(String message) {
        super(message);
    }

    public IntegrationException(String message, Throwable root) {
        super(message, root);
    }
}

