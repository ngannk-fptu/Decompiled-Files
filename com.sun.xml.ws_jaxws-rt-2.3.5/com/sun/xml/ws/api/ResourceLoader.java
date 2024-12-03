/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class ResourceLoader {
    public abstract URL getResource(String var1) throws MalformedURLException;
}

