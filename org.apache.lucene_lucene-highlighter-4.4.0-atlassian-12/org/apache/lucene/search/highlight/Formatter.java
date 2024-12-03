/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.highlight;

import org.apache.lucene.search.highlight.TokenGroup;

public interface Formatter {
    public String highlightTerm(String var1, TokenGroup var2);
}

