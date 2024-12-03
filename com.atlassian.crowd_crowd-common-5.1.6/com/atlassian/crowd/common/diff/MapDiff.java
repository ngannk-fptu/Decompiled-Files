/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.builder.DiffResult
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.crowd.common.diff;

import com.atlassian.crowd.common.diff.NormalizingDiffBuilder;
import com.google.common.base.Preconditions;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MapDiff {
    private MapDiff() {
    }

    public static DiffResult<Map<String, @Nullable Object>> diff(@Nonnull Map<String, @Nullable Object> left, @Nonnull Map<String, @Nullable Object> right) {
        Preconditions.checkNotNull(left);
        Preconditions.checkNotNull(right);
        StringKeyMapDiffBuilder builder = new StringKeyMapDiffBuilder(left, right);
        left.forEach((key, value) -> builder.append((String)key, value, right.get(key)));
        right.forEach((key, value) -> {
            if (!left.containsKey(key)) {
                builder.append((String)key, null, value);
            }
        });
        return builder.build();
    }

    private static final class CustomToStringStyle
    extends ToStringStyle {
        public static final ToStringStyle STYLE = new CustomToStringStyle();
        private static final long serialVersionUID = 1L;

        private CustomToStringStyle() {
            this.setUseClassName(false);
            this.setUseIdentityHashCode(false);
            this.setFieldSeparator(", ");
            this.setNullText("null");
        }

        private Object readResolve() {
            return STYLE;
        }
    }

    private static class StringKeyMapDiffBuilder
    extends NormalizingDiffBuilder<Map<String, Object>> {
        private StringKeyMapDiffBuilder(Map<String, @Nullable Object> left, Map<String, @Nullable Object> right) {
            super(left, right, CustomToStringStyle.STYLE);
        }
    }
}

