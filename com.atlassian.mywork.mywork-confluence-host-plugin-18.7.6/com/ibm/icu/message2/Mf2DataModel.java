/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.message2;

import com.ibm.icu.message2.Formatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Deprecated
public class Mf2DataModel {
    private final OrderedMap<String, Expression> localVariables;
    private final List<Expression> selectors;
    private final OrderedMap<SelectorKeys, Pattern> variants;
    private final Pattern pattern;

    private Mf2DataModel(Builder builder) {
        this.localVariables = builder.localVariables;
        this.selectors = builder.selectors;
        this.variants = builder.variants;
        this.pattern = builder.pattern;
    }

    @Deprecated
    public static Builder builder() {
        return new Builder();
    }

    @Deprecated
    public OrderedMap<String, Expression> getLocalVariables() {
        return this.localVariables;
    }

    @Deprecated
    public List<Expression> getSelectors() {
        return this.selectors;
    }

    @Deprecated
    public OrderedMap<SelectorKeys, Pattern> getVariants() {
        return this.variants;
    }

    @Deprecated
    public Pattern getPattern() {
        return this.pattern;
    }

    @Deprecated
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Map.Entry entry : this.localVariables.entrySet()) {
            result.append("let $").append((String)entry.getKey());
            result.append(" = ");
            result.append(entry.getValue());
            result.append("\n");
        }
        if (!this.selectors.isEmpty()) {
            result.append("match");
            for (Expression expression : this.selectors) {
                result.append(" ").append(expression);
            }
            result.append("\n");
            for (Map.Entry entry : this.variants.entrySet()) {
                result.append("  when ").append(entry.getKey());
                result.append(" ");
                result.append(entry.getValue());
                result.append("\n");
            }
        } else {
            result.append(this.pattern);
        }
        return result.toString();
    }

    @Deprecated
    public static class Builder {
        private final OrderedMap<String, Expression> localVariables = new OrderedMap();
        private final List<Expression> selectors = new ArrayList<Expression>();
        private final OrderedMap<SelectorKeys, Pattern> variants = new OrderedMap();
        private Pattern pattern = Pattern.builder().build();

        private Builder() {
        }

        @Deprecated
        public Builder addLocalVariable(String variableName, Expression expression) {
            this.localVariables.put(variableName, expression);
            return this;
        }

        @Deprecated
        public Builder addLocalVariables(OrderedMap<String, Expression> otherLocalVariables) {
            this.localVariables.putAll(otherLocalVariables);
            return this;
        }

        @Deprecated
        public Builder addSelector(Expression otherSelector) {
            this.selectors.add(otherSelector);
            return this;
        }

        @Deprecated
        public Builder addSelectors(List<Expression> otherSelectors) {
            this.selectors.addAll(otherSelectors);
            return this;
        }

        @Deprecated
        public Builder addVariant(SelectorKeys keys, Pattern newPattern) {
            this.variants.put(keys, newPattern);
            return this;
        }

        @Deprecated
        public Builder addVariants(OrderedMap<SelectorKeys, Pattern> otherVariants) {
            this.variants.putAll(otherVariants);
            return this;
        }

        @Deprecated
        public Builder setPattern(Pattern pattern) {
            this.pattern = pattern;
            return this;
        }

        @Deprecated
        public Mf2DataModel build() {
            return new Mf2DataModel(this);
        }
    }

    @Deprecated
    public static class OrderedMap<K, V>
    extends LinkedHashMap<K, V> {
        private static final long serialVersionUID = -7049361727790825496L;

        @Deprecated
        public OrderedMap() {
        }
    }

    @Deprecated
    public static class Variable {
        private final String name;

        private Variable(Builder builder) {
            this.name = builder.name;
        }

        @Deprecated
        public static Builder builder() {
            return new Builder();
        }

        @Deprecated
        public String getName() {
            return this.name;
        }

        @Deprecated
        public static class Builder {
            private String name;

            private Builder() {
            }

            @Deprecated
            public Builder setName(String name) {
                this.name = name;
                return this;
            }

            @Deprecated
            public Variable build() {
                return new Variable(this);
            }
        }
    }

    @Deprecated
    public static class Value {
        private final String literal;
        private final String variableName;

        private Value(Builder builder) {
            this.literal = builder.literal;
            this.variableName = builder.variableName;
        }

        @Deprecated
        public static Builder builder() {
            return new Builder();
        }

        @Deprecated
        public String getLiteral() {
            return this.literal;
        }

        @Deprecated
        public String getVariableName() {
            return this.variableName;
        }

        @Deprecated
        public boolean isLiteral() {
            return this.literal != null;
        }

        @Deprecated
        public boolean isVariable() {
            return this.variableName != null;
        }

        @Deprecated
        public String toString() {
            return this.isLiteral() ? "(" + this.literal + ")" : "$" + this.variableName;
        }

        @Deprecated
        public static class Builder {
            private String literal;
            private String variableName;

            private Builder() {
            }

            @Deprecated
            public Builder setLiteral(String literal) {
                this.literal = literal;
                this.variableName = null;
                return this;
            }

            @Deprecated
            public Builder setVariableName(String variableName) {
                this.variableName = variableName;
                this.literal = null;
                return this;
            }

            @Deprecated
            public Value build() {
                return new Value(this);
            }
        }
    }

    @Deprecated
    public static class Expression
    implements Part {
        private final Value operand;
        private final String functionName;
        private final Map<String, Value> options;
        Formatter formatter = null;

        private Expression(Builder builder) {
            this.operand = builder.operand;
            this.functionName = builder.functionName;
            this.options = builder.options;
        }

        @Deprecated
        public static Builder builder() {
            return new Builder();
        }

        @Deprecated
        public Value getOperand() {
            return this.operand;
        }

        @Deprecated
        public String getFunctionName() {
            return this.functionName;
        }

        @Deprecated
        public Map<String, Value> getOptions() {
            return this.options;
        }

        @Deprecated
        public String toString() {
            StringBuilder result = new StringBuilder();
            result.append("{");
            if (this.operand != null) {
                result.append(this.operand);
            }
            if (this.functionName != null) {
                result.append(" :").append(this.functionName);
            }
            for (Map.Entry<String, Value> option : this.options.entrySet()) {
                result.append(" ").append(option.getKey()).append("=").append(option.getValue());
            }
            result.append("}");
            return result.toString();
        }

        @Deprecated
        public static class Builder {
            private Value operand = null;
            private String functionName = null;
            private final OrderedMap<String, Value> options = new OrderedMap();

            private Builder() {
            }

            @Deprecated
            public Builder setOperand(Value operand) {
                this.operand = operand;
                return this;
            }

            @Deprecated
            public Builder setFunctionName(String functionName) {
                this.functionName = functionName;
                return this;
            }

            @Deprecated
            public Builder addOption(String key, Value value) {
                this.options.put(key, value);
                return this;
            }

            @Deprecated
            public Builder addOptions(Map<String, Value> otherOptions) {
                this.options.putAll(otherOptions);
                return this;
            }

            @Deprecated
            public Expression build() {
                return new Expression(this);
            }
        }
    }

    @Deprecated
    public static class Text
    implements Part {
        private final String value;

        @Deprecated
        private Text(Builder builder) {
            this(builder.value);
        }

        @Deprecated
        public static Builder builder() {
            return new Builder();
        }

        @Deprecated
        public Text(String value) {
            this.value = value;
        }

        @Deprecated
        public String getValue() {
            return this.value;
        }

        @Deprecated
        public String toString() {
            return this.value;
        }

        @Deprecated
        public static class Builder {
            private String value;

            private Builder() {
            }

            @Deprecated
            public Builder setValue(String value) {
                this.value = value;
                return this;
            }

            @Deprecated
            public Text build() {
                return new Text(this);
            }
        }
    }

    @Deprecated
    public static interface Part {
    }

    @Deprecated
    public static class Pattern {
        private final List<Part> parts = new ArrayList<Part>();

        private Pattern(Builder builder) {
            this.parts.addAll(builder.parts);
        }

        @Deprecated
        public static Builder builder() {
            return new Builder();
        }

        @Deprecated
        public List<Part> getParts() {
            return this.parts;
        }

        @Deprecated
        public String toString() {
            StringBuilder result = new StringBuilder();
            result.append("{");
            for (Part part : this.parts) {
                result.append(part);
            }
            result.append("}");
            return result.toString();
        }

        @Deprecated
        public static class Builder {
            private final List<Part> parts = new ArrayList<Part>();

            private Builder() {
            }

            @Deprecated
            public Builder add(Part part) {
                this.parts.add(part);
                return this;
            }

            @Deprecated
            public Builder addAll(Collection<Part> otherParts) {
                this.parts.addAll(otherParts);
                return this;
            }

            @Deprecated
            public Pattern build() {
                return new Pattern(this);
            }
        }
    }

    @Deprecated
    public static class SelectorKeys {
        private final List<String> keys = new ArrayList<String>();

        private SelectorKeys(Builder builder) {
            this.keys.addAll(builder.keys);
        }

        @Deprecated
        public static Builder builder() {
            return new Builder();
        }

        @Deprecated
        public List<String> getKeys() {
            return Collections.unmodifiableList(this.keys);
        }

        @Deprecated
        public String toString() {
            StringJoiner result = new StringJoiner(" ");
            for (String key : this.keys) {
                result.add(key);
            }
            return result.toString();
        }

        @Deprecated
        public static class Builder {
            private final List<String> keys = new ArrayList<String>();

            private Builder() {
            }

            @Deprecated
            public Builder add(String key) {
                this.keys.add(key);
                return this;
            }

            @Deprecated
            public Builder addAll(Collection<String> otherKeys) {
                this.keys.addAll(otherKeys);
                return this;
            }

            @Deprecated
            public SelectorKeys build() {
                return new SelectorKeys(this);
            }
        }
    }
}

