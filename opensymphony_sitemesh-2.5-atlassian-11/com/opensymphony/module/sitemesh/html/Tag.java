/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.SitemeshBufferFragment;

public interface Tag {
    public static final int OPEN = 1;
    public static final int CLOSE = 2;
    public static final int EMPTY = 3;
    public static final int OPEN_MAGIC_COMMENT = 4;
    public static final int CLOSE_MAGIC_COMMENT = 5;

    public String getContents();

    public void writeTo(SitemeshBufferFragment.Builder var1, int var2);

    public String getName();

    public int getType();

    public int getAttributeCount();

    public int getAttributeIndex(String var1, boolean var2);

    public String getAttributeName(int var1);

    public String getAttributeValue(int var1);

    public String getAttributeValue(String var1, boolean var2);

    public boolean hasAttribute(String var1, boolean var2);

    public int getPosition();

    public int getLength();
}

