/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.convert.html;

import com.atlassian.plugins.conversion.convert.html.Streamable;
import java.io.Serializable;

public interface HtmlConversionResult
extends Serializable {
    public String getHtml();

    public Streamable getImage(String var1);
}

