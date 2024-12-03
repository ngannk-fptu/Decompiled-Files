/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl;

import javax.wsdl.WSDLElement;
import javax.xml.namespace.QName;

public interface Part
extends WSDLElement {
    public void setName(String var1);

    public String getName();

    public void setElementName(QName var1);

    public QName getElementName();

    public void setTypeName(QName var1);

    public QName getTypeName();
}

