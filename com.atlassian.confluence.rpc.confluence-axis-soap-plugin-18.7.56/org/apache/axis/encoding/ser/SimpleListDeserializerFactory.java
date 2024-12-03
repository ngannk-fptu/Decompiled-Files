/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import java.io.ObjectStreamException;
import java.lang.reflect.Constructor;
import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.encoding.Deserializer;
import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.axis.encoding.ser.SimpleListDeserializer;
import org.apache.axis.utils.JavaUtils;

public class SimpleListDeserializerFactory
extends BaseDeserializerFactory {
    private static final Class[] STRING_CLASS = new Class[]{class$java$lang$String == null ? (class$java$lang$String = SimpleListDeserializerFactory.class$("java.lang.String")) : class$java$lang$String};
    private final Class clazzType;
    private transient Constructor constructor = null;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$SimpleListDeserializer;
    static /* synthetic */ Class class$java$lang$Object;

    public SimpleListDeserializerFactory(Class javaType, QName xmlType) {
        super(class$org$apache$axis$encoding$ser$SimpleListDeserializer == null ? (class$org$apache$axis$encoding$ser$SimpleListDeserializer = SimpleListDeserializerFactory.class$("org.apache.axis.encoding.ser.SimpleListDeserializer")) : class$org$apache$axis$encoding$ser$SimpleListDeserializer, xmlType, javaType.getComponentType());
        this.clazzType = javaType;
        Class<?> componentType = javaType.getComponentType();
        try {
            if (!componentType.isPrimitive()) {
                this.constructor = componentType.getDeclaredConstructor(STRING_CLASS);
            } else {
                Class wrapper = JavaUtils.getWrapperClass(componentType);
                if (wrapper != null) {
                    this.constructor = wrapper.getDeclaredConstructor(STRING_CLASS);
                }
            }
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e.toString());
        }
    }

    public Deserializer getDeserializerAs(String mechanismType) throws JAXRPCException {
        if (this.javaType == (class$java$lang$Object == null ? (class$java$lang$Object = SimpleListDeserializerFactory.class$("java.lang.Object")) : class$java$lang$Object)) {
            return null;
        }
        SimpleListDeserializer deser = (SimpleListDeserializer)super.getDeserializerAs(mechanismType);
        if (deser != null) {
            deser.setConstructor(this.constructor);
        }
        return deser;
    }

    private Object readResolve() throws ObjectStreamException {
        return new SimpleListDeserializerFactory(this.clazzType, this.xmlType);
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

