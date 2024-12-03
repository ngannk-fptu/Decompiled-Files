/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.AttributeSource
 */
package org.apache.lucene.analysis.sinks;

import java.io.IOException;
import org.apache.lucene.analysis.sinks.TeeSinkTokenFilter;
import org.apache.lucene.util.AttributeSource;

public class TokenRangeSinkFilter
extends TeeSinkTokenFilter.SinkFilter {
    private int lower;
    private int upper;
    private int count;

    public TokenRangeSinkFilter(int lower, int upper) {
        this.lower = lower;
        this.upper = upper;
    }

    @Override
    public boolean accept(AttributeSource source) {
        try {
            if (this.count >= this.lower && this.count < this.upper) {
                boolean bl = true;
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        finally {
            ++this.count;
        }
    }

    @Override
    public void reset() throws IOException {
        this.count = 0;
    }
}

