/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.core;

import java.io.Reader;
import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

public final class WhitespaceTokenizer
extends CharTokenizer {
    public WhitespaceTokenizer(Version matchVersion, Reader in) {
        super(matchVersion, in);
    }

    public WhitespaceTokenizer(Version matchVersion, AttributeSource.AttributeFactory factory, Reader in) {
        super(matchVersion, factory, in);
    }

    @Override
    protected boolean isTokenChar(int c) {
        return !Character.isWhitespace(c);
    }
}

