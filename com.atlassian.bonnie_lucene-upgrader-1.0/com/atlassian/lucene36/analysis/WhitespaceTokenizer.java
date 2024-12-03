/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.CharTokenizer;
import com.atlassian.lucene36.util.AttributeSource;
import com.atlassian.lucene36.util.Version;
import java.io.Reader;

public final class WhitespaceTokenizer
extends CharTokenizer {
    public WhitespaceTokenizer(Version matchVersion, Reader in) {
        super(matchVersion, in);
    }

    public WhitespaceTokenizer(Version matchVersion, AttributeSource source, Reader in) {
        super(matchVersion, source, in);
    }

    public WhitespaceTokenizer(Version matchVersion, AttributeSource.AttributeFactory factory, Reader in) {
        super(matchVersion, factory, in);
    }

    @Deprecated
    public WhitespaceTokenizer(Reader in) {
        super(in);
    }

    @Deprecated
    public WhitespaceTokenizer(AttributeSource source, Reader in) {
        super(source, in);
    }

    @Deprecated
    public WhitespaceTokenizer(AttributeSource.AttributeFactory factory, Reader in) {
        super(factory, in);
    }

    protected boolean isTokenChar(int c) {
        return !Character.isWhitespace(c);
    }
}

