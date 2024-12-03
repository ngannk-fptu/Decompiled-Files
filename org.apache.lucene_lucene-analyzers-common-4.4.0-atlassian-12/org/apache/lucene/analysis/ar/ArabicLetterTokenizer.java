/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.ar;

import java.io.Reader;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

@Deprecated
public class ArabicLetterTokenizer
extends LetterTokenizer {
    public ArabicLetterTokenizer(Version matchVersion, Reader in) {
        super(matchVersion, in);
    }

    public ArabicLetterTokenizer(Version matchVersion, AttributeSource.AttributeFactory factory, Reader in) {
        super(matchVersion, factory, in);
    }

    @Override
    protected boolean isTokenChar(int c) {
        return super.isTokenChar(c) || Character.getType(c) == 6;
    }
}

