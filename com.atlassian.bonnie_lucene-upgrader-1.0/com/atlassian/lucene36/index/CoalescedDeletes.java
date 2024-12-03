/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.BufferedDeletes;
import com.atlassian.lucene36.index.BufferedDeletesStream;
import com.atlassian.lucene36.index.FrozenBufferedDeletes;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.util.PriorityQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
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
                ArrayList<Iterator<Term>> subs = new ArrayList<Iterator<Term>>(CoalescedDeletes.this.iterables.size());
                for (Iterable<Term> iterable : CoalescedDeletes.this.iterables) {
                    subs.add(iterable.iterator());
                }
                return CoalescedDeletes.mergedIterator(subs);
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

    static Iterator<Term> mergedIterator(final List<Iterator<Term>> iterators) {
        return new Iterator<Term>(){
            Term current;
            TermMergeQueue queue;
            SubIterator[] top;
            int numTop;
            {
                this.queue = new TermMergeQueue(iterators.size());
                this.top = new SubIterator[iterators.size()];
                int index = 0;
                for (Iterator iterator : iterators) {
                    if (!iterator.hasNext()) continue;
                    SubIterator sub = new SubIterator();
                    sub.current = (Term)iterator.next();
                    sub.iterator = iterator;
                    sub.index = index++;
                    this.queue.add(sub);
                }
            }

            @Override
            public boolean hasNext() {
                if (this.queue.size() > 0) {
                    return true;
                }
                for (int i = 0; i < this.numTop; ++i) {
                    if (!this.top[i].iterator.hasNext()) continue;
                    return true;
                }
                return false;
            }

            @Override
            public Term next() {
                this.pushTop();
                if (this.queue.size() > 0) {
                    this.pullTop();
                } else {
                    this.current = null;
                }
                return this.current;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            private void pullTop() {
                assert (this.numTop == 0);
                do {
                    this.top[this.numTop++] = (SubIterator)this.queue.pop();
                } while (this.queue.size() != 0 && ((SubIterator)this.queue.top()).current.equals(this.top[0].current));
                this.current = this.top[0].current;
            }

            private void pushTop() {
                for (int i = 0; i < this.numTop; ++i) {
                    if (this.top[i].iterator.hasNext()) {
                        this.top[i].current = this.top[i].iterator.next();
                        this.queue.add(this.top[i]);
                        continue;
                    }
                    this.top[i].current = null;
                }
                this.numTop = 0;
            }
        };
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class TermMergeQueue
    extends PriorityQueue<SubIterator> {
        TermMergeQueue(int size) {
            this.initialize(size);
        }

        @Override
        protected boolean lessThan(SubIterator a, SubIterator b) {
            int cmp = a.current.compareTo(b.current);
            if (cmp != 0) {
                return cmp < 0;
            }
            return a.index < b.index;
        }
    }

    private static class SubIterator {
        Iterator<Term> iterator;
        Term current;
        int index;

        private SubIterator() {
        }
    }
}

