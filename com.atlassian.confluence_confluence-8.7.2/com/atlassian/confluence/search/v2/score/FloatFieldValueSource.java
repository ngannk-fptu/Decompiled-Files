/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.score;

import com.atlassian.confluence.search.v2.score.FieldValueSource;
import java.util.Objects;

public final class FloatFieldValueSource
implements FieldValueSource {
    private final String fieldName;

    public FloatFieldValueSource(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String getFieldName() {
        return this.fieldName;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FloatFieldValueSource)) {
            return false;
        }
        FloatFieldValueSource that = (FloatFieldValueSource)o;
        return Objects.equals(this.getFieldName(), that.getFieldName());
    }

    public int hashCode() {
        return Objects.hash(this.getFieldName());
    }
}

