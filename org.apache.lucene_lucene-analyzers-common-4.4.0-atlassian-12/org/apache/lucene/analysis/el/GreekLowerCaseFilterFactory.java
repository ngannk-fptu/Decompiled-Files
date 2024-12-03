/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.el;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.el.GreekLowerCaseFilter;
import org.apache.lucene.analysis.util.AbstractAnalysisFactory;
import org.apache.lucene.analysis.util.MultiTermAwareComponent;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class GreekLowerCaseFilterFactory
extends TokenFilterFactory
implements MultiTermAwareComponent {
    public GreekLowerCaseFilterFactory(Map<String, String> args) {
        super(args);
        this.assureMatchVersion();
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public GreekLowerCaseFilter create(TokenStream in) {
        return new GreekLowerCaseFilter(this.luceneMatchVersion, in);
    }

    @Override
    public AbstractAnalysisFactory getMultiTermComponent() {
        return this;
    }
}

