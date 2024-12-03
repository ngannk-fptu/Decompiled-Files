/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.SitemeshBufferFragment;

public interface Text {
    public String getContents();

    public void writeTo(SitemeshBufferFragment.Builder var1, int var2);

    public int getPosition();

    public int getLength();
}

