/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.document;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.StoredFieldVisitor;

public class DocumentStoredFieldVisitor
extends StoredFieldVisitor {
    private final Document doc = new Document();
    private final Set<String> fieldsToAdd;

    public DocumentStoredFieldVisitor(Set<String> fieldsToAdd) {
        this.fieldsToAdd = fieldsToAdd;
    }

    public DocumentStoredFieldVisitor(String ... fields) {
        this.fieldsToAdd = new HashSet<String>(fields.length);
        for (String field : fields) {
            this.fieldsToAdd.add(field);
        }
    }

    public DocumentStoredFieldVisitor() {
        this.fieldsToAdd = null;
    }

    @Override
    public void binaryField(FieldInfo fieldInfo, byte[] value) throws IOException {
        this.doc.add(new StoredField(fieldInfo.name, value));
    }

    @Override
    public void stringField(FieldInfo fieldInfo, String value) throws IOException {
        FieldType ft = new FieldType(TextField.TYPE_STORED);
        ft.setStoreTermVectors(fieldInfo.hasVectors());
        ft.setIndexed(fieldInfo.isIndexed());
        ft.setOmitNorms(fieldInfo.omitsNorms());
        ft.setIndexOptions(fieldInfo.getIndexOptions());
        this.doc.add(new Field(fieldInfo.name, value, ft));
    }

    @Override
    public void intField(FieldInfo fieldInfo, int value) {
        this.doc.add(new StoredField(fieldInfo.name, value));
    }

    @Override
    public void longField(FieldInfo fieldInfo, long value) {
        this.doc.add(new StoredField(fieldInfo.name, value));
    }

    @Override
    public void floatField(FieldInfo fieldInfo, float value) {
        this.doc.add(new StoredField(fieldInfo.name, value));
    }

    @Override
    public void doubleField(FieldInfo fieldInfo, double value) {
        this.doc.add(new StoredField(fieldInfo.name, value));
    }

    @Override
    public StoredFieldVisitor.Status needsField(FieldInfo fieldInfo) throws IOException {
        return this.fieldsToAdd == null || this.fieldsToAdd.contains(fieldInfo.name) ? StoredFieldVisitor.Status.YES : StoredFieldVisitor.Status.NO;
    }

    public Document getDocument() {
        return this.doc;
    }
}

