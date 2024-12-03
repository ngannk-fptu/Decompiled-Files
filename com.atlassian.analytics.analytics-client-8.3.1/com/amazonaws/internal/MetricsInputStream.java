/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.internal;

import com.amazonaws.internal.DelegateInputStream;
import com.amazonaws.util.AWSRequestMetrics;
import java.io.IOException;
import java.io.InputStream;

public class MetricsInputStream
extends DelegateInputStream {
    private AWSRequestMetrics metrics;

    public MetricsInputStream(InputStream in) {
        super(in);
    }

    public void setMetrics(AWSRequestMetrics metrics) {
        this.metrics = metrics;
    }

    @Override
    public int read() throws IOException {
        if (this.metrics != null) {
            this.metrics.startEvent(AWSRequestMetrics.Field.HttpSocketReadTime);
        }
        try {
            int n = this.in.read();
            return n;
        }
        finally {
            if (this.metrics != null) {
                this.metrics.endEvent(AWSRequestMetrics.Field.HttpSocketReadTime);
            }
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (this.metrics != null) {
            this.metrics.startEvent(AWSRequestMetrics.Field.HttpSocketReadTime);
        }
        try {
            int n = this.in.read(b);
            return n;
        }
        finally {
            if (this.metrics != null) {
                this.metrics.endEvent(AWSRequestMetrics.Field.HttpSocketReadTime);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.metrics != null) {
            this.metrics.startEvent(AWSRequestMetrics.Field.HttpSocketReadTime);
        }
        try {
            int n = this.in.read(b, off, len);
            return n;
        }
        finally {
            if (this.metrics != null) {
                this.metrics.endEvent(AWSRequestMetrics.Field.HttpSocketReadTime);
            }
        }
    }
}

