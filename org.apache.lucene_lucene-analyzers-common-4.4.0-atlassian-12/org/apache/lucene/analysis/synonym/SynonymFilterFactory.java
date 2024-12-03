/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.synonym;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.synonym.FSTSynonymFilterFactory;
import org.apache.lucene.analysis.synonym.SlowSynonymFilterFactory;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.lucene.util.Version;

public class SynonymFilterFactory
extends TokenFilterFactory
implements ResourceLoaderAware {
    private final TokenFilterFactory delegator;

    public SynonymFilterFactory(Map<String, String> args) {
        super(args);
        this.assureMatchVersion();
        if (this.luceneMatchVersion.onOrAfter(Version.LUCENE_34)) {
            this.delegator = new FSTSynonymFilterFactory(new HashMap<String, String>(this.getOriginalArgs()));
        } else {
            if (args.containsKey("format") && !args.get("format").equals("solr")) {
                throw new IllegalArgumentException("You must specify luceneMatchVersion >= 3.4 to use alternate synonyms formats");
            }
            this.delegator = new SlowSynonymFilterFactory(new HashMap<String, String>(this.getOriginalArgs()));
        }
    }

    @Override
    public TokenStream create(TokenStream input) {
        return this.delegator.create(input);
    }

    @Override
    public void inform(ResourceLoader loader) throws IOException {
        ((ResourceLoaderAware)((Object)this.delegator)).inform(loader);
    }

    @Deprecated
    TokenFilterFactory getDelegator() {
        return this.delegator;
    }
}

