/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import org.jfree.util.ObjectUtilities;

public class AbstractObjectList
implements Cloneable,
Serializable {
    private static final long serialVersionUID = 7789833772597351595L;
    public static final int DEFAULT_INITIAL_CAPACITY = 8;
    private transient Object[] objects;
    private int size = 0;
    private int increment = 8;

    protected AbstractObjectList() {
        this(8);
    }

    protected AbstractObjectList(int initialCapacity) {
        this(initialCapacity, initialCapacity);
    }

    protected AbstractObjectList(int initialCapacity, int increment) {
        this.objects = new Object[initialCapacity];
        this.increment = increment;
    }

    protected Object get(int index) {
        Object result = null;
        if (index >= 0 && index < this.size) {
            result = this.objects[index];
        }
        return result;
    }

    protected void set(int index, Object object) {
        if (index < 0) {
            throw new IllegalArgumentException("Requires index >= 0.");
        }
        if (index >= this.objects.length) {
            Object[] enlarged = new Object[index + this.increment];
            System.arraycopy(this.objects, 0, enlarged, 0, this.objects.length);
            this.objects = enlarged;
        }
        this.objects[index] = object;
        this.size = Math.max(this.size, index + 1);
    }

    public void clear() {
        Arrays.fill(this.objects, null);
        this.size = 0;
    }

    public int size() {
        return this.size;
    }

    protected int indexOf(Object object) {
        for (int index = 0; index < this.size; ++index) {
            if (this.objects[index] != object) continue;
            return index;
        }
        return -1;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractObjectList)) {
            return false;
        }
        AbstractObjectList other = (AbstractObjectList)obj;
        int listSize = this.size();
        for (int i = 0; i < listSize; ++i) {
            if (ObjectUtilities.equal(this.get(i), other.get(i))) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public Object clone() throws CloneNotSupportedException {
        AbstractObjectList clone = (AbstractObjectList)super.clone();
        if (this.objects != null) {
            clone.objects = new Object[this.objects.length];
            System.arraycopy(this.objects, 0, clone.objects, 0, this.objects.length);
        }
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        int count = this.size();
        stream.writeInt(count);
        for (int i = 0; i < count; ++i) {
            Object object = this.get(i);
            if (object != null && object instanceof Serializable) {
                stream.writeInt(i);
                stream.writeObject(object);
                continue;
            }
            stream.writeInt(-1);
        }
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.objects = new Object[this.size];
        int count = stream.readInt();
        for (int i = 0; i < count; ++i) {
            int index = stream.readInt();
            if (index == -1) continue;
            this.set(index, stream.readObject());
        }
    }
}

