/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.extractor.util;

import com.atlassian.confluence.search.v2.extractor.util.LimitReachedException;

public abstract class AbstractLengthLimitedStringBuilder {
    protected final LIMIT_BEHAVIOUR throwWhenLimitReached;
    protected StringBuilder buffer = new StringBuilder();
    protected boolean limitReached;

    AbstractLengthLimitedStringBuilder() {
        this(LIMIT_BEHAVIOUR.SILENT);
    }

    AbstractLengthLimitedStringBuilder(LIMIT_BEHAVIOUR limitBehaviour) {
        this.throwWhenLimitReached = limitBehaviour;
        this.limitReached = false;
    }

    public void setLength(int l) {
        this.buffer.setLength(l);
    }

    public int length() {
        return this.buffer.length();
    }

    public AbstractLengthLimitedStringBuilder append(char[] str, int offset, int len) {
        int length = len;
        if (this.limitReached(len)) {
            length = this.remainingLength();
        }
        this.buffer.append(str, offset, length);
        return this;
    }

    public AbstractLengthLimitedStringBuilder append(char c) {
        if (!this.limitReached(1)) {
            this.buffer.append(c);
        }
        return this;
    }

    public AbstractLengthLimitedStringBuilder append(String s) {
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

    protected int remainingLength() {
        return this.capacity() - this.buffer.length();
    }

    protected abstract int limit();

    protected boolean limitReached(int length) {
        int newCapacity;
        if (this.buffer.length() + length > this.capacity() && (newCapacity = (this.capacity() + 1) * 2) > this.limit()) {
            this.limitReached = true;
            if (this.throwWhenLimitReached == LIMIT_BEHAVIOUR.THROW) {
                throw new LimitReachedException();
            }
            return true;
        }
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

    public static enum LIMIT_BEHAVIOUR {
        THROW,
        SILENT;

    }
}

