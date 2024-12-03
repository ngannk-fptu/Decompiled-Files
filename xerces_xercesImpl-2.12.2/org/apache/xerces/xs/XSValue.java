/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xs;

import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;

public interface XSValue {
    public String getNormalizedValue();

    public Object getActualValue();

    public XSSimpleTypeDefinition getTypeDefinition();

    public XSSimpleTypeDefinition getMemberTypeDefinition();

    public XSObjectList getMemberTypeDefinitions();

    public short getActualValueType();

    public ShortList getListValueTypes();
}

