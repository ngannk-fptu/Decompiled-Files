/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.analysis.tokenattributes.TypeAttribute
 *  org.apache.lucene.util.AttributeSource$State
 */
package org.apache.lucene.analysis.commongrams;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.commongrams.CommonGramsFilter;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeSource;

public final class CommonGramsQueryFilter
extends TokenFilter {
    private final TypeAttribute typeAttribute = (TypeAttribute)this.addAttribute(TypeAttribute.class);
    private final PositionIncrementAttribute posIncAttribute = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
    private AttributeSource.State previous;
    private String previousType;
    private boolean exhausted;

    public CommonGramsQueryFilter(CommonGramsFilter input) {
        super((TokenStream)input);
    }

    public void reset() throws IOException {
        super.reset();
        this.previous = null;
        this.previousType = null;
        this.exhausted = false;
    }

    public boolean incrementToken() throws IOException {
        while (!this.exhausted && this.input.incrementToken()) {
            AttributeSource.State current = this.captureState();
            if (this.previous != null && !this.isGramType()) {
                this.restoreState(this.previous);
                this.previous = current;
                this.previousType = this.typeAttribute.type();
                if (this.isGramType()) {
                    this.posIncAttribute.setPositionIncrement(1);
                }
                return true;
            }
            this.previous = current;
        }
        this.exhausted = true;
        if (this.previous == null || "gram".equals(this.previousType)) {
            return false;
        }
        this.restoreState(this.previous);
        this.previous = null;
        if (this.isGramType()) {
            this.posIncAttribute.setPositionIncrement(1);
        }
        return true;
    }

    public boolean isGramType() {
        return "gram".equals(this.typeAttribute.type());
    }
}

