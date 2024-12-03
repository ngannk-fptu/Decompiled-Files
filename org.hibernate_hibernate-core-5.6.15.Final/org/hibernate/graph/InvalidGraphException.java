/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.graph;

import org.hibernate.HibernateException;

public class InvalidGraphException
extends HibernateException {
    private static final long serialVersionUID = 1L;

    public InvalidGraphException(String message) {
        super(message);
    }
}

