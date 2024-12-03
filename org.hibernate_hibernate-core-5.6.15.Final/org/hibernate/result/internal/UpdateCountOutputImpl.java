/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.result.internal;

import org.hibernate.result.UpdateCountOutput;

class UpdateCountOutputImpl
implements UpdateCountOutput {
    private final int updateCount;

    public UpdateCountOutputImpl(int updateCount) {
        this.updateCount = updateCount;
    }

    @Override
    public int getUpdateCount() {
        return this.updateCount;
    }

    @Override
    public boolean isResultSet() {
        return false;
    }
}

