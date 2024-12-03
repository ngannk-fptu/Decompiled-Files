/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.lucene.index.BufferedDeletes;
import org.apache.lucene.index.BufferedDeletesStream;
import org.apache.lucene.index.FrozenBufferedDeletes;
import org.apache.lucene.index.MergedIterator;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;

class CoalescedDeletes {
    final Map<Query, Integer> queries = new HashMap<Query, Integer>();
    final List<Iterable<Term>> iterables = new ArrayList<Iterable<Term>>();

    CoalescedDeletes() {
    }

    public String toString() {
        return "CoalescedDeletes(termSets=" + this.iterables.size() + ",queries=" + this.queries.size() + ")";
    }

    void update(FrozenBufferedDeletes in) {
        this.iterables.add(in.termsIterable());
        for (int queryIdx = 0; queryIdx < in.queries.length; ++queryIdx) {
            Query query = in.queries[queryIdx];
            this.queries.put(query, BufferedDeletes.MAX_INT);
        }
    }

    public Iterable<Term> termsIterable() {
        return new Iterable<Term>(){

            @Override
            public Iterator<Term> iterator() {
                Iterator[] subs = new Iterator[CoalescedDeletes.this.iterables.size()];
                for (int i = 0; i < CoalescedDeletes.this.iterables.size(); ++i) {
                    subs[i] = CoalescedDeletes.this.iterables.get(i).iterator();
                }
                return new MergedIterator<Term>(subs);
            }
        };
    }

    public Iterable<BufferedDeletesStream.QueryAndLimit> queriesIterable() {
        return new Iterable<BufferedDeletesStream.QueryAndLimit>(){

            @Override
            public Iterator<BufferedDeletesStream.QueryAndLimit> iterator() {
                return new Iterator<BufferedDeletesStream.QueryAndLimit>(){
                    private final Iterator<Map.Entry<Query, Integer>> iter;
                    {
                        this.iter = CoalescedDeletes.this.queries.entrySet().iterator();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }

                    @Override
                    public BufferedDeletesStream.QueryAndLimit next() {
                        Map.Entry<Query, Integer> ent = this.iter.next();
                        return new BufferedDeletesStream.QueryAndLimit(ent.getKey(), ent.getValue());
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
}

