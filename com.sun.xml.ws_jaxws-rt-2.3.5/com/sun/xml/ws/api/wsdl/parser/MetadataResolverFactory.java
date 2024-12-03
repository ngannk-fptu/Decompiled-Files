/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.api.wsdl.parser;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.wsdl.parser.MetaDataResolver;
import org.xml.sax.EntityResolver;

public abstract class MetadataResolverFactory {
    @NotNull
    public abstract MetaDataResolver metadataResolver(@Nullable EntityResolver var1);
}

