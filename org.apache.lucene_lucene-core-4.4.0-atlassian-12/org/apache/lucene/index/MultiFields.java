/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MergedIterator;
import org.apache.lucene.index.MultiBits;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.ReaderSlice;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;

public final class MultiFields
extends Fields {
    private final Fields[] subs;
    private final ReaderSlice[] subSlices;
    private final Map<String, Terms> terms = new ConcurrentHashMap<String, Terms>();

    public static Fields getFields(IndexReader reader) throws IOException {
        List<AtomicReaderContext> leaves = reader.leaves();
        switch (leaves.size()) {
            case 0: {
                return null;
            }
            case 1: {
                return leaves.get(0).reader().fields();
            }
        }
        ArrayList<Fields> fields = new ArrayList<Fields>();
        ArrayList<ReaderSlice> slices = new ArrayList<ReaderSlice>();
        for (AtomicReaderContext ctx : leaves) {
            AtomicReader r = ctx.reader();
            Fields f = r.fields();
            if (f == null) continue;
            fields.add(f);
            slices.add(new ReaderSlice(ctx.docBase, r.maxDoc(), fields.size() - 1));
        }
        if (fields.isEmpty()) {
            return null;
        }
        if (fields.size() == 1) {
            return (Fields)fields.get(0);
        }
        return new MultiFields(fields.toArray(Fields.EMPTY_ARRAY), slices.toArray(ReaderSlice.EMPTY_ARRAY));
    }

    public static Bits getLiveDocs(IndexReader reader) {
        if (reader.hasDeletions()) {
            List<AtomicReaderContext> leaves = reader.leaves();
            int size = leaves.size();
            assert (size > 0) : "A reader with deletions must have at least one leave";
            if (size == 1) {
                return leaves.get(0).reader().getLiveDocs();
            }
            Bits[] liveDocs = new Bits[size];
            int[] starts = new int[size + 1];
            for (int i = 0; i < size; ++i) {
                AtomicReaderContext ctx = leaves.get(i);
                liveDocs[i] = ctx.reader().getLiveDocs();
                starts[i] = ctx.docBase;
            }
            starts[size] = reader.maxDoc();
            return new MultiBits(liveDocs, starts, true);
        }
        return null;
    }

    public static Terms getTerms(IndexReader r, String field) throws IOException {
        Fields fields = MultiFields.getFields(r);
        if (fields == null) {
            return null;
        }
        return fields.terms(field);
    }

    public static DocsEnum getTermDocsEnum(IndexReader r, Bits liveDocs, String field, BytesRef term) throws IOException {
        return MultiFields.getTermDocsEnum(r, liveDocs, field, term, 1);
    }

    public static DocsEnum getTermDocsEnum(IndexReader r, Bits liveDocs, String field, BytesRef term, int flags) throws IOException {
        TermsEnum termsEnum;
        assert (field != null);
        assert (term != null);
        Terms terms = MultiFields.getTerms(r, field);
        if (terms != null && (termsEnum = terms.iterator(null)).seekExact(term, true)) {
            return termsEnum.docs(liveDocs, null, flags);
        }
        return null;
    }

    public static DocsAndPositionsEnum getTermPositionsEnum(IndexReader r, Bits liveDocs, String field, BytesRef term) throws IOException {
        return MultiFields.getTermPositionsEnum(r, liveDocs, field, term, 3);
    }

    public static DocsAndPositionsEnum getTermPositionsEnum(IndexReader r, Bits liveDocs, String field, BytesRef term, int flags) throws IOException {
        TermsEnum termsEnum;
        assert (field != null);
        assert (term != null);
        Terms terms = MultiFields.getTerms(r, field);
        if (terms != null && (termsEnum = terms.iterator(null)).seekExact(term, true)) {
            return termsEnum.docsAndPositions(liveDocs, null, flags);
        }
        return null;
    }

    public MultiFields(Fields[] subs, ReaderSlice[] subSlices) {
        this.subs = subs;
        this.subSlices = subSlices;
    }

    @Override
    public Iterator<String> iterator() {
        Iterator[] subIterators = new Iterator[this.subs.length];
        for (int i = 0; i < this.subs.length; ++i) {
            subIterators[i] = this.subs[i].iterator();
        }
        return new MergedIterator<String>(subIterators);
    }

    @Override
    public Terms terms(String field) throws IOException {
        Terms result = this.terms.get(field);
        if (result != null) {
            return result;
        }
        ArrayList<Terms> subs2 = new ArrayList<Terms>();
        ArrayList<ReaderSlice> slices2 = new ArrayList<ReaderSlice>();
        for (int i = 0; i < this.subs.length; ++i) {
            Terms terms = this.subs[i].terms(field);
            if (terms == null) continue;
            subs2.add(terms);
            slices2.add(this.subSlices[i]);
        }
        if (subs2.size() == 0) {
            result = null;
        } else {
            result = new MultiTerms(subs2.toArray(Terms.EMPTY_ARRAY), slices2.toArray(ReaderSlice.EMPTY_ARRAY));
            this.terms.put(field, result);
        }
        return result;
    }

    @Override
    public int size() {
        return -1;
    }

    public static FieldInfos getMergedFieldInfos(IndexReader reader) {
        FieldInfos.Builder builder = new FieldInfos.Builder();
        for (AtomicReaderContext ctx : reader.leaves()) {
            builder.add(ctx.reader().getFieldInfos());
        }
        return builder.finish();
    }

    public static Collection<String> getIndexedFields(IndexReader reader) {
        HashSet<String> fields = new HashSet<String>();
        for (FieldInfo fieldInfo : MultiFields.getMergedFieldInfos(reader)) {
            if (!fieldInfo.isIndexed()) continue;
            fields.add(fieldInfo.name);
        }
        return fields;
    }
}

