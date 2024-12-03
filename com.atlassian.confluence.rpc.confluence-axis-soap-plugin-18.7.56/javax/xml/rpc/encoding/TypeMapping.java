/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.rpc.encoding;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.DeserializerFactory;
import javax.xml.rpc.encoding.SerializerFactory;

public interface TypeMapping {
    public String[] getSupportedEncodings();

    public void setSupportedEncodings(String[] var1);

    public boolean isRegistered(Class var1, QName var2);

    public void register(Class var1, QName var2, SerializerFactory var3, DeserializerFactory var4);

    public SerializerFactory getSerializer(Class var1, QName var2);

    public DeserializerFactory getDeserializer(Class var1, QName var2);

    public void removeSerializer(Class var1, QName var2);

    public void removeDeserializer(Class var1, QName var2);
}

