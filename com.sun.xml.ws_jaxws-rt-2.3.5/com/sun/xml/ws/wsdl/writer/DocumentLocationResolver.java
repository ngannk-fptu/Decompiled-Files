/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.wsdl.writer;

import com.sun.istack.Nullable;

public interface DocumentLocationResolver {
    @Nullable
    public String getLocationFor(String var1, String var2);
}

