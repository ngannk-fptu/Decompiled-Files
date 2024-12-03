/*
 * Decompiled with CFR 0.152.
 */
package org.xml.sax.helpers;

import java.util.Vector;
import org.xml.sax.AttributeList;

public class AttributeListImpl
implements AttributeList {
    Vector names = new Vector();
    Vector types = new Vector();
    Vector values = new Vector();

    public AttributeListImpl() {
    }

    public AttributeListImpl(AttributeList attributeList) {
        this.setAttributeList(attributeList);
    }

    public void setAttributeList(AttributeList attributeList) {
        int n = attributeList.getLength();
        this.clear();
        int n2 = 0;
        while (n2 < n) {
            this.addAttribute(attributeList.getName(n2), attributeList.getType(n2), attributeList.getValue(n2));
            ++n2;
        }
    }

    public void addAttribute(String string, String string2, String string3) {
        this.names.addElement(string);
        this.types.addElement(string2);
        this.values.addElement(string3);
    }

    public void removeAttribute(String string) {
        int n = this.names.indexOf(string);
        if (n >= 0) {
            this.names.removeElementAt(n);
            this.types.removeElementAt(n);
            this.values.removeElementAt(n);
        }
    }

    public void clear() {
        this.names.removeAllElements();
        this.types.removeAllElements();
        this.values.removeAllElements();
    }

    public int getLength() {
        return this.names.size();
    }

    public String getName(int n) {
        if (n < 0) {
            return null;
        }
        try {
            return (String)this.names.elementAt(n);
        }
        catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            return null;
        }
    }

    public String getType(int n) {
        if (n < 0) {
            return null;
        }
        try {
            return (String)this.types.elementAt(n);
        }
        catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            return null;
        }
    }

    public String getValue(int n) {
        if (n < 0) {
            return null;
        }
        try {
            return (String)this.values.elementAt(n);
        }
        catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            return null;
        }
    }

    public String getType(String string) {
        return this.getType(this.names.indexOf(string));
    }

    public String getValue(String string) {
        return this.getValue(this.names.indexOf(string));
    }
}

