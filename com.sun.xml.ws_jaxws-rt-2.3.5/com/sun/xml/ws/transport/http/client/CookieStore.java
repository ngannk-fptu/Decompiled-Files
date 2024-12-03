/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.transport.http.client;

import com.sun.xml.ws.transport.http.client.HttpCookie;
import java.net.URI;
import java.util.List;

interface CookieStore {
    public void add(URI var1, HttpCookie var2);

    public List<HttpCookie> get(URI var1);

    public List<HttpCookie> getCookies();

    public List<URI> getURIs();

    public boolean remove(URI var1, HttpCookie var2);

    public boolean removeAll();
}

