/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.pats.events.token;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="personalaccesstoken.summary")
public class TokenSummaryEvent {
    private final long tokenTotal;

    public TokenSummaryEvent(long tokenTotal) {
        this.tokenTotal = tokenTotal;
    }

    public long getTokenTotal() {
        return this.tokenTotal;
    }

    public String toString() {
        return "TokenSummaryEvent(tokenTotal=" + this.getTokenTotal() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TokenSummaryEvent)) {
            return false;
        }
        TokenSummaryEvent other = (TokenSummaryEvent)o;
        if (!other.canEqual(this)) {
            return false;
        }
        return this.getTokenTotal() == other.getTokenTotal();
    }

    protected boolean canEqual(Object other) {
        return other instanceof TokenSummaryEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $tokenTotal = this.getTokenTotal();
        result = result * 59 + (int)($tokenTotal >>> 32 ^ $tokenTotal);
        return result;
    }
}

