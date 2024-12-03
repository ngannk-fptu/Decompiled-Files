/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import java.io.IOException;
import java.io.ObjectInputStream;
import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.axis.encoding.ser.BeanSerializer;
import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.BeanUtils;
import org.apache.axis.utils.JavaUtils;

public class BeanSerializerFactory
extends BaseSerializerFactory {
    protected transient TypeDesc typeDesc = null;
    protected transient BeanPropertyDescriptor[] propertyDescriptor = null;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$BeanSerializer;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$EnumSerializer;

    public BeanSerializerFactory(Class javaType, QName xmlType) {
        super(class$org$apache$axis$encoding$ser$BeanSerializer == null ? (class$org$apache$axis$encoding$ser$BeanSerializer = BeanSerializerFactory.class$("org.apache.axis.encoding.ser.BeanSerializer")) : class$org$apache$axis$encoding$ser$BeanSerializer, xmlType, javaType);
        this.init(javaType);
    }

    private void init(Class javaType) {
        if (JavaUtils.isEnumClass(javaType)) {
            this.serClass = class$org$apache$axis$encoding$ser$EnumSerializer == null ? (class$org$apache$axis$encoding$ser$EnumSerializer = BeanSerializerFactory.class$("org.apache.axis.encoding.ser.EnumSerializer")) : class$org$apache$axis$encoding$ser$EnumSerializer;
        }
        this.typeDesc = TypeDesc.getTypeDescForClass(javaType);
        this.propertyDescriptor = this.typeDesc != null ? this.typeDesc.getPropertyDescriptors() : BeanUtils.getPd(javaType, null);
    }

    public javax.xml.rpc.encoding.Serializer getSerializerAs(String mechanismType) throws JAXRPCException {
        return (Serializer)super.getSerializerAs(mechanismType);
    }

    protected Serializer getGeneralPurpose(String mechanismType) {
        if (this.javaType == null || this.xmlType == null) {
            return super.getGeneralPurpose(mechanismType);
        }
        if (this.serClass == (class$org$apache$axis$encoding$ser$EnumSerializer == null ? (class$org$apache$axis$encoding$ser$EnumSerializer = BeanSerializerFactory.class$("org.apache.axis.encoding.ser.EnumSerializer")) : class$org$apache$axis$encoding$ser$EnumSerializer)) {
            return super.getGeneralPurpose(mechanismType);
        }
        return new BeanSerializer(this.javaType, this.xmlType, this.typeDesc, this.propertyDescriptor);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.init(this.javaType);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

