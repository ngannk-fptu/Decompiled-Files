/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.universalchardet.prober;

import java.util.Arrays;
import org.mozilla.universalchardet.Constants;
import org.mozilla.universalchardet.prober.CharsetProber;
import org.mozilla.universalchardet.prober.distributionanalysis.EUCTWDistributionAnalysis;
import org.mozilla.universalchardet.prober.statemachine.CodingStateMachine;
import org.mozilla.universalchardet.prober.statemachine.EUCTWSMModel;
import org.mozilla.universalchardet.prober.statemachine.SMModel;

public class EUCTWProber
extends CharsetProber {
    private CodingStateMachine codingSM = new CodingStateMachine(smModel);
    private CharsetProber.ProbingState state;
    private EUCTWDistributionAnalysis distributionAnalyzer = new EUCTWDistributionAnalysis();
    private byte[] lastChar = new byte[2];
    private static final SMModel smModel = new EUCTWSMModel();

    public EUCTWProber() {
        this.reset();
    }

    @Override
    public String getCharSetName() {
        return Constants.CHARSET_EUC_TW;
    }

    @Override
    public float getConfidence() {
        float distribCf = this.distributionAnalyzer.getConfidence();
        return distribCf;
    }

    @Override
    public CharsetProber.ProbingState getState() {
        return this.state;
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
            if (codingState != 0) continue;
            int charLen = this.codingSM.getCurrentCharLen();
            if (i == offset) {
                this.lastChar[1] = buf[offset];
                this.distributionAnalyzer.handleOneChar(this.lastChar, 0, charLen);
                continue;
            }
            this.distributionAnalyzer.handleOneChar(buf, i - 1, charLen);
        }
        this.lastChar[0] = buf[maxPos - 1];
        if (this.state == CharsetProber.ProbingState.DETECTING && this.distributionAnalyzer.gotEnoughData() && this.getConfidence() > 0.95f) {
            this.state = CharsetProber.ProbingState.FOUND_IT;
        }
        return this.state;
    }

    @Override
    public void reset() {
        this.codingSM.reset();
        this.state = CharsetProber.ProbingState.DETECTING;
        this.distributionAnalyzer.reset();
        Arrays.fill(this.lastChar, (byte)0);
    }

    @Override
    public void setOption() {
    }
}

