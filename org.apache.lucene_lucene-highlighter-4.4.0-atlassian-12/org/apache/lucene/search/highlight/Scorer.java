/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.search.highlight;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.search.highlight.TextFragment;

public interface Scorer {
    public TokenStream init(TokenStream var1) throws IOException;

    public void startFragment(TextFragment var1);

    public float getTokenScore();

    public float getFragmentScore();
}

