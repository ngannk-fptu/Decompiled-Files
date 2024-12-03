/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.DerbyDialect;
import org.hibernate.dialect.function.AnsiTrimFunction;
import org.hibernate.dialect.function.DerbyConcatFunction;

public class DerbyTenFiveDialect
extends DerbyDialect {
    public DerbyTenFiveDialect() {
        this.registerFunction("concat", new DerbyConcatFunction());
        this.registerFunction("trim", new AnsiTrimFunction());
    }

    @Override
    public boolean supportsSequences() {
        return false;
    }

    @Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public boolean supportsLimitOffset() {
        return true;
    }
}

