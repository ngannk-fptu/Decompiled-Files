/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.fortuna.ical4j.filter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Filter<T> {
    public static final int MATCH_ANY = 1;
    public static final int MATCH_ALL = 2;
    private List<Predicate<T>> rules;
    private int type;

    @SafeVarargs
    public Filter(Predicate<T> ... rules) {
        this(rules, 1);
    }

    public Filter(Predicate<T>[] rules, int type) {
        this.rules = Arrays.asList(rules);
        this.type = type;
    }

    public final Collection<T> filter(Collection<T> c) {
        if (this.getRules() != null && this.getRules().length > 0) {
            ArrayList<T> filtered;
            try {
                filtered = (ArrayList<T>)c.getClass().newInstance();
            }
            catch (IllegalAccessException | InstantiationException e) {
                filtered = new ArrayList<T>();
            }
            if (this.type == 2) {
                filtered.addAll(this.matchAll(c));
            } else {
                filtered.addAll(this.matchAny(c));
            }
            return filtered;
        }
        return c;
    }

    private List<T> matchAll(Collection<T> c) {
        ArrayList<Object> list = new ArrayList<T>(c);
        ArrayList temp = new ArrayList();
        for (int n = 0; n < this.getRules().length; ++n) {
            for (Object o : list) {
                if (!this.getRules()[n].test(o)) continue;
                temp.add(o);
            }
            list = temp;
            temp = new ArrayList();
        }
        return list;
    }

    private List<T> matchAny(Collection<T> c) {
        ArrayList<T> matches = new ArrayList<T>();
        block0: for (T o : c) {
            for (int n = 0; n < this.getRules().length; ++n) {
                if (!this.getRules()[n].test(o)) continue;
                matches.add(o);
                continue block0;
            }
        }
        return matches;
    }

    public final T[] filter(T[] objects) {
        Collection<Object> filtered = this.filter(Arrays.asList(objects));
        try {
            return filtered.toArray((Object[])Array.newInstance(objects.getClass(), filtered.size()));
        }
        catch (ArrayStoreException ase) {
            Logger log = LoggerFactory.getLogger(Filter.class);
            log.warn("Error converting to array - using default approach", (Throwable)ase);
            return filtered.toArray();
        }
    }

    public final Predicate<T>[] getRules() {
        return this.rules.toArray(new Predicate[0]);
    }

    public final void setRules(Predicate<T>[] rules) {
        this.rules = Arrays.asList(rules);
    }
}

