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
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.server.WSEndpoint;
import java.security.Principal;

public interface WebServiceContextDelegate {
    public Principal getUserPrincipal(@NotNull Packet var1);

    public boolean isUserInRole(@NotNull Packet var1, String var2);

    @NotNull
    public String getEPRAddress(@NotNull Packet var1, @NotNull WSEndpoint var2);

    @Nullable
    public String getWSDLAddress(@NotNull Packet var1, @NotNull WSEndpoint var2);
}

