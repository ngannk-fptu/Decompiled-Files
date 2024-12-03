/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl;

import com.ibm.wsdl.AbstractWSDLElement;
import com.ibm.wsdl.Constants;
import java.util.Arrays;
import java.util.List;
import javax.wsdl.Fault;
import javax.wsdl.Message;

public class FaultImpl
extends AbstractWSDLElement
implements Fault {
    protected String name = null;
    protected Message message = null;
    protected List nativeAttributeNames = Arrays.asList(Constants.FAULT_ATTR_NAMES);
    public static final long serialVersionUID = 1L;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return this.message;
    }

    public List getNativeAttributeNames() {
        return this.nativeAttributeNames;
    }

    public String toString() {
        String superString;
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("Fault: name=" + this.name);
        if (this.message != null) {
            strBuf.append("\n" + this.message);
        }
        if (!(superString = super.toString()).equals("")) {
            strBuf.append("\n");
            strBuf.append(superString);
        }
        return strBuf.toString();
    }
}

