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
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.encoding.ser.BaseFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.encoding.ser.ElementSerializerFactory;
import org.apache.axis.encoding.ser.EnumSerializerFactory;
import org.apache.axis.encoding.ser.SimpleListSerializerFactory;
import org.apache.axis.encoding.ser.SimpleSerializerFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public abstract class BaseSerializerFactory
extends BaseFactory
implements SerializerFactory {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$BaseSerializerFactory == null ? (class$org$apache$axis$encoding$ser$BaseSerializerFactory = BaseSerializerFactory.class$("org.apache.axis.encoding.ser.BaseSerializerFactory")) : class$org$apache$axis$encoding$ser$BaseSerializerFactory).getName());
    static transient Vector mechanisms = null;
    protected Class serClass = null;
    protected QName xmlType = null;
    protected Class javaType = null;
    protected transient Serializer ser = null;
    protected transient Constructor serClassConstructor = null;
    protected transient Method getSerializer = null;
    private static final Class[] CLASS_QNAME_CLASS = new Class[]{class$java$lang$Class == null ? (class$java$lang$Class = BaseSerializerFactory.class$("java.lang.Class")) : class$java$lang$Class, class$javax$xml$namespace$QName == null ? (class$javax$xml$namespace$QName = BaseSerializerFactory.class$("javax.xml.namespace.QName")) : class$javax$xml$namespace$QName};
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$BaseSerializerFactory;
    static /* synthetic */ Class class$org$apache$axis$encoding$Serializer;
    static /* synthetic */ Class class$java$lang$Class;
    static /* synthetic */ Class class$javax$xml$namespace$QName;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$BeanSerializerFactory;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$SimpleSerializerFactory;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$EnumSerializerFactory;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$ElementSerializerFactory;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$SimpleListSerializerFactory;

    public BaseSerializerFactory(Class serClass) {
        if (!(class$org$apache$axis$encoding$Serializer == null ? (class$org$apache$axis$encoding$Serializer = BaseSerializerFactory.class$("org.apache.axis.encoding.Serializer")) : class$org$apache$axis$encoding$Serializer).isAssignableFrom(serClass)) {
            throw new ClassCastException(Messages.getMessage("BadImplementation00", serClass.getName(), (class$org$apache$axis$encoding$Serializer == null ? (class$org$apache$axis$encoding$Serializer = BaseSerializerFactory.class$("org.apache.axis.encoding.Serializer")) : class$org$apache$axis$encoding$Serializer).getName()));
        }
        this.serClass = serClass;
    }

    public BaseSerializerFactory(Class serClass, QName xmlType, Class javaType) {
        this(serClass);
        this.xmlType = xmlType;
        this.javaType = javaType;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public javax.xml.rpc.encoding.Serializer getSerializerAs(String mechanismType) throws JAXRPCException {
        BaseSerializerFactory baseSerializerFactory = this;
        synchronized (baseSerializerFactory) {
            if (this.ser == null) {
                this.ser = this.getSerializerAsInternal(mechanismType);
            }
            return this.ser;
        }
    }

    protected Serializer getSerializerAsInternal(String mechanismType) throws JAXRPCException {
        Serializer serializer = this.getSpecialized(mechanismType);
        if (serializer == null) {
            serializer = this.getGeneralPurpose(mechanismType);
        }
        try {
            if (serializer == null) {
                serializer = (Serializer)this.serClass.newInstance();
            }
        }
        catch (Exception e) {
            throw new JAXRPCException(Messages.getMessage("CantGetSerializer", this.serClass.getName()), e);
        }
        return serializer;
    }

    protected Serializer getGeneralPurpose(String mechanismType) {
        block7: {
            Constructor serClassConstructor;
            if (this.javaType != null && this.xmlType != null && (serClassConstructor = this.getSerClassConstructor()) != null) {
                try {
                    return (Serializer)serClassConstructor.newInstance(this.javaType, this.xmlType);
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

    protected Serializer getSpecialized(String mechanismType) {
        block5: {
            Method getSerializer;
            if (this.javaType != null && this.xmlType != null && (getSerializer = this.getGetSerializer()) != null) {
                try {
                    return (Serializer)getSerializer.invoke(null, mechanismType, this.javaType, this.xmlType);
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

    public QName getXMLType() {
        return this.xmlType;
    }

    public Class getJavaType() {
        return this.javaType;
    }

    public static SerializerFactory createFactory(Class factory, Class javaType, QName xmlType) {
        SerializerFactory sf;
        block29: {
            block28: {
                if (factory == null) {
                    return null;
                }
                try {
                    if (factory == (class$org$apache$axis$encoding$ser$BeanSerializerFactory == null ? (class$org$apache$axis$encoding$ser$BeanSerializerFactory = BaseSerializerFactory.class$("org.apache.axis.encoding.ser.BeanSerializerFactory")) : class$org$apache$axis$encoding$ser$BeanSerializerFactory)) {
                        return new BeanSerializerFactory(javaType, xmlType);
                    }
                    if (factory == (class$org$apache$axis$encoding$ser$SimpleSerializerFactory == null ? (class$org$apache$axis$encoding$ser$SimpleSerializerFactory = BaseSerializerFactory.class$("org.apache.axis.encoding.ser.SimpleSerializerFactory")) : class$org$apache$axis$encoding$ser$SimpleSerializerFactory)) {
                        return new SimpleSerializerFactory(javaType, xmlType);
                    }
                    if (factory == (class$org$apache$axis$encoding$ser$EnumSerializerFactory == null ? (class$org$apache$axis$encoding$ser$EnumSerializerFactory = BaseSerializerFactory.class$("org.apache.axis.encoding.ser.EnumSerializerFactory")) : class$org$apache$axis$encoding$ser$EnumSerializerFactory)) {
                        return new EnumSerializerFactory(javaType, xmlType);
                    }
                    if (factory == (class$org$apache$axis$encoding$ser$ElementSerializerFactory == null ? (class$org$apache$axis$encoding$ser$ElementSerializerFactory = BaseSerializerFactory.class$("org.apache.axis.encoding.ser.ElementSerializerFactory")) : class$org$apache$axis$encoding$ser$ElementSerializerFactory)) {
                        return new ElementSerializerFactory();
                    }
                    if (factory == (class$org$apache$axis$encoding$ser$SimpleListSerializerFactory == null ? (class$org$apache$axis$encoding$ser$SimpleListSerializerFactory = BaseSerializerFactory.class$("org.apache.axis.encoding.ser.SimpleListSerializerFactory")) : class$org$apache$axis$encoding$ser$SimpleListSerializerFactory)) {
                        return new SimpleListSerializerFactory(javaType, xmlType);
                    }
                }
                catch (Exception e) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)Messages.getMessage("exception00"), (Throwable)e);
                    }
                    return null;
                }
                sf = null;
                try {
                    Method method = factory.getMethod("create", CLASS_QNAME_CLASS);
                    sf = (SerializerFactory)method.invoke(null, javaType, xmlType);
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
            if (sf == null) {
                try {
                    Constructor constructor = factory.getConstructor(CLASS_QNAME_CLASS);
                    sf = (SerializerFactory)constructor.newInstance(javaType, xmlType);
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
        if (sf == null) {
            try {
                sf = (SerializerFactory)factory.newInstance();
            }
            catch (InstantiationException e) {
            }
            catch (IllegalAccessException e) {
                // empty catch block
            }
        }
        return sf;
    }

    protected Method getGetSerializer() {
        if (this.getSerializer == null) {
            this.getSerializer = this.getMethod(this.javaType, "getSerializer");
        }
        return this.getSerializer;
    }

    protected Constructor getSerClassConstructor() {
        if (this.serClassConstructor == null) {
            this.serClassConstructor = this.getConstructor(this.serClass);
        }
        return this.serClassConstructor;
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

