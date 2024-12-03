/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.universalchardet.prober;

import org.mozilla.universalchardet.Constants;
import org.mozilla.universalchardet.prober.CharsetProber;

public class HebrewProber
extends CharsetProber {
    public static final int FINAL_KAF = 234;
    public static final int NORMAL_KAF = 235;
    public static final int FINAL_MEM = 237;
    public static final int NORMAL_MEM = 238;
    public static final int FINAL_NUN = 239;
    public static final int NORMAL_NUN = 240;
    public static final int FINAL_PE = 243;
    public static final int NORMAL_PE = 244;
    public static final int FINAL_TSADI = 245;
    public static final int NORMAL_TSADI = 246;
    public static final byte SPACE = 32;
    public static final int MIN_FINAL_CHAR_DISTANCE = 5;
    public static final float MIN_MODEL_DISTANCE = 0.01f;
    private int finalCharLogicalScore;
    private int finalCharVisualScore;
    private byte prev;
    private byte beforePrev;
    private CharsetProber logicalProber = null;
    private CharsetProber visualProber = null;

    public HebrewProber() {
        this.reset();
    }

    public void setModalProbers(CharsetProber logicalProber, CharsetProber visualProber) {
        this.logicalProber = logicalProber;
        this.visualProber = visualProber;
    }

    @Override
    public String getCharSetName() {
        int finalsub = this.finalCharLogicalScore - this.finalCharVisualScore;
        if (finalsub >= 5) {
            return Constants.CHARSET_WINDOWS_1255;
        }
        if (finalsub <= -5) {
            return Constants.CHARSET_ISO_8859_8;
        }
        float modelsub = this.logicalProber.getConfidence() - this.visualProber.getConfidence();
        if (modelsub > 0.01f) {
            return Constants.CHARSET_WINDOWS_1255;
        }
        if (modelsub < -0.01f) {
            return Constants.CHARSET_ISO_8859_8;
        }
        if (finalsub < 0) {
            return Constants.CHARSET_ISO_8859_8;
        }
        return Constants.CHARSET_WINDOWS_1255;
    }

    @Override
    public float getConfidence() {
        return 0.0f;
    }

    @Override
    public CharsetProber.ProbingState getState() {
        if (this.logicalProber.getState() == CharsetProber.ProbingState.NOT_ME && this.visualProber.getState() == CharsetProber.ProbingState.NOT_ME) {
            return CharsetProber.ProbingState.NOT_ME;
        }
        return CharsetProber.ProbingState.DETECTING;
    }

    @Override
    public CharsetProber.ProbingState handleData(byte[] buf, int offset, int length) {
        if (this.getState() == CharsetProber.ProbingState.NOT_ME) {
            return CharsetProber.ProbingState.NOT_ME;
        }
        int maxPos = offset + length;
        for (int i = offset; i < maxPos; ++i) {
            byte c = buf[i];
            if (c == 32) {
                if (this.beforePrev != 32) {
                    if (HebrewProber.isFinal(this.prev)) {
                        ++this.finalCharLogicalScore;
                    } else if (HebrewProber.isNonFinal(this.prev)) {
                        ++this.finalCharVisualScore;
                    }
                }
            } else if (this.beforePrev == 32 && HebrewProber.isFinal(this.prev) && c != 32) {
                ++this.finalCharVisualScore;
            }
            this.beforePrev = this.prev;
            this.prev = c;
        }
        return CharsetProber.ProbingState.DETECTING;
    }

    @Override
    public void reset() {
        this.finalCharLogicalScore = 0;
        this.finalCharVisualScore = 0;
        this.prev = (byte)32;
        this.beforePrev = (byte)32;
    }

    @Override
    public void setOption() {
    }

    protected static boolean isFinal(byte b) {
        int c = b & 0xFF;
        return c == 234 || c == 237 || c == 239 || c == 243 || c == 245;
    }

    protected static boolean isNonFinal(byte b) {
        int c = b & 0xFF;
        return c == 235 || c == 238 || c == 240 || c == 244;
    }
}

