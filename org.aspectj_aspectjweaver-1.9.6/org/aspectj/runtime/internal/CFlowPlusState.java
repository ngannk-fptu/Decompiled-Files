/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.runtime.internal;

import org.aspectj.runtime.CFlow;

public class CFlowPlusState
extends CFlow {
    private Object[] state;

    public CFlowPlusState(Object[] state) {
        this.state = state;
    }

    public CFlowPlusState(Object[] state, Object _aspect) {
        super(_aspect);
        this.state = state;
    }

    @Override
    public Object get(int index) {
        return this.state[index];
    }
}

