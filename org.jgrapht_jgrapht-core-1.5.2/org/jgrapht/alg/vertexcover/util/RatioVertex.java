/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.vertexcover.util;

import java.util.LinkedHashMap;
import java.util.Map;
import org.jgrapht.util.TypeUtil;

public class RatioVertex<V>
implements Comparable<RatioVertex<V>> {
    public final V v;
    public double weight;
    @Deprecated(since="1.5.2", forRemoval=true)
    public final int ID;
    public final int id;
    protected int degree = 0;
    public final Map<RatioVertex<V>, Integer> neighbors;

    public RatioVertex(int id, V v, double weight) {
        this.id = this.ID = id;
        this.v = v;
        this.weight = weight;
        this.neighbors = new LinkedHashMap<RatioVertex<V>, Integer>();
    }

    public void addNeighbor(RatioVertex<V> v) {
        if (!this.neighbors.containsKey(v)) {
            this.neighbors.put(v, 1);
        } else {
            this.neighbors.put(v, this.neighbors.get(v) + 1);
        }
        ++this.degree;
        assert (this.neighbors.values().stream().mapToInt(Integer::intValue).sum() == this.degree);
    }

    public void removeNeighbor(RatioVertex<V> v) {
        this.degree -= this.neighbors.get(v).intValue();
        this.neighbors.remove(v);
    }

    public int getDegree() {
        return this.degree;
    }

    public double getRatio() {
        return this.weight / (double)this.degree;
    }

    @Override
    public int compareTo(RatioVertex<V> other) {
        if (this.id == other.id) {
            return 0;
        }
        int result = Double.compare(this.getRatio(), other.getRatio());
        if (result == 0) {
            return Integer.compare(this.id, other.id);
        }
        return result;
    }

    public int hashCode() {
        return this.id;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RatioVertex)) {
            return false;
        }
        RatioVertex other = (RatioVertex)TypeUtil.uncheckedCast(o);
        return this.id == other.id;
    }

    public String toString() {
        return "v" + this.id + "(" + this.degree + ")";
    }
}

