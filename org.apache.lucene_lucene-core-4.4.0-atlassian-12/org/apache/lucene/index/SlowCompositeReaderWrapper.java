/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.CompositeReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiDocValues;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.util.Bits;

public final class SlowCompositeReaderWrapper
extends AtomicReader {
    private final CompositeReader in;
    private final Fields fields;
    private final Bits liveDocs;
    private final Map<String, MultiDocValues.OrdinalMap> cachedOrdMaps = new HashMap<String, MultiDocValues.OrdinalMap>();

    public static AtomicReader wrap(IndexReader reader) throws IOException {
        if (reader instanceof CompositeReader) {
            return new SlowCompositeReaderWrapper((CompositeReader)reader);
        }
        assert (reader instanceof AtomicReader);
        return (AtomicReader)reader;
    }

    public SlowCompositeReaderWrapper(CompositeReader reader) throws IOException {
        this.in = reader;
        this.fields = MultiFields.getFields(this.in);
        this.liveDocs = MultiFields.getLiveDocs(this.in);
        this.in.registerParentReader(this);
    }

    public String toString() {
        return "SlowCompositeReaderWrapper(" + this.in + ")";
    }

    @Override
    public Fields fields() {
        this.ensureOpen();
        return this.fields;
    }

    @Override
    public NumericDocValues getNumericDocValues(String field) throws IOException {
        this.ensureOpen();
        return MultiDocValues.getNumericValues(this.in, field);
    }

    @Override
    public BinaryDocValues getBinaryDocValues(String field) throws IOException {
        this.ensureOpen();
        return MultiDocValues.getBinaryValues(this.in, field);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SortedDocValues getSortedDocValues(String field) throws IOException {
        this.ensureOpen();
        MultiDocValues.OrdinalMap map = null;
        Map<String, MultiDocValues.OrdinalMap> map2 = this.cachedOrdMaps;
        synchronized (map2) {
            map = this.cachedOrdMaps.get(field);
            if (map == null) {
                SortedDocValues dv = MultiDocValues.getSortedValues(this.in, field);
                if (dv instanceof MultiDocValues.MultiSortedDocValues) {
                    map = ((MultiDocValues.MultiSortedDocValues)dv).mapping;
                    if (map.owner == this.getCoreCacheKey()) {
                        this.cachedOrdMaps.put(field, map);
                    }
                }
                return dv;
            }
        }
        if (this.getFieldInfos().fieldInfo(field).getDocValuesType() != FieldInfo.DocValuesType.SORTED) {
            return null;
        }
        int size = this.in.leaves().size();
        SortedDocValues[] values = new SortedDocValues[size];
        int[] starts = new int[size + 1];
        for (int i = 0; i < size; ++i) {
            AtomicReaderContext context = this.in.leaves().get(i);
            SortedDocValues v = context.reader().getSortedDocValues(field);
            if (v == null) {
                v = SortedDocValues.EMPTY;
            }
            values[i] = v;
            starts[i] = context.docBase;
        }
        starts[size] = this.maxDoc();
        return new MultiDocValues.MultiSortedDocValues(values, starts, map);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SortedSetDocValues getSortedSetDocValues(String field) throws IOException {
        this.ensureOpen();
        MultiDocValues.OrdinalMap map = null;
        Map<String, MultiDocValues.OrdinalMap> map2 = this.cachedOrdMaps;
        synchronized (map2) {
            map = this.cachedOrdMaps.get(field);
            if (map == null) {
                SortedSetDocValues dv = MultiDocValues.getSortedSetValues(this.in, field);
                if (dv instanceof MultiDocValues.MultiSortedSetDocValues) {
                    map = ((MultiDocValues.MultiSortedSetDocValues)dv).mapping;
                    if (map.owner == this.getCoreCacheKey()) {
                        this.cachedOrdMaps.put(field, map);
                    }
                }
                return dv;
            }
        }
        if (this.getFieldInfos().fieldInfo(field).getDocValuesType() != FieldInfo.DocValuesType.SORTED_SET) {
            return null;
        }
        assert (map != null);
        int size = this.in.leaves().size();
        SortedSetDocValues[] values = new SortedSetDocValues[size];
        int[] starts = new int[size + 1];
        for (int i = 0; i < size; ++i) {
            AtomicReaderContext context = this.in.leaves().get(i);
            SortedSetDocValues v = context.reader().getSortedSetDocValues(field);
            if (v == null) {
                v = SortedSetDocValues.EMPTY;
            }
            values[i] = v;
            starts[i] = context.docBase;
        }
        starts[size] = this.maxDoc();
        return new MultiDocValues.MultiSortedSetDocValues(values, starts, map);
    }

    @Override
    public NumericDocValues getNormValues(String field) throws IOException {
        this.ensureOpen();
        return MultiDocValues.getNormValues(this.in, field);
    }

    @Override
    public Fields getTermVectors(int docID) throws IOException {
        this.ensureOpen();
        return this.in.getTermVectors(docID);
    }

    @Override
    public int numDocs() {
        return this.in.numDocs();
    }

    @Override
    public int maxDoc() {
        return this.in.maxDoc();
    }

    @Override
    public void document(int docID, StoredFieldVisitor visitor) throws IOException {
        this.ensureOpen();
        this.in.document(docID, visitor);
    }

    @Override
    public Bits getLiveDocs() {
        this.ensureOpen();
        return this.liveDocs;
    }

    @Override
    public FieldInfos getFieldInfos() {
        this.ensureOpen();
        return MultiFields.getMergedFieldInfos(this.in);
    }

    @Override
    public Object getCoreCacheKey() {
        return this.in.getCoreCacheKey();
    }

    @Override
    public Object getCombinedCoreAndDeletesKey() {
        return this.in.getCombinedCoreAndDeletesKey();
    }

    @Override
    protected void doClose() throws IOException {
        this.in.close();
    }
}

