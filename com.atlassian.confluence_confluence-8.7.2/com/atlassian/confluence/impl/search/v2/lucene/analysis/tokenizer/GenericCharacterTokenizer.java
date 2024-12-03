/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.util.CharTokenizer
 */
package com.atlassian.confluence.impl.search.v2.lucene.analysis.tokenizer;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneConstants;
import java.io.Reader;
import java.util.Arrays;
import java.util.Objects;
import org.apache.lucene.analysis.util.CharTokenizer;

public class GenericCharacterTokenizer
extends CharTokenizer {
    private final char[] delimiters;

    public GenericCharacterTokenizer(Reader input, char[] delimiters) {
        super(LuceneConstants.LUCENE_VERSION, input);
        if (delimiters == null || delimiters.length == 0) {
            throw new IllegalArgumentException("You must specify at least one delimiter.");
        }
        this.delimiters = delimiters;
    }

    protected boolean isTokenChar(int c) {
        for (char delimiter : this.delimiters) {
            if (delimiter != c) continue;
            return false;
        }
        return true;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || ((Object)((Object)this)).getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        GenericCharacterTokenizer that = (GenericCharacterTokenizer)((Object)o);
        return Arrays.equals(this.delimiters, that.delimiters);
    }

    public int hashCode() {
        return Objects.hash(super.hashCode(), Arrays.hashCode(this.delimiters));
    }
}

