/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.description;

import java.io.Serializable;
import javax.xml.namespace.QName;

public class FieldDesc
implements Serializable {
    private String fieldName;
    private QName xmlName;
    private QName xmlType;
    private Class javaType;
    private boolean _isElement = true;
    private boolean minOccursIs0 = false;

    protected FieldDesc(boolean isElement) {
        this._isElement = isElement;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public QName getXmlName() {
        return this.xmlName;
    }

    public void setXmlName(QName xmlName) {
        this.xmlName = xmlName;
    }

    public Class getJavaType() {
        return this.javaType;
    }

    public void setJavaType(Class javaType) {
        this.javaType = javaType;
    }

    public QName getXmlType() {
        return this.xmlType;
    }

    public void setXmlType(QName xmlType) {
        this.xmlType = xmlType;
    }

    public boolean isElement() {
        return this._isElement;
    }

    public boolean isIndexed() {
        return false;
    }

    public boolean isMinOccursZero() {
        return this.minOccursIs0;
    }

    public void setMinOccursIs0(boolean minOccursIs0) {
        this.minOccursIs0 = minOccursIs0;
    }
}

