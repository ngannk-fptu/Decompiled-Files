/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.naming.factory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.naming.ResourceRef;
import org.apache.naming.StringManager;

public class BeanFactory
implements ObjectFactory {
    private static final StringManager sm = StringManager.getManager(BeanFactory.class);
    private final Log log = LogFactory.getLog(BeanFactory.class);

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws NamingException {
        if (obj instanceof ResourceRef) {
            try {
                Reference ref = (Reference)obj;
                String beanClassName = ref.getClassName();
                Class<?> beanClass = null;
                ClassLoader tcl = Thread.currentThread().getContextClassLoader();
                try {
                    beanClass = tcl != null ? tcl.loadClass(beanClassName) : Class.forName(beanClassName);
                }
                catch (ClassNotFoundException cnfe) {
                    NamingException ne = new NamingException(sm.getString("beanFactory.classNotFound", beanClassName));
                    ne.initCause(cnfe);
                    throw ne;
                }
                BeanInfo bi = Introspector.getBeanInfo(beanClass);
                PropertyDescriptor[] pda = bi.getPropertyDescriptors();
                Object bean = beanClass.getConstructor(new Class[0]).newInstance(new Object[0]);
                RefAddr ra = ref.get("forceString");
                if (ra != null) {
                    this.log.warn((Object)sm.getString("beanFactory.noForceString"));
                }
                Enumeration<RefAddr> e = ref.getAll();
                while (e.hasMoreElements()) {
                    ra = e.nextElement();
                    String propName = ra.getType();
                    if (propName.equals("factory") || propName.equals("scope") || propName.equals("auth") || propName.equals("forceString") || propName.equals("singleton")) continue;
                    String value = (String)ra.getContent();
                    Object[] valueArray = new Object[1];
                    int i = 0;
                    for (i = 0; i < pda.length; ++i) {
                        if (!pda[i].getName().equals(propName)) continue;
                        Class<?> propType = pda[i].getPropertyType();
                        Method setProp = pda[i].getWriteMethod();
                        if (propType.equals(String.class)) {
                            valueArray[0] = value;
                        } else if (propType.equals(Character.class) || propType.equals(Character.TYPE)) {
                            valueArray[0] = Character.valueOf(value.charAt(0));
                        } else if (propType.equals(Byte.class) || propType.equals(Byte.TYPE)) {
                            valueArray[0] = Byte.valueOf(value);
                        } else if (propType.equals(Short.class) || propType.equals(Short.TYPE)) {
                            valueArray[0] = Short.valueOf(value);
                        } else if (propType.equals(Integer.class) || propType.equals(Integer.TYPE)) {
                            valueArray[0] = Integer.valueOf(value);
                        } else if (propType.equals(Long.class) || propType.equals(Long.TYPE)) {
                            valueArray[0] = Long.valueOf(value);
                        } else if (propType.equals(Float.class) || propType.equals(Float.TYPE)) {
                            valueArray[0] = Float.valueOf(value);
                        } else if (propType.equals(Double.class) || propType.equals(Double.TYPE)) {
                            valueArray[0] = Double.valueOf(value);
                        } else if (propType.equals(Boolean.class) || propType.equals(Boolean.TYPE)) {
                            valueArray[0] = Boolean.valueOf(value);
                        } else if (setProp != null) {
                            String setterName = setProp.getName();
                            try {
                                setProp = bean.getClass().getMethod(setterName, String.class);
                                valueArray[0] = value;
                            }
                            catch (NoSuchMethodException nsme) {
                                throw new NamingException(sm.getString("beanFactory.noStringConversion", propName, propType.getName()));
                            }
                        } else {
                            throw new NamingException(sm.getString("beanFactory.noStringConversion", propName, propType.getName()));
                        }
                        if (setProp != null) {
                            setProp.invoke(bean, valueArray);
                            break;
                        }
                        throw new NamingException(sm.getString("beanFactory.readOnlyProperty", propName));
                    }
                    if (i != pda.length) continue;
                    throw new NamingException(sm.getString("beanFactory.noSetMethod", propName));
                }
                return bean;
            }
            catch (IntrospectionException ie) {
                NamingException ne = new NamingException(ie.getMessage());
                ne.setRootCause(ie);
                throw ne;
            }
            catch (ReflectiveOperationException e) {
                Throwable cause = e.getCause();
                if (cause instanceof ThreadDeath) {
                    throw (ThreadDeath)cause;
                }
                if (cause instanceof VirtualMachineError) {
                    throw (VirtualMachineError)cause;
                }
                NamingException ne = new NamingException(e.getMessage());
                ne.setRootCause(e);
                throw ne;
            }
        }
        return null;
    }
}

