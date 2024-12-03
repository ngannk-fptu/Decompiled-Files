/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.querydsl.sql;

import com.google.common.collect.Lists;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.sql.WindowFunction;
import java.util.List;

public class WindowRows<A> {
    private static final String AND = " and";
    private static final String BETWEEN = " between";
    private static final String CURRENT_ROW = " current row";
    private static final String FOLLOWING = " following";
    private static final String PRECEDING = " preceding";
    private static final String UNBOUNDED = " unbounded";
    private final WindowFunction<A> rv;
    private final StringBuilder str = new StringBuilder();
    private final List<Expression<?>> args = Lists.newArrayList();
    private int offset;

    public WindowRows(WindowFunction<A> windowFunction, String prefix, int offset) {
        this.rv = windowFunction;
        this.offset = offset;
        this.str.append(prefix);
    }

    public Between between() {
        this.str.append(BETWEEN);
        return new Between();
    }

    public WindowFunction<A> unboundedPreceding() {
        this.str.append(UNBOUNDED);
        this.str.append(PRECEDING);
        return this.rv.withRowsOrRange(this.str.toString(), this.args);
    }

    public WindowFunction<A> currentRow() {
        this.str.append(CURRENT_ROW);
        return this.rv.withRowsOrRange(this.str.toString(), this.args);
    }

    public WindowFunction<A> preceding(Expression<Integer> expr) {
        this.args.add(expr);
        this.str.append(PRECEDING);
        this.str.append(" {" + this.offset++ + "}");
        return this.rv.withRowsOrRange(this.str.toString(), this.args);
    }

    public WindowFunction<A> preceding(int i) {
        return this.preceding(ConstantImpl.create(i));
    }

    public class BetweenAnd {
        public BetweenAnd() {
            WindowRows.this.str.append(WindowRows.AND);
        }

        public WindowFunction<A> unboundedFollowing() {
            WindowRows.this.str.append(WindowRows.UNBOUNDED);
            WindowRows.this.str.append(WindowRows.FOLLOWING);
            return WindowRows.this.rv.withRowsOrRange(WindowRows.this.str.toString(), WindowRows.this.args);
        }

        public WindowFunction<A> currentRow() {
            WindowRows.this.str.append(WindowRows.CURRENT_ROW);
            return WindowRows.this.rv.withRowsOrRange(WindowRows.this.str.toString(), WindowRows.this.args);
        }

        public WindowFunction<A> preceding(Expression<Integer> expr) {
            WindowRows.this.args.add(expr);
            WindowRows.this.str.append(WindowRows.PRECEDING);
            WindowRows.this.str.append(" {" + WindowRows.this.offset++ + "}");
            return WindowRows.this.rv.withRowsOrRange(WindowRows.this.str.toString(), WindowRows.this.args);
        }

        public WindowFunction<A> preceding(int i) {
            return this.preceding(ConstantImpl.create(i));
        }

        public WindowFunction<A> following(Expression<Integer> expr) {
            WindowRows.this.args.add(expr);
            WindowRows.this.str.append(WindowRows.FOLLOWING);
            WindowRows.this.str.append(" {" + WindowRows.this.offset++ + "}");
            return WindowRows.this.rv.withRowsOrRange(WindowRows.this.str.toString(), WindowRows.this.args);
        }

        public WindowFunction<A> following(int i) {
            return this.following(ConstantImpl.create(i));
        }
    }

    public class Between {
        public BetweenAnd unboundedPreceding() {
            WindowRows.this.str.append(WindowRows.UNBOUNDED);
            WindowRows.this.str.append(WindowRows.PRECEDING);
            return new BetweenAnd();
        }

        public BetweenAnd currentRow() {
            WindowRows.this.str.append(WindowRows.CURRENT_ROW);
            return new BetweenAnd();
        }

        public BetweenAnd preceding(Expression<Integer> expr) {
            WindowRows.this.args.add(expr);
            WindowRows.this.str.append(WindowRows.PRECEDING);
            WindowRows.this.str.append(" {" + WindowRows.this.offset++ + "}");
            return new BetweenAnd();
        }

        public BetweenAnd preceding(int i) {
            return this.preceding(ConstantImpl.create(i));
        }

        public BetweenAnd following(Expression<Integer> expr) {
            WindowRows.this.args.add(expr);
            WindowRows.this.str.append(WindowRows.FOLLOWING);
            WindowRows.this.str.append(" {" + WindowRows.this.offset++ + "}");
            return new BetweenAnd();
        }

        public BetweenAnd following(int i) {
            return this.following(ConstantImpl.create(i));
        }
    }
}

