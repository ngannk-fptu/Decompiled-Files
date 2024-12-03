/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 */
package org.apache.lucene.search.highlight;

import java.util.List;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.PositionSpan;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.WeightedSpanTerm;

public class SimpleSpanFragmenter
implements Fragmenter {
    private static final int DEFAULT_FRAGMENT_SIZE = 100;
    private int fragmentSize;
    private int currentNumFrags;
    private int position = -1;
    private QueryScorer queryScorer;
    private int waitForPos = -1;
    private int textSize;
    private CharTermAttribute termAtt;
    private PositionIncrementAttribute posIncAtt;
    private OffsetAttribute offsetAtt;

    public SimpleSpanFragmenter(QueryScorer queryScorer) {
        this(queryScorer, 100);
    }

    public SimpleSpanFragmenter(QueryScorer queryScorer, int fragmentSize) {
        this.fragmentSize = fragmentSize;
        this.queryScorer = queryScorer;
    }

    @Override
    public boolean isNewFragment() {
        boolean isNewFrag;
        this.position += this.posIncAtt.getPositionIncrement();
        if (this.waitForPos == this.position) {
            this.waitForPos = -1;
        } else if (this.waitForPos != -1) {
            return false;
        }
        WeightedSpanTerm wSpanTerm = this.queryScorer.getWeightedSpanTerm(this.termAtt.toString());
        if (wSpanTerm != null) {
            List<PositionSpan> positionSpans = wSpanTerm.getPositionSpans();
            for (int i = 0; i < positionSpans.size(); ++i) {
                if (positionSpans.get((int)i).start != this.position) continue;
                this.waitForPos = positionSpans.get((int)i).end + 1;
                break;
            }
        }
        boolean bl = isNewFrag = this.offsetAtt.endOffset() >= this.fragmentSize * this.currentNumFrags && this.textSize - this.offsetAtt.endOffset() >= this.fragmentSize >>> 1;
        if (isNewFrag) {
            ++this.currentNumFrags;
        }
        return isNewFrag;
    }

    @Override
    public void start(String originalText, TokenStream tokenStream) {
        this.position = -1;
        this.currentNumFrags = 1;
        this.textSize = originalText.length();
        this.termAtt = (CharTermAttribute)tokenStream.addAttribute(CharTermAttribute.class);
        this.posIncAtt = (PositionIncrementAttribute)tokenStream.addAttribute(PositionIncrementAttribute.class);
        this.offsetAtt = (OffsetAttribute)tokenStream.addAttribute(OffsetAttribute.class);
    }
}

