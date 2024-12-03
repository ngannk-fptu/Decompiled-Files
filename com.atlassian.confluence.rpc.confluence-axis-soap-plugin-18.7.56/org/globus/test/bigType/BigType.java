/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  test.wsdl.import2.types.StringType
 */
package org.globus.test.bigType;

import java.io.Serializable;
import javax.xml.namespace.QName;
import org.apache.axis.description.ElementDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ser.BeanDeserializer;
import org.apache.axis.encoding.ser.BeanSerializer;
import org.globus.test.otherTypes.OtherType;
import test.wsdl.import2.types.StringType;

public class BigType
implements Serializable {
    private String one;
    private StringType two;
    private OtherType three;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;
    private static TypeDesc typeDesc = new TypeDesc(class$org$globus$test$bigType$BigType == null ? (class$org$globus$test$bigType$BigType = BigType.class$("org.globus.test.bigType.BigType")) : class$org$globus$test$bigType$BigType, true);
    static /* synthetic */ Class class$org$globus$test$bigType$BigType;

    public BigType() {
    }

    public BigType(String one, StringType two, OtherType three) {
        this.one = one;
        this.two = two;
        this.three = three;
    }

    public String getOne() {
        return this.one;
    }

    public void setOne(String one) {
        this.one = one;
    }

    public StringType getTwo() {
        return this.two;
    }

    public void setTwo(StringType two) {
        this.two = two;
    }

    public OtherType getThree() {
        return this.three;
    }

    public void setThree(OtherType three) {
        this.three = three;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof BigType)) {
            return false;
        }
        BigType other = (BigType)obj;
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
        boolean _equals = (this.one == null && other.getOne() == null || this.one != null && this.one.equals(other.getOne())) && (this.two == null && other.getTwo() == null || this.two != null && this.two.equals((Object)other.getTwo())) && (this.three == null && other.getThree() == null || this.three != null && this.three.equals(other.getThree()));
        this.__equalsCalc = null;
        return _equals;
    }

    public synchronized int hashCode() {
        if (this.__hashCodeCalc) {
            return 0;
        }
        this.__hashCodeCalc = true;
        int _hashCode = 1;
        if (this.getOne() != null) {
            _hashCode += this.getOne().hashCode();
        }
        if (this.getTwo() != null) {
            _hashCode += this.getTwo().hashCode();
        }
        if (this.getThree() != null) {
            _hashCode += this.getThree().hashCode();
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
        typeDesc.setXmlType(new QName("http://test.globus.org/bigType", "BigType"));
        ElementDesc elemField = new ElementDesc();
        elemField.setFieldName("one");
        elemField.setXmlName(new QName("", "one"));
        elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new ElementDesc();
        elemField.setFieldName("two");
        elemField.setXmlName(new QName("", "two"));
        elemField.setXmlType(new QName("http://test.globus.org/types", "StringType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new ElementDesc();
        elemField.setFieldName("three");
        elemField.setXmlName(new QName("", "three"));
        elemField.setXmlType(new QName("http://test.globus.org/otherTypes", "OtherType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }
}

