/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl;

import java.util.List;
import javax.wsdl.BindingOperation;
import javax.wsdl.PortType;
import javax.wsdl.WSDLElement;
import javax.xml.namespace.QName;

public interface Binding
extends WSDLElement {
    public void setQName(QName var1);

    public QName getQName();

    public void setPortType(PortType var1);

    public PortType getPortType();

    public void addBindingOperation(BindingOperation var1);

    public BindingOperation getBindingOperation(String var1, String var2, String var3);

    public List getBindingOperations();

    public BindingOperation removeBindingOperation(String var1, String var2, String var3);

    public void setUndefined(boolean var1);

    public boolean isUndefined();
}

