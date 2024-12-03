/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.Deserializer;
import org.apache.axis.encoding.ser.ArrayDeserializer;
import org.apache.axis.encoding.ser.BaseDeserializerFactory;

public class ArrayDeserializerFactory
extends BaseDeserializerFactory {
    private QName componentXmlType;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$ArrayDeserializer;

    public ArrayDeserializerFactory() {
        super(class$org$apache$axis$encoding$ser$ArrayDeserializer == null ? (class$org$apache$axis$encoding$ser$ArrayDeserializer = ArrayDeserializerFactory.class$("org.apache.axis.encoding.ser.ArrayDeserializer")) : class$org$apache$axis$encoding$ser$ArrayDeserializer);
    }

    public ArrayDeserializerFactory(QName componentXmlType) {
        super(class$org$apache$axis$encoding$ser$ArrayDeserializer == null ? (class$org$apache$axis$encoding$ser$ArrayDeserializer = ArrayDeserializerFactory.class$("org.apache.axis.encoding.ser.ArrayDeserializer")) : class$org$apache$axis$encoding$ser$ArrayDeserializer);
        this.componentXmlType = componentXmlType;
    }

    public Deserializer getDeserializerAs(String mechanismType) {
        ArrayDeserializer dser = (ArrayDeserializer)super.getDeserializerAs(mechanismType);
        dser.defaultItemType = this.componentXmlType;
        return dser;
    }

    public void setComponentType(QName componentType) {
        this.componentXmlType = componentType;
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

