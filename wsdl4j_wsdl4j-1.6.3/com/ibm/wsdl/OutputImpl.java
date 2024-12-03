/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl;

import com.ibm.wsdl.AbstractWSDLElement;
import com.ibm.wsdl.Constants;
import java.util.Arrays;
import java.util.List;
import javax.wsdl.Message;
import javax.wsdl.Output;

public class OutputImpl
extends AbstractWSDLElement
implements Output {
    protected String name = null;
    protected Message message = null;
    protected List nativeAttributeNames = Arrays.asList(Constants.OUTPUT_ATTR_NAMES);
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
        strBuf.append("Output: name=" + this.name);
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

