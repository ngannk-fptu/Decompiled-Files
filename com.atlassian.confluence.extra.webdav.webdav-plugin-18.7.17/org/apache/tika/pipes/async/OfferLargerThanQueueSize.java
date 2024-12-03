/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes.async;

public class OfferLargerThanQueueSize
extends IllegalArgumentException {
    private final int sizeOffered;
    private final int queueSize;

    public OfferLargerThanQueueSize(int sizeOffered, int queueSize) {
        this.sizeOffered = sizeOffered;
        this.queueSize = queueSize;
    }

    @Override
    public String getMessage() {
        return "sizeOffered (" + this.sizeOffered + ") is greater than queue size (" + this.queueSize + ")";
    }

    public int getQueueSize() {
        return this.queueSize;
    }

    public int getSizeOffered() {
        return this.sizeOffered;
    }
}

