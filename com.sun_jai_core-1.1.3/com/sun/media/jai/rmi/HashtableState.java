/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.SerializableStateImpl;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class HashtableState
extends SerializableStateImpl {
    static /* synthetic */ Class class$java$util$Hashtable;

    public static Class[] getSupportedClasses() {
        return new Class[]{class$java$util$Hashtable == null ? (class$java$util$Hashtable = HashtableState.class$("java.util.Hashtable")) : class$java$util$Hashtable};
    }

    public HashtableState(Class c, Object o, RenderingHints h) {
        super(c, o, h);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        Set keySet;
        Hashtable table = (Hashtable)this.theObject;
        Hashtable<Object, Object> serializableTable = new Hashtable<Object, Object>();
        if (table != null && !table.isEmpty() && !(keySet = table.keySet()).isEmpty()) {
            Iterator keyIterator = keySet.iterator();
            while (keyIterator.hasNext()) {
                Object value;
                Object serializableValue;
                Object key = keyIterator.next();
                Object serializableKey = this.getSerializableForm(key);
                if (serializableKey == null || (serializableValue = this.getSerializableForm(value = table.get(key))) == null) continue;
                serializableTable.put(serializableKey, serializableValue);
            }
        }
        out.writeObject(serializableTable);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Hashtable<Object, Object> table;
        Hashtable serializableTable = (Hashtable)in.readObject();
        this.theObject = table = new Hashtable<Object, Object>();
        if (serializableTable.isEmpty()) {
            return;
        }
        Enumeration keys = serializableTable.keys();
        while (keys.hasMoreElements()) {
            Object serializableKey = keys.nextElement();
            Object key = this.getDeserializedFrom(serializableKey);
            Object serializableValue = serializableTable.get(serializableKey);
            Object value = this.getDeserializedFrom(serializableValue);
            table.put(key, value);
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

