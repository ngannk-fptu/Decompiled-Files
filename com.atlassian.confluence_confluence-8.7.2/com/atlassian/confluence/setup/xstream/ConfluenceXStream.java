/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup.xstream;

import java.io.Reader;
import java.io.Writer;

public interface ConfluenceXStream {
    public String toXML(Object var1);

    public void toXML(Object var1, Writer var2);

    public Object fromXML(String var1);

    public Object fromXML(Reader var1);
}

