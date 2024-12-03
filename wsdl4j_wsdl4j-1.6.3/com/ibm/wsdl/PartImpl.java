/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl;

import com.ibm.wsdl.AbstractWSDLElement;
import com.ibm.wsdl.Constants;
import java.util.Arrays;
import java.util.List;
import javax.wsdl.Part;
import javax.xml.namespace.QName;

public class PartImpl
extends AbstractWSDLElement
implements Part {
    protected String name = null;
    protected QName elementName = null;
    protected QName typeName = null;
    protected List nativeAttributeNames = Arrays.asList(Constants.PART_ATTR_NAMES);
    public static final long serialVersionUID = 1L;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setElementName(QName elementName) {
        this.elementName = elementName;
    }

    public QName getElementName() {
        return this.elementName;
    }

    public void setTypeName(QName typeName) {
        this.typeName = typeName;
    }

    public QName getTypeName() {
        return this.typeName;
    }

    public List getNativeAttributeNames() {
        return this.nativeAttributeNames;
    }

    public String toString() {
        String superString;
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("Part: name=" + this.name);
        if (this.elementName != null) {
            strBuf.append("\nelementName=" + this.elementName);
        }
        if (this.typeName != null) {
            strBuf.append("\ntypeName=" + this.typeName);
        }
        if (!(superString = super.toString()).equals("")) {
            strBuf.append("\n");
            strBuf.append(superString);
        }
        return strBuf.toString();
    }
}

