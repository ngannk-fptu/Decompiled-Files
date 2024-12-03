/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import javax.xml.namespace.QName;
import org.apache.axis.encoding.ser.SimpleDeserializer;
import org.apache.axis.types.HexBinary;

public class HexDeserializer
extends SimpleDeserializer {
    static /* synthetic */ Class array$B;

    public HexDeserializer(Class javaType, QName xmlType) {
        super(javaType, xmlType);
    }

    public Object makeValue(String source) throws Exception {
        Object result = this.javaType == (array$B == null ? (array$B = HexDeserializer.class$("[B")) : array$B) ? HexBinary.decode(source) : (Object)new HexBinary(source);
        if (result == null) {
            result = new HexBinary("");
        }
        return result;
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

