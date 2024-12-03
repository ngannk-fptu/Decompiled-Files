/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl;

import com.ibm.wsdl.AbstractWSDLElement;
import com.ibm.wsdl.Constants;
import java.util.Arrays;
import java.util.List;
import javax.wsdl.Types;

public class TypesImpl
extends AbstractWSDLElement
implements Types {
    protected List nativeAttributeNames = Arrays.asList(Constants.TYPES_ATTR_NAMES);
    public static final long serialVersionUID = 1L;

    public String toString() {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("Types:");
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

