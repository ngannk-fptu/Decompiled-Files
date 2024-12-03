/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.collections;

import java.util.Collection;
import java.util.HashSet;

public class Logic {
    private Logic() {
    }

    @SafeVarargs
    public static <T> Collection<T> retain(Collection<? extends T> first, Collection<? extends T> ... sets) {
        HashSet<T> result = new HashSet<T>(first);
        for (Collection<? extends T> set : sets) {
            result.retainAll(set);
        }
        return result;
    }

    @SafeVarargs
    public static <T> Collection<T> remove(Collection<? extends T> first, Collection<? extends T> ... sets) {
        HashSet<T> result = new HashSet<T>(first);
        for (Collection<? extends T> set : sets) {
            result.removeAll(set);
        }
        return result;
    }

    @SafeVarargs
    public static <T> boolean hasOverlap(Collection<? extends T> source, Collection<? extends T> ... toBeChecked) {
        for (T t : source) {
            for (Collection<T> collection : toBeChecked) {
                for (T r : collection) {
                    if (!t.equals(r)) continue;
                    return true;
                }
            }
        }
        return false;
    }
}

