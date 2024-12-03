/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xs;

import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;

public interface XSIDCDefinition
extends XSObject {
    public static final short IC_KEY = 1;
    public static final short IC_KEYREF = 2;
    public static final short IC_UNIQUE = 3;

    public short getCategory();

    public String getSelectorStr();

    public StringList getFieldStrs();

    public XSIDCDefinition getRefKey();

    public XSObjectList getAnnotations();
}

