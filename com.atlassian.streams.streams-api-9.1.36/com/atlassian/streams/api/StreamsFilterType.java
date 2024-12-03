/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.streams.api;

import com.google.common.collect.ImmutableList;

public enum StreamsFilterType {
    STRING("string", Operator.IS, Operator.NOT, Operator.CONTAINS, Operator.DOES_NOT_CONTAIN),
    STRING_LIKE("string", Operator.CONTAINS, Operator.DOES_NOT_CONTAIN),
    STRING_EXACT("string", Operator.IS, Operator.NOT),
    LIST("list", Operator.IS, Operator.NOT),
    DATE("date", Operator.BEFORE, Operator.AFTER, Operator.BETWEEN),
    SELECT("select", Operator.IS, Operator.NOT),
    USER("user", Operator.IS, Operator.NOT);

    private final String type;
    private final Iterable<Operator> operators;

    private StreamsFilterType(String type, Operator ... operators) {
        this.type = type;
        this.operators = ImmutableList.copyOf((Object[])operators);
    }

    public String getType() {
        return this.type;
    }

    public Iterable<Operator> getOperators() {
        return this.operators;
    }

    public static enum Operator {
        IS("is", "is", "streams.filter.operator.is"),
        NOT("not", "not", "streams.filter.operator.not"),
        CONTAINS("contains", "contains", "streams.filter.operator.contains"),
        DOES_NOT_CONTAIN("does_not_contain", "does not contain", "streams.filter.operator.does.not.contain"),
        BEFORE("before", "before", "streams.filter.operator.before"),
        AFTER("after", "after", "streams.filter.operator.after"),
        BETWEEN("between", "between", "streams.filter.operator.between");

        private final String displayName;
        private final String i18nKey;
        private final String key;

        private Operator(String key, String displayName, String i18nKey) {
            this.key = key;
            this.displayName = displayName;
            this.i18nKey = i18nKey;
        }

        public String getKey() {
            return this.key;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public String getI18nKey() {
            return this.i18nKey;
        }
    }
}

