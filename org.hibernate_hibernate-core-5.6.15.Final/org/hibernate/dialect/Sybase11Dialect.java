/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.SybaseDialect;
import org.hibernate.sql.JoinFragment;
import org.hibernate.sql.Sybase11JoinFragment;

public class Sybase11Dialect
extends SybaseDialect {
    @Override
    public JoinFragment createOuterJoinFragment() {
        return new Sybase11JoinFragment();
    }

    @Override
    public String getCrossJoinSeparator() {
        return ", ";
    }
}

