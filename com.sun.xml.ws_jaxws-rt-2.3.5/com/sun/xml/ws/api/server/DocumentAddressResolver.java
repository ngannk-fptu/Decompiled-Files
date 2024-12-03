/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.api.server;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.server.SDDocument;

public interface DocumentAddressResolver {
    @Nullable
    public String getRelativeAddressFor(@NotNull SDDocument var1, @NotNull SDDocument var2);
}

