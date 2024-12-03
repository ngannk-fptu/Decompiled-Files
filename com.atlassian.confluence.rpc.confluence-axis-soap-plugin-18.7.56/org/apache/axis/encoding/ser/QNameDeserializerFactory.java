/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import javax.xml.namespace.QName;
import org.apache.axis.encoding.ser.BaseDeserializerFactory;

public class QNameDeserializerFactory
extends BaseDeserializerFactory {
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$QNameDeserializer;

    public QNameDeserializerFactory(Class javaType, QName xmlType) {
        super(class$org$apache$axis$encoding$ser$QNameDeserializer == null ? (class$org$apache$axis$encoding$ser$QNameDeserializer = QNameDeserializerFactory.class$("org.apache.axis.encoding.ser.QNameDeserializer")) : class$org$apache$axis$encoding$ser$QNameDeserializer, xmlType, javaType);
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

