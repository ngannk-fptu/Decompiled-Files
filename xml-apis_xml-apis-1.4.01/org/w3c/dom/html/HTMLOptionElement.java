/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.html;

import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLFormElement;

public interface HTMLOptionElement
extends HTMLElement {
    public HTMLFormElement getForm();

    public boolean getDefaultSelected();

    public void setDefaultSelected(boolean var1);

    public String getText();

    public int getIndex();

    public void setIndex(int var1);

    public boolean getDisabled();

    public void setDisabled(boolean var1);

    public String getLabel();

    public void setLabel(String var1);

    public boolean getSelected();

    public String getValue();

    public void setValue(String var1);
}

