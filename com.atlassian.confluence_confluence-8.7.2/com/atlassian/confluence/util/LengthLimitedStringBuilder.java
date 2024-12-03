/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.extractor.util.LimitReachedException
 *  javax.annotation.concurrent.NotThreadSafe
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.search.v2.extractor.util.LimitReachedException;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class LengthLimitedStringBuilder {
    private final StringBuilder buffer;
    private final LIMIT_BEHAVIOUR throwWhenLimitReached;
    private final LengthLimitProvider lengthLimitProvider;
    private boolean limitReached;

    public LengthLimitedStringBuilder(LengthLimitProvider lengthLimitProvider) {
        this(lengthLimitProvider, LIMIT_BEHAVIOUR.SILENT);
    }

    public LengthLimitedStringBuilder(LengthLimitProvider lengthLimitProvider, LIMIT_BEHAVIOUR limitBehaviour) {
        this.lengthLimitProvider = lengthLimitProvider;
        this.buffer = new StringBuilder(Math.min(lengthLimitProvider.limit(), 16));
        this.throwWhenLimitReached = limitBehaviour;
        this.limitReached = false;
    }

    public void setLength(int l) {
        this.buffer.setLength(l);
    }

    public int length() {
        return this.buffer.length();
    }

    public LengthLimitedStringBuilder append(char[] str, int offset, int len) {
        int length = len;
        if (this.limitReached(len)) {
            length = this.remainingLength();
        }
        this.buffer.append(str, offset, length);
        return this;
    }

    public LengthLimitedStringBuilder append(char c) {
        if (!this.limitReached(1)) {
            this.buffer.append(c);
        }
        return this;
    }

    public LengthLimitedStringBuilder append(String s) {
        int length = s.length();
        if (this.limitReached(s.length())) {
            length = this.remainingLength();
        }
        this.buffer.append(s, 0, length);
        this.removeSplitCharacter();
        return this;
    }

    private void removeSplitCharacter() {
        int bufferLength = this.buffer.length();
        if (bufferLength > 1 && Character.isHighSurrogate(this.buffer.charAt(bufferLength - 1))) {
            this.buffer.setLength(bufferLength - 1);
        }
    }

    private int remainingLength() {
        return this.capacity() - this.buffer.length();
    }

    protected boolean limitReached(int length) {
        int newCapacity;
        if (this.buffer.length() + length > this.capacity() && (newCapacity = (this.capacity() + 1) * 2) > this.lengthLimitProvider.limit()) {
            this.limitReached = true;
            if (this.throwWhenLimitReached == LIMIT_BEHAVIOUR.THROW) {
                throw new LimitReachedException();
            }
            return true;
        }
        this.limitReached = false;
        return false;
    }

    public String toString() {
        return this.buffer.toString();
    }

    public int capacity() {
        return this.buffer.capacity();
    }

    public boolean isLimitReached() {
        return this.limitReached;
    }

    public static interface LengthLimitProvider {
        public int limit();
    }

    public static enum LIMIT_BEHAVIOUR {
        THROW,
        SILENT;

    }
}

