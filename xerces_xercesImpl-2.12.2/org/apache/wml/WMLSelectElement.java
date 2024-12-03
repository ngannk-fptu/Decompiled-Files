/*
 * Decompiled with CFR 0.152.
 */
package org.apache.wml;

import org.apache.wml.WMLElement;

public interface WMLSelectElement
extends WMLElement {
    public void setTabIndex(int var1);

    public int getTabIndex();

    public void setMultiple(boolean var1);

    public boolean getMultiple();

    public void setName(String var1);

    public String getName();

    public void setValue(String var1);

    public String getValue();

    public void setTitle(String var1);

    public String getTitle();

    public void setIName(String var1);

    public String getIName();

    public void setIValue(String var1);

    public String getIValue();

    public void setXmlLang(String var1);

    public String getXmlLang();
}

