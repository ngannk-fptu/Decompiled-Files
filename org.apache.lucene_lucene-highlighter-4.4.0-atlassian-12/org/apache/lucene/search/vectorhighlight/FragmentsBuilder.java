/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.IndexReader
 */
package org.apache.lucene.search.vectorhighlight;

import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.highlight.Encoder;
import org.apache.lucene.search.vectorhighlight.FieldFragList;

public interface FragmentsBuilder {
    public String createFragment(IndexReader var1, int var2, String var3, FieldFragList var4) throws IOException;

    public String[] createFragments(IndexReader var1, int var2, String var3, FieldFragList var4, int var5) throws IOException;

    public String createFragment(IndexReader var1, int var2, String var3, FieldFragList var4, String[] var5, String[] var6, Encoder var7) throws IOException;

    public String[] createFragments(IndexReader var1, int var2, String var3, FieldFragList var4, int var5, String[] var6, String[] var7, Encoder var8) throws IOException;
}

