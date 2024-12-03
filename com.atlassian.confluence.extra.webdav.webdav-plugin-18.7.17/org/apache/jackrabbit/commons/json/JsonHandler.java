/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.json;

import java.io.IOException;

public interface JsonHandler {
    public void object() throws IOException;

    public void endObject() throws IOException;

    public void array() throws IOException;

    public void endArray() throws IOException;

    public void key(String var1) throws IOException;

    public void value(String var1) throws IOException;

    public void value(boolean var1) throws IOException;

    public void value(long var1) throws IOException;

    public void value(double var1) throws IOException;
}

