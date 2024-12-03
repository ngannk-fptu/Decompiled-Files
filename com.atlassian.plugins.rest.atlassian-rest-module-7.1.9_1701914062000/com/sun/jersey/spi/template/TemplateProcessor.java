/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.template;

import java.io.IOException;
import java.io.OutputStream;

public interface TemplateProcessor {
    public String resolve(String var1);

    public void writeTo(String var1, Object var2, OutputStream var3) throws IOException;
}

