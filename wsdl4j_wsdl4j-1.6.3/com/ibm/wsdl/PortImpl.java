/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl;

import com.ibm.wsdl.AbstractWSDLElement;
import com.ibm.wsdl.Constants;
import java.util.Arrays;
import java.util.List;
import javax.wsdl.Binding;
import javax.wsdl.Port;

public class PortImpl
extends AbstractWSDLElement
implements Port {
    protected String name = null;
    protected Binding binding = null;
    protected List nativeAttributeNames = Arrays.asList(Constants.PORT_ATTR_NAMES);
    public static final long serialVersionUID = 1L;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setBinding(Binding binding) {
        this.binding = binding;
    }

    public Binding getBinding() {
        return this.binding;
    }

    public String toString() {
        String superString;
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("Port: name=" + this.name);
        if (this.binding != null) {
            strBuf.append("\n" + this.binding);
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

