/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.DerbyTenFiveDialect;

public class DerbyTenSixDialect
extends DerbyTenFiveDialect {
    @Override
    public boolean supportsSequences() {
        return true;
    }

    @Override
    public String getCrossJoinSeparator() {
        return " cross join ";
    }
}

