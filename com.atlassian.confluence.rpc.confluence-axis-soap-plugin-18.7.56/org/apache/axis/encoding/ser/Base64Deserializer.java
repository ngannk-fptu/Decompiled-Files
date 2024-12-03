/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import javax.xml.namespace.QName;
import org.apache.axis.encoding.Base64;
import org.apache.axis.encoding.ser.SimpleDeserializer;

public class Base64Deserializer
extends SimpleDeserializer {
    static /* synthetic */ Class array$Ljava$lang$Byte;

    public Base64Deserializer(Class javaType, QName xmlType) {
        super(javaType, xmlType);
    }

    public Object makeValue(String source) throws Exception {
        byte[] value = Base64.decode(source);
        if (value == null) {
            if (this.javaType == (array$Ljava$lang$Byte == null ? (array$Ljava$lang$Byte = Base64Deserializer.class$("[Ljava.lang.Byte;")) : array$Ljava$lang$Byte)) {
                return new Byte[0];
            }
            return new byte[0];
        }
        if (this.javaType == (array$Ljava$lang$Byte == null ? (array$Ljava$lang$Byte = Base64Deserializer.class$("[Ljava.lang.Byte;")) : array$Ljava$lang$Byte)) {
            Byte[] data = new Byte[value.length];
            for (int i = 0; i < data.length; ++i) {
                byte b = value[i];
                data[i] = new Byte(b);
            }
            return data;
        }
        return value;
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

