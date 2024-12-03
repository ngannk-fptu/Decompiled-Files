/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Maybe
 *  net.jcip.annotations.ThreadSafe
 */
package com.atlassian.confluence.core.persistence.schema.api;

import com.atlassian.confluence.util.FugueConversionUtil;
import com.atlassian.fugue.Maybe;
import java.util.Optional;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public interface SchemaElementComparison<T> {
    @Deprecated
    default public Maybe<T> getExpected() {
        return FugueConversionUtil.toComMaybe(this.expected());
    }

    public Optional<T> expected();

    @Deprecated
    default public Maybe<T> getActual() {
        return FugueConversionUtil.toComMaybe(this.actual());
    }

    public Optional<T> actual();

    public ComparisonResult getResult();

    public static interface IndexComparison
    extends SchemaElementComparison<String> {
        public String getIndexName();
    }

    public static interface ColumnComparison
    extends SchemaElementComparison<String> {
        public String getColumnName();
    }

    public static enum ComparisonResult {
        MATCH,
        MISMATCH,
        ACTUAL_ELEMENT_MISSING,
        EXPECTED_ELEMENT_MISSING;

    }
}

