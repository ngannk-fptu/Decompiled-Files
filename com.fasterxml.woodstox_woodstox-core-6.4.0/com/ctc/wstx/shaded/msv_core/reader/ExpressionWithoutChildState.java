/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader;

import com.ctc.wstx.shaded.msv_core.reader.ExpressionState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public abstract class ExpressionWithoutChildState
extends ExpressionState {
    protected final State createChildState(StartTagInfo tag) {
        return null;
    }
}

