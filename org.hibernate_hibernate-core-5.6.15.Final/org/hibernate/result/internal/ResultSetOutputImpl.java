/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.result.internal;

import java.util.List;
import java.util.function.Supplier;
import org.hibernate.result.ResultSetOutput;

class ResultSetOutputImpl
implements ResultSetOutput {
    private final Supplier<List> resultSetSupplier;

    public ResultSetOutputImpl(List results) {
        this.resultSetSupplier = () -> results;
    }

    public ResultSetOutputImpl(Supplier<List> resultSetSupplier) {
        this.resultSetSupplier = resultSetSupplier;
    }

    @Override
    public boolean isResultSet() {
        return true;
    }

    @Override
    public List getResultList() {
        return this.resultSetSupplier.get();
    }

    @Override
    public Object getSingleResult() {
        List results = this.getResultList();
        if (results == null || results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }
}

