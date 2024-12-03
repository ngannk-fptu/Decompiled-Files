/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReader
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.search.BitsFilteredDocIdSet
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.DocIdSet
 *  org.apache.lucene.search.DocIdSetIterator
 *  org.apache.lucene.search.Filter
 *  org.apache.lucene.util.Bits
 *  org.apache.lucene.util.FixedBitSet
 */
package org.apache.lucene.queries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.FilterClause;
import org.apache.lucene.search.BitsFilteredDocIdSet;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.FixedBitSet;

public class BooleanFilter
extends Filter
implements Iterable<FilterClause> {
    private final List<FilterClause> clauses = new ArrayList<FilterClause>();

    public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
        DocIdSetIterator disi;
        FixedBitSet res = null;
        AtomicReader reader = context.reader();
        boolean hasShouldClauses = false;
        for (FilterClause fc : this.clauses) {
            if (fc.getOccur() != BooleanClause.Occur.SHOULD) continue;
            hasShouldClauses = true;
            disi = BooleanFilter.getDISI(fc.getFilter(), context);
            if (disi == null) continue;
            if (res == null) {
                res = new FixedBitSet(reader.maxDoc());
            }
            res.or(disi);
        }
        if (hasShouldClauses && res == null) {
            return null;
        }
        for (FilterClause fc : this.clauses) {
            if (fc.getOccur() != BooleanClause.Occur.MUST_NOT) continue;
            if (res == null) {
                assert (!hasShouldClauses);
                res = new FixedBitSet(reader.maxDoc());
                res.set(0, reader.maxDoc());
            }
            if ((disi = BooleanFilter.getDISI(fc.getFilter(), context)) == null) continue;
            res.andNot(disi);
        }
        for (FilterClause fc : this.clauses) {
            if (fc.getOccur() != BooleanClause.Occur.MUST) continue;
            disi = BooleanFilter.getDISI(fc.getFilter(), context);
            if (disi == null) {
                return null;
            }
            if (res == null) {
                res = new FixedBitSet(reader.maxDoc());
                res.or(disi);
                continue;
            }
            res.and(disi);
        }
        return BitsFilteredDocIdSet.wrap(res, (Bits)acceptDocs);
    }

    private static DocIdSetIterator getDISI(Filter filter, AtomicReaderContext context) throws IOException {
        DocIdSet set = filter.getDocIdSet(context, null);
        return set == null ? null : set.iterator();
    }

    public void add(FilterClause filterClause) {
        this.clauses.add(filterClause);
    }

    public final void add(Filter filter, BooleanClause.Occur occur) {
        this.add(new FilterClause(filter, occur));
    }

    public List<FilterClause> clauses() {
        return this.clauses;
    }

    @Override
    public final Iterator<FilterClause> iterator() {
        return this.clauses().iterator();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        BooleanFilter other = (BooleanFilter)obj;
        return this.clauses.equals(other.clauses);
    }

    public int hashCode() {
        return 0x272B5EB6 ^ this.clauses.hashCode();
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder("BooleanFilter(");
        int minLen = buffer.length();
        for (FilterClause c : this.clauses) {
            if (buffer.length() > minLen) {
                buffer.append(' ');
            }
            buffer.append(c);
        }
        return buffer.append(')').toString();
    }
}

