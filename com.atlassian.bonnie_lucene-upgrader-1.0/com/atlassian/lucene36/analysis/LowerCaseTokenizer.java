/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.LetterTokenizer;
import com.atlassian.lucene36.util.AttributeSource;
import com.atlassian.lucene36.util.Version;
import java.io.Reader;

public final class LowerCaseTokenizer
extends LetterTokenizer {
    public LowerCaseTokenizer(Version matchVersion, Reader in) {
        super(matchVersion, in);
    }

    public LowerCaseTokenizer(Version matchVersion, AttributeSource source, Reader in) {
        super(matchVersion, source, in);
    }

    public LowerCaseTokenizer(Version matchVersion, AttributeSource.AttributeFactory factory, Reader in) {
        super(matchVersion, factory, in);
    }

    @Deprecated
    public LowerCaseTokenizer(Reader in) {
        super(Version.LUCENE_30, in);
    }

    @Deprecated
    public LowerCaseTokenizer(AttributeSource source, Reader in) {
        super(Version.LUCENE_30, source, in);
    }

    @Deprecated
    public LowerCaseTokenizer(AttributeSource.AttributeFactory factory, Reader in) {
        super(Version.LUCENE_30, factory, in);
    }

    protected int normalize(int c) {
        return Character.toLowerCase(c);
    }
}

