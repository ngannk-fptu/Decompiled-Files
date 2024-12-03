/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.query;

import com.atlassian.crowd.directory.query.MicrosoftGraphQueryParam;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ODataSelect
implements MicrosoftGraphQueryParam {
    private static final String SEPARATOR = ",";
    public static final String QUERY_PARAM_NAME = "$select";
    private final String value;

    public ODataSelect(String ... values) {
        this(Stream.of(values));
    }

    private ODataSelect(Stream<String> stream) {
        this.value = stream.distinct().collect(Collectors.joining(SEPARATOR));
    }

    public ODataSelect addColumns(String ... values) {
        return new ODataSelect(Stream.concat(Stream.of(this.value.split(SEPARATOR)), Stream.of(values)));
    }

    public ODataSelect merge(ODataSelect other) {
        return this.addColumns(other.value.split(SEPARATOR));
    }

    @Override
    public String getName() {
        return QUERY_PARAM_NAME;
    }

    @Override
    public String asRawValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ODataSelect that = (ODataSelect)o;
        return Objects.equals(this.value, that.value);
    }

    public int hashCode() {
        return Objects.hash(this.value);
    }
}

