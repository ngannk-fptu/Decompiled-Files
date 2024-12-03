/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.PhrasePositions;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PhraseQueue;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.OpenBitSet;

final class SloppyPhraseScorer
extends Scorer {
    private PhrasePositions min;
    private PhrasePositions max;
    private float sloppyFreq;
    private final Similarity.SimScorer docScorer;
    private final int slop;
    private final int numPostings;
    private final PhraseQueue pq;
    private int end;
    private boolean hasRpts;
    private boolean checkedRpts;
    private boolean hasMultiTermRpts;
    private PhrasePositions[][] rptGroups;
    private PhrasePositions[] rptStack;
    private int numMatches;
    private final long cost;

    SloppyPhraseScorer(Weight weight, PhraseQuery.PostingsAndFreq[] postings, int slop, Similarity.SimScorer docScorer) {
        super(weight);
        this.docScorer = docScorer;
        this.slop = slop;
        this.numPostings = postings == null ? 0 : postings.length;
        this.pq = new PhraseQueue(postings.length);
        this.cost = postings[0].postings.cost();
        if (postings.length > 0) {
            this.max = this.min = new PhrasePositions(postings[0].postings, postings[0].position, 0, postings[0].terms);
            this.max.doc = -1;
            for (int i = 1; i < postings.length; ++i) {
                PhrasePositions pp;
                this.max.next = pp = new PhrasePositions(postings[i].postings, postings[i].position, i, postings[i].terms);
                this.max = pp;
                this.max.doc = -1;
            }
            this.max.next = this.min;
        }
    }

    private float phraseFreq() throws IOException {
        if (!this.initPhrasePositions()) {
            return 0.0f;
        }
        float freq = 0.0f;
        this.numMatches = 0;
        PhrasePositions pp = (PhrasePositions)this.pq.pop();
        int matchLength = this.end - pp.position;
        int next = ((PhrasePositions)this.pq.top()).position;
        while (this.advancePP(pp) && (!this.hasRpts || this.advanceRpts(pp))) {
            if (pp.position > next) {
                if (matchLength <= this.slop) {
                    freq += this.docScorer.computeSlopFactor(matchLength);
                    ++this.numMatches;
                }
                this.pq.add(pp);
                pp = (PhrasePositions)this.pq.pop();
                next = ((PhrasePositions)this.pq.top()).position;
                matchLength = this.end - pp.position;
                continue;
            }
            int matchLength2 = this.end - pp.position;
            if (matchLength2 >= matchLength) continue;
            matchLength = matchLength2;
        }
        if (matchLength <= this.slop) {
            freq += this.docScorer.computeSlopFactor(matchLength);
            ++this.numMatches;
        }
        return freq;
    }

    private boolean advancePP(PhrasePositions pp) throws IOException {
        if (!pp.nextPosition()) {
            return false;
        }
        if (pp.position > this.end) {
            this.end = pp.position;
        }
        return true;
    }

    private boolean advanceRpts(PhrasePositions pp) throws IOException {
        int k;
        if (pp.rptGroup < 0) {
            return true;
        }
        PhrasePositions[] rg = this.rptGroups[pp.rptGroup];
        OpenBitSet bits = new OpenBitSet(rg.length);
        int k0 = pp.rptInd;
        while ((k = this.collide(pp)) >= 0) {
            if (!this.advancePP(pp = this.lesser(pp, rg[k]))) {
                return false;
            }
            if (k == k0) continue;
            bits.set(k);
        }
        int n = 0;
        while (bits.cardinality() > 0L) {
            PhrasePositions pp2 = (PhrasePositions)this.pq.pop();
            this.rptStack[n++] = pp2;
            if (pp2.rptGroup < 0 || !bits.get(pp2.rptInd)) continue;
            bits.clear(pp2.rptInd);
        }
        for (int i = n - 1; i >= 0; --i) {
            this.pq.add(this.rptStack[i]);
        }
        return true;
    }

    private PhrasePositions lesser(PhrasePositions pp, PhrasePositions pp2) {
        if (pp.position < pp2.position || pp.position == pp2.position && pp.offset < pp2.offset) {
            return pp;
        }
        return pp2;
    }

    private int collide(PhrasePositions pp) {
        int tpPos = this.tpPos(pp);
        PhrasePositions[] rg = this.rptGroups[pp.rptGroup];
        for (int i = 0; i < rg.length; ++i) {
            PhrasePositions pp2 = rg[i];
            if (pp2 == pp || this.tpPos(pp2) != tpPos) continue;
            return pp2.rptInd;
        }
        return -1;
    }

    private boolean initPhrasePositions() throws IOException {
        this.end = Integer.MIN_VALUE;
        if (!this.checkedRpts) {
            return this.initFirstTime();
        }
        if (!this.hasRpts) {
            this.initSimple();
            return true;
        }
        return this.initComplex();
    }

    private void initSimple() throws IOException {
        this.pq.clear();
        PhrasePositions pp = this.min;
        PhrasePositions prev = null;
        while (prev != this.max) {
            pp.firstPosition();
            if (pp.position > this.end) {
                this.end = pp.position;
            }
            this.pq.add(pp);
            prev = pp;
            pp = prev.next;
        }
    }

    private boolean initComplex() throws IOException {
        this.placeFirstPositions();
        if (!this.advanceRepeatGroups()) {
            return false;
        }
        this.fillQueue();
        return true;
    }

    private void placeFirstPositions() throws IOException {
        PhrasePositions pp = this.min;
        PhrasePositions prev = null;
        while (prev != this.max) {
            pp.firstPosition();
            prev = pp;
            pp = prev.next;
        }
    }

    private void fillQueue() {
        this.pq.clear();
        PhrasePositions pp = this.min;
        PhrasePositions prev = null;
        while (prev != this.max) {
            if (pp.position > this.end) {
                this.end = pp.position;
            }
            this.pq.add(pp);
            prev = pp;
            pp = prev.next;
        }
    }

    private boolean advanceRepeatGroups() throws IOException {
        for (PhrasePositions[] rg : this.rptGroups) {
            if (this.hasMultiTermRpts) {
                int incr;
                block1: for (int i = 0; i < rg.length; i += incr) {
                    int k;
                    incr = 1;
                    PhrasePositions pp = rg[i];
                    while ((k = this.collide(pp)) >= 0) {
                        PhrasePositions pp2 = this.lesser(pp, rg[k]);
                        if (!this.advancePP(pp2)) {
                            return false;
                        }
                        if (pp2.rptInd >= i) continue;
                        incr = 0;
                        continue block1;
                    }
                }
                continue;
            }
            for (int j = 1; j < rg.length; ++j) {
                for (int k = 0; k < j; ++k) {
                    if (rg[j].nextPosition()) continue;
                    return false;
                }
            }
        }
        return true;
    }

    private boolean initFirstTime() throws IOException {
        this.checkedRpts = true;
        this.placeFirstPositions();
        LinkedHashMap<Term, Integer> rptTerms = this.repeatingTerms();
        boolean bl = this.hasRpts = !rptTerms.isEmpty();
        if (this.hasRpts) {
            this.rptStack = new PhrasePositions[this.numPostings];
            ArrayList<ArrayList<PhrasePositions>> rgs = this.gatherRptGroups(rptTerms);
            this.sortRptGroups(rgs);
            if (!this.advanceRepeatGroups()) {
                return false;
            }
        }
        this.fillQueue();
        return true;
    }

    private void sortRptGroups(ArrayList<ArrayList<PhrasePositions>> rgs) {
        this.rptGroups = new PhrasePositions[rgs.size()][];
        Comparator<PhrasePositions> cmprtr = new Comparator<PhrasePositions>(){

            @Override
            public int compare(PhrasePositions pp1, PhrasePositions pp2) {
                return pp1.offset - pp2.offset;
            }
        };
        for (int i = 0; i < this.rptGroups.length; ++i) {
            PhrasePositions[] rg = rgs.get(i).toArray(new PhrasePositions[0]);
            Arrays.sort(rg, cmprtr);
            this.rptGroups[i] = rg;
            for (int j = 0; j < rg.length; ++j) {
                rg[j].rptInd = j;
            }
        }
    }

    private ArrayList<ArrayList<PhrasePositions>> gatherRptGroups(LinkedHashMap<Term, Integer> rptTerms) throws IOException {
        PhrasePositions[] rpp = this.repeatingPPs(rptTerms);
        ArrayList<ArrayList<PhrasePositions>> res = new ArrayList<ArrayList<PhrasePositions>>();
        if (!this.hasMultiTermRpts) {
            for (int i = 0; i < rpp.length; ++i) {
                PhrasePositions pp = rpp[i];
                if (pp.rptGroup >= 0) continue;
                int tpPos = this.tpPos(pp);
                for (int j = i + 1; j < rpp.length; ++j) {
                    PhrasePositions pp2 = rpp[j];
                    if (pp2.rptGroup >= 0 || pp2.offset == pp.offset || this.tpPos(pp2) != tpPos) continue;
                    int n = pp.rptGroup;
                    if (n < 0) {
                        pp.rptGroup = n = res.size();
                        ArrayList<PhrasePositions> rl = new ArrayList<PhrasePositions>(2);
                        rl.add(pp);
                        res.add(rl);
                    }
                    pp2.rptGroup = n;
                    res.get(n).add(pp2);
                }
            }
        } else {
            ArrayList tmp = new ArrayList();
            ArrayList<OpenBitSet> bb = this.ppTermsBitSets(rpp, rptTerms);
            this.unionTermGroups(bb);
            HashMap<Term, Integer> tg = this.termGroups(rptTerms, bb);
            HashSet<Integer> distinctGroupIDs = new HashSet<Integer>(tg.values());
            for (int i = 0; i < distinctGroupIDs.size(); ++i) {
                tmp.add(new HashSet());
            }
            for (PhrasePositions pp : rpp) {
                for (Term t : pp.terms) {
                    if (!rptTerms.containsKey(t)) continue;
                    int g = tg.get(t);
                    ((HashSet)tmp.get(g)).add(pp);
                    assert (pp.rptGroup == -1 || pp.rptGroup == g);
                    pp.rptGroup = g;
                }
            }
            for (HashSet hashSet : tmp) {
                res.add(new ArrayList(hashSet));
            }
        }
        return res;
    }

    private final int tpPos(PhrasePositions pp) {
        return pp.position + pp.offset;
    }

    private LinkedHashMap<Term, Integer> repeatingTerms() {
        LinkedHashMap<Term, Integer> tord = new LinkedHashMap<Term, Integer>();
        HashMap<Term, Integer> tcnt = new HashMap<Term, Integer>();
        PhrasePositions pp = this.min;
        PhrasePositions prev = null;
        while (prev != this.max) {
            for (Term t : pp.terms) {
                Integer cnt0 = (Integer)tcnt.get(t);
                Integer cnt = cnt0 == null ? new Integer(1) : new Integer(1 + cnt0);
                tcnt.put(t, cnt);
                if (cnt != 2) continue;
                tord.put(t, tord.size());
            }
            prev = pp;
            pp = prev.next;
        }
        return tord;
    }

    private PhrasePositions[] repeatingPPs(HashMap<Term, Integer> rptTerms) {
        ArrayList<PhrasePositions> rp = new ArrayList<PhrasePositions>();
        PhrasePositions pp = this.min;
        PhrasePositions prev = null;
        while (prev != this.max) {
            for (Term t : pp.terms) {
                if (!rptTerms.containsKey(t)) continue;
                rp.add(pp);
                this.hasMultiTermRpts |= pp.terms.length > 1;
                break;
            }
            prev = pp;
            pp = prev.next;
        }
        return rp.toArray(new PhrasePositions[0]);
    }

    private ArrayList<OpenBitSet> ppTermsBitSets(PhrasePositions[] rpp, HashMap<Term, Integer> tord) {
        ArrayList<OpenBitSet> bb = new ArrayList<OpenBitSet>(rpp.length);
        for (PhrasePositions pp : rpp) {
            OpenBitSet b = new OpenBitSet(tord.size());
            for (Term t : pp.terms) {
                Integer ord = tord.get(t);
                if (ord == null) continue;
                b.set(ord.intValue());
            }
            bb.add(b);
        }
        return bb;
    }

    private void unionTermGroups(ArrayList<OpenBitSet> bb) {
        int incr;
        for (int i = 0; i < bb.size() - 1; i += incr) {
            incr = 1;
            int j = i + 1;
            while (j < bb.size()) {
                if (bb.get(i).intersects(bb.get(j))) {
                    bb.get(i).union(bb.get(j));
                    bb.remove(j);
                    incr = 0;
                    continue;
                }
                ++j;
            }
        }
    }

    private HashMap<Term, Integer> termGroups(LinkedHashMap<Term, Integer> tord, ArrayList<OpenBitSet> bb) throws IOException {
        HashMap<Term, Integer> tg = new HashMap<Term, Integer>();
        Term[] t = tord.keySet().toArray(new Term[0]);
        for (int i = 0; i < bb.size(); ++i) {
            int ord;
            DocIdSetIterator bits = bb.get(i).iterator();
            while ((ord = bits.nextDoc()) != Integer.MAX_VALUE) {
                tg.put(t[ord], i);
            }
        }
        return tg;
    }

    @Override
    public int freq() {
        return this.numMatches;
    }

    float sloppyFreq() {
        return this.sloppyFreq;
    }

    private boolean advanceMin(int target) throws IOException {
        if (!this.min.skipTo(target)) {
            this.max.doc = Integer.MAX_VALUE;
            return false;
        }
        this.min = this.min.next;
        this.max = this.max.next;
        return true;
    }

    @Override
    public int docID() {
        return this.max.doc;
    }

    @Override
    public int nextDoc() throws IOException {
        return this.advance(this.max.doc + 1);
    }

    @Override
    public float score() {
        return this.docScorer.score(this.max.doc, this.sloppyFreq);
    }

    @Override
    public int advance(int target) throws IOException {
        assert (target > this.docID());
        do {
            if (!this.advanceMin(target)) {
                return Integer.MAX_VALUE;
            }
            while (this.min.doc < this.max.doc) {
                if (this.advanceMin(this.max.doc)) continue;
                return Integer.MAX_VALUE;
            }
            this.sloppyFreq = this.phraseFreq();
            target = this.min.doc + 1;
        } while (this.sloppyFreq == 0.0f);
        return this.max.doc;
    }

    @Override
    public long cost() {
        return this.cost;
    }

    public String toString() {
        return "scorer(" + this.weight + ")";
    }
}

