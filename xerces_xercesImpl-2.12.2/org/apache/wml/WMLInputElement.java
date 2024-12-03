/*
 * Decompiled with CFR 0.152.
 */
package org.apache.wml;

import org.apache.wml.WMLElement;

public interface WMLInputElement
extends WMLElement {
    public void setName(String var1);

    public String getName();

    public void setValue(String var1);

    public String getValue();

    public void setType(String var1);

    public String getType();

    public void setFormat(String var1);

    public String getFormat();

    public void setEmptyOk(boolean var1);

    public boolean getEmptyOk();

    public void setSize(int var1);

    public int getSize();

    public void setMaxLength(int var1);

    public int getMaxLength();

    public void setTitle(String var1);

    public String getTitle();

    public void setTabIndex(int var1);

    public int getTabIndex();

    public void setXmlLang(String var1);

    public String getXmlLang();
}

