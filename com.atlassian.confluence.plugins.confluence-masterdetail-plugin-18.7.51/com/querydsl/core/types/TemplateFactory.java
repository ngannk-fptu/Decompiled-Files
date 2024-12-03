/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 */
package com.querydsl.core.types;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Template;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateFactory {
    private static final Map<String, Operator> OPERATORS = ImmutableMap.of((Object)"+", (Object)Ops.ADD, (Object)"-", (Object)Ops.SUB, (Object)"*", (Object)Ops.MULT, (Object)"/", (Object)Ops.DIV);
    public static final TemplateFactory DEFAULT = new TemplateFactory('\\');
    private static final Constant<String> PERCENT = ConstantImpl.create("%");
    private static final Pattern elementPattern = Pattern.compile("\\{(%?%?)(\\d+)(?:([+-/*])(?:(\\d+)|'(-?\\d+(?:\\.\\d+)?)'))?([slu%]?%?)\\}");
    private final Map<String, Template> cache = new ConcurrentHashMap<String, Template>();
    private final char escape;
    private final Function<Object, Object> toLowerCase = new Function<Object, Object>(){

        public Object apply(Object arg) {
            if (arg instanceof Constant) {
                return ConstantImpl.create(this.apply(arg.toString()).toString());
            }
            if (arg instanceof Expression) {
                return ExpressionUtils.operation(String.class, (Operator)Ops.LOWER, (Expression)arg);
            }
            return String.valueOf(arg).toLowerCase();
        }
    };
    private final Function<Object, Object> toUpperCase = new Function<Object, Object>(){

        public Object apply(Object arg) {
            if (arg instanceof Constant) {
                return ConstantImpl.create(this.apply(arg.toString()).toString());
            }
            if (arg instanceof Expression) {
                return ExpressionUtils.operation(String.class, (Operator)Ops.UPPER, (Expression)arg);
            }
            return String.valueOf(arg).toUpperCase();
        }
    };
    private final Function<Object, Object> toStartsWithViaLike = new Function<Object, Object>(){

        public Object apply(Object arg) {
            if (arg instanceof Constant) {
                return ConstantImpl.create(this.apply(arg.toString()).toString());
            }
            if (arg instanceof Expression) {
                return ExpressionUtils.operation(String.class, (Operator)Ops.CONCAT, (Expression)arg, PERCENT);
            }
            return TemplateFactory.this.escapeForLike(String.valueOf(arg)) + "%";
        }
    };
    private final Function<Object, Object> toStartsWithViaLikeLower = new Function<Object, Object>(){

        public Object apply(Object arg) {
            if (arg instanceof Constant) {
                return ConstantImpl.create(this.apply(arg.toString()).toString());
            }
            if (arg instanceof Expression) {
                Operation<String> concatenated = ExpressionUtils.operation(String.class, (Operator)Ops.CONCAT, (Expression)arg, PERCENT);
                return ExpressionUtils.operation(String.class, (Operator)Ops.LOWER, concatenated);
            }
            return TemplateFactory.this.escapeForLike(String.valueOf(arg).toLowerCase()) + "%";
        }
    };
    private final Function<Object, Object> toEndsWithViaLike = new Function<Object, Object>(){

        public Object apply(Object arg) {
            if (arg instanceof Constant) {
                return ConstantImpl.create(this.apply(arg.toString()).toString());
            }
            if (arg instanceof Expression) {
                return ExpressionUtils.operation(String.class, (Operator)Ops.CONCAT, PERCENT, (Expression)arg);
            }
            return "%" + TemplateFactory.this.escapeForLike(String.valueOf(arg));
        }
    };
    private final Function<Object, Object> toEndsWithViaLikeLower = new Function<Object, Object>(){

        public Object apply(Object arg) {
            if (arg instanceof Constant) {
                return ConstantImpl.create(this.apply(arg.toString()).toString());
            }
            if (arg instanceof Expression) {
                Operation<String> concatenated = ExpressionUtils.operation(String.class, (Operator)Ops.CONCAT, PERCENT, (Expression)arg);
                return ExpressionUtils.operation(String.class, (Operator)Ops.LOWER, concatenated);
            }
            return "%" + TemplateFactory.this.escapeForLike(String.valueOf(arg).toLowerCase());
        }
    };
    private final Function<Object, Object> toContainsViaLike = new Function<Object, Object>(){

        public Object apply(Object arg) {
            if (arg instanceof Constant) {
                return ConstantImpl.create(this.apply(arg.toString()).toString());
            }
            if (arg instanceof Expression) {
                Operation<String> concatenated = ExpressionUtils.operation(String.class, (Operator)Ops.CONCAT, PERCENT, (Expression)arg);
                return ExpressionUtils.operation(String.class, (Operator)Ops.CONCAT, concatenated, PERCENT);
            }
            return "%" + TemplateFactory.this.escapeForLike(String.valueOf(arg)) + "%";
        }
    };
    private final Function<Object, Object> toContainsViaLikeLower = new Function<Object, Object>(){

        public Object apply(Object arg) {
            if (arg instanceof Constant) {
                return ConstantImpl.create(this.apply(arg.toString()).toString());
            }
            if (arg instanceof Expression) {
                Operation<String> concatenated = ExpressionUtils.operation(String.class, (Operator)Ops.CONCAT, PERCENT, (Expression)arg);
                concatenated = ExpressionUtils.operation(String.class, (Operator)Ops.CONCAT, concatenated, PERCENT);
                return ExpressionUtils.operation(String.class, (Operator)Ops.LOWER, concatenated);
            }
            return "%" + TemplateFactory.this.escapeForLike(String.valueOf(arg).toLowerCase()) + "%";
        }
    };

    public TemplateFactory(char escape) {
        this.escape = escape;
    }

    public Template create(String template) {
        if (this.cache.containsKey(template)) {
            return this.cache.get(template);
        }
        Matcher m = elementPattern.matcher(template);
        ImmutableList.Builder elements = ImmutableList.builder();
        int end = 0;
        while (m.find()) {
            Operator operator;
            if (m.start() > end) {
                elements.add((Object)new Template.StaticText(template.substring(end, m.start())));
            }
            String premodifiers = m.group(1).toLowerCase(Locale.ENGLISH);
            int index = Integer.parseInt(m.group(2));
            String postmodifiers = m.group(6).toLowerCase(Locale.ENGLISH);
            boolean asString = false;
            Function<Object, Object> transformer = null;
            switch (premodifiers.length()) {
                case 1: {
                    transformer = this.toEndsWithViaLike;
                    break;
                }
                case 2: {
                    transformer = this.toEndsWithViaLikeLower;
                }
            }
            switch (postmodifiers.length()) {
                case 1: {
                    switch (postmodifiers.charAt(0)) {
                        case '%': {
                            if (transformer == null) {
                                transformer = this.toStartsWithViaLike;
                                break;
                            }
                            transformer = this.toContainsViaLike;
                            break;
                        }
                        case 'l': {
                            transformer = this.toLowerCase;
                            break;
                        }
                        case 'u': {
                            transformer = this.toUpperCase;
                            break;
                        }
                        case 's': {
                            asString = true;
                        }
                    }
                    break;
                }
                case 2: {
                    transformer = transformer == null ? this.toStartsWithViaLikeLower : this.toContainsViaLikeLower;
                }
            }
            if (m.group(4) != null) {
                operator = OPERATORS.get(m.group(3));
                int index2 = Integer.parseInt(m.group(4));
                elements.add((Object)new Template.Operation(index, index2, operator, asString));
            } else if (m.group(5) != null) {
                operator = OPERATORS.get(m.group(3));
                Number number = m.group(5).contains(".") ? new BigDecimal(m.group(5)) : Integer.valueOf(m.group(5));
                elements.add((Object)new Template.OperationConst(index, number, operator, asString));
            } else if (asString) {
                elements.add((Object)new Template.AsString(index));
            } else if (transformer != null) {
                elements.add((Object)new Template.Transformed(index, transformer));
            } else {
                elements.add((Object)new Template.ByIndex(index));
            }
            end = m.end();
        }
        if (end < template.length()) {
            elements.add((Object)new Template.StaticText(template.substring(end)));
        }
        Template rv = new Template(template, (ImmutableList<Template.Element>)elements.build());
        this.cache.put(template, rv);
        return rv;
    }

    public String escapeForLike(String str) {
        StringBuilder rv = new StringBuilder(str.length() + 3);
        for (int i = 0; i < str.length(); ++i) {
            char ch = str.charAt(i);
            if (ch == this.escape || ch == '%' || ch == '_') {
                rv.append(this.escape);
            }
            rv.append(ch);
        }
        return rv.toString();
    }
}

