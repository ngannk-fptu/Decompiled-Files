/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.spi;

import org.hibernate.HibernateException;

public class CommandAcceptanceException
extends HibernateException {
    public CommandAcceptanceException(String message) {
        super(message);
    }

    public CommandAcceptanceException(String message, Throwable cause) {
        super(message, cause);
    }
}

