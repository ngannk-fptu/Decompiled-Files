/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.encoding.Deserializer;
import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.axis.encoding.ser.SimpleDeserializer;
import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.BeanUtils;
import org.apache.axis.utils.JavaUtils;

public class SimpleDeserializerFactory
extends BaseDeserializerFactory {
    private static final Class[] STRING_STRING_CLASS = new Class[]{class$java$lang$String == null ? (class$java$lang$String = SimpleDeserializerFactory.class$("java.lang.String")) : class$java$lang$String, class$java$lang$String == null ? (class$java$lang$String = SimpleDeserializerFactory.class$("java.lang.String")) : class$java$lang$String};
    private static final Class[] STRING_CLASS = new Class[]{class$java$lang$String == null ? (class$java$lang$String = SimpleDeserializerFactory.class$("java.lang.String")) : class$java$lang$String};
    private transient Constructor constructor = null;
    private boolean isBasicType = false;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$SimpleDeserializer;
    static /* synthetic */ Class class$javax$xml$namespace$QName;
    static /* synthetic */ Class class$java$lang$Object;

    public SimpleDeserializerFactory(Class javaType, QName xmlType) {
        super(class$org$apache$axis$encoding$ser$SimpleDeserializer == null ? (class$org$apache$axis$encoding$ser$SimpleDeserializer = SimpleDeserializerFactory.class$("org.apache.axis.encoding.ser.SimpleDeserializer")) : class$org$apache$axis$encoding$ser$SimpleDeserializer, xmlType, javaType);
        this.isBasicType = JavaUtils.isBasic(javaType);
        this.initConstructor(javaType);
    }

    private void initConstructor(Class javaType) {
        if (!this.isBasicType) {
            try {
                this.constructor = (class$javax$xml$namespace$QName == null ? (class$javax$xml$namespace$QName = SimpleDeserializerFactory.class$("javax.xml.namespace.QName")) : class$javax$xml$namespace$QName).isAssignableFrom(javaType) ? javaType.getDeclaredConstructor(STRING_STRING_CLASS) : javaType.getDeclaredConstructor(STRING_CLASS);
            }
            catch (NoSuchMethodException e) {
                try {
                    this.constructor = javaType.getDeclaredConstructor(new Class[0]);
                    BeanPropertyDescriptor[] pds = BeanUtils.getPd(javaType);
                    if (pds != null && BeanUtils.getSpecificPD(pds, "_value") != null) {
                        return;
                    }
                    throw new IllegalArgumentException(e.toString());
                }
                catch (NoSuchMethodException ex) {
                    throw new IllegalArgumentException(ex.toString());
                }
            }
        }
    }

    public Deserializer getDeserializerAs(String mechanismType) throws JAXRPCException {
        if (this.javaType == (class$java$lang$Object == null ? (class$java$lang$Object = SimpleDeserializerFactory.class$("java.lang.Object")) : class$java$lang$Object)) {
            return null;
        }
        if (this.isBasicType) {
            return new SimpleDeserializer(this.javaType, this.xmlType);
        }
        SimpleDeserializer deser = (SimpleDeserializer)super.getDeserializerAs(mechanismType);
        if (deser != null) {
            deser.setConstructor(this.constructor);
        }
        return deser;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.initConstructor(this.javaType);
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

