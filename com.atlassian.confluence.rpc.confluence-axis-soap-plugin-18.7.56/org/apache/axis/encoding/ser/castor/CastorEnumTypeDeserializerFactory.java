/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser.castor;

import javax.xml.namespace.QName;
import org.apache.axis.encoding.ser.BaseDeserializerFactory;

public class CastorEnumTypeDeserializerFactory
extends BaseDeserializerFactory {
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$castor$CastorEnumTypeDeserializer;

    public CastorEnumTypeDeserializerFactory(Class javaType, QName xmlType) {
        super(class$org$apache$axis$encoding$ser$castor$CastorEnumTypeDeserializer == null ? (class$org$apache$axis$encoding$ser$castor$CastorEnumTypeDeserializer = CastorEnumTypeDeserializerFactory.class$("org.apache.axis.encoding.ser.castor.CastorEnumTypeDeserializer")) : class$org$apache$axis$encoding$ser$castor$CastorEnumTypeDeserializer, xmlType, javaType);
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

