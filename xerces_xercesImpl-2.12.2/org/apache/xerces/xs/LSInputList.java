/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xs;

import java.util.List;
import org.w3c.dom.ls.LSInput;

public interface LSInputList
extends List {
    public int getLength();

    public LSInput item(int var1);
}

