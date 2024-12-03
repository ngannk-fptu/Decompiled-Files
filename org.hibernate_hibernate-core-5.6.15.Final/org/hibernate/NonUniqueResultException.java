/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.HibernateException;

public class NonUniqueResultException
extends HibernateException {
    public NonUniqueResultException(int resultCount) {
        super("query did not return a unique result: " + resultCount);
    }
}

