/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl;

import javax.wsdl.Definition;
import javax.wsdl.WSDLElement;

public interface Import
extends WSDLElement {
    public void setNamespaceURI(String var1);

    public String getNamespaceURI();

    public void setLocationURI(String var1);

    public String getLocationURI();

    public void setDefinition(Definition var1);

    public Definition getDefinition();
}

