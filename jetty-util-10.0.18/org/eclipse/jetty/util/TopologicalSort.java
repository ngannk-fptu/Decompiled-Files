/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class TopologicalSort<T> {
    private final Map<T, Set<T>> _dependencies = new HashMap<T, Set<T>>();

    public void addDependency(T dependent, T ... dependency) {
        Set<T> set = this._dependencies.get(dependent);
        if (set == null) {
            set = new HashSet<T>();
            this._dependencies.put(dependent, set);
        }
        for (T d : dependency) {
            set.add(d);
        }
    }

    public void addBeforeAfter(T before, T after) {
        this.addDependency(after, before);
    }

    public void sort(T[] array) {
        ArrayList sorted = new ArrayList();
        HashSet visited = new HashSet();
        InitialOrderComparator<T> comparator = new InitialOrderComparator<T>(array);
        for (T t : array) {
            this.visit(t, visited, sorted, comparator);
        }
        sorted.toArray(array);
    }

    public void sort(Collection<T> list) {
        ArrayList sorted = new ArrayList();
        HashSet visited = new HashSet();
        InitialOrderComparator<T> comparator = new InitialOrderComparator<T>(list);
        for (T t : list) {
            this.visit(t, visited, sorted, comparator);
        }
        list.clear();
        list.addAll(sorted);
    }

    private void visit(T item, Set<T> visited, List<T> sorted, Comparator<T> comparator) {
        if (!visited.contains(item)) {
            visited.add(item);
            Set<T> dependencies = this._dependencies.get(item);
            if (dependencies != null) {
                TreeSet<T> orderedDeps = new TreeSet<T>(comparator);
                orderedDeps.addAll(dependencies);
                try {
                    for (Object d : orderedDeps) {
                        this.visit(d, visited, sorted, comparator);
                    }
                }
                catch (CyclicException e) {
                    throw new CyclicException(item, e);
                }
            }
            sorted.add(item);
        } else if (!sorted.contains(item)) {
            throw new CyclicException(item);
        }
    }

    public String toString() {
        return "TopologicalSort " + this._dependencies;
    }

    private static class InitialOrderComparator<T>
    implements Comparator<T> {
        private final Map<T, Integer> _indexes = new HashMap<T, Integer>();

        InitialOrderComparator(T[] initial) {
            int i = 0;
            for (T t : initial) {
                this._indexes.put(t, i++);
            }
        }

        InitialOrderComparator(Collection<T> initial) {
            int i = 0;
            for (T t : initial) {
                this._indexes.put(t, i++);
            }
        }

        @Override
        public int compare(T o1, T o2) {
            Integer i1 = this._indexes.get(o1);
            Integer i2 = this._indexes.get(o2);
            if (i1 == null || i2 == null || i1.equals(o2)) {
                return 0;
            }
            if (i1 < i2) {
                return -1;
            }
            return 1;
        }
    }

    private static class CyclicException
    extends IllegalStateException {
        CyclicException(Object item) {
            super("cyclic at " + item);
        }

        CyclicException(Object item, CyclicException e) {
            super("cyclic at " + item, e);
        }
    }
}

