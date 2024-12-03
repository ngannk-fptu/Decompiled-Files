/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.internal;

import com.amazonaws.internal.MetricAware;
import com.amazonaws.internal.Releasable;
import com.amazonaws.internal.SdkIOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public class SdkDigestInputStream
extends DigestInputStream
implements MetricAware,
Releasable {
    private static final int SKIP_BUF_SIZE = 2048;

    public SdkDigestInputStream(InputStream stream, MessageDigest digest) {
        super(stream, digest);
    }

    @Override
    public final boolean isMetricActivated() {
        if (this.in instanceof MetricAware) {
            MetricAware metricAware = (MetricAware)((Object)this.in);
            return metricAware.isMetricActivated();
        }
        return false;
    }

    @Override
    public final long skip(long n) throws IOException {
        long m;
        int len;
        if (n <= 0L) {
            return n;
        }
        byte[] b = new byte[(int)Math.min(2048L, n)];
        for (m = n; m > 0L; m -= (long)len) {
            len = this.read(b, 0, (int)Math.min(m, (long)b.length));
            if (len != -1) continue;
            return n - m;
        }
        assert (m == 0L);
        return n;
    }

    @Override
    public final void release() {
        SdkIOUtils.closeQuietly(this);
        if (this.in instanceof Releasable) {
            Releasable r = (Releasable)((Object)this.in);
            r.release();
        }
    }
}

