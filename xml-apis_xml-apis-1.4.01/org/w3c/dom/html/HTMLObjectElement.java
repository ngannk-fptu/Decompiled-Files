/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.html;

import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLFormElement;

public interface HTMLObjectElement
extends HTMLElement {
    public HTMLFormElement getForm();

    public String getCode();

    public void setCode(String var1);

    public String getAlign();

    public void setAlign(String var1);

    public String getArchive();

    public void setArchive(String var1);

    public String getBorder();

    public void setBorder(String var1);

    public String getCodeBase();

    public void setCodeBase(String var1);

    public String getCodeType();

    public void setCodeType(String var1);

    public String getData();

    public void setData(String var1);

    public boolean getDeclare();

    public void setDeclare(boolean var1);

    public String getHeight();

    public void setHeight(String var1);

    public String getHspace();

    public void setHspace(String var1);

    public String getName();

    public void setName(String var1);

    public String getStandby();

    public void setStandby(String var1);

    public int getTabIndex();

    public void setTabIndex(int var1);

    public String getType();

    public void setType(String var1);

    public String getUseMap();

    public void setUseMap(String var1);

    public String getVspace();

    public void setVspace(String var1);

    public String getWidth();

    public void setWidth(String var1);
}

