/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.spi;

import org.hibernate.HibernateException;

public class NotInstrumentedException
extends HibernateException {
    public NotInstrumentedException(String message) {
        super(message);
    }
}

