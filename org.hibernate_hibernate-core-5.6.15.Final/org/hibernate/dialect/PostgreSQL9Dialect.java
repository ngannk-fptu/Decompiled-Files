/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.PostgreSQL82Dialect;

public class PostgreSQL9Dialect
extends PostgreSQL82Dialect {
    @Override
    public boolean supportsIfExistsBeforeConstraintName() {
        return true;
    }
}

