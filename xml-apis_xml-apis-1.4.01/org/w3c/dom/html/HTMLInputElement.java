/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.html;

import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLFormElement;

public interface HTMLInputElement
extends HTMLElement {
    public String getDefaultValue();

    public void setDefaultValue(String var1);

    public boolean getDefaultChecked();

    public void setDefaultChecked(boolean var1);

    public HTMLFormElement getForm();

    public String getAccept();

    public void setAccept(String var1);

    public String getAccessKey();

    public void setAccessKey(String var1);

    public String getAlign();

    public void setAlign(String var1);

    public String getAlt();

    public void setAlt(String var1);

    public boolean getChecked();

    public void setChecked(boolean var1);

    public boolean getDisabled();

    public void setDisabled(boolean var1);

    public int getMaxLength();

    public void setMaxLength(int var1);

    public String getName();

    public void setName(String var1);

    public boolean getReadOnly();

    public void setReadOnly(boolean var1);

    public String getSize();

    public void setSize(String var1);

    public String getSrc();

    public void setSrc(String var1);

    public int getTabIndex();

    public void setTabIndex(int var1);

    public String getType();

    public String getUseMap();

    public void setUseMap(String var1);

    public String getValue();

    public void setValue(String var1);

    public void blur();

    public void focus();

    public void select();

    public void click();
}

