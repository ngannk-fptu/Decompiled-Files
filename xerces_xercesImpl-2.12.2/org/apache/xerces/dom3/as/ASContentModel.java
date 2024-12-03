/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom3.as;

import org.apache.xerces.dom3.as.ASObject;
import org.apache.xerces.dom3.as.ASObjectList;
import org.apache.xerces.dom3.as.DOMASException;

public interface ASContentModel
extends ASObject {
    public static final int AS_UNBOUNDED = Integer.MAX_VALUE;
    public static final short AS_SEQUENCE = 0;
    public static final short AS_CHOICE = 1;
    public static final short AS_ALL = 2;
    public static final short AS_NONE = 3;

    public short getListOperator();

    public void setListOperator(short var1);

    public int getMinOccurs();

    public void setMinOccurs(int var1);

    public int getMaxOccurs();

    public void setMaxOccurs(int var1);

    public ASObjectList getSubModels();

    public void setSubModels(ASObjectList var1);

    public void removesubModel(ASObject var1);

    public void insertsubModel(ASObject var1) throws DOMASException;

    public int appendsubModel(ASObject var1) throws DOMASException;
}

