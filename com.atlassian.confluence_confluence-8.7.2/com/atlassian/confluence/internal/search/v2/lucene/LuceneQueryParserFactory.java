/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.apache.lucene.queryparser.flexible.standard.StandardQueryParser
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider;
import java.util.Map;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;

@Internal
public interface LuceneQueryParserFactory {
    public StandardQueryParser createQueryParser();

    default public StandardQueryParser createQueryParser(Map<String, ? extends AnalyzerDescriptorProvider> analyzerProviders) {
        return this.createQueryParser();
    }
}

