/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.unique;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.unique.DefaultUniqueDelegate;

public class MySQLUniqueDelegate
extends DefaultUniqueDelegate {
    public MySQLUniqueDelegate(Dialect dialect) {
        super(dialect);
    }

    @Override
    protected String getDropUnique() {
        return " drop index ";
    }
}

