/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.core;

import org.springframework.jdbc.core.SqlParameter;

public class SqlReturnUpdateCount
extends SqlParameter {
    public SqlReturnUpdateCount(String name) {
        super(name, 4);
    }

    @Override
    public boolean isInputValueProvided() {
        return false;
    }

    @Override
    public boolean isResultsParameter() {
        return true;
    }
}

