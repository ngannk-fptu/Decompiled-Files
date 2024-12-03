/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.bind.support;

import org.springframework.web.bind.support.SessionStatus;

public class SimpleSessionStatus
implements SessionStatus {
    private boolean complete = false;

    @Override
    public void setComplete() {
        this.complete = true;
    }

    @Override
    public boolean isComplete() {
        return this.complete;
    }
}

