/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Cookie
 *  javax.ws.rs.core.MediaType
 */
package com.sun.jersey.api.client;

import java.util.Locale;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;

public interface RequestBuilder<T extends RequestBuilder> {
    public T entity(Object var1);

    public T entity(Object var1, MediaType var2);

    public T entity(Object var1, String var2);

    public T type(MediaType var1);

    public T type(String var1);

    public T accept(MediaType ... var1);

    public T accept(String ... var1);

    public T acceptLanguage(Locale ... var1);

    public T acceptLanguage(String ... var1);

    public T cookie(Cookie var1);

    public T header(String var1, Object var2);
}

