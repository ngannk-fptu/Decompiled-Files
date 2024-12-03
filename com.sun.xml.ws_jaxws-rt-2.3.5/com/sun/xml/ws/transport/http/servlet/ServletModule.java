/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.servlet.http.HttpServletRequest
 */
package com.sun.xml.ws.transport.http.servlet;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.server.WebModule;
import com.sun.xml.ws.transport.http.servlet.ServletConnectionImpl;
import javax.servlet.http.HttpServletRequest;

public abstract class ServletModule
extends WebModule {
    @NotNull
    public String getContextPath(HttpServletRequest req) {
        return ServletConnectionImpl.getBaseAddress(req);
    }
}

