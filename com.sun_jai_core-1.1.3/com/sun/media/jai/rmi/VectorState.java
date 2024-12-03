/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.SerializableStateImpl;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Vector;

public class VectorState
extends SerializableStateImpl {
    static /* synthetic */ Class class$java$util$Vector;

    public static Class[] getSupportedClasses() {
        return new Class[]{class$java$util$Vector == null ? (class$java$util$Vector = VectorState.class$("java.util.Vector")) : class$java$util$Vector};
    }

    public VectorState(Class c, Object o, RenderingHints h) {
        super(c, o, h);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        Vector vector = (Vector)this.theObject;
        Vector<Object> serializableVector = new Vector<Object>();
        Iterator iterator = vector.iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            Object serializableObject = this.getSerializableForm(object);
            serializableVector.add(serializableObject);
        }
        out.writeObject(serializableVector);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Vector<Object> vector;
        Vector serializableVector = (Vector)in.readObject();
        this.theObject = vector = new Vector<Object>();
        if (serializableVector.isEmpty()) {
            return;
        }
        Iterator iterator = serializableVector.iterator();
        while (iterator.hasNext()) {
            Object serializableObject = iterator.next();
            Object object = this.getDeserializedFrom(serializableObject);
            vector.add(object);
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

