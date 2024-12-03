/*
 * Decompiled with CFR 0.152.
 */
package org.apache.wml;

import org.apache.wml.WMLElement;

public interface WMLSetvarElement
extends WMLElement {
    public void setValue(String var1);

    public String getValue();

    public void setName(String var1);

    public String getName();
}

