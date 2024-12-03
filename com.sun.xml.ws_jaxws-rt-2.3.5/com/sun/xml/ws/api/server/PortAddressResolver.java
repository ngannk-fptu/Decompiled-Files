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
import javax.xml.namespace.QName;

public abstract class PortAddressResolver {
    @Nullable
    public abstract String getAddressFor(@NotNull QName var1, @NotNull String var2);

    @Nullable
    public String getAddressFor(@NotNull QName serviceName, @NotNull String portName, String currentAddress) {
        return this.getAddressFor(serviceName, portName);
    }
}

