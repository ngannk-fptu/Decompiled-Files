/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.html;

import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLElement;

public interface HTMLFormElement
extends HTMLElement {
    public HTMLCollection getElements();

    public int getLength();

    public String getName();

    public void setName(String var1);

    public String getAcceptCharset();

    public void setAcceptCharset(String var1);

    public String getAction();

    public void setAction(String var1);

    public String getEnctype();

    public void setEnctype(String var1);

    public String getMethod();

    public void setMethod(String var1);

    public String getTarget();

    public void setTarget(String var1);

    public void submit();

    public void reset();
}

