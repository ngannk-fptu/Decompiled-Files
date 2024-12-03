/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PartialOrder {
    private static <T extends PartialComparable> void addNewPartialComparable(List<SortObject<T>> graph, T o) {
        SortObject<T> so = new SortObject<T>(o);
        for (SortObject<T> other : graph) {
            so.addDirectedLinks(other);
        }
        graph.add(so);
    }

    private static <T extends PartialComparable> void removeFromGraph(List<SortObject<T>> graph, SortObject<T> o) {
        Iterator<SortObject<T>> i = graph.iterator();
        while (i.hasNext()) {
            SortObject<T> other = i.next();
            if (o == other) {
                i.remove();
            }
            other.removeSmallerObject(o);
        }
    }

    public static <T extends PartialComparable> List<T> sort(List<T> objects) {
        if (objects.size() < 2) {
            return objects;
        }
        LinkedList<SortObject<T>> sortList = new LinkedList<SortObject<T>>();
        Iterator<T> i = objects.iterator();
        while (i.hasNext()) {
            PartialOrder.addNewPartialComparable(sortList, (PartialComparable)i.next());
        }
        int N = objects.size();
        for (int index = 0; index < N; ++index) {
            SortObject leastWithNoSmallers = null;
            for (SortObject sortObject : sortList) {
                if (!sortObject.hasNoSmallerObjects() || leastWithNoSmallers != null && sortObject.object.fallbackCompareTo(leastWithNoSmallers.object) >= 0) continue;
                leastWithNoSmallers = sortObject;
            }
            if (leastWithNoSmallers == null) {
                return null;
            }
            PartialOrder.removeFromGraph(sortList, leastWithNoSmallers);
            objects.set(index, leastWithNoSmallers.object);
        }
        return objects;
    }

    public static void main(String[] args) {
        ArrayList<Token> l = new ArrayList<Token>();
        l.add(new Token("a1"));
        l.add(new Token("c2"));
        l.add(new Token("b3"));
        l.add(new Token("f4"));
        l.add(new Token("e5"));
        l.add(new Token("d6"));
        l.add(new Token("c7"));
        l.add(new Token("b8"));
        l.add(new Token("z"));
        l.add(new Token("x"));
        l.add(new Token("f9"));
        l.add(new Token("e10"));
        l.add(new Token("a11"));
        l.add(new Token("d12"));
        l.add(new Token("b13"));
        l.add(new Token("c14"));
        System.out.println(l);
        PartialOrder.sort(l);
        System.out.println(l);
    }

    static class Token
    implements PartialComparable {
        private String s;

        Token(String s) {
            this.s = s;
        }

        @Override
        public int compareTo(Object other) {
            Token t = (Token)other;
            int cmp = this.s.charAt(0) - t.s.charAt(0);
            if (cmp == 1) {
                return 1;
            }
            if (cmp == -1) {
                return -1;
            }
            return 0;
        }

        @Override
        public int fallbackCompareTo(Object other) {
            return -this.s.compareTo(((Token)other).s);
        }

        public String toString() {
            return this.s;
        }
    }

    private static class SortObject<T extends PartialComparable> {
        T object;
        List<SortObject<T>> smallerObjects = new LinkedList<SortObject<T>>();
        List<SortObject<T>> biggerObjects = new LinkedList<SortObject<T>>();

        public SortObject(T o) {
            this.object = o;
        }

        boolean hasNoSmallerObjects() {
            return this.smallerObjects.size() == 0;
        }

        boolean removeSmallerObject(SortObject<T> o) {
            this.smallerObjects.remove(o);
            return this.hasNoSmallerObjects();
        }

        void addDirectedLinks(SortObject<T> other) {
            int cmp = this.object.compareTo(other.object);
            if (cmp == 0) {
                return;
            }
            if (cmp > 0) {
                this.smallerObjects.add(other);
                other.biggerObjects.add(this);
            } else {
                this.biggerObjects.add(other);
                other.smallerObjects.add(this);
            }
        }

        public String toString() {
            return this.object.toString();
        }
    }

    public static interface PartialComparable {
        public int compareTo(Object var1);

        public int fallbackCompareTo(Object var1);
    }
}

