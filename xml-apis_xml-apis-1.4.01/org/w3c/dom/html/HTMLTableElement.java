/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.html;

import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLTableCaptionElement;
import org.w3c.dom.html.HTMLTableSectionElement;

public interface HTMLTableElement
extends HTMLElement {
    public HTMLTableCaptionElement getCaption();

    public void setCaption(HTMLTableCaptionElement var1);

    public HTMLTableSectionElement getTHead();

    public void setTHead(HTMLTableSectionElement var1);

    public HTMLTableSectionElement getTFoot();

    public void setTFoot(HTMLTableSectionElement var1);

    public HTMLCollection getRows();

    public HTMLCollection getTBodies();

    public String getAlign();

    public void setAlign(String var1);

    public String getBgColor();

    public void setBgColor(String var1);

    public String getBorder();

    public void setBorder(String var1);

    public String getCellPadding();

    public void setCellPadding(String var1);

    public String getCellSpacing();

    public void setCellSpacing(String var1);

    public String getFrame();

    public void setFrame(String var1);

    public String getRules();

    public void setRules(String var1);

    public String getSummary();

    public void setSummary(String var1);

    public String getWidth();

    public void setWidth(String var1);

    public HTMLElement createTHead();

    public void deleteTHead();

    public HTMLElement createTFoot();

    public void deleteTFoot();

    public HTMLElement createCaption();

    public void deleteCaption();

    public HTMLElement insertRow(int var1);

    public void deleteRow(int var1);
}

