/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.naming;

import com.mchange.v2.beans.BeansUtils;
import com.mchange.v2.lang.Coerce;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.ser.SerializableUtils;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import javax.naming.BinaryRefAddr;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;

public class JavaBeanObjectFactory
implements ObjectFactory {
    private static final MLogger logger = MLog.getLogger(JavaBeanObjectFactory.class);
    static final Object NULL_TOKEN = new Object();

    public Object getObjectInstance(Object object, Name name, Context context, Hashtable hashtable) throws Exception {
        if (object instanceof Reference) {
            Object object2;
            Reference reference = (Reference)object;
            HashMap<String, RefAddr> hashMap = new HashMap<String, RefAddr>();
            Object object3 = reference.getAll();
            while (object3.hasMoreElements()) {
                object2 = object3.nextElement();
                hashMap.put(((RefAddr)object2).getType(), (RefAddr)object2);
            }
            object3 = Class.forName(reference.getClassName());
            object2 = null;
            BinaryRefAddr binaryRefAddr = (BinaryRefAddr)hashMap.remove("com.mchange.v2.naming.JavaBeanReferenceMaker.REF_PROPS_KEY");
            if (binaryRefAddr != null) {
                object2 = (Set)SerializableUtils.fromByteArray((byte[])((RefAddr)binaryRefAddr).getContent());
            }
            Map map = this.createPropertyMap((Class)object3, hashMap);
            return this.findBean((Class)object3, map, (Set)object2);
        }
        return null;
    }

    private Map createPropertyMap(Class clazz, Map map) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        PropertyDescriptor[] propertyDescriptorArray = beanInfo.getPropertyDescriptors();
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptorArray) {
            Object object;
            String string = propertyDescriptor.getName();
            Class<?> clazz2 = propertyDescriptor.getPropertyType();
            Object v = map.remove(string);
            if (v == null) continue;
            if (v instanceof StringRefAddr) {
                object = (String)((StringRefAddr)v).getContent();
                if (Coerce.canCoerce(clazz2)) {
                    hashMap.put(string, Coerce.toObject((String)object, clazz2));
                    continue;
                }
                PropertyEditor propertyEditor = BeansUtils.findPropertyEditor(propertyDescriptor);
                propertyEditor.setAsText((String)object);
                hashMap.put(string, propertyEditor.getValue());
                continue;
            }
            if (v instanceof BinaryRefAddr) {
                object = (byte[])((BinaryRefAddr)v).getContent();
                if (((Object)object).length == 0) {
                    hashMap.put(string, NULL_TOKEN);
                    continue;
                }
                hashMap.put(string, SerializableUtils.fromByteArray((byte[])object));
                continue;
            }
            if (!logger.isLoggable(MLevel.WARNING)) continue;
            logger.warning(this.getClass().getName() + " -- unknown RefAddr subclass: " + v.getClass().getName());
        }
        for (String string : map.keySet()) {
            if (!logger.isLoggable(MLevel.WARNING)) continue;
            logger.warning(this.getClass().getName() + " -- RefAddr for unknown property: " + string);
        }
        return hashMap;
    }

    protected Object createBlankInstance(Class clazz) throws Exception {
        return clazz.newInstance();
    }

    protected Object findBean(Class clazz, Map map, Set set) throws Exception {
        Object object = this.createBlankInstance(clazz);
        BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
        for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
            String string = propertyDescriptor.getName();
            Object v = map.get(string);
            Method method = propertyDescriptor.getWriteMethod();
            if (v != null) {
                if (method != null) {
                    method.invoke(object, v == NULL_TOKEN ? null : v);
                    continue;
                }
                if (!logger.isLoggable(MLevel.WARNING)) continue;
                logger.warning(this.getClass().getName() + ": Could not restore read-only property '" + string + "'.");
                continue;
            }
            if (method == null || set != null && !set.contains(string) || !logger.isLoggable(MLevel.WARNING)) continue;
            logger.warning(this.getClass().getName() + " -- Expected writable property ''" + string + "'' left at default value");
        }
        return object;
    }
}

