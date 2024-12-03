/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.cn;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.ChineseFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;

@Deprecated
public class ChineseFilterFactory
extends TokenFilterFactory {
    public ChineseFilterFactory(Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public ChineseFilter create(TokenStream in) {
        return new ChineseFilter(in);
    }
}

