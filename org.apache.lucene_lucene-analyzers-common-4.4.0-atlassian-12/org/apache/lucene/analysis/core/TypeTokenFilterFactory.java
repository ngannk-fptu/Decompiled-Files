/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.core;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.TypeTokenFilter;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class TypeTokenFilterFactory
extends TokenFilterFactory
implements ResourceLoaderAware {
    private final boolean useWhitelist;
    private final boolean enablePositionIncrements;
    private final String stopTypesFiles;
    private Set<String> stopTypes;

    public TypeTokenFilterFactory(Map<String, String> args) {
        super(args);
        this.stopTypesFiles = this.require(args, "types");
        this.enablePositionIncrements = this.getBoolean(args, "enablePositionIncrements", true);
        this.useWhitelist = this.getBoolean(args, "useWhitelist", false);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public void inform(ResourceLoader loader) throws IOException {
        List<String> files = this.splitFileNames(this.stopTypesFiles);
        if (files.size() > 0) {
            this.stopTypes = new HashSet<String>();
            for (String file : files) {
                List<String> typesLines = this.getLines(loader, file.trim());
                this.stopTypes.addAll(typesLines);
            }
        }
    }

    public boolean isEnablePositionIncrements() {
        return this.enablePositionIncrements;
    }

    public Set<String> getStopTypes() {
        return this.stopTypes;
    }

    @Override
    public TokenStream create(TokenStream input) {
        TypeTokenFilter filter = new TypeTokenFilter(this.luceneMatchVersion, this.enablePositionIncrements, input, this.stopTypes, this.useWhitelist);
        return filter;
    }
}

