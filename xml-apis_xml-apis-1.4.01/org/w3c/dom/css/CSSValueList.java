/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.css;

import org.w3c.dom.css.CSSValue;

public interface CSSValueList
extends CSSValue {
    public int getLength();

    public CSSValue item(int var1);
}

