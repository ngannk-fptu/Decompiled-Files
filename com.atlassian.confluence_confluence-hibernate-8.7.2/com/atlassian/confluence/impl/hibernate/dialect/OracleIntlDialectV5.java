/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.dialect.Oracle9iDialect
 */
package com.atlassian.confluence.impl.hibernate.dialect;

import org.hibernate.dialect.Oracle9iDialect;

@Deprecated
public class OracleIntlDialectV5
extends Oracle9iDialect {
    public OracleIntlDialectV5() {
        this.registerColumnType(12, 2000L, "nvarchar2($l)");
        this.registerColumnType(12, 4000L, "nclob");
        this.registerColumnType(12, "nclob");
    }
}

