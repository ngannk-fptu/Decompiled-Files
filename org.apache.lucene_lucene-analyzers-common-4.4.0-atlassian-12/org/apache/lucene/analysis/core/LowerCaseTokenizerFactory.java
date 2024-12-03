/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 */
package org.apache.lucene.analysis.core;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.util.AbstractAnalysisFactory;
import org.apache.lucene.analysis.util.MultiTermAwareComponent;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource;

public class LowerCaseTokenizerFactory
extends TokenizerFactory
implements MultiTermAwareComponent {
    public LowerCaseTokenizerFactory(Map<String, String> args) {
        super(args);
        this.assureMatchVersion();
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public LowerCaseTokenizer create(AttributeSource.AttributeFactory factory, Reader input) {
        return new LowerCaseTokenizer(this.luceneMatchVersion, factory, input);
    }

    @Override
    public AbstractAnalysisFactory getMultiTermComponent() {
        return new LowerCaseFilterFactory(new HashMap<String, String>(this.getOriginalArgs()));
    }
}

