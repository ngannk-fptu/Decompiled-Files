/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.util.profiling.CompositeTicker;
import com.atlassian.util.profiling.Ticker;

@Internal
class Tickers {
    private Tickers() {
        throw new UnsupportedOperationException("Tickers is an utility class and should not be instantiated");
    }

    static Ticker of(Ticker ticker1, Ticker ticker2) {
        if (ticker1 == Ticker.NO_OP || ticker1 == null) {
            return ticker2;
        }
        if (ticker2 == Ticker.NO_OP || ticker2 == null) {
            return ticker1;
        }
        return new CompositeTicker(ticker1, ticker2);
    }

    public static Ticker of(Ticker ticker1, Ticker ... tickers) {
        CompositeTicker result = Tickers.addTicker(ticker1, null);
        for (Ticker ticker : tickers) {
            result = Tickers.addTicker(ticker, result);
        }
        return result == null ? Ticker.NO_OP : result;
    }

    static CompositeTicker addTicker(Ticker ticker, CompositeTicker compositeTicker) {
        if (ticker == null || ticker == Ticker.NO_OP) {
            return compositeTicker;
        }
        CompositeTicker result = compositeTicker == null ? new CompositeTicker() : compositeTicker;
        result.add(ticker);
        return result;
    }
}

