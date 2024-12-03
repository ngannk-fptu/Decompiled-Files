/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import org.apache.commons.collections.map.AbstractReferenceMap;

public class ReferenceIdentityMap
extends AbstractReferenceMap
implements Serializable {
    private static final long serialVersionUID = -1266190134568365852L;

    public ReferenceIdentityMap() {
        super(0, 1, 16, 0.75f, false);
    }

    public ReferenceIdentityMap(int keyType, int valueType) {
        super(keyType, valueType, 16, 0.75f, false);
    }

    public ReferenceIdentityMap(int keyType, int valueType, boolean purgeValues) {
        super(keyType, valueType, 16, 0.75f, purgeValues);
    }

    public ReferenceIdentityMap(int keyType, int valueType, int capacity, float loadFactor) {
        super(keyType, valueType, capacity, loadFactor, false);
    }

    public ReferenceIdentityMap(int keyType, int valueType, int capacity, float loadFactor, boolean purgeValues) {
        super(keyType, valueType, capacity, loadFactor, purgeValues);
    }

    protected int hash(Object key) {
        return System.identityHashCode(key);
    }

    protected int hashEntry(Object key, Object value) {
        return System.identityHashCode(key) ^ System.identityHashCode(value);
    }

    protected boolean isEqualKey(Object key1, Object key2) {
        key2 = this.keyType > 0 ? ((Reference)key2).get() : key2;
        return key1 == key2;
    }

    protected boolean isEqualValue(Object value1, Object value2) {
        return value1 == value2;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        this.doWriteObject(out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.doReadObject(in);
    }
}

