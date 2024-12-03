/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.beans.container.internal;

import org.hibernate.HibernateException;

public class NoSuchBeanException
extends HibernateException {
    public NoSuchBeanException(Throwable cause) {
        super(cause);
    }

    public NoSuchBeanException(String message, Throwable cause) {
        super(message, cause);
    }
}

