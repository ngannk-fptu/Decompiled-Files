/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import java.awt.RenderingHints;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import javax.media.jai.JAI;

public class RenderingHintsProxy
implements Serializable {
    private transient RenderingHints hints;
    private static final Class[] KEY_CLASSES = new Class[]{class$java$awt$RenderingHints == null ? (class$java$awt$RenderingHints = RenderingHintsProxy.class$("java.awt.RenderingHints")) : class$java$awt$RenderingHints, class$javax$media$jai$JAI == null ? (class$javax$media$jai$JAI = RenderingHintsProxy.class$("javax.media.jai.JAI")) : class$javax$media$jai$JAI};
    private static final Object[] SUPPRESSED_KEYS = new Object[]{JAI.KEY_OPERATION_REGISTRY, JAI.KEY_TILE_CACHE, JAI.KEY_RETRY_INTERVAL, JAI.KEY_NUM_RETRIES, JAI.KEY_NEGOTIATION_PREFERENCES};
    private static SoftReference suppressedKeyReference = null;
    private static SoftReference hintTableReference = null;
    static /* synthetic */ Class class$java$awt$RenderingHints;
    static /* synthetic */ Class class$javax$media$jai$JAI;

    public RenderingHintsProxy(RenderingHints source) {
        this.hints = source;
    }

    public RenderingHints getRenderingHints() {
        return this.hints;
    }

    private static synchronized Vector getSuppressedKeys() {
        Vector<Object> suppressedKeys = null;
        if (SUPPRESSED_KEYS != null) {
            Vector<Object> vector = suppressedKeys = suppressedKeyReference != null ? (Vector<Object>)suppressedKeyReference.get() : null;
            if (suppressedKeys == null) {
                int numSuppressedKeys = SUPPRESSED_KEYS.length;
                suppressedKeys = new Vector<Object>(numSuppressedKeys);
                for (int i = 0; i < numSuppressedKeys; ++i) {
                    suppressedKeys.add(SUPPRESSED_KEYS[i]);
                }
                suppressedKeyReference = new SoftReference(suppressedKeys);
            }
        }
        return suppressedKeys;
    }

    private static synchronized Hashtable getHintTable() {
        Hashtable<Object, HintElement> table;
        Hashtable<Object, HintElement> hashtable = table = hintTableReference != null ? (Hashtable<Object, HintElement>)hintTableReference.get() : null;
        if (table == null) {
            table = new Hashtable<Object, HintElement>();
            for (int i = 0; i < KEY_CLASSES.length; ++i) {
                Class cls = KEY_CLASSES[i];
                Field[] fields = cls.getFields();
                for (int j = 0; j < fields.length; ++j) {
                    Field fld = fields[j];
                    int modifiers = fld.getModifiers();
                    if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers)) continue;
                    try {
                        Object fieldValue = fld.get(null);
                        table.put(fieldValue, new HintElement(cls, fld));
                        continue;
                    }
                    catch (Exception e) {
                        // empty catch block
                    }
                }
            }
            hintTableReference = new SoftReference(table);
        }
        return table;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        Set<Object> keySet;
        Hashtable<HintElement, HintElement> table = new Hashtable<HintElement, HintElement>();
        if (this.hints != null && !this.hints.isEmpty() && !(keySet = this.hints.keySet()).isEmpty()) {
            Iterator<Object> keyIterator = keySet.iterator();
            Hashtable hintTable = RenderingHintsProxy.getHintTable();
            Vector suppressedKeys = RenderingHintsProxy.getSuppressedKeys();
            while (keyIterator.hasNext()) {
                HintElement keyElement;
                Object key = keyIterator.next();
                if (suppressedKeys != null && suppressedKeys.indexOf(key) != -1 || (keyElement = (HintElement)hintTable.get(key)) == null) continue;
                Object value = this.hints.get(key);
                HintElement valueElement = null;
                try {
                    valueElement = new HintElement(value);
                }
                catch (NotSerializableException nse) {
                    valueElement = (HintElement)hintTable.get(value);
                }
                if (valueElement == null) continue;
                table.put(keyElement, valueElement);
            }
        }
        out.writeObject(table);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Hashtable table = (Hashtable)in.readObject();
        this.hints = new RenderingHints(null);
        if (table.isEmpty()) {
            return;
        }
        Enumeration keys = table.keys();
        while (keys.hasMoreElements()) {
            HintElement keyElement = (HintElement)keys.nextElement();
            Object key = keyElement.getObject();
            HintElement valueElement = (HintElement)table.get(keyElement);
            Object value = valueElement.getObject();
            this.hints.put(key, value);
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

    private static class HintElement
    implements Serializable {
        private static final int TYPE_OBJECT = 1;
        private static final int TYPE_FIELD = 2;
        private int type;
        private Object obj;
        private String className;
        private String fieldName;

        public HintElement(Object obj) throws NotSerializableException {
            if (!(obj instanceof Serializable)) {
                throw new NotSerializableException();
            }
            this.type = 1;
            this.obj = obj;
        }

        public HintElement(Class cls, Field fld) {
            this.type = 2;
            this.className = cls.getName();
            this.fieldName = fld.getName();
        }

        public Object getObject() {
            Object elt = null;
            if (this.type == 1) {
                elt = this.obj;
            } else if (this.type == 2) {
                try {
                    Class<?> cls = Class.forName(this.className);
                    Field fld = cls.getField(this.fieldName);
                    elt = fld.get(null);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            return elt;
        }
    }
}

