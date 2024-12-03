/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.PostgreSQL9Dialect;

public class PostgreSQL91Dialect
extends PostgreSQL9Dialect {
    @Override
    public boolean supportsPartitionBy() {
        return true;
    }

    @Override
    public boolean supportsNonQueryWithCTE() {
        return true;
    }
}

