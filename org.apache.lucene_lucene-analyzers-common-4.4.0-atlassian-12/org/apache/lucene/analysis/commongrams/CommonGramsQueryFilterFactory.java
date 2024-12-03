/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.commongrams;

import java.util.Map;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.commongrams.CommonGramsFilter;
import org.apache.lucene.analysis.commongrams.CommonGramsFilterFactory;
import org.apache.lucene.analysis.commongrams.CommonGramsQueryFilter;

public class CommonGramsQueryFilterFactory
extends CommonGramsFilterFactory {
    public CommonGramsQueryFilterFactory(Map<String, String> args) {
        super(args);
    }

    @Override
    public TokenFilter create(TokenStream input) {
        CommonGramsFilter commonGrams = (CommonGramsFilter)super.create(input);
        return new CommonGramsQueryFilter(commonGrams);
    }
}

