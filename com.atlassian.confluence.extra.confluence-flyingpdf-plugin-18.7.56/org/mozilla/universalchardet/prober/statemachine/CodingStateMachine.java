/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.universalchardet.prober.statemachine;

import org.mozilla.universalchardet.prober.statemachine.SMModel;

public class CodingStateMachine {
    protected SMModel model;
    protected int currentState;
    protected int currentCharLen;
    protected int currentBytePos;

    public CodingStateMachine(SMModel model) {
        this.model = model;
        this.currentState = 0;
    }

    public int nextState(byte c) {
        int byteCls = this.model.getClass(c);
        if (this.currentState == 0) {
            this.currentBytePos = 0;
            this.currentCharLen = this.model.getCharLen(byteCls);
        }
        this.currentState = this.model.getNextState(byteCls, this.currentState);
        ++this.currentBytePos;
        return this.currentState;
    }

    public int getCurrentCharLen() {
        return this.currentCharLen;
    }

    public void reset() {
        this.currentState = 0;
    }

    public String getCodingStateMachine() {
        return this.model.getName();
    }
}

