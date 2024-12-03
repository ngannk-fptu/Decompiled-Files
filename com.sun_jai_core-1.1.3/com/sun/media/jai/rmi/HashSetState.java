/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.SerializableStateImpl;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Iterator;

public class HashSetState
extends SerializableStateImpl {
    static /* synthetic */ Class class$java$util$HashSet;

    public static Class[] getSupportedClasses() {
        return new Class[]{class$java$util$HashSet == null ? (class$java$util$HashSet = HashSetState.class$("java.util.HashSet")) : class$java$util$HashSet};
    }

    public HashSetState(Class c, Object o, RenderingHints h) {
        super(c, o, h);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        HashSet set = (HashSet)this.theObject;
        HashSet<Object> serializableSet = new HashSet<Object>();
        if (set != null && !set.isEmpty()) {
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                Object object = iterator.next();
                Object serializableObject = this.getSerializableForm(object);
                serializableSet.add(serializableObject);
            }
        }
        out.writeObject(serializableSet);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        HashSet serializableSet = (HashSet)in.readObject();
        HashSet<Object> set = new HashSet<Object>();
        if (serializableSet.isEmpty()) {
            this.theObject = set;
            return;
        }
        Iterator iterator = serializableSet.iterator();
        while (iterator.hasNext()) {
            Object serializableObject = iterator.next();
            Object object = this.getDeserializedFrom(serializableObject);
            set.add(object);
        }
        this.theObject = set;
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

