/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.util.AttributeSource;

public abstract class DocsEnum
extends DocIdSetIterator {
    public static final int FLAG_NONE = 0;
    public static final int FLAG_FREQS = 1;
    private AttributeSource atts = null;

    protected DocsEnum() {
    }

    public abstract int freq() throws IOException;

    public AttributeSource attributes() {
        if (this.atts == null) {
            this.atts = new AttributeSource();
        }
        return this.atts;
    }
}

