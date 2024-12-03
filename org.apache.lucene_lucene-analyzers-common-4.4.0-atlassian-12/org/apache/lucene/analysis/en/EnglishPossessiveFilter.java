/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.en;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

public final class EnglishPossessiveFilter
extends TokenFilter {
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private Version matchVersion;

    @Deprecated
    public EnglishPossessiveFilter(TokenStream input) {
        this(Version.LUCENE_35, input);
    }

    public EnglishPossessiveFilter(Version version, TokenStream input) {
        super(input);
        this.matchVersion = version;
    }

    public boolean incrementToken() throws IOException {
        if (!this.input.incrementToken()) {
            return false;
        }
        char[] buffer = this.termAtt.buffer();
        int bufferLength = this.termAtt.length();
        if (!(bufferLength < 2 || buffer[bufferLength - 2] != '\'' && (!this.matchVersion.onOrAfter(Version.LUCENE_36) || buffer[bufferLength - 2] != '\u2019' && buffer[bufferLength - 2] != '\uff07') || buffer[bufferLength - 1] != 's' && buffer[bufferLength - 1] != 'S')) {
            this.termAtt.setLength(bufferLength - 2);
        }
        return true;
    }
}

