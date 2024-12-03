/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.util.pattern;

import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.pattern.PathPattern;

abstract class PathElement {
    protected static final int WILDCARD_WEIGHT = 100;
    protected static final int CAPTURE_VARIABLE_WEIGHT = 1;
    protected static final MultiValueMap<String, String> NO_PARAMETERS = new LinkedMultiValueMap<String, String>();
    protected final int pos;
    protected final char separator;
    @Nullable
    protected PathElement next;
    @Nullable
    protected PathElement prev;

    PathElement(int pos, char separator) {
        this.pos = pos;
        this.separator = separator;
    }

    public abstract boolean matches(int var1, PathPattern.MatchingContext var2);

    public abstract int getNormalizedLength();

    public abstract char[] getChars();

    public int getCaptureCount() {
        return 0;
    }

    public int getWildcardCount() {
        return 0;
    }

    public int getScore() {
        return 0;
    }

    protected final boolean isNoMorePattern() {
        return this.next == null;
    }
}

