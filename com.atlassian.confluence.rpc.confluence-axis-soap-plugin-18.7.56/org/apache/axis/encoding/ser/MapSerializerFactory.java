/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import javax.xml.namespace.QName;
import org.apache.axis.encoding.ser.BaseSerializerFactory;

public class MapSerializerFactory
extends BaseSerializerFactory {
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$MapSerializer;

    public MapSerializerFactory(Class javaType, QName xmlType) {
        super(class$org$apache$axis$encoding$ser$MapSerializer == null ? (class$org$apache$axis$encoding$ser$MapSerializer = MapSerializerFactory.class$("org.apache.axis.encoding.ser.MapSerializer")) : class$org$apache$axis$encoding$ser$MapSerializer, xmlType, javaType);
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

