/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.html;

import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLFormElement;

public interface HTMLButtonElement
extends HTMLElement {
    public HTMLFormElement getForm();

    public String getAccessKey();

    public void setAccessKey(String var1);

    public boolean getDisabled();

    public void setDisabled(boolean var1);

    public String getName();

    public void setName(String var1);

    public int getTabIndex();

    public void setTabIndex(int var1);

    public String getType();

    public String getValue();

    public void setValue(String var1);
}

