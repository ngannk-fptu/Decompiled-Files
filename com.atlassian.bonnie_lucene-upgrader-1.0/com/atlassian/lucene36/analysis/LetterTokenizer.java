/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.CharTokenizer;
import com.atlassian.lucene36.util.AttributeSource;
import com.atlassian.lucene36.util.Version;
import java.io.Reader;

public class LetterTokenizer
extends CharTokenizer {
    public LetterTokenizer(Version matchVersion, Reader in) {
        super(matchVersion, in);
    }

    public LetterTokenizer(Version matchVersion, AttributeSource source, Reader in) {
        super(matchVersion, source, in);
    }

    public LetterTokenizer(Version matchVersion, AttributeSource.AttributeFactory factory, Reader in) {
        super(matchVersion, factory, in);
    }

    @Deprecated
    public LetterTokenizer(Reader in) {
        super(Version.LUCENE_30, in);
    }

    @Deprecated
    public LetterTokenizer(AttributeSource source, Reader in) {
        super(Version.LUCENE_30, source, in);
    }

    @Deprecated
    public LetterTokenizer(AttributeSource.AttributeFactory factory, Reader in) {
        super(Version.LUCENE_30, factory, in);
    }

    protected boolean isTokenChar(int c) {
        return Character.isLetter(c);
    }
}

