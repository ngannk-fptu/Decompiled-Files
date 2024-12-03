/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser.castor;

import javax.xml.namespace.QName;
import org.apache.axis.encoding.ser.BaseDeserializerFactory;

public class CastorDeserializerFactory
extends BaseDeserializerFactory {
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$castor$CastorDeserializer;

    public CastorDeserializerFactory(Class javaType, QName xmlType) {
        super(class$org$apache$axis$encoding$ser$castor$CastorDeserializer == null ? (class$org$apache$axis$encoding$ser$castor$CastorDeserializer = CastorDeserializerFactory.class$("org.apache.axis.encoding.ser.castor.CastorDeserializer")) : class$org$apache$axis$encoding$ser$castor$CastorDeserializer, xmlType, javaType);
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

