/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl;

import java.util.Map;
import javax.wsdl.Port;
import javax.wsdl.WSDLElement;
import javax.xml.namespace.QName;

public interface Service
extends WSDLElement {
    public void setQName(QName var1);

    public QName getQName();

    public void addPort(Port var1);

    public Port getPort(String var1);

    public Port removePort(String var1);

    public Map getPorts();
}

