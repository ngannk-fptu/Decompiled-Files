/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.core;

import java.io.Reader;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

public final class LowerCaseTokenizer
extends LetterTokenizer {
    public LowerCaseTokenizer(Version matchVersion, Reader in) {
        super(matchVersion, in);
    }

    public LowerCaseTokenizer(Version matchVersion, AttributeSource.AttributeFactory factory, Reader in) {
        super(matchVersion, factory, in);
    }

    @Override
    protected int normalize(int c) {
        return Character.toLowerCase(c);
    }
}

