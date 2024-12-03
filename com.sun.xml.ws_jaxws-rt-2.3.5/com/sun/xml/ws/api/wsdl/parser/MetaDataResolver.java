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
import com.sun.xml.ws.api.wsdl.parser.ServiceDescriptor;
import java.net.URI;

public abstract class MetaDataResolver {
    @Nullable
    public abstract ServiceDescriptor resolve(@NotNull URI var1);
}

