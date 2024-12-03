/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom3.as;

import org.apache.xerces.dom3.as.ASObject;
import org.w3c.dom.DOMException;

public interface ASNamedObjectMap {
    public int getLength();

    public ASObject getNamedItem(String var1);

    public ASObject getNamedItemNS(String var1, String var2);

    public ASObject item(int var1);

    public ASObject removeNamedItem(String var1) throws DOMException;

    public ASObject removeNamedItemNS(String var1, String var2) throws DOMException;

    public ASObject setNamedItem(ASObject var1) throws DOMException;

    public ASObject setNamedItemNS(ASObject var1) throws DOMException;
}

