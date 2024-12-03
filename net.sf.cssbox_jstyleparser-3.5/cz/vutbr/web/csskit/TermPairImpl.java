/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.TermPair;
import cz.vutbr.web.csskit.TermImpl;

public class TermPairImpl<K, V>
extends TermImpl<V>
implements TermPair<K, V> {
    protected K key;

    protected TermPairImpl() {
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public TermPair<K, V> setKey(K key) {
        this.key = key;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.operator != null) {
            sb.append(this.operator.value());
        }
        if (this.key != null) {
            sb.append(this.key);
        }
        if (this.value != null) {
            sb.append(" ").append(this.value);
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.key == null ? 0 : this.key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof TermPairImpl)) {
            return false;
        }
        TermPairImpl other = (TermPairImpl)obj;
        return !(this.key == null ? other.key != null : !this.key.equals(other.key));
    }
}

