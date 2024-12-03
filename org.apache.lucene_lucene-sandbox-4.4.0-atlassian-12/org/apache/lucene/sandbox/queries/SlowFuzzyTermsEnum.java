/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.FilteredTermsEnum
 *  org.apache.lucene.index.FilteredTermsEnum$AcceptStatus
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.index.TermsEnum
 *  org.apache.lucene.search.BoostAttribute
 *  org.apache.lucene.search.FuzzyTermsEnum
 *  org.apache.lucene.util.AttributeSource
 *  org.apache.lucene.util.BytesRef
 *  org.apache.lucene.util.IntsRef
 *  org.apache.lucene.util.StringHelper
 *  org.apache.lucene.util.UnicodeUtil
 */
package org.apache.lucene.sandbox.queries;

import java.io.IOException;
import org.apache.lucene.index.FilteredTermsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.BoostAttribute;
import org.apache.lucene.search.FuzzyTermsEnum;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.StringHelper;
import org.apache.lucene.util.UnicodeUtil;

@Deprecated
public final class SlowFuzzyTermsEnum
extends FuzzyTermsEnum {
    public SlowFuzzyTermsEnum(Terms terms, AttributeSource atts, Term term, float minSimilarity, int prefixLength) throws IOException {
        super(terms, atts, term, minSimilarity, prefixLength, false);
    }

    protected void maxEditDistanceChanged(BytesRef lastTerm, int maxEdits, boolean init) throws IOException {
        TermsEnum newEnum = this.getAutomatonEnum(maxEdits, lastTerm);
        if (newEnum != null) {
            this.setEnum(newEnum);
        } else if (init) {
            this.setEnum((TermsEnum)new LinearFuzzyTermsEnum());
        }
    }

    private class LinearFuzzyTermsEnum
    extends FilteredTermsEnum {
        private int[] d;
        private int[] p;
        private final int[] text;
        private final BoostAttribute boostAtt;
        private final BytesRef prefixBytesRef;
        private final IntsRef utf32;

        public LinearFuzzyTermsEnum() throws IOException {
            super(SlowFuzzyTermsEnum.this.terms.iterator(null));
            this.boostAtt = (BoostAttribute)this.attributes().addAttribute(BoostAttribute.class);
            this.utf32 = new IntsRef(20);
            this.text = new int[SlowFuzzyTermsEnum.this.termLength - SlowFuzzyTermsEnum.this.realPrefixLength];
            System.arraycopy(SlowFuzzyTermsEnum.this.termText, SlowFuzzyTermsEnum.this.realPrefixLength, this.text, 0, this.text.length);
            String prefix = UnicodeUtil.newString((int[])SlowFuzzyTermsEnum.this.termText, (int)0, (int)SlowFuzzyTermsEnum.this.realPrefixLength);
            this.prefixBytesRef = new BytesRef((CharSequence)prefix);
            this.d = new int[this.text.length + 1];
            this.p = new int[this.text.length + 1];
            this.setInitialSeekTerm(this.prefixBytesRef);
        }

        protected final FilteredTermsEnum.AcceptStatus accept(BytesRef term) {
            if (StringHelper.startsWith((BytesRef)term, (BytesRef)this.prefixBytesRef)) {
                UnicodeUtil.UTF8toUTF32((BytesRef)term, (IntsRef)this.utf32);
                int distance = this.calcDistance(this.utf32.ints, SlowFuzzyTermsEnum.this.realPrefixLength, this.utf32.length - SlowFuzzyTermsEnum.this.realPrefixLength);
                if (distance == Integer.MIN_VALUE) {
                    return FilteredTermsEnum.AcceptStatus.NO;
                }
                if (SlowFuzzyTermsEnum.this.raw && distance > SlowFuzzyTermsEnum.this.maxEdits) {
                    return FilteredTermsEnum.AcceptStatus.NO;
                }
                float similarity = this.calcSimilarity(distance, this.utf32.length - SlowFuzzyTermsEnum.this.realPrefixLength, this.text.length);
                if (SlowFuzzyTermsEnum.this.raw || !SlowFuzzyTermsEnum.this.raw && similarity > SlowFuzzyTermsEnum.this.minSimilarity) {
                    this.boostAtt.setBoost((similarity - SlowFuzzyTermsEnum.this.minSimilarity) * SlowFuzzyTermsEnum.this.scale_factor);
                    return FilteredTermsEnum.AcceptStatus.YES;
                }
                return FilteredTermsEnum.AcceptStatus.NO;
            }
            return FilteredTermsEnum.AcceptStatus.END;
        }

        private final int calcDistance(int[] target, int offset, int length) {
            int m = length;
            int n = this.text.length;
            if (n == 0) {
                return m;
            }
            if (m == 0) {
                return n;
            }
            int maxDistance = this.calculateMaxDistance(m);
            if (maxDistance < Math.abs(m - n)) {
                return Integer.MIN_VALUE;
            }
            for (int i = 0; i <= n; ++i) {
                this.p[i] = i;
            }
            for (int j = 1; j <= m; ++j) {
                int bestPossibleEditDistance = m;
                int t_j = target[offset + j - 1];
                this.d[0] = j;
                for (int i = 1; i <= n; ++i) {
                    this.d[i] = t_j != this.text[i - 1] ? Math.min(Math.min(this.d[i - 1], this.p[i]), this.p[i - 1]) + 1 : Math.min(Math.min(this.d[i - 1] + 1, this.p[i] + 1), this.p[i - 1]);
                    bestPossibleEditDistance = Math.min(bestPossibleEditDistance, this.d[i]);
                }
                if (j > maxDistance && bestPossibleEditDistance > maxDistance) {
                    return Integer.MIN_VALUE;
                }
                int[] _d = this.p;
                this.p = this.d;
                this.d = _d;
            }
            return this.p[n];
        }

        private float calcSimilarity(int edits, int m, int n) {
            return 1.0f - (float)edits / (float)(SlowFuzzyTermsEnum.this.realPrefixLength + Math.min(n, m));
        }

        private int calculateMaxDistance(int m) {
            return SlowFuzzyTermsEnum.this.raw ? SlowFuzzyTermsEnum.this.maxEdits : Math.min(SlowFuzzyTermsEnum.this.maxEdits, (int)((1.0f - SlowFuzzyTermsEnum.this.minSimilarity) * (float)(Math.min(this.text.length, m) + SlowFuzzyTermsEnum.this.realPrefixLength)));
        }
    }
}

