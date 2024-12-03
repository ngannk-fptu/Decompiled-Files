/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.StemmerOverrideFilter;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class StemmerOverrideFilterFactory
extends TokenFilterFactory
implements ResourceLoaderAware {
    private StemmerOverrideFilter.StemmerOverrideMap dictionary;
    private final String dictionaryFiles;
    private final boolean ignoreCase;

    public StemmerOverrideFilterFactory(Map<String, String> args) {
        super(args);
        this.dictionaryFiles = this.get(args, "dictionary");
        this.ignoreCase = this.getBoolean(args, "ignoreCase", false);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public void inform(ResourceLoader loader) throws IOException {
        if (this.dictionaryFiles != null) {
            this.assureMatchVersion();
            List<String> files = this.splitFileNames(this.dictionaryFiles);
            if (files.size() > 0) {
                StemmerOverrideFilter.Builder builder = new StemmerOverrideFilter.Builder(this.ignoreCase);
                for (String file : files) {
                    List<String> list = this.getLines(loader, file.trim());
                    for (String line : list) {
                        String[] mapping = line.split("\t", 2);
                        builder.add(mapping[0], mapping[1]);
                    }
                }
                this.dictionary = builder.build();
            }
        }
    }

    public boolean isIgnoreCase() {
        return this.ignoreCase;
    }

    @Override
    public TokenStream create(TokenStream input) {
        return this.dictionary == null ? input : new StemmerOverrideFilter(input, this.dictionary);
    }
}

