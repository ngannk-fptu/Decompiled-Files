/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.ar;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ar.ArabicNormalizationFilter;
import org.apache.lucene.analysis.util.AbstractAnalysisFactory;
import org.apache.lucene.analysis.util.MultiTermAwareComponent;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class ArabicNormalizationFilterFactory
extends TokenFilterFactory
implements MultiTermAwareComponent {
    public ArabicNormalizationFilterFactory(Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public ArabicNormalizationFilter create(TokenStream input) {
        return new ArabicNormalizationFilter(input);
    }

    @Override
    public AbstractAnalysisFactory getMultiTermComponent() {
        return this;
    }
}

