/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text.similarity;

import org.apache.commons.text.similarity.SimilarityScore;

public interface EditDistance<R>
extends SimilarityScore<R> {
    @Override
    public R apply(CharSequence var1, CharSequence var2);
}

