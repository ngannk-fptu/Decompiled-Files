/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 */
package org.apache.lucene.search.highlight;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.search.highlight.Fragmenter;

public class SimpleFragmenter
implements Fragmenter {
    private static final int DEFAULT_FRAGMENT_SIZE = 100;
    private int currentNumFrags;
    private int fragmentSize;
    private OffsetAttribute offsetAtt;

    public SimpleFragmenter() {
        this(100);
    }

    public SimpleFragmenter(int fragmentSize) {
        this.fragmentSize = fragmentSize;
    }

    @Override
    public void start(String originalText, TokenStream stream) {
        this.offsetAtt = (OffsetAttribute)stream.addAttribute(OffsetAttribute.class);
        this.currentNumFrags = 1;
    }

    @Override
    public boolean isNewFragment() {
        boolean isNewFrag;
        boolean bl = isNewFrag = this.offsetAtt.endOffset() >= this.fragmentSize * this.currentNumFrags;
        if (isNewFrag) {
            ++this.currentNumFrags;
        }
        return isNewFrag;
    }

    public int getFragmentSize() {
        return this.fragmentSize;
    }

    public void setFragmentSize(int size) {
        this.fragmentSize = size;
    }
}

