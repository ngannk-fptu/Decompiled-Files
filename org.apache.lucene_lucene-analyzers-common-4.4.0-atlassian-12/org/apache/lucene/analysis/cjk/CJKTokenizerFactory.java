/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 */
package org.apache.lucene.analysis.cjk;

import java.io.Reader;
import java.util.Map;
import org.apache.lucene.analysis.cjk.CJKTokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource;

@Deprecated
public class CJKTokenizerFactory
extends TokenizerFactory {
    public CJKTokenizerFactory(Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public CJKTokenizer create(AttributeSource.AttributeFactory factory, Reader in) {
        return new CJKTokenizer(factory, in);
    }
}

