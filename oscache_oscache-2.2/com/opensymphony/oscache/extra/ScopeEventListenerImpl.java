/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.extra;

import com.opensymphony.oscache.base.events.ScopeEvent;
import com.opensymphony.oscache.base.events.ScopeEventListener;
import com.opensymphony.oscache.base.events.ScopeEventType;

public class ScopeEventListenerImpl
implements ScopeEventListener {
    public static final String[] SCOPE_NAMES = new String[]{null, "page", "request", "session", "application"};
    public static final int NB_SCOPES = SCOPE_NAMES.length - 1;
    public static final int PAGE_SCOPE = 1;
    public static final int REQUEST_SCOPE = 2;
    public static final int SESSION_SCOPE = 3;
    public static final int APPLICATION_SCOPE = 4;
    private int[] scopeFlushCount = new int[NB_SCOPES + 1];

    public int getApplicationScopeFlushCount() {
        return this.scopeFlushCount[4];
    }

    public int getPageScopeFlushCount() {
        return this.scopeFlushCount[1];
    }

    public int getRequestScopeFlushCount() {
        return this.scopeFlushCount[2];
    }

    public int getSessionScopeFlushCount() {
        return this.scopeFlushCount[3];
    }

    public int getTotalScopeFlushCount() {
        int total = 0;
        for (int count = 1; count <= NB_SCOPES; ++count) {
            total += this.scopeFlushCount[count];
        }
        return total;
    }

    public void scopeFlushed(ScopeEvent event) {
        ScopeEventType eventType = event.getEventType();
        if (eventType == ScopeEventType.ALL_SCOPES_FLUSHED) {
            int count = 1;
            while (count <= NB_SCOPES) {
                int n = count++;
                this.scopeFlushCount[n] = this.scopeFlushCount[n] + 1;
            }
        } else if (eventType == ScopeEventType.SCOPE_FLUSHED) {
            int n = event.getScope();
            this.scopeFlushCount[n] = this.scopeFlushCount[n] + 1;
        } else {
            throw new IllegalArgumentException("Unknown Scope Event type received");
        }
    }

    public String toString() {
        StringBuffer returnString = new StringBuffer("Flush count for ");
        for (int count = 1; count <= NB_SCOPES; ++count) {
            returnString.append("scope " + SCOPE_NAMES[count] + " = " + this.scopeFlushCount[count] + ", ");
        }
        returnString.setLength(returnString.length() - 2);
        return returnString.toString();
    }
}

