/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.TypeInfo;

public interface Attr
extends Node {
    public String getName();

    public boolean getSpecified();

    public String getValue();

    public void setValue(String var1) throws DOMException;

    public Element getOwnerElement();

    public TypeInfo getSchemaTypeInfo();

    public boolean isId();
}

