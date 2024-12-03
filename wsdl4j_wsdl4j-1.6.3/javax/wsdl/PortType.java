/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl;

import java.util.List;
import javax.wsdl.Operation;
import javax.wsdl.WSDLElement;
import javax.xml.namespace.QName;

public interface PortType
extends WSDLElement {
    public void setQName(QName var1);

    public QName getQName();

    public void addOperation(Operation var1);

    public Operation getOperation(String var1, String var2, String var3);

    public List getOperations();

    public Operation removeOperation(String var1, String var2, String var3);

    public void setUndefined(boolean var1);

    public boolean isUndefined();
}

