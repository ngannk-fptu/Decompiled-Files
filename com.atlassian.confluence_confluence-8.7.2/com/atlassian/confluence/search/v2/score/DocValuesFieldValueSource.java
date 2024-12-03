/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.score;

import com.atlassian.confluence.search.v2.score.FieldValueSource;
import java.util.Objects;
import java.util.function.Function;

@Deprecated(forRemoval=true)
public class DocValuesFieldValueSource
implements FieldValueSource {
    private final String fieldName;
    private final Function<byte[], Double> extractor;

    public DocValuesFieldValueSource(String fieldName, Function<byte[], Double> extractor) {
        this.fieldName = fieldName;
        this.extractor = extractor;
    }

    @Override
    public String getFieldName() {
        return this.fieldName;
    }

    public Function<byte[], Double> getExtractor() {
        return this.extractor;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocValuesFieldValueSource)) {
            return false;
        }
        DocValuesFieldValueSource that = (DocValuesFieldValueSource)o;
        return Objects.equals(this.getFieldName(), that.getFieldName()) && Objects.equals(this.getExtractor(), that.getExtractor());
    }

    public int hashCode() {
        return Objects.hash(this.getFieldName(), this.getExtractor());
    }
}

