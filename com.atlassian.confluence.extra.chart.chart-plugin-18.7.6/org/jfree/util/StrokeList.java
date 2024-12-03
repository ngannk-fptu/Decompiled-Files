/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.util;

import java.awt.Stroke;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.jfree.io.SerialUtilities;
import org.jfree.util.AbstractObjectList;

public class StrokeList
extends AbstractObjectList {
    public Stroke getStroke(int index) {
        return (Stroke)this.get(index);
    }

    public void setStroke(int index, Stroke stroke) {
        this.set(index, stroke);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o instanceof StrokeList) {
            return super.equals(o);
        }
        return false;
    }

    public int hashCode() {
        return super.hashCode();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        int count = this.size();
        stream.writeInt(count);
        for (int i = 0; i < count; ++i) {
            Stroke stroke = this.getStroke(i);
            if (stroke != null) {
                stream.writeInt(i);
                SerialUtilities.writeStroke(stroke, stream);
                continue;
            }
            stream.writeInt(-1);
        }
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        int count = stream.readInt();
        for (int i = 0; i < count; ++i) {
            int index = stream.readInt();
            if (index == -1) continue;
            this.setStroke(index, SerialUtilities.readStroke(stream));
        }
    }
}

