/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.svg;

import org.w3c.dom.Attr;

public interface LiveAttributeValue {
    public void attrAdded(Attr var1, String var2);

    public void attrModified(Attr var1, String var2, String var3);

    public void attrRemoved(Attr var1, String var2);
}

