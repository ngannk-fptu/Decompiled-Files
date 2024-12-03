/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xs;

import org.apache.xerces.xs.XSObject;

public interface XSTypeDefinition
extends XSObject {
    public static final short COMPLEX_TYPE = 15;
    public static final short SIMPLE_TYPE = 16;

    public short getTypeCategory();

    public XSTypeDefinition getBaseType();

    public boolean isFinal(short var1);

    public short getFinal();

    public boolean getAnonymous();

    public boolean derivedFromType(XSTypeDefinition var1, short var2);

    public boolean derivedFrom(String var1, String var2, short var3);
}

