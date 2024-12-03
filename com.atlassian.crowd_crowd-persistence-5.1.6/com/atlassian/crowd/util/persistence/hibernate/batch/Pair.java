/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.util.persistence.hibernate.batch;

class Pair<K, V> {
    private final K first;
    private final V second;

    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    public K getFirst() {
        return this.first;
    }

    public V getSecond() {
        return this.second;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Pair pair = (Pair)o;
        if (this.first != null ? !this.first.equals(pair.first) : pair.first != null) {
            return false;
        }
        return !(this.second != null ? !this.second.equals(pair.second) : pair.second != null);
    }

    public int hashCode() {
        int result = this.first != null ? this.first.hashCode() : 0;
        result = 31 * result + (this.second != null ? this.second.hashCode() : 0);
        return result;
    }
}

