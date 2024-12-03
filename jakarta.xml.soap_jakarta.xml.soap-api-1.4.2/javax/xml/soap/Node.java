/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public interface Node
extends org.w3c.dom.Node {
    public String getValue();

    public void setValue(String var1);

    public void setParentElement(SOAPElement var1) throws SOAPException;

    public SOAPElement getParentElement();

    public void detachNode();

    public void recycleNode();
}

