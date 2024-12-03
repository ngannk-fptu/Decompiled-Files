/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  test.wsdl.import2.types.StringType
 */
package org.globus.test.otherTypes;

import java.io.Serializable;
import javax.xml.namespace.QName;
import org.apache.axis.description.ElementDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ser.BeanDeserializer;
import org.apache.axis.encoding.ser.BeanSerializer;
import test.wsdl.import2.types.StringType;

public class OtherType
implements Serializable {
    private Integer myString;
    private StringType two;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;
    private static TypeDesc typeDesc = new TypeDesc(class$org$globus$test$otherTypes$OtherType == null ? (class$org$globus$test$otherTypes$OtherType = OtherType.class$("org.globus.test.otherTypes.OtherType")) : class$org$globus$test$otherTypes$OtherType, true);
    static /* synthetic */ Class class$org$globus$test$otherTypes$OtherType;

    public OtherType() {
    }

    public OtherType(Integer myString, StringType two) {
        this.myString = myString;
        this.two = two;
    }

    public Integer getMyString() {
        return this.myString;
    }

    public void setMyString(Integer myString) {
        this.myString = myString;
    }

    public StringType getTwo() {
        return this.two;
    }

    public void setTwo(StringType two) {
        this.two = two;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof OtherType)) {
            return false;
        }
        OtherType other = (OtherType)obj;
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (this.__equalsCalc != null) {
            return this.__equalsCalc == obj;
        }
        this.__equalsCalc = obj;
        boolean _equals = (this.myString == null && other.getMyString() == null || this.myString != null && this.myString.equals(other.getMyString())) && (this.two == null && other.getTwo() == null || this.two != null && this.two.equals((Object)other.getTwo()));
        this.__equalsCalc = null;
        return _equals;
    }

    public synchronized int hashCode() {
        if (this.__hashCodeCalc) {
            return 0;
        }
        this.__hashCodeCalc = true;
        int _hashCode = 1;
        if (this.getMyString() != null) {
            _hashCode += this.getMyString().hashCode();
        }
        if (this.getTwo() != null) {
            _hashCode += this.getTwo().hashCode();
        }
        this.__hashCodeCalc = false;
        return _hashCode;
    }

    public static TypeDesc getTypeDesc() {
        return typeDesc;
    }

    public static Serializer getSerializer(String mechType, Class _javaType, QName _xmlType) {
        return new BeanSerializer(_javaType, _xmlType, typeDesc);
    }

    public static Deserializer getDeserializer(String mechType, Class _javaType, QName _xmlType) {
        return new BeanDeserializer(_javaType, _xmlType, typeDesc);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        typeDesc.setXmlType(new QName("http://test.globus.org/otherTypes", "OtherType"));
        ElementDesc elemField = new ElementDesc();
        elemField.setFieldName("myString");
        elemField.setXmlName(new QName("", "myString"));
        elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new ElementDesc();
        elemField.setFieldName("two");
        elemField.setXmlName(new QName("", "two"));
        elemField.setXmlType(new QName("http://test.globus.org/types", "StringType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }
}

