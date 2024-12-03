/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.naming;

import com.mchange.v2.beans.BeansUtils;
import com.mchange.v2.lang.Coerce;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.naming.ReferenceIndirector;
import com.mchange.v2.naming.ReferenceMaker;
import com.mchange.v2.ser.IndirectPolicy;
import com.mchange.v2.ser.SerializableUtils;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import javax.naming.BinaryRefAddr;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;

public class JavaBeanReferenceMaker
implements ReferenceMaker {
    private static final MLogger logger = MLog.getLogger(JavaBeanReferenceMaker.class);
    static final String REF_PROPS_KEY = "com.mchange.v2.naming.JavaBeanReferenceMaker.REF_PROPS_KEY";
    static final Object[] EMPTY_ARGS = new Object[0];
    static final byte[] NULL_TOKEN_BYTES = new byte[0];
    String factoryClassName = "com.mchange.v2.naming.JavaBeanObjectFactory";
    String defaultFactoryClassLocation = null;
    Set referenceProperties = new HashSet();
    ReferenceIndirector indirector = new ReferenceIndirector();

    public Hashtable getEnvironmentProperties() {
        return this.indirector.getEnvironmentProperties();
    }

    public void setEnvironmentProperties(Hashtable hashtable) {
        this.indirector.setEnvironmentProperties(hashtable);
    }

    public void setFactoryClassName(String string) {
        this.factoryClassName = string;
    }

    public String getFactoryClassName() {
        return this.factoryClassName;
    }

    public String getDefaultFactoryClassLocation() {
        return this.defaultFactoryClassLocation;
    }

    public void setDefaultFactoryClassLocation(String string) {
        this.defaultFactoryClassLocation = string;
    }

    public void addReferenceProperty(String string) {
        this.referenceProperties.add(string);
    }

    public void removeReferenceProperty(String string) {
        this.referenceProperties.remove(string);
    }

    @Override
    public Reference createReference(Object object) throws NamingException {
        try {
            boolean bl;
            BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
            PropertyDescriptor[] propertyDescriptorArray = beanInfo.getPropertyDescriptors();
            ArrayList<BinaryRefAddr> arrayList = new ArrayList<BinaryRefAddr>();
            String string = this.defaultFactoryClassLocation;
            boolean bl2 = bl = this.referenceProperties.size() > 0;
            if (bl) {
                arrayList.add(new BinaryRefAddr(REF_PROPS_KEY, SerializableUtils.toByteArray(this.referenceProperties)));
            }
            for (PropertyDescriptor propertyDescriptor : propertyDescriptorArray) {
                String string2 = propertyDescriptor.getName();
                if (bl && !this.referenceProperties.contains(string2)) continue;
                Class<?> clazz = propertyDescriptor.getPropertyType();
                Method method = propertyDescriptor.getReadMethod();
                Method method2 = propertyDescriptor.getWriteMethod();
                if (method != null && method2 != null) {
                    RefAddr refAddr;
                    Object object2 = method.invoke(object, EMPTY_ARGS);
                    if (string2.equals("factoryClassLocation")) {
                        if (String.class != clazz) {
                            throw new NamingException(this.getClass().getName() + " requires a factoryClassLocation property to be a string, " + clazz.getName() + " is not valid.");
                        }
                        string = (String)object2;
                    }
                    if (object2 == null) {
                        refAddr = new BinaryRefAddr(string2, NULL_TOKEN_BYTES);
                        arrayList.add((BinaryRefAddr)refAddr);
                        continue;
                    }
                    if (Coerce.canCoerce(clazz)) {
                        refAddr = new StringRefAddr(string2, String.valueOf(object2));
                        arrayList.add((BinaryRefAddr)refAddr);
                        continue;
                    }
                    refAddr = null;
                    PropertyEditor propertyEditor = BeansUtils.findPropertyEditor(propertyDescriptor);
                    if (propertyEditor != null) {
                        propertyEditor.setValue(object2);
                        String string3 = propertyEditor.getAsText();
                        if (string3 != null) {
                            refAddr = new StringRefAddr(string2, string3);
                        }
                    }
                    if (refAddr == null) {
                        refAddr = new BinaryRefAddr(string2, SerializableUtils.toByteArray(object2, this.indirector, IndirectPolicy.INDIRECT_ON_EXCEPTION));
                    }
                    arrayList.add((BinaryRefAddr)refAddr);
                    continue;
                }
                if (!logger.isLoggable(MLevel.WARNING)) continue;
                logger.warning(this.getClass().getName() + ": Skipping " + string2 + " because it is " + (method2 == null ? "read-only." : "write-only."));
            }
            Reference reference = new Reference(object.getClass().getName(), this.factoryClassName, string);
            Iterator iterator = arrayList.iterator();
            while (iterator.hasNext()) {
                reference.add((RefAddr)iterator.next());
            }
            return reference;
        }
        catch (Exception exception) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Exception trying to create Reference.", exception);
            }
            throw new NamingException("Could not create reference from bean: " + exception.toString());
        }
    }
}

