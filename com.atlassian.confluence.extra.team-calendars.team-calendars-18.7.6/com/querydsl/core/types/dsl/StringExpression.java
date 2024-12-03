/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.LiteralExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import javax.annotation.Nullable;

public abstract class StringExpression
extends LiteralExpression<String> {
    private static final long serialVersionUID = 1536955079961023361L;
    @Nullable
    private volatile transient NumberExpression<Integer> length;
    @Nullable
    private volatile transient StringExpression lower;
    @Nullable
    private volatile transient StringExpression trim;
    @Nullable
    private volatile transient StringExpression upper;
    @Nullable
    private volatile transient StringExpression min;
    @Nullable
    private volatile transient StringExpression max;
    @Nullable
    private volatile transient BooleanExpression isempty;

    public StringExpression(Expression<String> mixin) {
        super(mixin);
    }

    public StringExpression as(Path<String> alias) {
        return Expressions.stringOperation(Ops.ALIAS, this.mixin, alias);
    }

    @Override
    public StringExpression as(String alias) {
        return this.as((Path)ExpressionUtils.path(String.class, alias));
    }

    public StringExpression append(Expression<String> str) {
        return Expressions.stringOperation(Ops.CONCAT, this.mixin, str);
    }

    public StringExpression append(String str) {
        return this.append(ConstantImpl.create(str));
    }

    public SimpleExpression<Character> charAt(Expression<Integer> i) {
        return Expressions.comparableOperation(Character.class, Ops.CHAR_AT, this.mixin, i);
    }

    public SimpleExpression<Character> charAt(int i) {
        return this.charAt(ConstantImpl.create(i));
    }

    public StringExpression concat(Expression<String> str) {
        return this.append(str);
    }

    public StringExpression concat(String str) {
        return this.append(str);
    }

    public BooleanExpression contains(Expression<String> str) {
        return Expressions.booleanOperation(Ops.STRING_CONTAINS, this.mixin, str);
    }

    public BooleanExpression contains(String str) {
        return this.contains(ConstantImpl.create(str));
    }

    public BooleanExpression containsIgnoreCase(Expression<String> str) {
        return Expressions.booleanOperation(Ops.STRING_CONTAINS_IC, this.mixin, str);
    }

    public BooleanExpression containsIgnoreCase(String str) {
        return this.containsIgnoreCase(ConstantImpl.create(str));
    }

    public BooleanExpression endsWith(Expression<String> str) {
        return Expressions.booleanOperation(Ops.ENDS_WITH, this.mixin, str);
    }

    public BooleanExpression endsWithIgnoreCase(Expression<String> str) {
        return Expressions.booleanOperation(Ops.ENDS_WITH_IC, this.mixin, str);
    }

    public BooleanExpression endsWith(String str) {
        return this.endsWith(ConstantImpl.create(str));
    }

    public BooleanExpression endsWithIgnoreCase(String str) {
        return this.endsWithIgnoreCase(ConstantImpl.create(str));
    }

    public BooleanExpression equalsIgnoreCase(Expression<String> str) {
        return Expressions.booleanOperation(Ops.EQ_IGNORE_CASE, this.mixin, str);
    }

    public BooleanExpression equalsIgnoreCase(String str) {
        return this.equalsIgnoreCase(ConstantImpl.create(str));
    }

    public NumberExpression<Integer> indexOf(Expression<String> str) {
        return Expressions.numberOperation(Integer.class, Ops.INDEX_OF, this.mixin, str);
    }

    public NumberExpression<Integer> indexOf(String str) {
        return this.indexOf(ConstantImpl.create(str));
    }

    public NumberExpression<Integer> indexOf(String str, int i) {
        return this.indexOf(ConstantImpl.create(str), i);
    }

    public NumberExpression<Integer> indexOf(Expression<String> str, int i) {
        return Expressions.numberOperation(Integer.class, Ops.INDEX_OF_2ARGS, this.mixin, str, ConstantImpl.create(i));
    }

    public BooleanExpression isEmpty() {
        if (this.isempty == null) {
            this.isempty = Expressions.booleanOperation(Ops.STRING_IS_EMPTY, this.mixin);
        }
        return this.isempty;
    }

    public BooleanExpression isNotEmpty() {
        return this.isEmpty().not();
    }

    public NumberExpression<Integer> length() {
        if (this.length == null) {
            this.length = Expressions.numberOperation(Integer.class, Ops.STRING_LENGTH, this.mixin);
        }
        return this.length;
    }

    public BooleanExpression like(String str) {
        return Expressions.booleanOperation(Ops.LIKE, this, ConstantImpl.create(str));
    }

    public BooleanExpression like(Expression<String> str) {
        return Expressions.booleanOperation(Ops.LIKE, this.mixin, str);
    }

    public BooleanExpression likeIgnoreCase(String str) {
        return Expressions.booleanOperation(Ops.LIKE_IC, this.mixin, ConstantImpl.create(str));
    }

    public BooleanExpression likeIgnoreCase(Expression<String> str) {
        return Expressions.booleanOperation(Ops.LIKE_IC, this.mixin, str);
    }

    public BooleanExpression like(String str, char escape) {
        return Expressions.booleanOperation(Ops.LIKE_ESCAPE, this.mixin, ConstantImpl.create(str), ConstantImpl.create(escape));
    }

    public BooleanExpression like(Expression<String> str, char escape) {
        return Expressions.booleanOperation(Ops.LIKE_ESCAPE, this.mixin, str, ConstantImpl.create(escape));
    }

    public BooleanExpression likeIgnoreCase(String str, char escape) {
        return Expressions.booleanOperation(Ops.LIKE_ESCAPE_IC, this.mixin, ConstantImpl.create(str), ConstantImpl.create(escape));
    }

    public BooleanExpression likeIgnoreCase(Expression<String> str, char escape) {
        return Expressions.booleanOperation(Ops.LIKE_ESCAPE_IC, this.mixin, str, ConstantImpl.create(escape));
    }

    public NumberExpression<Integer> locate(Expression<String> str) {
        return Expressions.numberOperation(Integer.class, Ops.StringOps.LOCATE, str, this.mixin);
    }

    public NumberExpression<Integer> locate(String str) {
        return Expressions.numberOperation(Integer.class, Ops.StringOps.LOCATE, ConstantImpl.create(str), this.mixin);
    }

    public NumberExpression<Integer> locate(Expression<String> str, NumberExpression<Integer> start) {
        return Expressions.numberOperation(Integer.class, Ops.StringOps.LOCATE2, str, this.mixin, start);
    }

    public NumberExpression<Integer> locate(String str, int start) {
        return Expressions.numberOperation(Integer.class, Ops.StringOps.LOCATE2, ConstantImpl.create(str), this.mixin, ConstantImpl.create(start));
    }

    public NumberExpression<Integer> locate(String str, Expression<Integer> start) {
        return Expressions.numberOperation(Integer.class, Ops.StringOps.LOCATE2, ConstantImpl.create(str), this.mixin, start);
    }

    public StringExpression lower() {
        if (this.lower == null) {
            this.lower = Expressions.stringOperation(Ops.LOWER, this.mixin);
        }
        return this.lower;
    }

    public BooleanExpression matches(Expression<String> regex) {
        return Expressions.booleanOperation(Ops.MATCHES, this.mixin, regex);
    }

    public BooleanExpression matches(String regex) {
        return this.matches(ConstantImpl.create(regex));
    }

    public StringExpression max() {
        if (this.max == null) {
            this.max = Expressions.stringOperation(Ops.AggOps.MAX_AGG, this.mixin);
        }
        return this.max;
    }

    public StringExpression min() {
        if (this.min == null) {
            this.min = Expressions.stringOperation(Ops.AggOps.MIN_AGG, this.mixin);
        }
        return this.min;
    }

    public BooleanExpression notEqualsIgnoreCase(Expression<String> str) {
        return this.equalsIgnoreCase(str).not();
    }

    public BooleanExpression notEqualsIgnoreCase(String str) {
        return this.equalsIgnoreCase(str).not();
    }

    public BooleanExpression notLike(String str) {
        return this.like(str).not();
    }

    public BooleanExpression notLike(Expression<String> str) {
        return this.like(str).not();
    }

    public BooleanExpression notLike(String str, char escape) {
        return this.like(str, escape).not();
    }

    public BooleanExpression notLike(Expression<String> str, char escape) {
        return this.like(str, escape).not();
    }

    public StringExpression prepend(Expression<String> str) {
        return Expressions.stringOperation(Ops.CONCAT, str, this.mixin);
    }

    public StringExpression prepend(String str) {
        return this.prepend(ConstantImpl.create(str));
    }

    public BooleanExpression startsWith(Expression<String> str) {
        return Expressions.booleanOperation(Ops.STARTS_WITH, this.mixin, str);
    }

    public BooleanExpression startsWithIgnoreCase(Expression<String> str) {
        return Expressions.booleanOperation(Ops.STARTS_WITH_IC, this.mixin, str);
    }

    public BooleanExpression startsWith(String str) {
        return this.startsWith(ConstantImpl.create(str));
    }

    public BooleanExpression startsWithIgnoreCase(String str) {
        return this.startsWithIgnoreCase(ConstantImpl.create(str));
    }

    @Override
    public StringExpression stringValue() {
        return this;
    }

    public StringExpression substring(int beginIndex) {
        return Expressions.stringOperation(Ops.SUBSTR_1ARG, this.mixin, ConstantImpl.create(beginIndex));
    }

    public StringExpression substring(int beginIndex, int endIndex) {
        return Expressions.stringOperation(Ops.SUBSTR_2ARGS, this.mixin, ConstantImpl.create(beginIndex), ConstantImpl.create(endIndex));
    }

    public StringExpression substring(Expression<Integer> beginIndex, int endIndex) {
        return Expressions.stringOperation(Ops.SUBSTR_2ARGS, this.mixin, beginIndex, ConstantImpl.create(endIndex));
    }

    public StringExpression substring(int beginIndex, Expression<Integer> endIndex) {
        return Expressions.stringOperation(Ops.SUBSTR_2ARGS, this.mixin, ConstantImpl.create(beginIndex), endIndex);
    }

    public StringExpression substring(Expression<Integer> beginIndex) {
        return Expressions.stringOperation(Ops.SUBSTR_1ARG, this.mixin, beginIndex);
    }

    public StringExpression substring(Expression<Integer> beginIndex, Expression<Integer> endIndex) {
        return Expressions.stringOperation(Ops.SUBSTR_2ARGS, this.mixin, beginIndex, endIndex);
    }

    public StringExpression toLowerCase() {
        return this.lower();
    }

    public StringExpression toUpperCase() {
        return this.upper();
    }

    public StringExpression trim() {
        if (this.trim == null) {
            this.trim = Expressions.stringOperation(Ops.TRIM, this.mixin);
        }
        return this.trim;
    }

    public StringExpression upper() {
        if (this.upper == null) {
            this.upper = Expressions.stringOperation(Ops.UPPER, this.mixin);
        }
        return this.upper;
    }
}

