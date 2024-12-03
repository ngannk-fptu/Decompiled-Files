/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.html;

import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLElement;

public interface HTMLTableRowElement
extends HTMLElement {
    public int getRowIndex();

    public void setRowIndex(int var1);

    public int getSectionRowIndex();

    public void setSectionRowIndex(int var1);

    public HTMLCollection getCells();

    public void setCells(HTMLCollection var1);

    public String getAlign();

    public void setAlign(String var1);

    public String getBgColor();

    public void setBgColor(String var1);

    public String getCh();

    public void setCh(String var1);

    public String getChOff();

    public void setChOff(String var1);

    public String getVAlign();

    public void setVAlign(String var1);

    public HTMLElement insertCell(int var1);

    public void deleteCell(int var1);
}

