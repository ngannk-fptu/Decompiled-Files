/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.extend;

public interface TreeResolver {
    public static final String NO_NAMESPACE = "";

    public Object getParentElement(Object var1);

    public String getElementName(Object var1);

    public Object getPreviousSiblingElement(Object var1);

    public boolean isFirstChildElement(Object var1);

    public boolean isLastChildElement(Object var1);

    public int getPositionOfElement(Object var1);

    public boolean matchesElement(Object var1, String var2, String var3);
}

