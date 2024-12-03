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
import java.util.Vector;
import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.xml.namespace.QName;

public class MessageImpl
extends AbstractWSDLElement
implements Message {
    protected Map parts = new HashMap();
    protected List additionOrderOfParts = new Vector();
    protected QName name = null;
    protected List nativeAttributeNames = Arrays.asList(Constants.MESSAGE_ATTR_NAMES);
    protected boolean isUndefined = true;
    public static final long serialVersionUID = 1L;

    public void setQName(QName name) {
        this.name = name;
    }

    public QName getQName() {
        return this.name;
    }

    public void addPart(Part part) {
        String partName = part.getName();
        this.parts.put(partName, part);
        this.additionOrderOfParts.add(partName);
    }

    public Part getPart(String name) {
        return (Part)this.parts.get(name);
    }

    public Part removePart(String name) {
        return (Part)this.parts.remove(name);
    }

    public Map getParts() {
        return this.parts;
    }

    public List getOrderedParts(List partOrder) {
        Vector<Part> orderedParts = new Vector<Part>();
        if (partOrder == null) {
            partOrder = this.additionOrderOfParts;
        }
        for (String partName : partOrder) {
            Part part = this.getPart(partName);
            if (part == null) continue;
            orderedParts.add(part);
        }
        return orderedParts;
    }

    public void setUndefined(boolean isUndefined) {
        this.isUndefined = isUndefined;
    }

    public boolean isUndefined() {
        return this.isUndefined;
    }

    public String toString() {
        String superString;
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("Message: name=" + this.name);
        if (this.parts != null) {
            Iterator partsIterator = this.parts.values().iterator();
            while (partsIterator.hasNext()) {
                strBuf.append("\n" + partsIterator.next());
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

