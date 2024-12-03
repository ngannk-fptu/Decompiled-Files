/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.util.CharArraySet
 *  org.apache.lucene.analysis.util.ResourceLoader
 *  org.apache.lucene.analysis.util.ResourceLoaderAware
 *  org.apache.lucene.analysis.util.TokenFilterFactory
 */
package org.apache.lucene.analysis.ja;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ja.JapanesePartOfSpeechStopFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class JapanesePartOfSpeechStopFilterFactory
extends TokenFilterFactory
implements ResourceLoaderAware {
    private final String stopTagFiles;
    private final boolean enablePositionIncrements;
    private Set<String> stopTags;

    public JapanesePartOfSpeechStopFilterFactory(Map<String, String> args) {
        super(args);
        this.stopTagFiles = this.get(args, "tags");
        this.enablePositionIncrements = this.getBoolean(args, "enablePositionIncrements", true);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public void inform(ResourceLoader loader) throws IOException {
        this.stopTags = null;
        CharArraySet cas = this.getWordSet(loader, this.stopTagFiles, false);
        if (cas != null) {
            this.stopTags = new HashSet<String>();
            for (Object element : cas) {
                char[] chars = (char[])element;
                this.stopTags.add(new String(chars));
            }
        }
    }

    public TokenStream create(TokenStream stream) {
        if (this.stopTags != null) {
            JapanesePartOfSpeechStopFilter filter = new JapanesePartOfSpeechStopFilter(this.luceneMatchVersion, this.enablePositionIncrements, stream, this.stopTags);
            return filter;
        }
        return stream;
    }
}

