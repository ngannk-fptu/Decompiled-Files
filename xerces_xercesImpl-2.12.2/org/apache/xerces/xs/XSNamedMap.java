/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xs;

import java.util.Map;
import org.apache.xerces.xs.XSObject;

public interface XSNamedMap
extends Map {
    public int getLength();

    public XSObject item(int var1);

    public XSObject itemByName(String var1, String var2);
}

