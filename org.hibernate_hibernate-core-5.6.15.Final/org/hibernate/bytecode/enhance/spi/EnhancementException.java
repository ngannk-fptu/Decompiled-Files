/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.enhance.spi;

import org.hibernate.HibernateException;

public class EnhancementException
extends HibernateException {
    public EnhancementException(String message) {
        super(message);
    }

    public EnhancementException(String message, Throwable cause) {
        super(message, cause);
    }
}

