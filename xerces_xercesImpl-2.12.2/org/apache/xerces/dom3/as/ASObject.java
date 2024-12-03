/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom3.as;

import org.apache.xerces.dom3.as.ASModel;

public interface ASObject {
    public static final short AS_ELEMENT_DECLARATION = 1;
    public static final short AS_ATTRIBUTE_DECLARATION = 2;
    public static final short AS_NOTATION_DECLARATION = 3;
    public static final short AS_ENTITY_DECLARATION = 4;
    public static final short AS_CONTENTMODEL = 5;
    public static final short AS_MODEL = 6;

    public short getAsNodeType();

    public ASModel getOwnerASModel();

    public void setOwnerASModel(ASModel var1);

    public String getNodeName();

    public void setNodeName(String var1);

    public String getPrefix();

    public void setPrefix(String var1);

    public String getLocalName();

    public void setLocalName(String var1);

    public String getNamespaceURI();

    public void setNamespaceURI(String var1);

    public ASObject cloneASObject(boolean var1);
}

