/*
 * Decompiled with CFR 0.152.
 */
package org.apache.wml;

import org.apache.wml.WMLElement;

public interface WMLMetaElement
extends WMLElement {
    public void setName(String var1);

    public String getName();

    public void setHttpEquiv(String var1);

    public String getHttpEquiv();

    public void setForua(boolean var1);

    public boolean getForua();

    public void setScheme(String var1);

    public String getScheme();

    public void setContent(String var1);

    public String getContent();
}

