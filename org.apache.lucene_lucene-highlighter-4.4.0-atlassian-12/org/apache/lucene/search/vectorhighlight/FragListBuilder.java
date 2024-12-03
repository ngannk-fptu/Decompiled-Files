/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.vectorhighlight;

import org.apache.lucene.search.vectorhighlight.FieldFragList;
import org.apache.lucene.search.vectorhighlight.FieldPhraseList;

public interface FragListBuilder {
    public FieldFragList createFieldFragList(FieldPhraseList var1, int var2);
}

