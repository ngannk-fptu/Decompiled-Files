/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.MariaDB103Dialect;

public class MariaDB106Dialect
extends MariaDB103Dialect {
    @Override
    public boolean supportsSkipLocked() {
        return true;
    }
}

