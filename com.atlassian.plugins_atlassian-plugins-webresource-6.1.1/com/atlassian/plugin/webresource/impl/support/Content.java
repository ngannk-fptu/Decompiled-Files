/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sourcemap.ReadableSourceMap
 */
package com.atlassian.plugin.webresource.impl.support;

import com.atlassian.sourcemap.ReadableSourceMap;
import java.io.OutputStream;

public interface Content {
    public ReadableSourceMap writeTo(OutputStream var1, boolean var2);

    public String getContentType();

    public boolean isTransformed();

    default public boolean isPresent() {
        return true;
    }
}

