/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import javax.xml.namespace.QName;
import org.apache.axis.Constants;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ser.ArraySerializer;
import org.apache.axis.encoding.ser.BaseSerializerFactory;

public class ArraySerializerFactory
extends BaseSerializerFactory {
    private QName componentType = null;
    private QName componentQName = null;
    static /* synthetic */ Class array$Ljava$lang$Object;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$ArraySerializer;

    public ArraySerializerFactory() {
        this(array$Ljava$lang$Object == null ? (array$Ljava$lang$Object = ArraySerializerFactory.class$("[Ljava.lang.Object;")) : array$Ljava$lang$Object, Constants.SOAP_ARRAY);
    }

    public ArraySerializerFactory(Class javaType, QName xmlType) {
        super(class$org$apache$axis$encoding$ser$ArraySerializer == null ? (class$org$apache$axis$encoding$ser$ArraySerializer = ArraySerializerFactory.class$("org.apache.axis.encoding.ser.ArraySerializer")) : class$org$apache$axis$encoding$ser$ArraySerializer, xmlType, javaType);
    }

    public ArraySerializerFactory(QName componentType) {
        super(class$org$apache$axis$encoding$ser$ArraySerializer == null ? (class$org$apache$axis$encoding$ser$ArraySerializer = ArraySerializerFactory.class$("org.apache.axis.encoding.ser.ArraySerializer")) : class$org$apache$axis$encoding$ser$ArraySerializer, Constants.SOAP_ARRAY, array$Ljava$lang$Object == null ? (array$Ljava$lang$Object = ArraySerializerFactory.class$("[Ljava.lang.Object;")) : array$Ljava$lang$Object);
        this.componentType = componentType;
    }

    public ArraySerializerFactory(QName componentType, QName componentQName) {
        this(componentType);
        this.componentQName = componentQName;
    }

    public void setComponentQName(QName componentQName) {
        this.componentQName = componentQName;
    }

    public void setComponentType(QName componentType) {
        this.componentType = componentType;
    }

    public QName getComponentQName() {
        return this.componentQName;
    }

    public QName getComponentType() {
        return this.componentType;
    }

    protected Serializer getGeneralPurpose(String mechanismType) {
        if (this.componentType == null) {
            return super.getGeneralPurpose(mechanismType);
        }
        return new ArraySerializer(this.javaType, this.xmlType, this.componentType, this.componentQName);
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

