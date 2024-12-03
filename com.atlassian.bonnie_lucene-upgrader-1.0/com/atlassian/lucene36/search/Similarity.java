/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.FieldInvertState;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.DefaultSimilarity;
import com.atlassian.lucene36.search.Explanation;
import com.atlassian.lucene36.search.Searcher;
import com.atlassian.lucene36.util.SmallFloat;
import com.atlassian.lucene36.util.VirtualMethod;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class Similarity
implements Serializable {
    private static final VirtualMethod<Similarity> withoutDocFreqMethod = new VirtualMethod<Similarity>(Similarity.class, "idfExplain", Term.class, Searcher.class);
    private static final VirtualMethod<Similarity> withDocFreqMethod = new VirtualMethod<Similarity>(Similarity.class, "idfExplain", Term.class, Searcher.class, Integer.TYPE);
    private final boolean hasIDFExplainWithDocFreqAPI = VirtualMethod.compareImplementationDistance(this.getClass(), withDocFreqMethod, withoutDocFreqMethod) >= 0;
    private static Similarity defaultImpl = new DefaultSimilarity();
    public static final int NO_DOC_ID_PROVIDED = -1;
    private static final float[] NORM_TABLE = new float[256];

    public static void setDefault(Similarity similarity) {
        defaultImpl = similarity;
    }

    public static Similarity getDefault() {
        return defaultImpl;
    }

    @Deprecated
    public static float decodeNorm(byte b) {
        return NORM_TABLE[b & 0xFF];
    }

    public float decodeNormValue(byte b) {
        return NORM_TABLE[b & 0xFF];
    }

    @Deprecated
    public static float[] getNormDecoder() {
        return NORM_TABLE;
    }

    public abstract float computeNorm(String var1, FieldInvertState var2);

    @Deprecated
    public final float lengthNorm(String fieldName, int numTokens) {
        throw new UnsupportedOperationException("please use computeNorm instead");
    }

    public abstract float queryNorm(float var1);

    public byte encodeNormValue(float f) {
        return SmallFloat.floatToByte315(f);
    }

    @Deprecated
    public static byte encodeNorm(float f) {
        return SmallFloat.floatToByte315(f);
    }

    public float tf(int freq) {
        return this.tf((float)freq);
    }

    public abstract float sloppyFreq(int var1);

    public abstract float tf(float var1);

    public Explanation.IDFExplanation idfExplain(Term term, Searcher searcher, int docFreq) throws IOException {
        if (!this.hasIDFExplainWithDocFreqAPI) {
            return this.idfExplain(term, searcher);
        }
        final int df = docFreq;
        final int max = searcher.maxDoc();
        final float idf = this.idf(df, max);
        return new Explanation.IDFExplanation(){

            public String explain() {
                return "idf(docFreq=" + df + ", maxDocs=" + max + ")";
            }

            public float getIdf() {
                return idf;
            }
        };
    }

    public Explanation.IDFExplanation idfExplain(Term term, Searcher searcher) throws IOException {
        return this.idfExplain(term, searcher, searcher.docFreq(term));
    }

    public Explanation.IDFExplanation idfExplain(Collection<Term> terms, Searcher searcher) throws IOException {
        int max = searcher.maxDoc();
        float idf = 0.0f;
        final StringBuilder exp = new StringBuilder();
        for (Term term : terms) {
            int df = searcher.docFreq(term);
            idf += this.idf(df, max);
            exp.append(" ");
            exp.append(term.text());
            exp.append("=");
            exp.append(df);
        }
        final float fIdf = idf;
        return new Explanation.IDFExplanation(){

            public float getIdf() {
                return fIdf;
            }

            public String explain() {
                return exp.toString();
            }
        };
    }

    public abstract float idf(int var1, int var2);

    public abstract float coord(int var1, int var2);

    public float scorePayload(int docId, String fieldName, int start, int end, byte[] payload, int offset, int length) {
        return 1.0f;
    }

    static {
        for (int i = 0; i < 256; ++i) {
            Similarity.NORM_TABLE[i] = SmallFloat.byte315ToFloat((byte)i);
        }
    }
}

