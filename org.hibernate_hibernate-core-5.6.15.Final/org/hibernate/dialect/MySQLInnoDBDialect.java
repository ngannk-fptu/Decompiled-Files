/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.InnoDBStorageEngine;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.MySQLStorageEngine;

@Deprecated
public class MySQLInnoDBDialect
extends MySQLDialect {
    @Override
    protected MySQLStorageEngine getDefaultMySQLStorageEngine() {
        return InnoDBStorageEngine.INSTANCE;
    }
}

