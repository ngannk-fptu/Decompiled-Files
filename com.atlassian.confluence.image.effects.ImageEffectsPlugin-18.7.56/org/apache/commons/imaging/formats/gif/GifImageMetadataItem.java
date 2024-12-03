/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.gif;

import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.gif.DisposalMethod;

public class GifImageMetadataItem
implements ImageMetadata.ImageMetadataItem {
    private static final String NEWLINE = System.getProperty("line.separator");
    private final int delay;
    private final int leftPosition;
    private final int topPosition;
    private final DisposalMethod disposalMethod;

    GifImageMetadataItem(int delay, int leftPosition, int topPosition, DisposalMethod disposalMethod) {
        this.delay = delay;
        this.leftPosition = leftPosition;
        this.topPosition = topPosition;
        this.disposalMethod = disposalMethod;
    }

    public int getDelay() {
        return this.delay;
    }

    public int getLeftPosition() {
        return this.leftPosition;
    }

    public int getTopPosition() {
        return this.topPosition;
    }

    public DisposalMethod getDisposalMethod() {
        return this.disposalMethod;
    }

    @Override
    public String toString(String prefix) {
        prefix = prefix == null ? "" : prefix;
        StringBuilder result = new StringBuilder();
        result.append(String.format("%sDelay: %d%s", prefix, this.delay, NEWLINE));
        result.append(String.format("%sLeft position: %d%s", prefix, this.leftPosition, NEWLINE));
        result.append(String.format("%sTop position: %d%s", prefix, this.topPosition, NEWLINE));
        result.append(String.format("%sDisposal method: %s%s", new Object[]{prefix, this.disposalMethod, NEWLINE}));
        return result.toString();
    }
}

