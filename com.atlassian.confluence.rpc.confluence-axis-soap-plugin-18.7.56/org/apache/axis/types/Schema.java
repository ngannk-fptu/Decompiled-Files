/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.types;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import javax.xml.namespace.QName;
import org.apache.axis.description.AttributeDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ser.BeanDeserializer;
import org.apache.axis.encoding.ser.BeanSerializer;
import org.apache.axis.message.MessageElement;
import org.apache.axis.types.Id;
import org.apache.axis.types.NormalizedString;
import org.apache.axis.types.URI;

public class Schema
implements Serializable {
    private MessageElement[] _any;
    private URI targetNamespace;
    private NormalizedString version;
    private Id id;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;
    private static TypeDesc typeDesc = new TypeDesc(class$org$apache$axis$types$Schema == null ? (class$org$apache$axis$types$Schema = Schema.class$("org.apache.axis.types.Schema")) : class$org$apache$axis$types$Schema);
    static /* synthetic */ Class class$org$apache$axis$types$Schema;

    public MessageElement[] get_any() {
        return this._any;
    }

    public void set_any(MessageElement[] _any) {
        this._any = _any;
    }

    public URI getTargetNamespace() {
        return this.targetNamespace;
    }

    public void setTargetNamespace(URI targetNamespace) {
        this.targetNamespace = targetNamespace;
    }

    public NormalizedString getVersion() {
        return this.version;
    }

    public void setVersion(NormalizedString version) {
        this.version = version;
    }

    public Id getId() {
        return this.id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof Schema)) {
            return false;
        }
        Schema other = (Schema)obj;
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
        boolean _equals = (this._any == null && other.get_any() == null || this._any != null && Arrays.equals(this._any, other.get_any())) && (this.targetNamespace == null && other.getTargetNamespace() == null || this.targetNamespace != null && this.targetNamespace.equals(other.getTargetNamespace())) && (this.version == null && other.getVersion() == null || this.version != null && this.version.equals(other.getVersion())) && (this.id == null && other.getId() == null || this.id != null && this.id.equals(other.getId()));
        this.__equalsCalc = null;
        return _equals;
    }

    public synchronized int hashCode() {
        if (this.__hashCodeCalc) {
            return 0;
        }
        this.__hashCodeCalc = true;
        int _hashCode = 1;
        if (this.get_any() != null) {
            for (int i = 0; i < Array.getLength(this.get_any()); ++i) {
                Object obj = Array.get(this.get_any(), i);
                if (obj == null || obj.getClass().isArray()) continue;
                _hashCode += obj.hashCode();
            }
        }
        if (this.getTargetNamespace() != null) {
            _hashCode += this.getTargetNamespace().hashCode();
        }
        if (this.getVersion() != null) {
            _hashCode += this.getVersion().hashCode();
        }
        if (this.getId() != null) {
            _hashCode += this.getId().hashCode();
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
        AttributeDesc field = new AttributeDesc();
        field.setFieldName("targetNamespace");
        field.setXmlName(new QName("", "targetNamespace"));
        field.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        typeDesc.addFieldDesc(field);
        field = new AttributeDesc();
        field.setFieldName("version");
        field.setXmlName(new QName("", "version"));
        field.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "normalizedString"));
        typeDesc.addFieldDesc(field);
        field = new AttributeDesc();
        field.setFieldName("id");
        field.setXmlName(new QName("", "id"));
        field.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "ID"));
        typeDesc.addFieldDesc(field);
    }
}

