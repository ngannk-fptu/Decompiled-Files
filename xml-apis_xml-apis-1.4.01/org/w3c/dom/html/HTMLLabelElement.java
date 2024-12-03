/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.html;

import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLFormElement;

public interface HTMLLabelElement
extends HTMLElement {
    public HTMLFormElement getForm();

    public String getAccessKey();

    public void setAccessKey(String var1);

    public String getHtmlFor();

    public void setHtmlFor(String var1);
}

