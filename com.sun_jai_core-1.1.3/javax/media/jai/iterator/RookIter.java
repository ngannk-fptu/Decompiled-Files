/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.iterator;

import javax.media.jai.iterator.RectIter;

public interface RookIter
extends RectIter {
    public void prevLine();

    public boolean prevLineDone();

    public void endLines();

    public void prevPixel();

    public boolean prevPixelDone();

    public void endPixels();

    public void prevBand();

    public boolean prevBandDone();

    public void endBands();
}

