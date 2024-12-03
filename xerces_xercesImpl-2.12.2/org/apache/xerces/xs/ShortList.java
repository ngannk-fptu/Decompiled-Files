/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xs;

import java.util.List;
import org.apache.xerces.xs.XSException;

public interface ShortList
extends List {
    public int getLength();

    public boolean contains(short var1);

    public short item(int var1) throws XSException;
}

