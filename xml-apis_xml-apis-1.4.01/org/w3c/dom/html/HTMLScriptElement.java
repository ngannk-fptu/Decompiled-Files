/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.html;

import org.w3c.dom.html.HTMLElement;

public interface HTMLScriptElement
extends HTMLElement {
    public String getText();

    public void setText(String var1);

    public String getHtmlFor();

    public void setHtmlFor(String var1);

    public String getEvent();

    public void setEvent(String var1);

    public String getCharset();

    public void setCharset(String var1);

    public boolean getDefer();

    public void setDefer(boolean var1);

    public String getSrc();

    public void setSrc(String var1);

    public String getType();

    public void setType(String var1);
}

