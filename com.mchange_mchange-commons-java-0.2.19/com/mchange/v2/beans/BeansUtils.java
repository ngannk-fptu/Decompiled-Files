/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.beans;

import com.mchange.v2.lang.Coerce;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public final class BeansUtils {
    static final MLogger logger = MLog.getLogger(BeansUtils.class);
    static final Object[] EMPTY_ARGS = new Object[0];

    public static PropertyEditor findPropertyEditor(PropertyDescriptor propertyDescriptor) {
        PropertyEditor propertyEditor;
        block4: {
            propertyEditor = null;
            Class<?> clazz = null;
            try {
                clazz = propertyDescriptor.getPropertyEditorClass();
                if (clazz != null) {
                    propertyEditor = (PropertyEditor)clazz.newInstance();
                }
            }
            catch (Exception exception) {
                if (!logger.isLoggable(MLevel.WARNING)) break block4;
                logger.log(MLevel.WARNING, "Bad property editor class " + clazz.getName() + " registered for property " + propertyDescriptor.getName(), exception);
            }
        }
        if (propertyEditor == null) {
            propertyEditor = PropertyEditorManager.findEditor(propertyDescriptor.getPropertyType());
        }
        return propertyEditor;
    }

    public static boolean equalsByAccessibleProperties(Object object, Object object2) throws IntrospectionException {
        return BeansUtils.equalsByAccessibleProperties(object, object2, Collections.EMPTY_SET);
    }

    public static boolean equalsByAccessibleProperties(Object object, Object object2, Collection collection) throws IntrospectionException {
        HashMap hashMap = new HashMap();
        HashMap hashMap2 = new HashMap();
        BeansUtils.extractAccessiblePropertiesToMap(hashMap, object, collection);
        BeansUtils.extractAccessiblePropertiesToMap(hashMap2, object2, collection);
        return hashMap.equals(hashMap2);
    }

    public static boolean equalsByAccessiblePropertiesVerbose(Object object, Object object2, Collection collection) throws IntrospectionException {
        Object object3;
        HashMap hashMap = new HashMap();
        HashMap hashMap2 = new HashMap();
        BeansUtils.extractAccessiblePropertiesToMap(hashMap, object, collection);
        BeansUtils.extractAccessiblePropertiesToMap(hashMap2, object2, collection);
        boolean bl = true;
        if (hashMap.size() != hashMap2.size()) {
            System.err.println("Unequal sizes --> Map0: " + hashMap.size() + "; m1: " + hashMap2.size());
            Set set = hashMap.keySet();
            set.removeAll(hashMap2.keySet());
            Object object4 = hashMap2.keySet();
            object4.removeAll(hashMap.keySet());
            if (set.size() > 0) {
                System.err.println("Map0 extras:");
                object3 = set.iterator();
                while (object3.hasNext()) {
                    System.err.println('\t' + object3.next().toString());
                }
            }
            if (object4.size() > 0) {
                System.err.println("Map1 extras:");
                object3 = object4.iterator();
                while (object3.hasNext()) {
                    System.err.println('\t' + object3.next().toString());
                }
            }
            bl = false;
        }
        for (Object object4 : hashMap.keySet()) {
            object3 = hashMap.get(object4);
            Object v = hashMap2.get(object4);
            if ((object3 != null || v == null) && (object3 == null || object3.equals(v))) continue;
            System.err.println('\t' + (String)object4 + ": " + object3 + " != " + v);
            bl = false;
        }
        return bl;
    }

    public static void overwriteAccessibleProperties(Object object, Object object2) throws IntrospectionException {
        BeansUtils.overwriteAccessibleProperties(object, object2, Collections.EMPTY_SET);
    }

    public static void overwriteAccessibleProperties(Object object, Object object2, Collection collection) throws IntrospectionException {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass(), Object.class);
            for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                if (collection.contains(propertyDescriptor.getName())) continue;
                Method method = propertyDescriptor.getReadMethod();
                Method method2 = propertyDescriptor.getWriteMethod();
                if (method == null || method2 == null) {
                    if (propertyDescriptor instanceof IndexedPropertyDescriptor && logger.isLoggable(MLevel.WARNING)) {
                        logger.warning("BeansUtils.overwriteAccessibleProperties() does not support indexed properties that do not provide single-valued array getters and setters! [The indexed methods provide no means of modifying the size of the array in the destination bean if it does not match the source.]");
                    }
                    if (!logger.isLoggable(MLevel.INFO)) continue;
                    logger.info("Property inaccessible for overwriting: " + propertyDescriptor.getName());
                    continue;
                }
                Object object3 = method.invoke(object, EMPTY_ARGS);
                method2.invoke(object2, object3);
            }
        }
        catch (IntrospectionException introspectionException) {
            throw introspectionException;
        }
        catch (Exception exception) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Converting exception to throwable IntrospectionException");
            }
            throw new IntrospectionException(exception.getMessage());
        }
    }

    public static void overwriteAccessiblePropertiesFromMap(Map map, Object object, boolean bl) throws IntrospectionException {
        BeansUtils.overwriteAccessiblePropertiesFromMap(map, object, bl, Collections.EMPTY_SET);
    }

    public static void overwriteAccessiblePropertiesFromMap(Map map, Object object, boolean bl, Collection collection) throws IntrospectionException {
        BeansUtils.overwriteAccessiblePropertiesFromMap(map, object, bl, collection, false, MLevel.WARNING, MLevel.WARNING, true);
    }

    public static void overwriteAccessiblePropertiesFromMap(Map map, Object object, boolean bl, Collection collection, boolean bl2, MLevel mLevel, MLevel mLevel2, boolean bl3) throws IntrospectionException {
        if (mLevel == null) {
            mLevel = MLevel.WARNING;
        }
        if (mLevel2 == null) {
            mLevel2 = MLevel.WARNING;
        }
        Set set = map.keySet();
        String string = null;
        BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass(), Object.class);
        for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
            Object object2;
            Object v;
            string = propertyDescriptor.getName();
            if (!set.contains(string) || collection != null && collection.contains(string) || (v = map.get(string)) == null && bl) continue;
            Method method = propertyDescriptor.getWriteMethod();
            boolean bl4 = false;
            Class<?> clazz = propertyDescriptor.getPropertyType();
            if (method == null) {
                if (propertyDescriptor instanceof IndexedPropertyDescriptor && logger.isLoggable(MLevel.FINER)) {
                    logger.finer("BeansUtils.overwriteAccessiblePropertiesFromMap() does not support indexed properties that do not provide single-valued array getters and setters! [The indexed methods provide no means of modifying the size of the array in the destination bean if it does not match the source.]");
                }
                if (!logger.isLoggable(mLevel)) continue;
                object2 = "Property inaccessible for overwriting: " + string;
                logger.log(mLevel, (String)object2);
                if (!bl3) continue;
                bl4 = true;
                throw new IntrospectionException((String)object2);
            }
            if (bl2 && v != null && v.getClass() == String.class && (clazz = propertyDescriptor.getPropertyType()) != String.class && Coerce.canCoerce(clazz)) {
                String string2;
                try {
                    object2 = Coerce.toObject((String)v, clazz);
                    method.invoke(object, object2);
                    continue;
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    string2 = "Failed to coerce property: " + string + " [propVal: " + v + "; propType: " + clazz + "]";
                    if (logger.isLoggable(mLevel2)) {
                        logger.log(mLevel2, string2, illegalArgumentException);
                    }
                    if (!bl3) continue;
                    bl4 = true;
                    throw new IntrospectionException(string2);
                }
                catch (Exception exception) {
                    string2 = "Failed to set property: " + string + " [propVal: " + v + "; propType: " + clazz + "]";
                    if (logger.isLoggable(mLevel)) {
                        logger.log(mLevel, string2, exception);
                    }
                    if (!bl3) continue;
                    bl4 = true;
                    throw new IntrospectionException(string2);
                }
            }
            try {
                method.invoke(object, v);
            }
            catch (Exception exception) {
                String string3 = "Failed to set property: " + string + " [propVal: " + v + "; propType: " + clazz + "]";
                if (logger.isLoggable(mLevel)) {
                    logger.log(mLevel, string3, exception);
                }
                if (!bl3) continue;
                bl4 = true;
                throw new IntrospectionException(string3);
            }
        }
    }

    public static void appendPropNamesAndValues(StringBuffer stringBuffer, Object object, Collection collection) throws IntrospectionException {
        TreeMap treeMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        BeansUtils.extractAccessiblePropertiesToMap(treeMap, object, collection);
        boolean bl = true;
        for (String string : treeMap.keySet()) {
            Object v = treeMap.get(string);
            if (bl) {
                bl = false;
            } else {
                stringBuffer.append(", ");
            }
            stringBuffer.append(string);
            stringBuffer.append(" -> ");
            stringBuffer.append(v);
        }
    }

    public static void extractAccessiblePropertiesToMap(Map map, Object object) throws IntrospectionException {
        BeansUtils.extractAccessiblePropertiesToMap(map, object, Collections.EMPTY_SET);
    }

    public static void extractAccessiblePropertiesToMap(Map map, Object object, Collection collection) throws IntrospectionException {
        String string = null;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass(), Object.class);
            for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                string = propertyDescriptor.getName();
                if (collection.contains(string)) continue;
                Method method = propertyDescriptor.getReadMethod();
                Object object2 = method.invoke(object, EMPTY_ARGS);
                map.put(string, object2);
            }
        }
        catch (IntrospectionException introspectionException) {
            if (logger.isLoggable(MLevel.WARNING)) {
                logger.warning("Problem occurred while overwriting property: " + string);
            }
            if (logger.isLoggable(MLevel.FINE)) {
                logger.logp(MLevel.FINE, BeansUtils.class.getName(), "extractAccessiblePropertiesToMap( Map fillMe, Object bean, Collection ignoreProps )", (string != null ? "Problem occurred while overwriting property: " + string : "") + " throwing...", introspectionException);
            }
            throw introspectionException;
        }
        catch (Exception exception) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.logp(MLevel.FINE, BeansUtils.class.getName(), "extractAccessiblePropertiesToMap( Map fillMe, Object bean, Collection ignoreProps )", "Caught unexpected Exception; Converting to IntrospectionException.", exception);
            }
            throw new IntrospectionException(exception.toString() + (string == null ? "" : " [" + string + ']'));
        }
    }

    private static void overwriteProperty(String string, Object object, Method method, Object object2) throws Exception {
        if (method.getDeclaringClass().isAssignableFrom(object2.getClass())) {
            method.invoke(object2, object);
        } else {
            BeanInfo beanInfo = Introspector.getBeanInfo(object2.getClass(), Object.class);
            PropertyDescriptor propertyDescriptor = null;
            PropertyDescriptor[] propertyDescriptorArray = beanInfo.getPropertyDescriptors();
            int n = propertyDescriptorArray.length;
            for (int i = 0; i < n; ++i) {
                if (!string.equals(propertyDescriptorArray[i].getName())) continue;
                propertyDescriptor = propertyDescriptorArray[i];
                break;
            }
            Method method2 = propertyDescriptor.getWriteMethod();
            method2.invoke(object2, object);
        }
    }

    public static void overwriteSpecificAccessibleProperties(Object object, Object object2, Collection collection) throws IntrospectionException {
        try {
            HashSet hashSet = new HashSet(collection);
            BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass(), Object.class);
            for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                String string = propertyDescriptor.getName();
                if (!hashSet.remove(string)) continue;
                Method method = propertyDescriptor.getReadMethod();
                Method method2 = propertyDescriptor.getWriteMethod();
                if (method == null || method2 == null) {
                    if (propertyDescriptor instanceof IndexedPropertyDescriptor && logger.isLoggable(MLevel.WARNING)) {
                        logger.warning("BeansUtils.overwriteAccessibleProperties() does not support indexed properties that do not provide single-valued array getters and setters! [The indexed methods provide no means of modifying the size of the array in the destination bean if it does not match the source.]");
                    }
                    if (!logger.isLoggable(MLevel.INFO)) continue;
                    logger.info("Property inaccessible for overwriting: " + propertyDescriptor.getName());
                    continue;
                }
                Object object3 = method.invoke(object, EMPTY_ARGS);
                BeansUtils.overwriteProperty(string, object3, method2, object2);
            }
            if (logger.isLoggable(MLevel.WARNING)) {
                Iterator iterator = hashSet.iterator();
                while (iterator.hasNext()) {
                    logger.warning("failed to find expected property: " + iterator.next());
                }
            }
        }
        catch (IntrospectionException introspectionException) {
            throw introspectionException;
        }
        catch (Exception exception) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.logp(MLevel.FINE, BeansUtils.class.getName(), "overwriteSpecificAccessibleProperties( Object sourceBean, Object destBean, Collection props )", "Caught unexpected Exception; Converting to IntrospectionException.", exception);
            }
            throw new IntrospectionException(exception.getMessage());
        }
    }

    public static void debugShowPropertyChange(PropertyChangeEvent propertyChangeEvent) {
        System.err.println("PropertyChangeEvent: [ propertyName -> " + propertyChangeEvent.getPropertyName() + ", oldValue -> " + propertyChangeEvent.getOldValue() + ", newValue -> " + propertyChangeEvent.getNewValue() + " ]");
    }

    private BeansUtils() {
    }
}

