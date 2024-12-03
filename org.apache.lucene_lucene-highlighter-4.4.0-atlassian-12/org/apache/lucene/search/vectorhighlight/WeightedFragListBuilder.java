/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.vectorhighlight;

import org.apache.lucene.search.vectorhighlight.BaseFragListBuilder;
import org.apache.lucene.search.vectorhighlight.FieldFragList;
import org.apache.lucene.search.vectorhighlight.FieldPhraseList;
import org.apache.lucene.search.vectorhighlight.WeightedFieldFragList;

public class WeightedFragListBuilder
extends BaseFragListBuilder {
    public WeightedFragListBuilder() {
    }

    public WeightedFragListBuilder(int margin) {
        super(margin);
    }

    @Override
    public FieldFragList createFieldFragList(FieldPhraseList fieldPhraseList, int fragCharSize) {
        return this.createFieldFragList(fieldPhraseList, new WeightedFieldFragList(fragCharSize), fragCharSize);
    }
}

