/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xs.datatypes;

import java.util.List;
import org.apache.xerces.xs.XSException;

public interface ByteList
extends List {
    public int getLength();

    public boolean contains(byte var1);

    public byte item(int var1) throws XSException;

    public byte[] toByteArray();
}

