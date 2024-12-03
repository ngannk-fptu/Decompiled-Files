/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.commons.collections.map.AbstractReferenceMap;

public class ReferenceMap
extends AbstractReferenceMap
implements Serializable {
    private static final long serialVersionUID = 1555089888138299607L;

    public ReferenceMap() {
        super(0, 1, 16, 0.75f, false);
    }

    public ReferenceMap(int keyType, int valueType) {
        super(keyType, valueType, 16, 0.75f, false);
    }

    public ReferenceMap(int keyType, int valueType, boolean purgeValues) {
        super(keyType, valueType, 16, 0.75f, purgeValues);
    }

    public ReferenceMap(int keyType, int valueType, int capacity, float loadFactor) {
        super(keyType, valueType, capacity, loadFactor, false);
    }

    public ReferenceMap(int keyType, int valueType, int capacity, float loadFactor, boolean purgeValues) {
        super(keyType, valueType, capacity, loadFactor, purgeValues);
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

