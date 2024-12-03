/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding.ser;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Vector;
import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.ser.BaseFactory;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.ElementDeserializerFactory;
import org.apache.axis.encoding.ser.EnumDeserializerFactory;
import org.apache.axis.encoding.ser.SimpleDeserializerFactory;
import org.apache.axis.encoding.ser.SimpleListDeserializerFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public abstract class BaseDeserializerFactory
extends BaseFactory
implements DeserializerFactory {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$BaseDeserializerFactory == null ? (class$org$apache$axis$encoding$ser$BaseDeserializerFactory = BaseDeserializerFactory.class$("org.apache.axis.encoding.ser.BaseDeserializerFactory")) : class$org$apache$axis$encoding$ser$BaseDeserializerFactory).getName());
    static transient Vector mechanisms = null;
    protected Class deserClass = null;
    protected QName xmlType = null;
    protected Class javaType = null;
    protected transient Constructor deserClassConstructor = null;
    protected transient Method getDeserializer = null;
    private static final Class[] CLASS_QNAME_CLASS = new Class[]{class$java$lang$Class == null ? (class$java$lang$Class = BaseDeserializerFactory.class$("java.lang.Class")) : class$java$lang$Class, class$javax$xml$namespace$QName == null ? (class$javax$xml$namespace$QName = BaseDeserializerFactory.class$("javax.xml.namespace.QName")) : class$javax$xml$namespace$QName};
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$BaseDeserializerFactory;
    static /* synthetic */ Class class$org$apache$axis$encoding$Deserializer;
    static /* synthetic */ Class class$java$lang$Class;
    static /* synthetic */ Class class$javax$xml$namespace$QName;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$BeanDeserializerFactory;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$SimpleDeserializerFactory;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$EnumDeserializerFactory;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$ElementDeserializerFactory;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$SimpleListDeserializerFactory;

    public BaseDeserializerFactory(Class deserClass) {
        if (!(class$org$apache$axis$encoding$Deserializer == null ? (class$org$apache$axis$encoding$Deserializer = BaseDeserializerFactory.class$("org.apache.axis.encoding.Deserializer")) : class$org$apache$axis$encoding$Deserializer).isAssignableFrom(deserClass)) {
            throw new ClassCastException(org.apache.axis.i18n.Messages.getMessage("BadImplementation00", deserClass.getName(), (class$org$apache$axis$encoding$Deserializer == null ? (class$org$apache$axis$encoding$Deserializer = BaseDeserializerFactory.class$("org.apache.axis.encoding.Deserializer")) : class$org$apache$axis$encoding$Deserializer).getName()));
        }
        this.deserClass = deserClass;
    }

    public BaseDeserializerFactory(Class deserClass, QName xmlType, Class javaType) {
        this(deserClass);
        this.xmlType = xmlType;
        this.javaType = javaType;
    }

    public javax.xml.rpc.encoding.Deserializer getDeserializerAs(String mechanismType) throws JAXRPCException {
        Deserializer deser = null;
        deser = this.getSpecialized(mechanismType);
        if (deser == null) {
            deser = this.getGeneralPurpose(mechanismType);
        }
        try {
            if (deser == null) {
                deser = (Deserializer)this.deserClass.newInstance();
            }
        }
        catch (Exception e) {
            throw new JAXRPCException(e);
        }
        return deser;
    }

    protected Deserializer getGeneralPurpose(String mechanismType) {
        block7: {
            Constructor deserClassConstructor;
            if (this.javaType != null && this.xmlType != null && (deserClassConstructor = this.getDeserClassConstructor()) != null) {
                try {
                    return (Deserializer)deserClassConstructor.newInstance(this.javaType, this.xmlType);
                }
                catch (InstantiationException e) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)Messages.getMessage("exception00"), (Throwable)e);
                    }
                }
                catch (IllegalAccessException e) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)Messages.getMessage("exception00"), (Throwable)e);
                    }
                }
                catch (InvocationTargetException e) {
                    if (!log.isDebugEnabled()) break block7;
                    log.debug((Object)Messages.getMessage("exception00"), (Throwable)e);
                }
            }
        }
        return null;
    }

    private Constructor getConstructor(Class clazz) {
        try {
            return clazz.getConstructor(CLASS_QNAME_CLASS);
        }
        catch (NoSuchMethodException noSuchMethodException) {
            return null;
        }
    }

    protected Deserializer getSpecialized(String mechanismType) {
        block5: {
            Method getDeserializer;
            if (this.javaType != null && this.xmlType != null && (getDeserializer = this.getGetDeserializer()) != null) {
                try {
                    return (Deserializer)getDeserializer.invoke(null, mechanismType, this.javaType, this.xmlType);
                }
                catch (IllegalAccessException e) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)Messages.getMessage("exception00"), (Throwable)e);
                    }
                }
                catch (InvocationTargetException e) {
                    if (!log.isDebugEnabled()) break block5;
                    log.debug((Object)Messages.getMessage("exception00"), (Throwable)e);
                }
            }
        }
        return null;
    }

    public Iterator getSupportedMechanismTypes() {
        if (mechanisms == null) {
            mechanisms = new Vector(1);
            mechanisms.add("Axis SAX Mechanism");
        }
        return mechanisms.iterator();
    }

    public static DeserializerFactory createFactory(Class factory, Class javaType, QName xmlType) {
        DeserializerFactory df;
        block29: {
            block28: {
                if (factory == null) {
                    return null;
                }
                try {
                    if (factory == (class$org$apache$axis$encoding$ser$BeanDeserializerFactory == null ? (class$org$apache$axis$encoding$ser$BeanDeserializerFactory = BaseDeserializerFactory.class$("org.apache.axis.encoding.ser.BeanDeserializerFactory")) : class$org$apache$axis$encoding$ser$BeanDeserializerFactory)) {
                        return new BeanDeserializerFactory(javaType, xmlType);
                    }
                    if (factory == (class$org$apache$axis$encoding$ser$SimpleDeserializerFactory == null ? (class$org$apache$axis$encoding$ser$SimpleDeserializerFactory = BaseDeserializerFactory.class$("org.apache.axis.encoding.ser.SimpleDeserializerFactory")) : class$org$apache$axis$encoding$ser$SimpleDeserializerFactory)) {
                        return new SimpleDeserializerFactory(javaType, xmlType);
                    }
                    if (factory == (class$org$apache$axis$encoding$ser$EnumDeserializerFactory == null ? (class$org$apache$axis$encoding$ser$EnumDeserializerFactory = BaseDeserializerFactory.class$("org.apache.axis.encoding.ser.EnumDeserializerFactory")) : class$org$apache$axis$encoding$ser$EnumDeserializerFactory)) {
                        return new EnumDeserializerFactory(javaType, xmlType);
                    }
                    if (factory == (class$org$apache$axis$encoding$ser$ElementDeserializerFactory == null ? (class$org$apache$axis$encoding$ser$ElementDeserializerFactory = BaseDeserializerFactory.class$("org.apache.axis.encoding.ser.ElementDeserializerFactory")) : class$org$apache$axis$encoding$ser$ElementDeserializerFactory)) {
                        return new ElementDeserializerFactory();
                    }
                    if (factory == (class$org$apache$axis$encoding$ser$SimpleListDeserializerFactory == null ? (class$org$apache$axis$encoding$ser$SimpleListDeserializerFactory = BaseDeserializerFactory.class$("org.apache.axis.encoding.ser.SimpleListDeserializerFactory")) : class$org$apache$axis$encoding$ser$SimpleListDeserializerFactory)) {
                        return new SimpleListDeserializerFactory(javaType, xmlType);
                    }
                }
                catch (Exception e) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)Messages.getMessage("exception00"), (Throwable)e);
                    }
                    return null;
                }
                df = null;
                try {
                    Method method = factory.getMethod("create", CLASS_QNAME_CLASS);
                    df = (DeserializerFactory)method.invoke(null, javaType, xmlType);
                }
                catch (NoSuchMethodException e) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)Messages.getMessage("exception00"), (Throwable)e);
                    }
                }
                catch (IllegalAccessException e) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)Messages.getMessage("exception00"), (Throwable)e);
                    }
                }
                catch (InvocationTargetException e) {
                    if (!log.isDebugEnabled()) break block28;
                    log.debug((Object)Messages.getMessage("exception00"), (Throwable)e);
                }
            }
            if (df == null) {
                try {
                    Constructor constructor = factory.getConstructor(CLASS_QNAME_CLASS);
                    df = (DeserializerFactory)constructor.newInstance(javaType, xmlType);
                }
                catch (NoSuchMethodException e) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)Messages.getMessage("exception00"), (Throwable)e);
                    }
                }
                catch (InstantiationException e) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)Messages.getMessage("exception00"), (Throwable)e);
                    }
                }
                catch (IllegalAccessException e) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)Messages.getMessage("exception00"), (Throwable)e);
                    }
                }
                catch (InvocationTargetException e) {
                    if (!log.isDebugEnabled()) break block29;
                    log.debug((Object)Messages.getMessage("exception00"), (Throwable)e);
                }
            }
        }
        if (df == null) {
            try {
                df = (DeserializerFactory)factory.newInstance();
            }
            catch (InstantiationException e) {
            }
            catch (IllegalAccessException e) {
                // empty catch block
            }
        }
        return df;
    }

    protected Constructor getDeserClassConstructor() {
        if (this.deserClassConstructor == null) {
            this.deserClassConstructor = this.getConstructor(this.deserClass);
        }
        return this.deserClassConstructor;
    }

    protected Method getGetDeserializer() {
        if (this.getDeserializer == null) {
            this.getDeserializer = this.getMethod(this.javaType, "getDeserializer");
        }
        return this.getDeserializer;
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

