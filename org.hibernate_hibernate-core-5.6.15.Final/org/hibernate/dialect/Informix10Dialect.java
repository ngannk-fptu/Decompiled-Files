/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.InformixDialect;
import org.hibernate.dialect.pagination.Informix10LimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;

public class Informix10Dialect
extends InformixDialect {
    @Override
    public LimitHandler getLimitHandler() {
        return Informix10LimitHandler.INSTANCE;
    }
}

