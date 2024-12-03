/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap;

import org.apache.axiom.om.OMElement;

public interface SOAPFaultNode
extends OMElement {
    public void setNodeValue(String var1);

    public String getNodeValue();

    public void setFaultNodeValue(String var1);

    public String getFaultNodeValue();
}

