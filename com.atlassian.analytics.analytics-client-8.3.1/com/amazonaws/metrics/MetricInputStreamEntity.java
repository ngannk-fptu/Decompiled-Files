/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.entity.ContentType
 *  org.apache.http.entity.InputStreamEntity
 */
package com.amazonaws.metrics;

import com.amazonaws.internal.MetricAware;
import com.amazonaws.metrics.ByteThroughputHelper;
import com.amazonaws.metrics.ThroughputMetricType;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;

public class MetricInputStreamEntity
extends InputStreamEntity {
    private static final int BUFFER_SIZE = 2048;
    private final ByteThroughputHelper helper;

    public MetricInputStreamEntity(ThroughputMetricType metricType, InputStream instream, long length) {
        super(instream, length);
        this.helper = new ByteThroughputHelper(metricType);
    }

    public MetricInputStreamEntity(ThroughputMetricType metricType, InputStream instream, long length, ContentType contentType) {
        super(instream, length, contentType);
        this.helper = new ByteThroughputHelper(metricType);
    }

    public void writeTo(OutputStream outstream) throws IOException {
        MetricAware aware;
        if (outstream instanceof MetricAware && (aware = (MetricAware)((Object)outstream)).isMetricActivated()) {
            super.writeTo(outstream);
            return;
        }
        this.writeToWithMetrics(outstream);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeToWithMetrics(OutputStream outstream) throws IOException {
        block8: {
            if (outstream == null) {
                throw new IllegalArgumentException("Output stream may not be null");
            }
            InputStream content = this.getContent();
            long length = this.getContentLength();
            InputStream instream = content;
            try {
                int l;
                byte[] buffer = new byte[2048];
                if (length < 0L) {
                    int l2;
                    while ((l2 = instream.read(buffer)) != -1) {
                        long startNano = this.helper.startTiming();
                        outstream.write(buffer, 0, l2);
                        this.helper.increment(l2, startNano);
                    }
                    break block8;
                }
                for (long remaining = length; remaining > 0L; remaining -= (long)l) {
                    l = instream.read(buffer, 0, (int)Math.min(2048L, remaining));
                    if (l == -1) {
                        break;
                    }
                    long startNano = this.helper.startTiming();
                    outstream.write(buffer, 0, l);
                    this.helper.increment(l, startNano);
                }
            }
            finally {
                this.helper.reportMetrics();
                instream.close();
            }
        }
    }
}

