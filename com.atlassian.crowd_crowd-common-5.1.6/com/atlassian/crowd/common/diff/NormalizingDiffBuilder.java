/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  org.apache.commons.lang3.Validate
 *  org.apache.commons.lang3.builder.Diff
 *  org.apache.commons.lang3.builder.DiffBuilder
 *  org.apache.commons.lang3.builder.DiffResult
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.crowd.common.diff;

import com.google.common.base.Strings;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.Diff;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.ToStringStyle;

public class NormalizingDiffBuilder<T>
extends DiffBuilder<T> {
    public NormalizingDiffBuilder(T left, T right, ToStringStyle style) {
        super(left, right, style);
    }

    public NormalizingDiffBuilder<T> append(String fieldName, String leftValue, String rightValue) {
        super.append(fieldName, (Object)Strings.emptyToNull((String)leftValue), (Object)Strings.emptyToNull((String)rightValue));
        return this;
    }

    public NormalizingDiffBuilder<T> appendDiff(String fieldName, DiffResult<?> diffResult) {
        Validate.notNull((Object)fieldName, (String)"fieldName", (Object[])new Object[0]);
        Validate.notNull(diffResult, (String)"diffResult", (Object[])new Object[0]);
        for (Diff diff : diffResult.getDiffs()) {
            this.append(fieldName + "." + diff.getFieldName(), diff.getLeft(), diff.getRight());
        }
        return this;
    }
}

