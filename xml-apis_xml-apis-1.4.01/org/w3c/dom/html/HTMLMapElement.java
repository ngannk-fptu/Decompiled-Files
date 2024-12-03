/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.html;

import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLElement;

public interface HTMLMapElement
extends HTMLElement {
    public HTMLCollection getAreas();

    public String getName();

    public void setName(String var1);
}

