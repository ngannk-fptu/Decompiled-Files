/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.FilteredTermsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermState;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.BoostAttribute;
import org.apache.lucene.search.MaxNonCompetitiveBoostAttribute;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.UnicodeUtil;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.BasicAutomata;
import org.apache.lucene.util.automaton.BasicOperations;
import org.apache.lucene.util.automaton.ByteRunAutomaton;
import org.apache.lucene.util.automaton.CompiledAutomaton;
import org.apache.lucene.util.automaton.LevenshteinAutomata;

public class FuzzyTermsEnum
extends TermsEnum {
    private TermsEnum actualEnum;
    private BoostAttribute actualBoostAtt;
    private final BoostAttribute boostAtt = this.attributes().addAttribute(BoostAttribute.class);
    private final MaxNonCompetitiveBoostAttribute maxBoostAtt;
    private final LevenshteinAutomataAttribute dfaAtt;
    private float bottom;
    private BytesRef bottomTerm;
    private final Comparator<BytesRef> termComparator = BytesRef.getUTF8SortedAsUnicodeComparator();
    protected final float minSimilarity;
    protected final float scale_factor;
    protected final int termLength;
    protected int maxEdits;
    protected final boolean raw;
    protected final Terms terms;
    private final Term term;
    protected final int[] termText;
    protected final int realPrefixLength;
    private final boolean transpositions;
    private BytesRef queuedBottom = null;

    public FuzzyTermsEnum(Terms terms, AttributeSource atts, Term term, float minSimilarity, int prefixLength, boolean transpositions) throws IOException {
        int cp;
        if (minSimilarity >= 1.0f && minSimilarity != (float)((int)minSimilarity)) {
            throw new IllegalArgumentException("fractional edit distances are not allowed");
        }
        if (minSimilarity < 0.0f) {
            throw new IllegalArgumentException("minimumSimilarity cannot be less than 0");
        }
        if (prefixLength < 0) {
            throw new IllegalArgumentException("prefixLength cannot be less than 0");
        }
        this.terms = terms;
        this.term = term;
        String utf16 = term.text();
        this.termText = new int[utf16.codePointCount(0, utf16.length())];
        int j = 0;
        for (int i = 0; i < utf16.length(); i += Character.charCount(cp)) {
            this.termText[j++] = cp = utf16.codePointAt(i);
        }
        this.termLength = this.termText.length;
        this.dfaAtt = atts.addAttribute(LevenshteinAutomataAttribute.class);
        int n = this.realPrefixLength = prefixLength > this.termLength ? this.termLength : prefixLength;
        if (minSimilarity >= 1.0f) {
            this.minSimilarity = 0.0f;
            this.maxEdits = (int)minSimilarity;
            this.raw = true;
        } else {
            this.minSimilarity = minSimilarity;
            this.maxEdits = this.initialMaxDistance(this.minSimilarity, this.termLength);
            this.raw = false;
        }
        if (transpositions && this.maxEdits > 2) {
            throw new UnsupportedOperationException("with transpositions enabled, distances > 2 are not supported ");
        }
        this.transpositions = transpositions;
        this.scale_factor = 1.0f / (1.0f - this.minSimilarity);
        this.maxBoostAtt = atts.addAttribute(MaxNonCompetitiveBoostAttribute.class);
        this.bottom = this.maxBoostAtt.getMaxNonCompetitiveBoost();
        this.bottomTerm = this.maxBoostAtt.getCompetitiveTerm();
        this.bottomChanged(null, true);
    }

    protected TermsEnum getAutomatonEnum(int editDistance, BytesRef lastTerm) throws IOException {
        List<CompiledAutomaton> runAutomata = this.initAutomata(editDistance);
        if (editDistance < runAutomata.size()) {
            CompiledAutomaton compiled = runAutomata.get(editDistance);
            return new AutomatonFuzzyTermsEnum(this.terms.intersect(compiled, lastTerm == null ? null : compiled.floor(lastTerm, new BytesRef())), runAutomata.subList(0, editDistance + 1).toArray(new CompiledAutomaton[editDistance + 1]));
        }
        return null;
    }

    private List<CompiledAutomaton> initAutomata(int maxDistance) {
        List<CompiledAutomaton> runAutomata = this.dfaAtt.automata();
        if (runAutomata.size() <= maxDistance && maxDistance <= 2) {
            LevenshteinAutomata builder = new LevenshteinAutomata(UnicodeUtil.newString(this.termText, this.realPrefixLength, this.termText.length - this.realPrefixLength), this.transpositions);
            for (int i = runAutomata.size(); i <= maxDistance; ++i) {
                Automaton a = builder.toAutomaton(i);
                if (this.realPrefixLength > 0) {
                    Automaton prefix = BasicAutomata.makeString(UnicodeUtil.newString(this.termText, 0, this.realPrefixLength));
                    a = BasicOperations.concatenate(prefix, a);
                }
                runAutomata.add(new CompiledAutomaton(a, true, false));
            }
        }
        return runAutomata;
    }

    protected void setEnum(TermsEnum actualEnum) {
        this.actualEnum = actualEnum;
        this.actualBoostAtt = actualEnum.attributes().addAttribute(BoostAttribute.class);
    }

    private void bottomChanged(BytesRef lastTerm, boolean init) throws IOException {
        boolean termAfter;
        int oldMaxEdits = this.maxEdits;
        boolean bl = termAfter = this.bottomTerm == null || lastTerm != null && this.termComparator.compare(lastTerm, this.bottomTerm) >= 0;
        while (this.maxEdits > 0 && (termAfter ? this.bottom >= this.calculateMaxBoost(this.maxEdits) : this.bottom > this.calculateMaxBoost(this.maxEdits))) {
            --this.maxEdits;
        }
        if (oldMaxEdits != this.maxEdits || init) {
            this.maxEditDistanceChanged(lastTerm, this.maxEdits, init);
        }
    }

    protected void maxEditDistanceChanged(BytesRef lastTerm, int maxEdits, boolean init) throws IOException {
        TermsEnum newEnum = this.getAutomatonEnum(maxEdits, lastTerm);
        if (newEnum == null) {
            assert (maxEdits > 2);
            throw new IllegalArgumentException("maxEdits cannot be > LevenshteinAutomata.MAXIMUM_SUPPORTED_DISTANCE");
        }
        this.setEnum(newEnum);
    }

    private int initialMaxDistance(float minimumSimilarity, int termLen) {
        return (int)((1.0 - (double)minimumSimilarity) * (double)termLen);
    }

    private float calculateMaxBoost(int nEdits) {
        float similarity = 1.0f - (float)nEdits / (float)this.termLength;
        return (similarity - this.minSimilarity) * this.scale_factor;
    }

    @Override
    public BytesRef next() throws IOException {
        if (this.queuedBottom != null) {
            this.bottomChanged(this.queuedBottom, false);
            this.queuedBottom = null;
        }
        BytesRef term = this.actualEnum.next();
        this.boostAtt.setBoost(this.actualBoostAtt.getBoost());
        float bottom = this.maxBoostAtt.getMaxNonCompetitiveBoost();
        BytesRef bottomTerm = this.maxBoostAtt.getCompetitiveTerm();
        if (term != null && (bottom != this.bottom || bottomTerm != this.bottomTerm)) {
            this.bottom = bottom;
            this.bottomTerm = bottomTerm;
            this.queuedBottom = BytesRef.deepCopyOf(term);
        }
        return term;
    }

    @Override
    public int docFreq() throws IOException {
        return this.actualEnum.docFreq();
    }

    @Override
    public long totalTermFreq() throws IOException {
        return this.actualEnum.totalTermFreq();
    }

    @Override
    public DocsEnum docs(Bits liveDocs, DocsEnum reuse, int flags) throws IOException {
        return this.actualEnum.docs(liveDocs, reuse, flags);
    }

    @Override
    public DocsAndPositionsEnum docsAndPositions(Bits liveDocs, DocsAndPositionsEnum reuse, int flags) throws IOException {
        return this.actualEnum.docsAndPositions(liveDocs, reuse, flags);
    }

    @Override
    public void seekExact(BytesRef term, TermState state) throws IOException {
        this.actualEnum.seekExact(term, state);
    }

    @Override
    public TermState termState() throws IOException {
        return this.actualEnum.termState();
    }

    @Override
    public Comparator<BytesRef> getComparator() {
        return this.actualEnum.getComparator();
    }

    @Override
    public long ord() throws IOException {
        return this.actualEnum.ord();
    }

    @Override
    public boolean seekExact(BytesRef text, boolean useCache) throws IOException {
        return this.actualEnum.seekExact(text, useCache);
    }

    @Override
    public TermsEnum.SeekStatus seekCeil(BytesRef text, boolean useCache) throws IOException {
        return this.actualEnum.seekCeil(text, useCache);
    }

    @Override
    public void seekExact(long ord) throws IOException {
        this.actualEnum.seekExact(ord);
    }

    @Override
    public BytesRef term() throws IOException {
        return this.actualEnum.term();
    }

    public float getMinSimilarity() {
        return this.minSimilarity;
    }

    public float getScaleFactor() {
        return this.scale_factor;
    }

    public static final class LevenshteinAutomataAttributeImpl
    extends AttributeImpl
    implements LevenshteinAutomataAttribute {
        private final List<CompiledAutomaton> automata = new ArrayList<CompiledAutomaton>();

        @Override
        public List<CompiledAutomaton> automata() {
            return this.automata;
        }

        @Override
        public void clear() {
            this.automata.clear();
        }

        public int hashCode() {
            return this.automata.hashCode();
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof LevenshteinAutomataAttributeImpl)) {
                return false;
            }
            return this.automata.equals(((LevenshteinAutomataAttributeImpl)other).automata);
        }

        @Override
        public void copyTo(AttributeImpl target) {
            List<CompiledAutomaton> targetAutomata = ((LevenshteinAutomataAttribute)((Object)target)).automata();
            targetAutomata.clear();
            targetAutomata.addAll(this.automata);
        }
    }

    public static interface LevenshteinAutomataAttribute
    extends Attribute {
        public List<CompiledAutomaton> automata();
    }

    private class AutomatonFuzzyTermsEnum
    extends FilteredTermsEnum {
        private final ByteRunAutomaton[] matchers;
        private final BytesRef termRef;
        private final BoostAttribute boostAtt;

        public AutomatonFuzzyTermsEnum(TermsEnum tenum, CompiledAutomaton[] compiled) {
            super(tenum, false);
            this.boostAtt = this.attributes().addAttribute(BoostAttribute.class);
            this.matchers = new ByteRunAutomaton[compiled.length];
            for (int i = 0; i < compiled.length; ++i) {
                this.matchers[i] = compiled[i].runAutomaton;
            }
            this.termRef = new BytesRef(FuzzyTermsEnum.this.term.text());
        }

        @Override
        protected FilteredTermsEnum.AcceptStatus accept(BytesRef term) {
            int ed;
            for (ed = this.matchers.length - 1; ed > 0 && this.matches(term, ed - 1); --ed) {
            }
            if (ed == 0) {
                this.boostAtt.setBoost(1.0f);
                return FilteredTermsEnum.AcceptStatus.YES;
            }
            int codePointCount = UnicodeUtil.codePointCount(term);
            float similarity = 1.0f - (float)ed / (float)Math.min(codePointCount, FuzzyTermsEnum.this.termLength);
            if (similarity > FuzzyTermsEnum.this.minSimilarity) {
                this.boostAtt.setBoost((similarity - FuzzyTermsEnum.this.minSimilarity) * FuzzyTermsEnum.this.scale_factor);
                return FilteredTermsEnum.AcceptStatus.YES;
            }
            return FilteredTermsEnum.AcceptStatus.NO;
        }

        final boolean matches(BytesRef term, int k) {
            return k == 0 ? term.equals(this.termRef) : this.matchers[k].run(term.bytes, term.offset, term.length);
        }
    }
}

