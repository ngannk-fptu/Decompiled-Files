/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.html;

import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLFormElement;

public interface HTMLSelectElement
extends HTMLElement {
    public String getType();

    public int getSelectedIndex();

    public void setSelectedIndex(int var1);

    public String getValue();

    public void setValue(String var1);

    public int getLength();

    public HTMLFormElement getForm();

    public HTMLCollection getOptions();

    public boolean getDisabled();

    public void setDisabled(boolean var1);

    public boolean getMultiple();

    public void setMultiple(boolean var1);

    public String getName();

    public void setName(String var1);

    public int getSize();

    public void setSize(int var1);

    public int getTabIndex();

    public void setTabIndex(int var1);

    public void add(HTMLElement var1, HTMLElement var2);

    public void remove(int var1);

    public void blur();

    public void focus();
}

