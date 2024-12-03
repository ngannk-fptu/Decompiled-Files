/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart;

import java.awt.Stroke;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.jfree.io.SerialUtilities;
import org.jfree.util.ObjectUtilities;

public class StrokeMap
implements Cloneable,
Serializable {
    static final long serialVersionUID = -8148916785963525169L;
    private transient Map store = new TreeMap();

    public Stroke getStroke(Comparable key) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        return (Stroke)this.store.get(key);
    }

    public boolean containsKey(Comparable key) {
        return this.store.containsKey(key);
    }

    public void put(Comparable key, Stroke stroke) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        this.store.put(key, stroke);
    }

    public void clear() {
        this.store.clear();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StrokeMap)) {
            return false;
        }
        StrokeMap that = (StrokeMap)obj;
        if (this.store.size() != that.store.size()) {
            return false;
        }
        Set keys = this.store.keySet();
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            Stroke s2;
            Comparable key = (Comparable)iterator.next();
            Stroke s1 = this.getStroke(key);
            if (ObjectUtilities.equal(s1, s2 = that.getStroke(key))) continue;
            return false;
        }
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(this.store.size());
        Set keys = this.store.keySet();
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            Comparable key = (Comparable)iterator.next();
            stream.writeObject(key);
            Stroke stroke = this.getStroke(key);
            SerialUtilities.writeStroke(stroke, stream);
        }
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.store = new TreeMap();
        int keyCount = stream.readInt();
        for (int i = 0; i < keyCount; ++i) {
            Comparable key = (Comparable)stream.readObject();
            Stroke stroke = SerialUtilities.readStroke(stream);
            this.store.put(key, stroke);
        }
    }
}

