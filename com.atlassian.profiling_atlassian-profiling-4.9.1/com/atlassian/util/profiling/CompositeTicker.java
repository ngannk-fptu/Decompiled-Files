/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
class CompositeTicker
implements Ticker {
    private static final Logger log = LoggerFactory.getLogger(Timers.class);
    private final List<Ticker> tickers;
    private volatile boolean closed;

    CompositeTicker() {
        this.tickers = new ArrayList<Ticker>(4);
    }

    CompositeTicker(Ticker ... values) {
        this.tickers = new ArrayList<Ticker>(values.length);
        this.tickers.addAll(Arrays.asList(values));
    }

    @Override
    public void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        for (Ticker ticker : this.tickers) {
            try {
                ticker.close();
            }
            catch (Exception e) {
                log.debug("Failure closing ticker", (Throwable)e);
            }
        }
    }

    void add(Ticker ticker) {
        this.tickers.add(ticker);
    }
}

