/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.FlowControlException;

class BreakOrContinueException
extends FlowControlException {
    static final BreakOrContinueException BREAK_INSTANCE = new BreakOrContinueException();
    static final BreakOrContinueException CONTINUE_INSTANCE = new BreakOrContinueException();

    private BreakOrContinueException() {
    }
}

