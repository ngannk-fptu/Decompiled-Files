/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.dialect.Oracle9iDialect
 */
package com.atlassian.migration.agent.store.jpa.impl;

import org.hibernate.dialect.Oracle9iDialect;

class OracleDialect
extends Oracle9iDialect {
    OracleDialect() {
        this.registerColumnType(91, "date");
        this.registerColumnType(92, "date");
        this.registerColumnType(93, "date");
        this.registerColumnType(1, "char(1)");
    }
}

