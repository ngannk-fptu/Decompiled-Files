/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.util.BytesRef;

public final class Document
implements Iterable<IndexableField> {
    private final List<IndexableField> fields = new ArrayList<IndexableField>();
    private static final String[] NO_STRINGS = new String[0];

    @Override
    public Iterator<IndexableField> iterator() {
        return this.fields.iterator();
    }

    public final void add(IndexableField field) {
        this.fields.add(field);
    }

    public final void removeField(String name) {
        Iterator<IndexableField> it = this.fields.iterator();
        while (it.hasNext()) {
            IndexableField field = it.next();
            if (!field.name().equals(name)) continue;
            it.remove();
            return;
        }
    }

    public final void removeFields(String name) {
        Iterator<IndexableField> it = this.fields.iterator();
        while (it.hasNext()) {
            IndexableField field = it.next();
            if (!field.name().equals(name)) continue;
            it.remove();
        }
    }

    public final BytesRef[] getBinaryValues(String name) {
        ArrayList<BytesRef> result = new ArrayList<BytesRef>();
        for (IndexableField field : this.fields) {
            BytesRef bytes;
            if (!field.name().equals(name) || (bytes = field.binaryValue()) == null) continue;
            result.add(bytes);
        }
        return result.toArray(new BytesRef[result.size()]);
    }

    public final BytesRef getBinaryValue(String name) {
        for (IndexableField field : this.fields) {
            BytesRef bytes;
            if (!field.name().equals(name) || (bytes = field.binaryValue()) == null) continue;
            return bytes;
        }
        return null;
    }

    public final IndexableField getField(String name) {
        for (IndexableField field : this.fields) {
            if (!field.name().equals(name)) continue;
            return field;
        }
        return null;
    }

    public IndexableField[] getFields(String name) {
        ArrayList<IndexableField> result = new ArrayList<IndexableField>();
        for (IndexableField field : this.fields) {
            if (!field.name().equals(name)) continue;
            result.add(field);
        }
        return result.toArray(new IndexableField[result.size()]);
    }

    public final List<IndexableField> getFields() {
        return this.fields;
    }

    public final String[] getValues(String name) {
        ArrayList<String> result = new ArrayList<String>();
        for (IndexableField field : this.fields) {
            if (!field.name().equals(name) || field.stringValue() == null) continue;
            result.add(field.stringValue());
        }
        if (result.size() == 0) {
            return NO_STRINGS;
        }
        return result.toArray(new String[result.size()]);
    }

    public final String get(String name) {
        for (IndexableField field : this.fields) {
            if (!field.name().equals(name) || field.stringValue() == null) continue;
            return field.stringValue();
        }
        return null;
    }

    public final String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Document<");
        for (int i = 0; i < this.fields.size(); ++i) {
            IndexableField field = this.fields.get(i);
            buffer.append(field.toString());
            if (i == this.fields.size() - 1) continue;
            buffer.append(" ");
        }
        buffer.append(">");
        return buffer.toString();
    }
}

