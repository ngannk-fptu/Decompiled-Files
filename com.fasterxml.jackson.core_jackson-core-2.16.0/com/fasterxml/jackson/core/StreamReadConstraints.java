/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.exc.StreamConstraintsException;
import java.io.Serializable;

public class StreamReadConstraints
implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int DEFAULT_MAX_DEPTH = 1000;
    public static final long DEFAULT_MAX_DOC_LEN = -1L;
    public static final int DEFAULT_MAX_NUM_LEN = 1000;
    public static final int DEFAULT_MAX_STRING_LEN = 20000000;
    public static final int DEFAULT_MAX_NAME_LEN = 50000;
    private static final int MAX_BIGINT_SCALE_MAGNITUDE = 100000;
    protected final int _maxNestingDepth;
    protected final long _maxDocLen;
    protected final int _maxNumLen;
    protected final int _maxStringLen;
    protected final int _maxNameLen;
    private static StreamReadConstraints DEFAULT = new StreamReadConstraints(1000, -1L, 1000, 20000000, 50000);

    public static void overrideDefaultStreamReadConstraints(StreamReadConstraints streamReadConstraints) {
        DEFAULT = streamReadConstraints == null ? new StreamReadConstraints(1000, -1L, 1000, 20000000) : streamReadConstraints;
    }

    @Deprecated
    protected StreamReadConstraints(int maxNestingDepth, long maxDocLen, int maxNumLen, int maxStringLen) {
        this(maxNestingDepth, -1L, maxNumLen, maxStringLen, 50000);
    }

    protected StreamReadConstraints(int maxNestingDepth, long maxDocLen, int maxNumLen, int maxStringLen, int maxNameLen) {
        this._maxNestingDepth = maxNestingDepth;
        this._maxDocLen = maxDocLen;
        this._maxNumLen = maxNumLen;
        this._maxStringLen = maxStringLen;
        this._maxNameLen = maxNameLen;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static StreamReadConstraints defaults() {
        return DEFAULT;
    }

    public Builder rebuild() {
        return new Builder(this);
    }

    public int getMaxNestingDepth() {
        return this._maxNestingDepth;
    }

    public long getMaxDocumentLength() {
        return this._maxDocLen;
    }

    public boolean hasMaxDocumentLength() {
        return this._maxDocLen > 0L;
    }

    public int getMaxNumberLength() {
        return this._maxNumLen;
    }

    public int getMaxStringLength() {
        return this._maxStringLen;
    }

    public int getMaxNameLength() {
        return this._maxNameLen;
    }

    public void validateNestingDepth(int depth) throws StreamConstraintsException {
        if (depth > this._maxNestingDepth) {
            throw this._constructException("Document nesting depth (%d) exceeds the maximum allowed (%d, from %s)", depth, this._maxNestingDepth, this._constrainRef("getMaxNestingDepth"));
        }
    }

    public void validateDocumentLength(long len) throws StreamConstraintsException {
        if (len > this._maxDocLen && this._maxDocLen > 0L) {
            throw this._constructException("Document length (%d) exceeds the maximum allowed (%d, from %s)", len, this._maxDocLen, this._constrainRef("getMaxDocumentLength"));
        }
    }

    public void validateFPLength(int length) throws StreamConstraintsException {
        if (length > this._maxNumLen) {
            throw this._constructException("Number value length (%d) exceeds the maximum allowed (%d, from %s)", length, this._maxNumLen, this._constrainRef("getMaxNumberLength"));
        }
    }

    public void validateIntegerLength(int length) throws StreamConstraintsException {
        if (length > this._maxNumLen) {
            throw this._constructException("Number value length (%d) exceeds the maximum allowed (%d, from %s)", length, this._maxNumLen, this._constrainRef("getMaxNumberLength"));
        }
    }

    public void validateStringLength(int length) throws StreamConstraintsException {
        if (length > this._maxStringLen) {
            throw this._constructException("String value length (%d) exceeds the maximum allowed (%d, from %s)", length, this._maxStringLen, this._constrainRef("getMaxStringLength"));
        }
    }

    public void validateNameLength(int length) throws StreamConstraintsException {
        if (length > this._maxNameLen) {
            throw this._constructException("Name length (%d) exceeds the maximum allowed (%d, from %s)", length, this._maxNameLen, this._constrainRef("getMaxNameLength"));
        }
    }

    public void validateBigIntegerScale(int scale) throws StreamConstraintsException {
        int absScale = Math.abs(scale);
        int limit = 100000;
        if (absScale > 100000) {
            throw this._constructException("BigDecimal scale (%d) magnitude exceeds the maximum allowed (%d)", scale, 100000);
        }
    }

    protected StreamConstraintsException _constructException(String msgTemplate, Object ... args) throws StreamConstraintsException {
        throw new StreamConstraintsException(String.format(msgTemplate, args));
    }

    protected String _constrainRef(String method) {
        return "`StreamReadConstraints." + method + "()`";
    }

    public static final class Builder {
        private long maxDocLen;
        private int maxNestingDepth;
        private int maxNumLen;
        private int maxStringLen;
        private int maxNameLen;

        public Builder maxNestingDepth(int maxNestingDepth) {
            if (maxNestingDepth < 0) {
                throw new IllegalArgumentException("Cannot set maxNestingDepth to a negative value");
            }
            this.maxNestingDepth = maxNestingDepth;
            return this;
        }

        public Builder maxDocumentLength(long maxDocLen) {
            if (maxDocLen <= 0L) {
                maxDocLen = -1L;
            }
            this.maxDocLen = maxDocLen;
            return this;
        }

        public Builder maxNumberLength(int maxNumLen) {
            if (maxNumLen < 0) {
                throw new IllegalArgumentException("Cannot set maxNumberLength to a negative value");
            }
            this.maxNumLen = maxNumLen;
            return this;
        }

        public Builder maxStringLength(int maxStringLen) {
            if (maxStringLen < 0) {
                throw new IllegalArgumentException("Cannot set maxStringLen to a negative value");
            }
            this.maxStringLen = maxStringLen;
            return this;
        }

        public Builder maxNameLength(int maxNameLen) {
            if (maxNameLen < 0) {
                throw new IllegalArgumentException("Cannot set maxNameLen to a negative value");
            }
            this.maxNameLen = maxNameLen;
            return this;
        }

        Builder() {
            this(1000, -1L, 1000, 20000000, 50000);
        }

        Builder(int maxNestingDepth, long maxDocLen, int maxNumLen, int maxStringLen, int maxNameLen) {
            this.maxNestingDepth = maxNestingDepth;
            this.maxDocLen = maxDocLen;
            this.maxNumLen = maxNumLen;
            this.maxStringLen = maxStringLen;
            this.maxNameLen = maxNameLen;
        }

        Builder(StreamReadConstraints src) {
            this.maxNestingDepth = src._maxNestingDepth;
            this.maxDocLen = src._maxDocLen;
            this.maxNumLen = src._maxNumLen;
            this.maxStringLen = src._maxStringLen;
            this.maxNameLen = src._maxNameLen;
        }

        public StreamReadConstraints build() {
            return new StreamReadConstraints(this.maxNestingDepth, this.maxDocLen, this.maxNumLen, this.maxStringLen, this.maxNameLen);
        }
    }
}

