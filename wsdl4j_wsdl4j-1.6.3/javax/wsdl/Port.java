/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl;

import javax.wsdl.Binding;
import javax.wsdl.WSDLElement;

public interface Port
extends WSDLElement {
    public void setName(String var1);

    public String getName();

    public void setBinding(Binding var1);

    public Binding getBinding();
}

