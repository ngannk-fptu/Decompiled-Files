/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 */
package org.apache.lucene.analysis.cn;

import java.io.Reader;
import java.util.Map;
import org.apache.lucene.analysis.cn.ChineseTokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource;

@Deprecated
public class ChineseTokenizerFactory
extends TokenizerFactory {
    public ChineseTokenizerFactory(Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public ChineseTokenizer create(AttributeSource.AttributeFactory factory, Reader in) {
        return new ChineseTokenizer(factory, in);
    }
}

