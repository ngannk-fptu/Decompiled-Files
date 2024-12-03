/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.universalchardet.prober;

import org.mozilla.universalchardet.prober.CharsetProber;
import org.mozilla.universalchardet.prober.statemachine.CodingStateMachine;
import org.mozilla.universalchardet.prober.statemachine.HZSMModel;
import org.mozilla.universalchardet.prober.statemachine.ISO2022CNSMModel;
import org.mozilla.universalchardet.prober.statemachine.ISO2022JPSMModel;
import org.mozilla.universalchardet.prober.statemachine.ISO2022KRSMModel;

public class EscCharsetProber
extends CharsetProber {
    private CodingStateMachine[] codingSM = new CodingStateMachine[4];
    private int activeSM;
    private CharsetProber.ProbingState state;
    private String detectedCharset;
    private static final HZSMModel hzsModel = new HZSMModel();
    private static final ISO2022CNSMModel iso2022cnModel = new ISO2022CNSMModel();
    private static final ISO2022JPSMModel iso2022jpModel = new ISO2022JPSMModel();
    private static final ISO2022KRSMModel iso2022krModel = new ISO2022KRSMModel();

    public EscCharsetProber() {
        this.codingSM[0] = new CodingStateMachine(hzsModel);
        this.codingSM[1] = new CodingStateMachine(iso2022cnModel);
        this.codingSM[2] = new CodingStateMachine(iso2022jpModel);
        this.codingSM[3] = new CodingStateMachine(iso2022krModel);
        this.reset();
    }

    @Override
    public String getCharSetName() {
        return this.detectedCharset;
    }

    @Override
    public float getConfidence() {
        return 0.99f;
    }

    @Override
    public CharsetProber.ProbingState getState() {
        return this.state;
    }

    @Override
    public CharsetProber.ProbingState handleData(byte[] buf, int offset, int length) {
        int maxPos = offset + length;
        for (int i = offset; i < maxPos && this.state == CharsetProber.ProbingState.DETECTING; ++i) {
            for (int j = this.activeSM - 1; j >= 0; --j) {
                int codingState = this.codingSM[j].nextState(buf[i]);
                if (codingState == 1) {
                    --this.activeSM;
                    if (this.activeSM <= 0) {
                        this.state = CharsetProber.ProbingState.NOT_ME;
                        return this.state;
                    }
                    if (j == this.activeSM) continue;
                    CodingStateMachine t = this.codingSM[this.activeSM];
                    this.codingSM[this.activeSM] = this.codingSM[j];
                    this.codingSM[j] = t;
                    continue;
                }
                if (codingState != 2) continue;
                this.state = CharsetProber.ProbingState.FOUND_IT;
                this.detectedCharset = this.codingSM[j].getCodingStateMachine();
                return this.state;
            }
        }
        return this.state;
    }

    @Override
    public void reset() {
        this.state = CharsetProber.ProbingState.DETECTING;
        for (int i = 0; i < this.codingSM.length; ++i) {
            this.codingSM[i].reset();
        }
        this.activeSM = this.codingSM.length;
        this.detectedCharset = null;
    }

    @Override
    public void setOption() {
    }
}

