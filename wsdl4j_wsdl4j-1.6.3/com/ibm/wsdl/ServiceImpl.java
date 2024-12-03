/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl;

import com.ibm.wsdl.AbstractWSDLElement;
import com.ibm.wsdl.Constants;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.xml.namespace.QName;

public class ServiceImpl
extends AbstractWSDLElement
implements Service {
    protected QName name = null;
    protected Map ports = new HashMap();
    protected List nativeAttributeNames = Arrays.asList(Constants.SERVICE_ATTR_NAMES);
    public static final long serialVersionUID = 1L;

    public void setQName(QName name) {
        this.name = name;
    }

    public QName getQName() {
        return this.name;
    }

    public void addPort(Port port) {
        this.ports.put(port.getName(), port);
    }

    public Port getPort(String name) {
        return (Port)this.ports.get(name);
    }

    public Port removePort(String name) {
        return (Port)this.ports.remove(name);
    }

    public Map getPorts() {
        return this.ports;
    }

    public String toString() {
        String superString;
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("Service: name=" + this.name);
        if (this.ports != null) {
            Iterator portIterator = this.ports.values().iterator();
            while (portIterator.hasNext()) {
                strBuf.append("\n" + portIterator.next());
            }
        }
        if (!(superString = super.toString()).equals("")) {
            strBuf.append("\n");
            strBuf.append(superString);
        }
        return strBuf.toString();
    }

    public List getNativeAttributeNames() {
        return this.nativeAttributeNames;
    }
}

