/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.TypeAttribute
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.core;

import java.util.Set;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.util.FilteringTokenFilter;
import org.apache.lucene.util.Version;

public final class TypeTokenFilter
extends FilteringTokenFilter {
    private final Set<String> stopTypes;
    private final TypeAttribute typeAttribute = (TypeAttribute)this.addAttribute(TypeAttribute.class);
    private final boolean useWhiteList;

    @Deprecated
    public TypeTokenFilter(Version version, boolean enablePositionIncrements, TokenStream input, Set<String> stopTypes, boolean useWhiteList) {
        super(version, enablePositionIncrements, input);
        this.stopTypes = stopTypes;
        this.useWhiteList = useWhiteList;
    }

    @Deprecated
    public TypeTokenFilter(Version version, boolean enablePositionIncrements, TokenStream input, Set<String> stopTypes) {
        this(version, enablePositionIncrements, input, stopTypes, false);
    }

    public TypeTokenFilter(Version version, TokenStream input, Set<String> stopTypes, boolean useWhiteList) {
        super(version, input);
        this.stopTypes = stopTypes;
        this.useWhiteList = useWhiteList;
    }

    public TypeTokenFilter(Version version, TokenStream input, Set<String> stopTypes) {
        this(version, input, stopTypes, false);
    }

    @Override
    protected boolean accept() {
        return this.useWhiteList == this.stopTypes.contains(this.typeAttribute.type());
    }
}

