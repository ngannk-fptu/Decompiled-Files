/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.json;

public class ReferenceEqualityKey<K> {
    private final K wrappedKey;

    public ReferenceEqualityKey(K wrappedKey) {
        this.wrappedKey = wrappedKey;
    }

    public K get() {
        return this.wrappedKey;
    }

    public int hashCode() {
        K key = this.wrappedKey;
        return key == null ? 0 : System.identityHashCode(key);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ReferenceEqualityKey)) {
            return false;
        }
        return this.wrappedKey == ((ReferenceEqualityKey)obj).wrappedKey;
    }

    public String toString() {
        K key = this.wrappedKey;
        return key == null ? "null" : key.toString();
    }
}

