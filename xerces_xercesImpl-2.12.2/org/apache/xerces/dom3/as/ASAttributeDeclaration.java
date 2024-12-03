/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom3.as;

import org.apache.xerces.dom3.as.ASDataType;
import org.apache.xerces.dom3.as.ASObject;
import org.apache.xerces.dom3.as.ASObjectList;

public interface ASAttributeDeclaration
extends ASObject {
    public static final short VALUE_NONE = 0;
    public static final short VALUE_DEFAULT = 1;
    public static final short VALUE_FIXED = 2;

    public ASDataType getDataType();

    public void setDataType(ASDataType var1);

    public String getDataValue();

    public void setDataValue(String var1);

    public String getEnumAttr();

    public void setEnumAttr(String var1);

    public ASObjectList getOwnerElements();

    public void setOwnerElements(ASObjectList var1);

    public short getDefaultType();

    public void setDefaultType(short var1);
}

