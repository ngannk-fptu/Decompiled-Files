/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenStream;

public final class EmptyTokenStream
extends TokenStream {
    public final boolean incrementToken() {
        return false;
    }
}

