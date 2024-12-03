/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer;

import java.util.regex.Pattern;

public enum TokenType {
    INLINE("inltokxyzkdtnhgnsbdfinltok"),
    BLOCK("blktokxyzkdtnhgnsbdfblktok"),
    INLINE_BLOCK("ibltokxyzkdtnhgnsbdfibltok");

    private final String tokenString;
    private final String tokenPatternString;
    private final Pattern tokenPattern;

    private TokenType(String tokenString) {
        this.tokenString = tokenString;
        this.tokenPatternString = tokenString + "\\d+" + tokenString;
        this.tokenPattern = Pattern.compile(this.tokenPatternString);
    }

    public String getTokenMarker() {
        return this.tokenString;
    }

    public String getTokenPatternString() {
        return this.tokenPatternString;
    }

    public Pattern getTokenPattern() {
        return this.tokenPattern;
    }
}

