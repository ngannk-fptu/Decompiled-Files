/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.api.engine;

public interface WikiRenderEngine {
    public static final int LINK_TYPE_NORMAL = 1;
    public static final int LINK_TYPE_CAMEL_CASE = 2;

    public boolean exists(String var1, int var2);

    public boolean showCreate(int var1);

    public boolean isExternal(String var1, int var2);

    public void appendExternalLink(StringBuffer var1, String var2, String var3, String var4, int var5);

    public void appendExternalLink(StringBuffer var1, String var2, String var3, String var4, String var5, int var6);

    public void appendLink(StringBuffer var1, String var2, String var3, String var4, String var5, int var6);

    public void appendLink(StringBuffer var1, String var2, String var3, String var4, int var5);

    public void appendCreateLink(StringBuffer var1, String var2, String var3, String var4, int var5);
}

