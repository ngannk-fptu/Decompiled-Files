/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamedInformationItem;
import org.apache.axiom.om.OMNamespace;

public interface OMAttribute
extends OMNamedInformationItem {
    public String getAttributeValue();

    public void setAttributeValue(String var1);

    public String getAttributeType();

    public void setAttributeType(String var1);

    public void setOMNamespace(OMNamespace var1);

    public OMElement getOwner();
}

