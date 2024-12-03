/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.util.FilteringTokenFilter
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.ja;

import java.util.Set;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute;
import org.apache.lucene.analysis.util.FilteringTokenFilter;
import org.apache.lucene.util.Version;

public final class JapanesePartOfSpeechStopFilter
extends FilteringTokenFilter {
    private final Set<String> stopTags;
    private final PartOfSpeechAttribute posAtt = (PartOfSpeechAttribute)this.addAttribute(PartOfSpeechAttribute.class);

    @Deprecated
    public JapanesePartOfSpeechStopFilter(Version version, boolean enablePositionIncrements, TokenStream input, Set<String> stopTags) {
        super(version, enablePositionIncrements, input);
        this.stopTags = stopTags;
    }

    public JapanesePartOfSpeechStopFilter(Version version, TokenStream input, Set<String> stopTags) {
        super(version, input);
        this.stopTags = stopTags;
    }

    protected boolean accept() {
        String pos = this.posAtt.getPartOfSpeech();
        return pos == null || !this.stopTags.contains(pos);
    }
}

