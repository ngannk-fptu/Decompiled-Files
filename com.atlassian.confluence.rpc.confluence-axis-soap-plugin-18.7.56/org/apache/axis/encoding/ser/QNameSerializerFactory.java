/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import javax.xml.namespace.QName;
import org.apache.axis.encoding.ser.BaseSerializerFactory;

public class QNameSerializerFactory
extends BaseSerializerFactory {
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$QNameSerializer;

    public QNameSerializerFactory(Class javaType, QName xmlType) {
        super(class$org$apache$axis$encoding$ser$QNameSerializer == null ? (class$org$apache$axis$encoding$ser$QNameSerializer = QNameSerializerFactory.class$("org.apache.axis.encoding.ser.QNameSerializer")) : class$org$apache$axis$encoding$ser$QNameSerializer, xmlType, javaType);
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

