/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.common.usermodel;

import org.apache.poi.common.usermodel.HyperlinkType;

public interface Hyperlink {
    public String getAddress();

    public void setAddress(String var1);

    public String getLabel();

    public void setLabel(String var1);

    public HyperlinkType getType();
}

