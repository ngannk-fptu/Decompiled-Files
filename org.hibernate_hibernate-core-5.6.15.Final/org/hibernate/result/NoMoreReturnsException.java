/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.result;

import org.hibernate.HibernateException;

public class NoMoreReturnsException
extends HibernateException {
    public NoMoreReturnsException(String message) {
        super(message);
    }

    public NoMoreReturnsException() {
        super("Results have been exhausted");
    }
}

