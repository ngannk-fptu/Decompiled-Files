/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.miscellaneous;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.util.AbstractAnalysisFactory;
import org.apache.lucene.analysis.util.MultiTermAwareComponent;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class ASCIIFoldingFilterFactory
extends TokenFilterFactory
implements MultiTermAwareComponent {
    public ASCIIFoldingFilterFactory(Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public ASCIIFoldingFilter create(TokenStream input) {
        return new ASCIIFoldingFilter(input);
    }

    @Override
    public AbstractAnalysisFactory getMultiTermComponent() {
        return this;
    }
}

