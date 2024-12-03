/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.MySQLDialect;

@Deprecated
public class MySQLMyISAMDialect
extends MySQLDialect {
    @Override
    public String getTableTypeString() {
        return " type=MyISAM";
    }

    @Override
    public boolean dropConstraints() {
        return false;
    }
}

