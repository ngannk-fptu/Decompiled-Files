/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.extend;

public interface AttributeResolver {
    public String getAttributeValue(Object var1, String var2);

    public String getAttributeValue(Object var1, String var2, String var3);

    public String getClass(Object var1);

    public String getID(Object var1);

    public String getNonCssStyling(Object var1);

    public String getElementStyling(Object var1);

    public String getLang(Object var1);

    public boolean isLink(Object var1);

    public boolean isVisited(Object var1);

    public boolean isHover(Object var1);

    public boolean isActive(Object var1);

    public boolean isFocus(Object var1);
}

