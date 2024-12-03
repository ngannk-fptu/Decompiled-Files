/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.vectorhighlight;

import org.apache.lucene.search.vectorhighlight.BaseFragListBuilder;
import org.apache.lucene.search.vectorhighlight.FieldFragList;
import org.apache.lucene.search.vectorhighlight.FieldPhraseList;
import org.apache.lucene.search.vectorhighlight.SimpleFieldFragList;

public class SimpleFragListBuilder
extends BaseFragListBuilder {
    public SimpleFragListBuilder() {
    }

    public SimpleFragListBuilder(int margin) {
        super(margin);
    }

    @Override
    public FieldFragList createFieldFragList(FieldPhraseList fieldPhraseList, int fragCharSize) {
        return this.createFieldFragList(fieldPhraseList, new SimpleFieldFragList(fragCharSize), fragCharSize);
    }
}

