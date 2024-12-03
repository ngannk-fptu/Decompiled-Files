/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.oro.text.regex.MatchResult
 */
package org.radeox.regex;

import org.radeox.regex.MatchResult;

public class OroMatchResult
extends MatchResult {
    private org.apache.oro.text.regex.MatchResult matchResult;

    public OroMatchResult(org.apache.oro.text.regex.MatchResult matchResult) {
        this.matchResult = matchResult;
    }

    public int groups() {
        return this.matchResult.groups();
    }

    public String group(int i) {
        return this.matchResult.group(i);
    }

    public int beginOffset(int i) {
        return this.matchResult.begin(i);
    }

    public int endOffset(int i) {
        return this.matchResult.end(i);
    }
}

