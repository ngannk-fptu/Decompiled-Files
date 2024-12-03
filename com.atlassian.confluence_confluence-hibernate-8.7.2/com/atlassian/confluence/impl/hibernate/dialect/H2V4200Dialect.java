/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.dialect.H2Dialect
 */
package com.atlassian.confluence.impl.hibernate.dialect;

import org.hibernate.dialect.H2Dialect;

public class H2V4200Dialect
extends H2Dialect {
    public String getCascadeConstraintsString() {
        return " CASCADE ";
    }

    public boolean supportsIfExistsAfterTableName() {
        return false;
    }

    public boolean supportsIfExistsBeforeTableName() {
        return true;
    }
}

