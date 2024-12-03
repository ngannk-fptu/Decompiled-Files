/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.ws.EndpointReference
 *  javax.xml.ws.WebServiceContext
 *  javax.xml.ws.wsaddressing.W3CEndpointReference
 */
package com.sun.xml.ws.developer;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.developer.EPRRecipe;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

public interface StatefulWebServiceManager<T> {
    @NotNull
    public <EPR extends EndpointReference> EPR export(Class<EPR> var1, T var2);

    @NotNull
    public <EPR extends EndpointReference> EPR export(Class<EPR> var1, T var2, @Nullable EPRRecipe var3);

    @NotNull
    public W3CEndpointReference export(T var1);

    @NotNull
    public <EPR extends EndpointReference> EPR export(Class<EPR> var1, @NotNull WebServiceContext var2, T var3);

    @NotNull
    public <EPR extends EndpointReference> EPR export(Class<EPR> var1, @NotNull Packet var2, T var3);

    @NotNull
    public <EPR extends EndpointReference> EPR export(Class<EPR> var1, @NotNull Packet var2, T var3, EPRRecipe var4);

    @NotNull
    public <EPR extends EndpointReference> EPR export(Class<EPR> var1, String var2, T var3);

    public void unexport(@Nullable T var1);

    @Nullable
    public T resolve(@NotNull EndpointReference var1);

    public void setFallbackInstance(T var1);

    public void setTimeout(long var1, @Nullable Callback<T> var3);

    public void touch(T var1);

    public static interface Callback<T> {
        public void onTimeout(@NotNull T var1, @NotNull StatefulWebServiceManager<T> var2);
    }
}

