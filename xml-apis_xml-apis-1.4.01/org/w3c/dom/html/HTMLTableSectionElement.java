/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.html;

import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLElement;

public interface HTMLTableSectionElement
extends HTMLElement {
    public String getAlign();

    public void setAlign(String var1);

    public String getCh();

    public void setCh(String var1);

    public String getChOff();

    public void setChOff(String var1);

    public String getVAlign();

    public void setVAlign(String var1);

    public HTMLCollection getRows();

    public HTMLElement insertRow(int var1);

    public void deleteRow(int var1);
}

