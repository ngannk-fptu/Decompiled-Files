/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.universalchardet.prober;

import org.mozilla.universalchardet.Constants;
import org.mozilla.universalchardet.prober.CharsetProber;
import org.mozilla.universalchardet.prober.statemachine.CodingStateMachine;
import org.mozilla.universalchardet.prober.statemachine.SMModel;
import org.mozilla.universalchardet.prober.statemachine.UTF8SMModel;

public class UTF8Prober
extends CharsetProber {
    public static final float ONE_CHAR_PROB = 0.5f;
    private CodingStateMachine codingSM = new CodingStateMachine(smModel);
    private CharsetProber.ProbingState state;
    private int numOfMBChar = 0;
    private static final SMModel smModel = new UTF8SMModel();

    public UTF8Prober() {
        this.reset();
    }

    @Override
    public String getCharSetName() {
        return Constants.CHARSET_UTF_8;
    }

    @Override
    public CharsetProber.ProbingState handleData(byte[] buf, int offset, int length) {
        int maxPos = offset + length;
        for (int i = offset; i < maxPos; ++i) {
            int codingState = this.codingSM.nextState(buf[i]);
            if (codingState == 1) {
                this.state = CharsetProber.ProbingState.NOT_ME;
                break;
            }
            if (codingState == 2) {
                this.state = CharsetProber.ProbingState.FOUND_IT;
                break;
            }
            if (codingState != 0 || this.codingSM.getCurrentCharLen() < 2) continue;
            ++this.numOfMBChar;
        }
        if (this.state == CharsetProber.ProbingState.DETECTING && this.getConfidence() > 0.95f) {
            this.state = CharsetProber.ProbingState.FOUND_IT;
        }
        return this.state;
    }

    @Override
    public CharsetProber.ProbingState getState() {
        return this.state;
    }

    @Override
    public void reset() {
        this.codingSM.reset();
        this.numOfMBChar = 0;
        this.state = CharsetProber.ProbingState.DETECTING;
    }

    @Override
    public float getConfidence() {
        float unlike = 0.99f;
        if (this.numOfMBChar < 6) {
            for (int i = 0; i < this.numOfMBChar; ++i) {
                unlike *= 0.5f;
            }
            return 1.0f - unlike;
        }
        return 0.99f;
    }

    @Override
    public void setOption() {
    }
}

