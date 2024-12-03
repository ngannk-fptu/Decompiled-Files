/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.uri.rules;

import com.sun.jersey.api.core.Traceable;
import java.util.regex.MatchResult;

public interface UriMatchResultContext
extends Traceable {
    public MatchResult getMatchResult();

    public void setMatchResult(MatchResult var1);
}

