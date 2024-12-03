/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.scalability.outputlength;

public class MaxOutputLengthExceeded
extends RuntimeException {
    private final long maxOutputLength;
    private final int maximumOutputExceededHttpCode;

    public MaxOutputLengthExceeded(long maxOutputLength, int maximumOutputExceededHttpCode) {
        super("The maximum output length of " + maxOutputLength + " bytes has been exceeded");
        this.maxOutputLength = maxOutputLength;
        this.maximumOutputExceededHttpCode = maximumOutputExceededHttpCode;
    }

    public long getMaxOutputLength() {
        return this.maxOutputLength;
    }

    public int getMaximumOutputExceededHttpCode() {
        return this.maximumOutputExceededHttpCode;
    }
}

