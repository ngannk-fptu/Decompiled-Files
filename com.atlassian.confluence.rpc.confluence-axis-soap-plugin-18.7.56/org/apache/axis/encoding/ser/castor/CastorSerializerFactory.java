/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser.castor;

import javax.xml.namespace.QName;
import org.apache.axis.encoding.ser.BaseSerializerFactory;

public class CastorSerializerFactory
extends BaseSerializerFactory {
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$castor$CastorSerializer;

    public CastorSerializerFactory(Class javaType, QName xmlType) {
        super(class$org$apache$axis$encoding$ser$castor$CastorSerializer == null ? (class$org$apache$axis$encoding$ser$castor$CastorSerializer = CastorSerializerFactory.class$("org.apache.axis.encoding.ser.castor.CastorSerializer")) : class$org$apache$axis$encoding$ser$castor$CastorSerializer, xmlType, javaType);
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

