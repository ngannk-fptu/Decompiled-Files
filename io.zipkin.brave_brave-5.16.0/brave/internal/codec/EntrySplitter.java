/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.codec;

import brave.internal.Platform;

public final class EntrySplitter {
    final char keyValueSeparator;
    final char entrySeparator;
    int maxEntries;
    final boolean trimOWSAroundEntrySeparator;
    final boolean trimOWSAroundKeyValueSeparator;
    final boolean keyValueSeparatorRequired;
    final boolean shouldThrow;
    final String missingKey;
    final String missingKeyValueSeparator;
    final String overMaxEntries;

    public static Builder newBuilder() {
        return new Builder();
    }

    EntrySplitter(Builder builder) {
        this.keyValueSeparator = builder.keyValueSeparator;
        this.entrySeparator = builder.entrySeparator;
        this.maxEntries = builder.maxEntries;
        this.trimOWSAroundEntrySeparator = builder.trimOWSAroundEntrySeparator;
        this.trimOWSAroundKeyValueSeparator = builder.trimOWSAroundKeyValueSeparator;
        this.keyValueSeparatorRequired = builder.keyValueSeparatorRequired;
        this.shouldThrow = builder.shouldThrow;
        this.missingKey = "Invalid input: no key before '" + this.keyValueSeparator + "'";
        this.missingKeyValueSeparator = "Invalid input: missing key value separator '" + this.keyValueSeparator + "'";
        this.overMaxEntries = "Invalid input: over " + this.maxEntries + " entries";
    }

    public <T> boolean parse(Handler<T> handler, T target, CharSequence input) {
        if (input == null) {
            throw new NullPointerException("input == null");
        }
        return this.parse(handler, target, input, 0, input.length());
    }

    public <T> boolean parse(Handler<T> handler, T target, CharSequence input, int beginIndex, int endIndex) {
        if (handler == null) {
            throw new NullPointerException("handler == null");
        }
        if (target == null) {
            throw new NullPointerException("target == null");
        }
        if (input == null) {
            throw new NullPointerException("input == null");
        }
        if (beginIndex < 0) {
            throw new IllegalArgumentException("beginIndex < 0");
        }
        if (endIndex > input.length()) {
            throw new IllegalArgumentException("endIndex > input.length()");
        }
        int remainingEntries = this.maxEntries;
        int beginKey = -1;
        int endKey = -1;
        int beginValue = -1;
        for (int i = beginIndex; i < endIndex; ++i) {
            boolean nextIsEnd;
            char c = input.charAt(i);
            boolean bl = nextIsEnd = i + 1 == endIndex;
            if (c == this.entrySeparator || nextIsEnd) {
                int endValue;
                if (c == this.keyValueSeparator) {
                    beginValue = i;
                }
                if (beginKey == -1 && beginValue == -1) continue;
                if (beginKey == -1) {
                    return EntrySplitter.logOrThrow(this.missingKey, this.shouldThrow);
                }
                if (nextIsEnd && beginValue == -1) {
                    int n = beginValue = c == this.entrySeparator ? i + 1 : i;
                }
                if (endKey == -1) {
                    if (this.keyValueSeparatorRequired && c != this.keyValueSeparator) {
                        return EntrySplitter.logOrThrow(this.missingKeyValueSeparator, this.shouldThrow);
                    }
                    int n = endKey = nextIsEnd && c != this.keyValueSeparator ? i + 1 : i;
                    if (this.trimOWSAroundKeyValueSeparator) {
                        endKey = EntrySplitter.rewindOWS(input, beginKey, endKey);
                    }
                    beginValue = endValue = endKey;
                } else {
                    int n = endValue = nextIsEnd ? i + 1 : i;
                    if (this.trimOWSAroundEntrySeparator) {
                        endValue = EntrySplitter.rewindOWS(input, beginValue, endValue);
                    }
                }
                if (remainingEntries-- == 0) {
                    EntrySplitter.logOrThrow(this.overMaxEntries, this.shouldThrow);
                }
                if (!handler.onEntry(target, input, beginKey, endKey, beginValue, endValue)) {
                    return false;
                }
                beginValue = -1;
                endKey = -1;
                beginKey = -1;
                continue;
            }
            if (beginKey == -1) {
                if (this.trimOWSAroundEntrySeparator && EntrySplitter.isOWS(c)) continue;
                if (c == this.keyValueSeparator && (i == beginIndex || input.charAt(i - 1) == this.entrySeparator)) {
                    return EntrySplitter.logOrThrow(this.missingKey, this.shouldThrow);
                }
                beginKey = i;
                continue;
            }
            if (endKey == -1 && c == this.keyValueSeparator) {
                endKey = i;
                if (!this.trimOWSAroundKeyValueSeparator) continue;
                endKey = EntrySplitter.rewindOWS(input, beginIndex, endKey);
                continue;
            }
            if (endKey == -1 || beginValue != -1 || this.trimOWSAroundKeyValueSeparator && EntrySplitter.isOWS(c) || c == this.keyValueSeparator) continue;
            beginValue = i;
        }
        return true;
    }

    static int rewindOWS(CharSequence input, int beginIndex, int endIndex) {
        while (EntrySplitter.isOWS(input.charAt(endIndex - 1))) {
            if (--endIndex != beginIndex) continue;
            return beginIndex;
        }
        return endIndex;
    }

    static boolean isOWS(char c) {
        return c == ' ' || c == '\t';
    }

    static boolean logOrThrow(String msg, boolean shouldThrow) {
        if (shouldThrow) {
            throw new IllegalArgumentException(msg);
        }
        Platform.get().log(msg, null);
        return false;
    }

    public static interface Handler<T> {
        public boolean onEntry(T var1, CharSequence var2, int var3, int var4, int var5, int var6);
    }

    public static final class Builder {
        int maxEntries = Integer.MAX_VALUE;
        char entrySeparator = (char)44;
        char keyValueSeparator = (char)61;
        boolean trimOWSAroundEntrySeparator = true;
        boolean trimOWSAroundKeyValueSeparator = true;
        boolean keyValueSeparatorRequired = true;
        boolean shouldThrow = false;

        public Builder maxEntries(int maxEntries) {
            if (maxEntries <= 0) {
                throw new IllegalArgumentException("maxEntries <= 0");
            }
            this.maxEntries = maxEntries;
            return this;
        }

        public Builder entrySeparator(char entrySeparator) {
            if (entrySeparator == '\u0000') {
                throw new IllegalArgumentException("entrySeparator == 0");
            }
            this.entrySeparator = entrySeparator;
            return this;
        }

        public Builder keyValueSeparator(char keyValueSeparator) {
            if (keyValueSeparator == '\u0000') {
                throw new IllegalArgumentException("keyValueSeparator == 0");
            }
            this.keyValueSeparator = keyValueSeparator;
            return this;
        }

        public Builder trimOWSAroundEntrySeparator(boolean trimOWSAroundEntrySeparator) {
            this.trimOWSAroundEntrySeparator = trimOWSAroundEntrySeparator;
            return this;
        }

        public Builder trimOWSAroundKeyValueSeparator(boolean trimOWSAroundKeyValueSeparator) {
            this.trimOWSAroundKeyValueSeparator = trimOWSAroundKeyValueSeparator;
            return this;
        }

        public Builder keyValueSeparatorRequired(boolean keyValueSeparatorRequired) {
            this.keyValueSeparatorRequired = keyValueSeparatorRequired;
            return this;
        }

        public Builder shouldThrow(boolean shouldThrow) {
            this.shouldThrow = shouldThrow;
            return this;
        }

        public EntrySplitter build() {
            if (this.entrySeparator == this.keyValueSeparator) {
                throw new IllegalArgumentException("entrySeparator == keyValueSeparator");
            }
            return new EntrySplitter(this);
        }
    }
}

