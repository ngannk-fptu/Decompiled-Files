/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.universalchardet.prober;

import java.util.ArrayList;
import java.util.List;
import org.mozilla.universalchardet.prober.Big5Prober;
import org.mozilla.universalchardet.prober.CharsetProber;
import org.mozilla.universalchardet.prober.EUCJPProber;
import org.mozilla.universalchardet.prober.EUCKRProber;
import org.mozilla.universalchardet.prober.EUCTWProber;
import org.mozilla.universalchardet.prober.SJISProber;
import org.mozilla.universalchardet.prober.UTF8Prober;

public class MBCSGroupProber
extends CharsetProber {
    private CharsetProber.ProbingState state;
    private List<CharsetProber> probers = new ArrayList<CharsetProber>();
    private CharsetProber bestGuess;
    private int activeNum;

    public MBCSGroupProber() {
        this.probers.add(new UTF8Prober());
        this.probers.add(new SJISProber());
        this.probers.add(new EUCJPProber());
        this.probers.add(new EUCKRProber());
        this.probers.add(new Big5Prober());
        this.probers.add(new EUCTWProber());
        this.reset();
    }

    @Override
    public String getCharSetName() {
        if (this.bestGuess == null) {
            this.getConfidence();
            if (this.bestGuess == null) {
                this.bestGuess = this.probers.get(0);
            }
        }
        return this.bestGuess.getCharSetName();
    }

    @Override
    public float getConfidence() {
        float bestConf = 0.0f;
        if (this.state == CharsetProber.ProbingState.FOUND_IT) {
            return 0.99f;
        }
        if (this.state == CharsetProber.ProbingState.NOT_ME) {
            return 0.01f;
        }
        for (CharsetProber prober : this.probers) {
            float cf;
            if (!prober.isActive() || !(bestConf < (cf = prober.getConfidence()))) continue;
            bestConf = cf;
            this.bestGuess = prober;
        }
        return bestConf;
    }

    @Override
    public CharsetProber.ProbingState getState() {
        return this.state;
    }

    @Override
    public CharsetProber.ProbingState handleData(byte[] buf, int offset, int length) {
        boolean keepNext = true;
        byte[] highbyteBuf = new byte[length];
        int highpos = 0;
        int maxPos = offset + length;
        for (int i = offset; i < maxPos; ++i) {
            if ((buf[i] & 0x80) != 0) {
                highbyteBuf[highpos++] = buf[i];
                keepNext = true;
                continue;
            }
            if (!keepNext) continue;
            highbyteBuf[highpos++] = buf[i];
            keepNext = false;
        }
        for (CharsetProber prober : this.probers) {
            if (!prober.isActive()) continue;
            CharsetProber.ProbingState st = prober.handleData(highbyteBuf, 0, highpos);
            if (st == CharsetProber.ProbingState.FOUND_IT) {
                this.bestGuess = prober;
                this.state = CharsetProber.ProbingState.FOUND_IT;
                break;
            }
            if (st != CharsetProber.ProbingState.NOT_ME) continue;
            prober.setActive(false);
            --this.activeNum;
            if (this.activeNum > 0) continue;
            this.state = CharsetProber.ProbingState.NOT_ME;
            break;
        }
        return this.state;
    }

    @Override
    public void reset() {
        this.activeNum = 0;
        for (CharsetProber prober : this.probers) {
            prober.reset();
            prober.setActive(true);
            ++this.activeNum;
        }
        this.bestGuess = null;
        this.state = CharsetProber.ProbingState.DETECTING;
    }

    @Override
    public void setOption() {
    }
}

