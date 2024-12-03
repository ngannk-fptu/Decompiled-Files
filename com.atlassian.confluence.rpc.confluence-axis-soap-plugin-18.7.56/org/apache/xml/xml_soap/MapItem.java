/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.xml_soap;

import java.io.Serializable;
import javax.xml.namespace.QName;
import org.apache.axis.description.ElementDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ser.BeanDeserializer;
import org.apache.axis.encoding.ser.BeanSerializer;

public class MapItem
implements Serializable {
    private Object key;
    private Object value;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;
    private static TypeDesc typeDesc = new TypeDesc(class$org$apache$xml$xml_soap$MapItem == null ? (class$org$apache$xml$xml_soap$MapItem = MapItem.class$("org.apache.xml.xml_soap.MapItem")) : class$org$apache$xml$xml_soap$MapItem, true);
    static /* synthetic */ Class class$org$apache$xml$xml_soap$MapItem;

    public MapItem() {
    }

    public MapItem(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    public Object getKey() {
        return this.key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof MapItem)) {
            return false;
        }
        MapItem other = (MapItem)obj;
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
        boolean _equals = (this.key == null && other.getKey() == null || this.key != null && this.key.equals(other.getKey())) && (this.value == null && other.getValue() == null || this.value != null && this.value.equals(other.getValue()));
        this.__equalsCalc = null;
        return _equals;
    }

    public synchronized int hashCode() {
        if (this.__hashCodeCalc) {
            return 0;
        }
        this.__hashCodeCalc = true;
        int _hashCode = 1;
        if (this.getKey() != null) {
            _hashCode += this.getKey().hashCode();
        }
        if (this.getValue() != null) {
            _hashCode += this.getValue().hashCode();
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
        typeDesc.setXmlType(new QName("http://xml.apache.org/xml-soap", "mapItem"));
        ElementDesc elemField = new ElementDesc();
        elemField.setFieldName("key");
        elemField.setXmlName(new QName("", "key"));
        elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new ElementDesc();
        elemField.setFieldName("value");
        elemField.setXmlName(new QName("", "value"));
        elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }
}

