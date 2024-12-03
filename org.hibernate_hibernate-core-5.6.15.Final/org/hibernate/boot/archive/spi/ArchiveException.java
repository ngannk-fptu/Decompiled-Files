/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.spi;

import org.hibernate.HibernateException;

public class ArchiveException
extends HibernateException {
    public ArchiveException(String message) {
        super(message);
    }

    public ArchiveException(String message, Throwable cause) {
        super(message, cause);
    }
}

