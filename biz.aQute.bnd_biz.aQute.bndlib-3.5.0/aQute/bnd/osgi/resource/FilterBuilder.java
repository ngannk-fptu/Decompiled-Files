/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.VersionRange
 */
package aQute.bnd.osgi.resource;

import aQute.bnd.version.VersionRange;
import java.util.ArrayList;
import java.util.List;

public class FilterBuilder {
    Sub current = new Sub("&", null);

    public FilterBuilder or() {
        this.current = new Sub("|", this.current);
        return this;
    }

    public FilterBuilder and() {
        this.current = new Sub("&", this.current);
        return this;
    }

    public FilterBuilder not() {
        this.current = new Sub("!", this.current);
        return this;
    }

    public FilterBuilder end() {
        this.current.previous.members.add(this.current);
        this.current = this.current.previous;
        return this;
    }

    public FilterBuilder eq(String key, Object value) {
        this.simple(key, Operator.EQ, value);
        return this;
    }

    public FilterBuilder neq(String key, Object value) {
        this.not();
        this.simple(key, Operator.EQ, value);
        this.end();
        return this;
    }

    public FilterBuilder gt(String key, Object value) {
        this.not();
        this.simple(key, Operator.LE, value);
        this.end();
        return this;
    }

    public FilterBuilder lt(String key, Object value) {
        this.not();
        this.simple(key, Operator.GE, value);
        this.end();
        return this;
    }

    public FilterBuilder ge(String key, Object value) {
        this.simple(key, Operator.GE, value);
        return this;
    }

    public FilterBuilder le(String key, Object value) {
        this.simple(key, Operator.LE, value);
        return this;
    }

    public FilterBuilder isSet(String key) {
        this.simple(key, Operator.EQ, "*");
        return this;
    }

    public FilterBuilder approximate(String key, Object value) {
        this.simple(key, Operator.APPROX, value);
        return this;
    }

    public FilterBuilder simple(String key, Operator op, Object value) {
        this.current.members.add("(" + key + op.name + FilterBuilder.escape(value) + ")");
        return this;
    }

    public FilterBuilder literal(String string) {
        this.current.members.add(string);
        return this;
    }

    static String escape(Object value) {
        String s = value.toString();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            switch (c) {
                case '(': 
                case ')': 
                case '\\': {
                    sb.append("\\");
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public String toString() {
        return this.current.toString();
    }

    public FilterBuilder isPresent(String key) {
        return this.simple(key, Operator.EQ, "*");
    }

    public FilterBuilder in(String key, org.osgi.framework.VersionRange range) {
        this.and();
        if (range.getLeftType() == '[') {
            this.ge(key, range.getLeft());
        } else {
            this.gt(key, range.getLeft());
        }
        if (range.getRightType() == ']') {
            this.le(key, range.getRight());
        } else {
            this.lt(key, range.getRight());
        }
        this.end();
        return this;
    }

    public FilterBuilder in(String key, VersionRange range) {
        this.and();
        if (range.includeLow()) {
            this.ge(key, range.getLow());
        } else {
            this.gt(key, range.getLow());
        }
        if (range.includeHigh()) {
            this.le(key, range.getHigh());
        } else {
            this.lt(key, range.getHigh());
        }
        this.end();
        return this;
    }

    public void endAnd() {
        if (!this.current.op.equals("&")) {
            throw new IllegalStateException("Expected an & but had " + this.current.op);
        }
        this.end();
    }

    public void endOr() {
        if (!this.current.op.equals("|")) {
            throw new IllegalStateException("Expected an | but had " + this.current.op);
        }
        this.end();
    }

    static class Sub {
        Sub previous;
        String op;
        List<Object> members = new ArrayList<Object>();

        public Sub(String op, Sub current) {
            this.op = op;
            this.previous = current;
        }

        public String toString() {
            if (this.members.isEmpty()) {
                return "";
            }
            if (!this.op.equals("!") && this.members.size() == 1) {
                return this.members.get(0).toString();
            }
            StringBuilder sb = new StringBuilder();
            sb.append("(").append(this.op);
            for (Object top : this.members) {
                sb.append(top);
            }
            sb.append(")");
            return sb.toString();
        }
    }

    public static enum Operator {
        EQ("="),
        APPROX("~="),
        GE(">="),
        LE("<=");

        String name;

        private Operator(String name) {
            this.name = name;
        }
    }
}

