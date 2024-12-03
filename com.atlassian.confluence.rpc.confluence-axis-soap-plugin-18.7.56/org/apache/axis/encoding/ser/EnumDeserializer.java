/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import java.beans.IntrospectionException;
import java.lang.reflect.Method;
import javax.xml.namespace.QName;
import org.apache.axis.encoding.ser.SimpleDeserializer;
import org.apache.axis.utils.cache.MethodCache;

public class EnumDeserializer
extends SimpleDeserializer {
    private Method fromStringMethod = null;
    private static final Class[] STRING_CLASS = new Class[]{class$java$lang$String == null ? (class$java$lang$String = EnumDeserializer.class$("java.lang.String")) : class$java$lang$String};
    static /* synthetic */ Class class$java$lang$String;

    public EnumDeserializer(Class javaType, QName xmlType) {
        super(javaType, xmlType);
    }

    public Object makeValue(String source) throws Exception {
        if (this.isNil) {
            return null;
        }
        if (this.fromStringMethod == null) {
            try {
                this.fromStringMethod = MethodCache.getInstance().getMethod(this.javaType, "fromString", STRING_CLASS);
            }
            catch (Exception e) {
                throw new IntrospectionException(e.toString());
            }
        }
        return this.fromStringMethod.invoke(null, source);
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

