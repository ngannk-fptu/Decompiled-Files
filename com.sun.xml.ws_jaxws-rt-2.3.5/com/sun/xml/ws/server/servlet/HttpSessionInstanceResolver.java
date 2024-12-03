/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.server.servlet;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.developer.servlet.HttpSessionScope;
import com.sun.xml.ws.server.AbstractMultiInstanceResolver;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceException;

public class HttpSessionInstanceResolver<T>
extends AbstractMultiInstanceResolver<T> {
    public HttpSessionInstanceResolver(@NotNull Class<T> clazz) {
        super(clazz);
    }

    @Override
    @NotNull
    public T resolve(Packet request) {
        HttpServletRequest sr = (HttpServletRequest)request.get("javax.xml.ws.servlet.request");
        if (sr == null) {
            throw new WebServiceException(this.clazz + " has @" + HttpSessionScope.class.getSimpleName() + " but it's deployed on non-servlet endpoint");
        }
        HttpSession session = sr.getSession();
        Object o = this.clazz.cast(session.getAttribute(this.clazz.getName()));
        if (o == null) {
            o = this.create();
            session.setAttribute(this.clazz.getName(), o);
        }
        return o;
    }
}

