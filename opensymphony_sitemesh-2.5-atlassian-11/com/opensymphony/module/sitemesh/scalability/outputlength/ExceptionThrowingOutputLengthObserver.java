/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.scalability.outputlength;

import com.opensymphony.module.sitemesh.scalability.outputlength.MaxOutputLengthExceeded;
import com.opensymphony.module.sitemesh.scalability.outputlength.OutputLengthObserver;

public class ExceptionThrowingOutputLengthObserver
implements OutputLengthObserver {
    private final long maxOutputLength;
    private final int maximumOutputExceededHttpCode;
    private long soFar;

    public ExceptionThrowingOutputLengthObserver(long maxOutputLength, int maximumOutputExceededHttpCode) {
        this.maxOutputLength = maxOutputLength;
        this.maximumOutputExceededHttpCode = maximumOutputExceededHttpCode;
    }

    public void nBytes(long n) {
        if (this.soFar + n > this.maxOutputLength) {
            throw new MaxOutputLengthExceeded(this.maxOutputLength, this.maximumOutputExceededHttpCode);
        }
        this.soFar += n;
    }

    public void nChars(long n) {
        if (this.soFar + n * 2L > this.maxOutputLength) {
            throw new MaxOutputLengthExceeded(this.maxOutputLength, this.maximumOutputExceededHttpCode);
        }
        this.soFar += n * 2L;
    }
}

