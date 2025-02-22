/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.util;

import java.awt.Paint;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.jfree.io.SerialUtilities;
import org.jfree.util.AbstractObjectList;
import org.jfree.util.PaintUtilities;

public class PaintList
extends AbstractObjectList {
    public Paint getPaint(int index) {
        return (Paint)this.get(index);
    }

    public void setPaint(int index, Paint paint) {
        this.set(index, paint);
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof PaintList) {
            PaintList that = (PaintList)obj;
            int listSize = this.size();
            for (int i = 0; i < listSize; ++i) {
                if (PaintUtilities.equal(this.getPaint(i), that.getPaint(i))) continue;
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        return super.hashCode();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        int count = this.size();
        stream.writeInt(count);
        for (int i = 0; i < count; ++i) {
            Paint paint = this.getPaint(i);
            if (paint != null) {
                stream.writeInt(i);
                SerialUtilities.writePaint(paint, stream);
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
            this.setPaint(index, SerialUtilities.readPaint(stream));
        }
    }
}

