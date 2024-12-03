/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.document;

import com.atlassian.lucene36.document.Field;
import com.atlassian.lucene36.document.Fieldable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Document
implements Serializable {
    List<Fieldable> fields = new ArrayList<Fieldable>();
    private float boost = 1.0f;
    private static final Field[] NO_FIELDS = new Field[0];
    private static final Fieldable[] NO_FIELDABLES = new Fieldable[0];
    private static final String[] NO_STRINGS = new String[0];
    private static final byte[][] NO_BYTES = new byte[0][];

    public void setBoost(float boost) {
        this.boost = boost;
    }

    public float getBoost() {
        return this.boost;
    }

    public final void add(Fieldable field) {
        this.fields.add(field);
    }

    public final void removeField(String name) {
        Iterator<Fieldable> it = this.fields.iterator();
        while (it.hasNext()) {
            Fieldable field = it.next();
            if (!field.name().equals(name)) continue;
            it.remove();
            return;
        }
    }

    public final void removeFields(String name) {
        Iterator<Fieldable> it = this.fields.iterator();
        while (it.hasNext()) {
            Fieldable field = it.next();
            if (!field.name().equals(name)) continue;
            it.remove();
        }
    }

    @Deprecated
    public final Field getField(String name) {
        return (Field)this.getFieldable(name);
    }

    public Fieldable getFieldable(String name) {
        for (Fieldable field : this.fields) {
            if (!field.name().equals(name)) continue;
            return field;
        }
        return null;
    }

    public final String get(String name) {
        for (Fieldable field : this.fields) {
            if (!field.name().equals(name) || field.isBinary()) continue;
            return field.stringValue();
        }
        return null;
    }

    public final List<Fieldable> getFields() {
        return this.fields;
    }

    @Deprecated
    public final Field[] getFields(String name) {
        ArrayList<Field> result = new ArrayList<Field>();
        for (Fieldable field : this.fields) {
            if (!field.name().equals(name)) continue;
            result.add((Field)field);
        }
        if (result.size() == 0) {
            return NO_FIELDS;
        }
        return result.toArray(new Field[result.size()]);
    }

    public Fieldable[] getFieldables(String name) {
        ArrayList<Fieldable> result = new ArrayList<Fieldable>();
        for (Fieldable field : this.fields) {
            if (!field.name().equals(name)) continue;
            result.add(field);
        }
        if (result.size() == 0) {
            return NO_FIELDABLES;
        }
        return result.toArray(new Fieldable[result.size()]);
    }

    public final String[] getValues(String name) {
        ArrayList<String> result = new ArrayList<String>();
        for (Fieldable field : this.fields) {
            if (!field.name().equals(name) || field.isBinary()) continue;
            result.add(field.stringValue());
        }
        if (result.size() == 0) {
            return NO_STRINGS;
        }
        return result.toArray(new String[result.size()]);
    }

    public final byte[][] getBinaryValues(String name) {
        ArrayList<byte[]> result = new ArrayList<byte[]>();
        for (Fieldable field : this.fields) {
            if (!field.name().equals(name) || !field.isBinary()) continue;
            result.add(field.getBinaryValue());
        }
        if (result.size() == 0) {
            return NO_BYTES;
        }
        return (byte[][])result.toArray((T[])new byte[result.size()][]);
    }

    public final byte[] getBinaryValue(String name) {
        for (Fieldable field : this.fields) {
            if (!field.name().equals(name) || !field.isBinary()) continue;
            return field.getBinaryValue();
        }
        return null;
    }

    public final String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Document<");
        for (int i = 0; i < this.fields.size(); ++i) {
            Fieldable field = this.fields.get(i);
            buffer.append(field.toString());
            if (i == this.fields.size() - 1) continue;
            buffer.append(" ");
        }
        buffer.append(">");
        return buffer.toString();
    }
}

