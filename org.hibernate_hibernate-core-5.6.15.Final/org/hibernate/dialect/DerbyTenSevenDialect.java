/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.DerbyTenSixDialect;

public class DerbyTenSevenDialect
extends DerbyTenSixDialect {
    public DerbyTenSevenDialect() {
        this.registerColumnType(16, "boolean");
    }

    @Override
    public String toBooleanValueString(boolean bool) {
        return String.valueOf(bool);
    }
}

