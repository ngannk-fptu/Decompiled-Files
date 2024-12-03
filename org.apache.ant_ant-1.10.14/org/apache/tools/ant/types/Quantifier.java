/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.EnumeratedAttribute;

public class Quantifier
extends EnumeratedAttribute {
    private static final String[] VALUES = (String[])Stream.of(Predicate.values()).map(Predicate::getNames).flatMap(Collection::stream).toArray(String[]::new);
    public static final Quantifier ALL = new Quantifier(Predicate.ALL);
    public static final Quantifier ANY = new Quantifier(Predicate.ANY);
    public static final Quantifier ONE = new Quantifier(Predicate.ONE);
    public static final Quantifier MAJORITY = new Quantifier(Predicate.MAJORITY);
    public static final Quantifier NONE = new Quantifier(Predicate.NONE);

    public Quantifier() {
    }

    public Quantifier(String value) {
        this.setValue(value);
    }

    private Quantifier(Predicate impl) {
        this.setValue(impl.getNames().iterator().next());
    }

    @Override
    public String[] getValues() {
        return VALUES;
    }

    public boolean evaluate(boolean[] b) {
        int t = 0;
        for (boolean bn : b) {
            if (!bn) continue;
            ++t;
        }
        return this.evaluate(t, b.length - t);
    }

    public boolean evaluate(int t, int f) {
        int index = this.getIndex();
        if (index == -1) {
            throw new BuildException("Quantifier value not set.");
        }
        return Predicate.get(VALUES[index]).eval(t, f);
    }

    private static enum Predicate {
        ALL("all", new String[]{"each", "every"}){

            @Override
            boolean eval(int t, int f) {
                return f == 0;
            }
        }
        ,
        ANY("any", new String[]{"some"}){

            @Override
            boolean eval(int t, int f) {
                return t > 0;
            }
        }
        ,
        ONE("one", new String[0]){

            @Override
            boolean eval(int t, int f) {
                return t == 1;
            }
        }
        ,
        MAJORITY("majority", new String[]{"most"}){

            @Override
            boolean eval(int t, int f) {
                return t > f;
            }
        }
        ,
        NONE("none", new String[0]){

            @Override
            boolean eval(int t, int f) {
                return t == 0;
            }
        };

        final Set<String> names;

        static Predicate get(String name) {
            return Stream.of(Predicate.values()).filter(p -> p.names.contains(name)).findFirst().orElseThrow(() -> new IllegalArgumentException(name));
        }

        private Predicate(String primaryName, String ... additionalNames) {
            LinkedHashSet<String> names = new LinkedHashSet<String>();
            names.add(primaryName);
            Collections.addAll(names, additionalNames);
            this.names = Collections.unmodifiableSet(names);
        }

        Set<String> getNames() {
            return this.names;
        }

        abstract boolean eval(int var1, int var2);
    }
}

