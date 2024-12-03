/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl;

import com.ibm.wsdl.AbstractWSDLElement;
import com.ibm.wsdl.Constants;
import java.util.Arrays;
import java.util.List;
import javax.wsdl.BindingFault;

public class BindingFaultImpl
extends AbstractWSDLElement
implements BindingFault {
    protected String name = null;
    protected List nativeAttributeNames = Arrays.asList(Constants.BINDING_FAULT_ATTR_NAMES);
    public static final long serialVersionUID = 1L;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("BindingFault: name=");
        strBuf.append(this.name);
        String superString = super.toString();
        if (!superString.equals("")) {
            strBuf.append("\n");
            strBuf.append(superString);
        }
        return strBuf.toString();
    }

    public List getNativeAttributeNames() {
        return this.nativeAttributeNames;
    }
}

