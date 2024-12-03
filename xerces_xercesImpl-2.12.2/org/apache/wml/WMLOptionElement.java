/*
 * Decompiled with CFR 0.152.
 */
package org.apache.wml;

import org.apache.wml.WMLElement;

public interface WMLOptionElement
extends WMLElement {
    public void setValue(String var1);

    public String getValue();

    public void setTitle(String var1);

    public String getTitle();

    public void setOnPick(String var1);

    public String getOnPick();

    public void setXmlLang(String var1);

    public String getXmlLang();
}

