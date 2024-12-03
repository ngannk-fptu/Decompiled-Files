/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.encoding.Serializer;
import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.axis.encoding.ser.SimpleSerializer;
import org.apache.axis.utils.JavaUtils;

public class SimpleSerializerFactory
extends BaseSerializerFactory {
    private boolean isBasicType = false;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$SimpleSerializer;

    public SimpleSerializerFactory(Class javaType, QName xmlType) {
        super(class$org$apache$axis$encoding$ser$SimpleSerializer == null ? (class$org$apache$axis$encoding$ser$SimpleSerializer = SimpleSerializerFactory.class$("org.apache.axis.encoding.ser.SimpleSerializer")) : class$org$apache$axis$encoding$ser$SimpleSerializer, xmlType, javaType);
        this.isBasicType = JavaUtils.isBasic(javaType);
    }

    public Serializer getSerializerAs(String mechanismType) throws JAXRPCException {
        if (this.isBasicType) {
            return new SimpleSerializer(this.javaType, this.xmlType);
        }
        return super.getSerializerAs(mechanismType);
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

