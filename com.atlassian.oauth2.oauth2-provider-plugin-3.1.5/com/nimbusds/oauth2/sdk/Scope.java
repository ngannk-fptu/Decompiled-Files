/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.id.Identifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.StringTokenizer;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class Scope
extends LinkedHashSet<Value> {
    private static final long serialVersionUID = -553103514038936007L;

    public Scope() {
    }

    public Scope(Scope scope) {
        if (scope == null) {
            return;
        }
        this.addAll(scope);
    }

    public Scope(String ... values) {
        for (String v : values) {
            this.add(new Value(v));
        }
    }

    public Scope(Value ... values) {
        this.addAll(Arrays.asList(values));
    }

    @Override
    public boolean add(String value) {
        return this.add(new Value(value));
    }

    public boolean contains(String value) {
        return this.contains(new Value(value));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Value v : this) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(v.toString());
        }
        return sb.toString();
    }

    public List<String> toStringList() {
        ArrayList<String> list = new ArrayList<String>(this.size());
        for (Value v : this) {
            list.add(v.getValue());
        }
        return list;
    }

    public static Scope parse(Collection<String> collection) {
        if (collection == null) {
            return null;
        }
        Scope scope = new Scope();
        for (String v : collection) {
            scope.add(new Value(v));
        }
        return scope;
    }

    public static Scope parse(String s) {
        if (s == null) {
            return null;
        }
        Scope scope = new Scope();
        if (s.trim().isEmpty()) {
            return scope;
        }
        StringTokenizer st = new StringTokenizer(s, " ,");
        while (st.hasMoreTokens()) {
            scope.add(new Value(st.nextToken()));
        }
        return scope;
    }

    @Immutable
    public static class Value
    extends Identifier {
        private static final long serialVersionUID = -1885648673808651565L;
        private final Requirement requirement;

        public Value(String value) {
            this(value, null);
        }

        public Value(String value, Requirement requirement) {
            super(value);
            this.requirement = requirement;
        }

        public Requirement getRequirement() {
            return this.requirement;
        }

        @Override
        public boolean equals(Object object) {
            return object instanceof Value && this.toString().equals(object.toString());
        }

        public static enum Requirement {
            REQUIRED,
            OPTIONAL;

        }
    }
}

