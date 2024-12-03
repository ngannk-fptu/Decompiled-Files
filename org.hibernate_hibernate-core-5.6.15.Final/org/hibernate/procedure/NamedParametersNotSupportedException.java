/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.procedure;

import org.hibernate.HibernateException;

public class NamedParametersNotSupportedException
extends HibernateException {
    public NamedParametersNotSupportedException(String message) {
        super(message);
    }
}

