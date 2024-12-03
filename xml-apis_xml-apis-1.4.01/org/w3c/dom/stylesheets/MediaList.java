/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.stylesheets;

import org.w3c.dom.DOMException;

public interface MediaList {
    public String getMediaText();

    public void setMediaText(String var1) throws DOMException;

    public int getLength();

    public String item(int var1);

    public void deleteMedium(String var1) throws DOMException;

    public void appendMedium(String var1) throws DOMException;
}

