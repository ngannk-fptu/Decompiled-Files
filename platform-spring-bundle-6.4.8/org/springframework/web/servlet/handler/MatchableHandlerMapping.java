/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.RequestMatchResult;
import org.springframework.web.util.pattern.PathPatternParser;

public interface MatchableHandlerMapping
extends HandlerMapping {
    @Nullable
    default public PathPatternParser getPatternParser() {
        return null;
    }

    @Nullable
    public RequestMatchResult match(HttpServletRequest var1, String var2);
}

