/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.wsdl;

import com.sun.istack.Nullable;
import com.sun.xml.ws.api.server.SDDocument;

public interface SDDocumentResolver {
    @Nullable
    public SDDocument resolve(String var1);
}

