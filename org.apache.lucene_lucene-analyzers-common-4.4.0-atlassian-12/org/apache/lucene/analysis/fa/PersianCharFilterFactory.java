/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.CharFilter
 */
package org.apache.lucene.analysis.fa;

import java.io.Reader;
import java.util.Map;
import org.apache.lucene.analysis.CharFilter;
import org.apache.lucene.analysis.fa.PersianCharFilter;
import org.apache.lucene.analysis.util.AbstractAnalysisFactory;
import org.apache.lucene.analysis.util.CharFilterFactory;
import org.apache.lucene.analysis.util.MultiTermAwareComponent;

public class PersianCharFilterFactory
extends CharFilterFactory
implements MultiTermAwareComponent {
    public PersianCharFilterFactory(Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public CharFilter create(Reader input) {
        return new PersianCharFilter(input);
    }

    @Override
    public AbstractAnalysisFactory getMultiTermComponent() {
        return this;
    }
}

