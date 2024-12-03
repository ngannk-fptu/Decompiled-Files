/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.walking.spi;

import org.hibernate.HibernateError;

public class WalkingException
extends HibernateError {
    public WalkingException(String message) {
        super(message);
    }

    public WalkingException(String message, Throwable root) {
        super(message, root);
    }
}

