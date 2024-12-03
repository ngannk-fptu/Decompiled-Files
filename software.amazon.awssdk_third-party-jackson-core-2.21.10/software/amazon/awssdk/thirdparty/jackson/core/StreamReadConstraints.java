/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.thirdparty.jackson.core;

import java.io.Serializable;
import software.amazon.awssdk.thirdparty.jackson.core.exc.StreamConstraintsException;

public class StreamReadConstraints
implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int DEFAULT_MAX_DEPTH = 1000;
    public static final int DEFAULT_MAX_NUM_LEN = 1000;
    public static final int DEFAULT_MAX_STRING_LEN = 20000000;
    private static final int MAX_BIGINT_SCALE_MAGNITUDE = 100000;
    protected final int _maxNestingDepth;
    protected final int _maxNumLen;
    protected final int _maxStringLen;
    private static StreamReadConstraints DEFAULT = new StreamReadConstraints(1000, 1000, 20000000);

    public static void overrideDefaultStreamReadConstraints(StreamReadConstraints streamReadConstraints) {
        DEFAULT = streamReadConstraints == null ? new StreamReadConstraints(1000, 1000, 20000000) : streamReadConstraints;
    }

    protected StreamReadConstraints(int maxNestingDepth, int maxNumLen, int maxStringLen) {
        this._maxNestingDepth = maxNestingDepth;
        this._maxNumLen = maxNumLen;
        this._maxStringLen = maxStringLen;
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

    public int getMaxNumberLength() {
        return this._maxNumLen;
    }

    public int getMaxStringLength() {
        return this._maxStringLen;
    }

    public void validateNestingDepth(int depth) throws StreamConstraintsException {
        if (depth > this._maxNestingDepth) {
            throw new StreamConstraintsException(String.format("Depth (%d) exceeds the maximum allowed nesting depth (%d)", depth, this._maxNestingDepth));
        }
    }

    public void validateFPLength(int length) throws StreamConstraintsException {
        if (length > this._maxNumLen) {
            throw new StreamConstraintsException(String.format("Number length (%d) exceeds the maximum length (%d)", length, this._maxNumLen));
        }
    }

    public void validateIntegerLength(int length) throws StreamConstraintsException {
        if (length > this._maxNumLen) {
            throw new StreamConstraintsException(String.format("Number length (%d) exceeds the maximum length (%d)", length, this._maxNumLen));
        }
    }

    public void validateStringLength(int length) throws StreamConstraintsException {
        if (length > this._maxStringLen) {
            throw new StreamConstraintsException(String.format("String length (%d) exceeds the maximum length (%d)", length, this._maxStringLen));
        }
    }

    public void validateBigIntegerScale(int scale) throws StreamConstraintsException {
        int absScale = Math.abs(scale);
        int limit = 100000;
        if (absScale > 100000) {
            throw new StreamConstraintsException(String.format("BigDecimal scale (%d) magnitude exceeds maximum allowed (%d)", scale, 100000));
        }
    }

    public static final class Builder {
        private int maxNestingDepth;
        private int maxNumLen;
        private int maxStringLen;

        public Builder maxNestingDepth(int maxNestingDepth) {
            if (maxNestingDepth < 0) {
                throw new IllegalArgumentException("Cannot set maxNestingDepth to a negative value");
            }
            this.maxNestingDepth = maxNestingDepth;
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

        Builder() {
            this(1000, 1000, 20000000);
        }

        Builder(int maxNestingDepth, int maxNumLen, int maxStringLen) {
            this.maxNestingDepth = maxNestingDepth;
            this.maxNumLen = maxNumLen;
            this.maxStringLen = maxStringLen;
        }

        Builder(StreamReadConstraints src) {
            this.maxNestingDepth = src._maxNestingDepth;
            this.maxNumLen = src._maxNumLen;
            this.maxStringLen = src._maxStringLen;
        }

        public StreamReadConstraints build() {
            return new StreamReadConstraints(this.maxNestingDepth, this.maxNumLen, this.maxStringLen);
        }
    }
}

