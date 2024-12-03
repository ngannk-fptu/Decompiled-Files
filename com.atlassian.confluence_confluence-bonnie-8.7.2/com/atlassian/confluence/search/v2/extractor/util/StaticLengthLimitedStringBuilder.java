/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.extractor.util;

import com.atlassian.confluence.search.v2.extractor.util.AbstractLengthLimitedStringBuilder;

public class StaticLengthLimitedStringBuilder
extends AbstractLengthLimitedStringBuilder {
    private final int maxLength;

    public StaticLengthLimitedStringBuilder(int maxLength) {
        this(maxLength, AbstractLengthLimitedStringBuilder.LIMIT_BEHAVIOUR.SILENT);
    }

    public StaticLengthLimitedStringBuilder(int maxLength, AbstractLengthLimitedStringBuilder.LIMIT_BEHAVIOUR limitBehaviour) {
        super(limitBehaviour);
        this.buffer = new StringBuilder(maxLength < 16 ? maxLength : 16);
        this.maxLength = maxLength;
    }

    @Override
    protected int limit() {
        return this.maxLength;
    }
}

