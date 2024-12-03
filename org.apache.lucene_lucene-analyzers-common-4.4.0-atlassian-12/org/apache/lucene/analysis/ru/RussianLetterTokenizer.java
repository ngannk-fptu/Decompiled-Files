/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.ru;

import java.io.Reader;
import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

@Deprecated
public class RussianLetterTokenizer
extends CharTokenizer {
    private static final int DIGIT_0 = 48;
    private static final int DIGIT_9 = 57;

    public RussianLetterTokenizer(Version matchVersion, Reader in) {
        super(matchVersion, in);
    }

    public RussianLetterTokenizer(Version matchVersion, AttributeSource.AttributeFactory factory, Reader in) {
        super(matchVersion, factory, in);
    }

    @Override
    protected boolean isTokenChar(int c) {
        return Character.isLetter(c) || c >= 48 && c <= 57;
    }
}

