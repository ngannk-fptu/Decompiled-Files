/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.FilteringTokenFilter;
import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.analysis.tokenattributes.TypeAttribute;
import java.io.IOException;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class TypeTokenFilter
extends FilteringTokenFilter {
    private final Set<String> stopTypes;
    private final TypeAttribute typeAttribute = this.addAttribute(TypeAttribute.class);
    private final boolean useWhiteList;

    public TypeTokenFilter(boolean enablePositionIncrements, TokenStream input, Set<String> stopTypes, boolean useWhiteList) {
        super(enablePositionIncrements, input);
        this.stopTypes = stopTypes;
        this.useWhiteList = useWhiteList;
    }

    public TypeTokenFilter(boolean enablePositionIncrements, TokenStream input, Set<String> stopTypes) {
        this(enablePositionIncrements, input, stopTypes, false);
    }

    @Override
    protected boolean accept() throws IOException {
        return this.useWhiteList == this.stopTypes.contains(this.typeAttribute.type());
    }
}

