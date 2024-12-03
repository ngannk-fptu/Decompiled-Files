/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.sitemesh;

import java.io.IOException;
import java.io.Writer;

public interface Content {
    public void writeOriginal(Writer var1) throws IOException;

    public void writeBody(Writer var1) throws IOException;

    public void writeHead(Writer var1) throws IOException;

    public String getTitle();

    public String getProperty(String var1);

    public String[] getPropertyKeys();

    public void addProperty(String var1, String var2);
}

