/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.universalchardet.prober;

import org.mozilla.universalchardet.prober.CharsetProber;
import org.mozilla.universalchardet.prober.sequence.SequenceModel;

public class SingleByteCharsetProber
extends CharsetProber {
    public static final int SAMPLE_SIZE = 64;
    public static final int SB_ENOUGH_REL_THRESHOLD = 1024;
    public static final float POSITIVE_SHORTCUT_THRESHOLD = 0.95f;
    public static final float NEGATIVE_SHORTCUT_THRESHOLD = 0.05f;
    public static final int SYMBOL_CAT_ORDER = 250;
    public static final int NUMBER_OF_SEQ_CAT = 4;
    public static final int POSITIVE_CAT = 3;
    public static final int NEGATIVE_CAT = 0;
    private CharsetProber.ProbingState state;
    private SequenceModel model;
    private boolean reversed;
    private short lastOrder;
    private int totalSeqs;
    private int[] seqCounters;
    private int totalChar;
    private int freqChar;
    private CharsetProber nameProber;

    public SingleByteCharsetProber(SequenceModel model) {
        this.model = model;
        this.reversed = false;
        this.nameProber = null;
        this.seqCounters = new int[4];
        this.reset();
    }

    public SingleByteCharsetProber(SequenceModel model, boolean reversed, CharsetProber nameProber) {
        this.model = model;
        this.reversed = reversed;
        this.nameProber = nameProber;
        this.seqCounters = new int[4];
        this.reset();
    }

    boolean keepEnglishLetters() {
        return this.model.getKeepEnglishLetter();
    }

    @Override
    public String getCharSetName() {
        if (this.nameProber == null) {
            return this.model.getCharsetName();
        }
        return this.nameProber.getCharSetName();
    }

    @Override
    public float getConfidence() {
        if (this.totalSeqs > 0) {
            float r = 1.0f * (float)this.seqCounters[3] / (float)this.totalSeqs / this.model.getTypicalPositiveRatio();
            if ((r = r * (float)this.freqChar / (float)this.totalChar) >= 1.0f) {
                r = 0.99f;
            }
            return r;
        }
        return 0.01f;
    }

    @Override
    public CharsetProber.ProbingState getState() {
        return this.state;
    }

    @Override
    public CharsetProber.ProbingState handleData(byte[] buf, int offset, int length) {
        int maxPos = offset + length;
        for (int i = offset; i < maxPos; ++i) {
            short order = this.model.getOrder(buf[i]);
            if (order < 250) {
                ++this.totalChar;
            }
            if (order < 64) {
                ++this.freqChar;
                if (this.lastOrder < 64) {
                    ++this.totalSeqs;
                    if (!this.reversed) {
                        byte by = this.model.getPrecedence(this.lastOrder * 64 + order);
                        this.seqCounters[by] = this.seqCounters[by] + 1;
                    } else {
                        byte by = this.model.getPrecedence(order * 64 + this.lastOrder);
                        this.seqCounters[by] = this.seqCounters[by] + 1;
                    }
                }
            }
            this.lastOrder = order;
        }
        if (this.state == CharsetProber.ProbingState.DETECTING && this.totalSeqs > 1024) {
            float cf = this.getConfidence();
            if (cf > 0.95f) {
                this.state = CharsetProber.ProbingState.FOUND_IT;
            } else if (cf < 0.05f) {
                this.state = CharsetProber.ProbingState.NOT_ME;
            }
        }
        return this.state;
    }

    @Override
    public void reset() {
        this.state = CharsetProber.ProbingState.DETECTING;
        this.lastOrder = (short)255;
        for (int i = 0; i < 4; ++i) {
            this.seqCounters[i] = 0;
        }
        this.totalSeqs = 0;
        this.totalChar = 0;
        this.freqChar = 0;
    }

    @Override
    public void setOption() {
    }
}

