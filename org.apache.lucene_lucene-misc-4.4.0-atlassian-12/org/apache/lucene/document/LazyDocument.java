/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.document.Document
 *  org.apache.lucene.index.FieldInfo
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.IndexableField
 *  org.apache.lucene.index.IndexableFieldType
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.document;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.util.BytesRef;

public class LazyDocument {
    private final IndexReader reader;
    private final int docID;
    private Document doc;
    private Map<Integer, List<LazyField>> fields = new HashMap<Integer, List<LazyField>>();
    private Set<String> fieldNames = new HashSet<String>();

    public LazyDocument(IndexReader reader, int docID) {
        this.reader = reader;
        this.docID = docID;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public IndexableField getField(FieldInfo fieldInfo) {
        this.fieldNames.add(fieldInfo.name);
        List<LazyField> values = this.fields.get(fieldInfo.number);
        if (null == values) {
            values = new ArrayList<LazyField>();
            this.fields.put(fieldInfo.number, values);
        }
        LazyField value = new LazyField(fieldInfo.name, fieldInfo.number);
        values.add(value);
        LazyDocument lazyDocument = this;
        synchronized (lazyDocument) {
            this.doc = null;
        }
        return value;
    }

    synchronized Document getDocument() {
        if (this.doc == null) {
            try {
                this.doc = this.reader.document(this.docID, this.fieldNames);
            }
            catch (IOException ioe) {
                throw new IllegalStateException("unable to load document", ioe);
            }
        }
        return this.doc;
    }

    private void fetchRealValues(String name, int fieldNum) {
        Document d = this.getDocument();
        List<LazyField> lazyValues = this.fields.get(fieldNum);
        IndexableField[] realValues = d.getFields(name);
        assert (realValues.length <= lazyValues.size()) : "More lazy values then real values for field: " + name;
        for (int i = 0; i < lazyValues.size(); ++i) {
            LazyField f = lazyValues.get(i);
            if (null == f) continue;
            f.realValue = realValues[i];
        }
    }

    public class LazyField
    implements IndexableField {
        private String name;
        private int fieldNum;
        volatile IndexableField realValue = null;

        private LazyField(String name, int fieldNum) {
            this.name = name;
            this.fieldNum = fieldNum;
        }

        public boolean hasBeenLoaded() {
            return null != this.realValue;
        }

        private IndexableField getRealValue() {
            if (null == this.realValue) {
                LazyDocument.this.fetchRealValues(this.name, this.fieldNum);
            }
            assert (this.hasBeenLoaded()) : "field value was not lazy loaded";
            assert (this.realValue.name().equals(this.name())) : "realvalue name != name: " + this.realValue.name() + " != " + this.name();
            return this.realValue;
        }

        public String name() {
            return this.name;
        }

        public float boost() {
            return 1.0f;
        }

        public BytesRef binaryValue() {
            return this.getRealValue().binaryValue();
        }

        public String stringValue() {
            return this.getRealValue().stringValue();
        }

        public Reader readerValue() {
            return this.getRealValue().readerValue();
        }

        public Number numericValue() {
            return this.getRealValue().numericValue();
        }

        public IndexableFieldType fieldType() {
            return this.getRealValue().fieldType();
        }

        public TokenStream tokenStream(Analyzer analyzer) throws IOException {
            return this.getRealValue().tokenStream(analyzer);
        }
    }
}

