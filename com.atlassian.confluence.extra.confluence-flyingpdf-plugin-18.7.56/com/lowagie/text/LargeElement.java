/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.Element;

public interface LargeElement
extends Element {
    public void setComplete(boolean var1);

    public boolean isComplete();

    public void flushContent();
}

