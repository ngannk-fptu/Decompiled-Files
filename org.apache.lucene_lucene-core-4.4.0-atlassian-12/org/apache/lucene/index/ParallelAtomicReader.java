/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.index.Terms;
import org.apache.lucene.util.Bits;

public class ParallelAtomicReader
extends AtomicReader {
    private final FieldInfos fieldInfos;
    private final ParallelFields fields = new ParallelFields();
    private final AtomicReader[] parallelReaders;
    private final AtomicReader[] storedFieldsReaders;
    private final Set<AtomicReader> completeReaderSet = Collections.newSetFromMap(new IdentityHashMap());
    private final boolean closeSubReaders;
    private final int maxDoc;
    private final int numDocs;
    private final boolean hasDeletions;
    private final SortedMap<String, AtomicReader> fieldToReader = new TreeMap<String, AtomicReader>();
    private final SortedMap<String, AtomicReader> tvFieldToReader = new TreeMap<String, AtomicReader>();

    public ParallelAtomicReader(AtomicReader ... readers) throws IOException {
        this(true, readers);
    }

    public ParallelAtomicReader(boolean closeSubReaders, AtomicReader ... readers) throws IOException {
        this(closeSubReaders, readers, readers);
    }

    public ParallelAtomicReader(boolean closeSubReaders, AtomicReader[] readers, AtomicReader[] storedFieldsReaders) throws IOException {
        this.closeSubReaders = closeSubReaders;
        if (readers.length == 0 && storedFieldsReaders.length > 0) {
            throw new IllegalArgumentException("There must be at least one main reader if storedFieldsReaders are used.");
        }
        this.parallelReaders = (AtomicReader[])readers.clone();
        this.storedFieldsReaders = (AtomicReader[])storedFieldsReaders.clone();
        if (this.parallelReaders.length > 0) {
            AtomicReader first = this.parallelReaders[0];
            this.maxDoc = first.maxDoc();
            this.numDocs = first.numDocs();
            this.hasDeletions = first.hasDeletions();
        } else {
            this.numDocs = 0;
            this.maxDoc = 0;
            this.hasDeletions = false;
        }
        Collections.addAll(this.completeReaderSet, this.parallelReaders);
        Collections.addAll(this.completeReaderSet, this.storedFieldsReaders);
        for (AtomicReader reader : this.completeReaderSet) {
            if (reader.maxDoc() == this.maxDoc) continue;
            throw new IllegalArgumentException("All readers must have same maxDoc: " + this.maxDoc + "!=" + reader.maxDoc());
        }
        FieldInfos.Builder builder = new FieldInfos.Builder();
        for (AtomicReader reader : this.parallelReaders) {
            FieldInfos readerFieldInfos = reader.getFieldInfos();
            for (FieldInfo fieldInfo : readerFieldInfos) {
                if (this.fieldToReader.containsKey(fieldInfo.name)) continue;
                builder.add(fieldInfo);
                this.fieldToReader.put(fieldInfo.name, reader);
                if (!fieldInfo.hasVectors()) continue;
                this.tvFieldToReader.put(fieldInfo.name, reader);
            }
        }
        this.fieldInfos = builder.finish();
        for (AtomicReader reader : this.parallelReaders) {
            Fields readerFields = reader.fields();
            if (readerFields == null) continue;
            for (String field : readerFields) {
                if (this.fieldToReader.get(field) != reader) continue;
                this.fields.addField(field, readerFields.terms(field));
            }
        }
        for (AtomicReader reader : this.completeReaderSet) {
            if (!closeSubReaders) {
                reader.incRef();
            }
            reader.registerParentReader(this);
        }
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder("ParallelAtomicReader(");
        Iterator<AtomicReader> iter = this.completeReaderSet.iterator();
        while (iter.hasNext()) {
            buffer.append(iter.next());
            if (!iter.hasNext()) continue;
            buffer.append(", ");
        }
        return buffer.append(')').toString();
    }

    @Override
    public FieldInfos getFieldInfos() {
        return this.fieldInfos;
    }

    @Override
    public Bits getLiveDocs() {
        this.ensureOpen();
        return this.hasDeletions ? this.parallelReaders[0].getLiveDocs() : null;
    }

    @Override
    public Fields fields() {
        this.ensureOpen();
        return this.fields;
    }

    @Override
    public int numDocs() {
        return this.numDocs;
    }

    @Override
    public int maxDoc() {
        return this.maxDoc;
    }

    @Override
    public void document(int docID, StoredFieldVisitor visitor) throws IOException {
        this.ensureOpen();
        for (AtomicReader reader : this.storedFieldsReaders) {
            reader.document(docID, visitor);
        }
    }

    @Override
    public Fields getTermVectors(int docID) throws IOException {
        this.ensureOpen();
        ParallelFields fields = null;
        for (Map.Entry<String, AtomicReader> ent : this.tvFieldToReader.entrySet()) {
            String fieldName = ent.getKey();
            Terms vector = ent.getValue().getTermVector(docID, fieldName);
            if (vector == null) continue;
            if (fields == null) {
                fields = new ParallelFields();
            }
            fields.addField(fieldName, vector);
        }
        return fields;
    }

    @Override
    protected synchronized void doClose() throws IOException {
        IOException ioe = null;
        for (AtomicReader reader : this.completeReaderSet) {
            try {
                if (this.closeSubReaders) {
                    reader.close();
                    continue;
                }
                reader.decRef();
            }
            catch (IOException e) {
                if (ioe != null) continue;
                ioe = e;
            }
        }
        if (ioe != null) {
            throw ioe;
        }
    }

    @Override
    public NumericDocValues getNumericDocValues(String field) throws IOException {
        this.ensureOpen();
        AtomicReader reader = (AtomicReader)this.fieldToReader.get(field);
        return reader == null ? null : reader.getNumericDocValues(field);
    }

    @Override
    public BinaryDocValues getBinaryDocValues(String field) throws IOException {
        this.ensureOpen();
        AtomicReader reader = (AtomicReader)this.fieldToReader.get(field);
        return reader == null ? null : reader.getBinaryDocValues(field);
    }

    @Override
    public SortedDocValues getSortedDocValues(String field) throws IOException {
        this.ensureOpen();
        AtomicReader reader = (AtomicReader)this.fieldToReader.get(field);
        return reader == null ? null : reader.getSortedDocValues(field);
    }

    @Override
    public SortedSetDocValues getSortedSetDocValues(String field) throws IOException {
        this.ensureOpen();
        AtomicReader reader = (AtomicReader)this.fieldToReader.get(field);
        return reader == null ? null : reader.getSortedSetDocValues(field);
    }

    @Override
    public NumericDocValues getNormValues(String field) throws IOException {
        this.ensureOpen();
        AtomicReader reader = (AtomicReader)this.fieldToReader.get(field);
        NumericDocValues values = reader == null ? null : reader.getNormValues(field);
        return values;
    }

    private final class ParallelFields
    extends Fields {
        final Map<String, Terms> fields = new TreeMap<String, Terms>();

        ParallelFields() {
        }

        void addField(String fieldName, Terms terms) {
            this.fields.put(fieldName, terms);
        }

        @Override
        public Iterator<String> iterator() {
            return Collections.unmodifiableSet(this.fields.keySet()).iterator();
        }

        @Override
        public Terms terms(String field) {
            return this.fields.get(field);
        }

        @Override
        public int size() {
            return this.fields.size();
        }
    }
}

