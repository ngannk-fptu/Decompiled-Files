/*
 * Decompiled with CFR 0.152.
 */
package org.apache.wml;

import org.apache.wml.WMLElement;

public interface WMLAElement
extends WMLElement {
    public void setHref(String var1);

    public String getHref();

    public void setTitle(String var1);

    public String getTitle();

    @Override
    public void setId(String var1);

    @Override
    public String getId();

    public void setXmlLang(String var1);

    public String getXmlLang();
}

