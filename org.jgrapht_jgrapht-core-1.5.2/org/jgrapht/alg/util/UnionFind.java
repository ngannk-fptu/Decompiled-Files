/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class UnionFind<T> {
    private final Map<T, T> parentMap = new LinkedHashMap<T, T>();
    private final Map<T, Integer> rankMap = new HashMap<T, Integer>();
    private int count;

    public UnionFind(Set<T> elements) {
        for (T element : elements) {
            this.parentMap.put(element, element);
            this.rankMap.put(element, 0);
        }
        this.count = elements.size();
    }

    public void addElement(T element) {
        if (this.parentMap.containsKey(element)) {
            throw new IllegalArgumentException("element is already contained in UnionFind: " + element);
        }
        this.parentMap.put(element, element);
        this.rankMap.put(element, 0);
        ++this.count;
    }

    protected Map<T, T> getParentMap() {
        return this.parentMap;
    }

    protected Map<T, Integer> getRankMap() {
        return this.rankMap;
    }

    public T find(T element) {
        T parent;
        if (!this.parentMap.containsKey(element)) {
            throw new IllegalArgumentException("element is not contained in this UnionFind data structure: " + element);
        }
        T current = element;
        while (!(parent = this.parentMap.get(current)).equals(current)) {
            current = parent;
        }
        T root = current;
        current = element;
        while (!current.equals(root)) {
            T parent2 = this.parentMap.get(current);
            this.parentMap.put(current, root);
            current = parent2;
        }
        return root;
    }

    public void union(T element1, T element2) {
        int rank2;
        T parent2;
        if (!this.parentMap.containsKey(element1) || !this.parentMap.containsKey(element2)) {
            throw new IllegalArgumentException("elements must be contained in given set");
        }
        T parent1 = this.find(element1);
        if (parent1.equals(parent2 = this.find(element2))) {
            return;
        }
        int rank1 = this.rankMap.get(parent1);
        if (rank1 > (rank2 = this.rankMap.get(parent2).intValue())) {
            this.parentMap.put(parent2, parent1);
        } else if (rank1 < rank2) {
            this.parentMap.put(parent1, parent2);
        } else {
            this.parentMap.put(parent2, parent1);
            this.rankMap.put(parent1, rank1 + 1);
        }
        --this.count;
    }

    public boolean inSameSet(T element1, T element2) {
        return this.find(element1).equals(this.find(element2));
    }

    public int numberOfSets() {
        assert (this.count >= 1 && this.count <= this.parentMap.keySet().size());
        return this.count;
    }

    public int size() {
        return this.parentMap.size();
    }

    public void reset() {
        for (T element : this.parentMap.keySet()) {
            this.parentMap.put(element, element);
            this.rankMap.put(element, 0);
        }
        this.count = this.parentMap.size();
    }

    public String toString() {
        LinkedHashMap setRep = new LinkedHashMap();
        for (T t : this.parentMap.keySet()) {
            T representative = this.find(t);
            if (!setRep.containsKey(representative)) {
                setRep.put(representative, new LinkedHashSet());
            }
            ((Set)setRep.get(representative)).add(t);
        }
        return setRep.keySet().stream().map(key -> "{" + key + ":" + ((Set)setRep.get(key)).stream().map(Objects::toString).collect(Collectors.joining(",")) + "}").collect(Collectors.joining(", ", "{", "}"));
    }
}

