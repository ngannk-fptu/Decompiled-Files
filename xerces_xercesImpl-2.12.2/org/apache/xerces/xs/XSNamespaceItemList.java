/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xs;

import java.util.List;
import org.apache.xerces.xs.XSNamespaceItem;

public interface XSNamespaceItemList
extends List {
    public int getLength();

    public XSNamespaceItem item(int var1);
}

