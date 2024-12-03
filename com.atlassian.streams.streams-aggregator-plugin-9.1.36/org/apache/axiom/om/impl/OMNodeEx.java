/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMNode;

public interface OMNodeEx
extends OMNode {
    public void setNextOMSibling(OMNode var1);

    public void setPreviousOMSibling(OMNode var1);

    public void setParent(OMContainer var1);

    public void setComplete(boolean var1);

    public void internalSerialize(XMLStreamWriter var1, boolean var2) throws XMLStreamException;

    public void internalSerialize(XMLStreamWriter var1) throws XMLStreamException;

    public void internalSerializeAndConsume(XMLStreamWriter var1) throws XMLStreamException;

    public OMNode getNextOMSiblingIfAvailable();
}

