/*
 * Decompiled with CFR 0.152.
 */
package org.apache.wml;

import org.apache.wml.WMLElement;

public interface WMLCardElement
extends WMLElement {
    public void setOnEnterBackward(String var1);

    public String getOnEnterBackward();

    public void setOnEnterForward(String var1);

    public String getOnEnterForward();

    public void setOnTimer(String var1);

    public String getOnTimer();

    public void setTitle(String var1);

    public String getTitle();

    public void setNewContext(boolean var1);

    public boolean getNewContext();

    public void setOrdered(boolean var1);

    public boolean getOrdered();

    public void setXmlLang(String var1);

    public String getXmlLang();
}

