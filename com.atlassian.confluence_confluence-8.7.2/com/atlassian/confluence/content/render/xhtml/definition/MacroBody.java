/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.definition;

import com.atlassian.confluence.content.render.xhtml.Streamable;

public interface MacroBody {
    public Streamable getBodyStream();

    public String getBody();

    public Streamable getTransformedBodyStream();

    public Streamable getStorageBodyStream();
}

