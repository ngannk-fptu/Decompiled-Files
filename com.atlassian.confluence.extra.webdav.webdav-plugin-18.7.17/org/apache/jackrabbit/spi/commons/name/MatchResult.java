/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.name;

import org.apache.jackrabbit.spi.Path;

public class MatchResult {
    private final Path path;
    private final int pathLength;
    private int matchPos;
    private final int matchLength;

    MatchResult(Path path, int length) {
        this(path, 0, length);
    }

    MatchResult(Path path, int pos, int length) {
        if (!path.isNormalized()) {
            throw new IllegalArgumentException("Path not normalized");
        }
        this.path = path;
        this.matchPos = pos;
        this.matchLength = length;
        this.pathLength = path.getLength();
    }

    public Path getRemainder() {
        if (this.matchPos + this.matchLength >= this.pathLength) {
            return null;
        }
        return this.path.subPath(this.matchPos + this.matchLength, this.pathLength);
    }

    public Path getMatch() {
        if (this.matchLength == 0) {
            return null;
        }
        return this.path.subPath(this.matchPos, this.matchPos + this.matchLength);
    }

    public int getMatchPos() {
        return this.matchPos;
    }

    public int getMatchLength() {
        return this.matchLength;
    }

    public boolean isMatch() {
        return this.matchLength > 0;
    }

    public boolean isFullMatch() {
        return this.pathLength == this.matchLength;
    }

    MatchResult setPos(int matchPos) {
        this.matchPos = matchPos;
        return this;
    }
}

