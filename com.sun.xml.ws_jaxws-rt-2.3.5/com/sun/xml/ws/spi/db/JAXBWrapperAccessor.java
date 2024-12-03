/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElementRef
 *  javax.xml.bind.annotation.XmlElementWrapper
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.spi.db;

import com.sun.xml.ws.spi.db.DatabindingException;
import com.sun.xml.ws.spi.db.FieldGetter;
import com.sun.xml.ws.spi.db.FieldSetter;
import com.sun.xml.ws.spi.db.MethodGetter;
import com.sun.xml.ws.spi.db.MethodSetter;
import com.sun.xml.ws.spi.db.PropertyAccessor;
import com.sun.xml.ws.spi.db.PropertyGetter;
import com.sun.xml.ws.spi.db.PropertyGetterBase;
import com.sun.xml.ws.spi.db.PropertySetter;
import com.sun.xml.ws.spi.db.PropertySetterBase;
import com.sun.xml.ws.spi.db.WrapperAccessor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public class JAXBWrapperAccessor
extends WrapperAccessor {
    protected Class<?> contentClass;
    protected HashMap<Object, Class> elementDeclaredTypes;

    public JAXBWrapperAccessor(Class<?> wrapperBean) {
        PropertyGetterBase.verifyWrapperType(wrapperBean);
        this.contentClass = wrapperBean;
        HashMap<QName, PropertySetter> setByQName = new HashMap<QName, PropertySetter>();
        HashMap<String, PropertySetter> setByLocalpart = new HashMap<String, PropertySetter>();
        HashMap<String, Method> publicSetters = new HashMap<String, Method>();
        HashMap<QName, PropertyGetter> getByQName = new HashMap<QName, PropertyGetter>();
        HashMap<String, PropertyGetter> getByLocalpart = new HashMap<String, PropertyGetter>();
        HashMap<String, Method> publicGetters = new HashMap<String, Method>();
        HashMap elementDeclaredTypesByQName = new HashMap();
        HashMap elementDeclaredTypesByLocalpart = new HashMap();
        for (Method method : this.contentClass.getMethods()) {
            if (PropertySetterBase.setterPattern(method)) {
                String key = method.getName().substring(3, method.getName().length()).toLowerCase();
                publicSetters.put(key, method);
            }
            if (!PropertyGetterBase.getterPattern(method)) continue;
            String methodName = method.getName();
            String key = methodName.startsWith("is") ? methodName.substring(2, method.getName().length()).toLowerCase() : methodName.substring(3, method.getName().length()).toLowerCase();
            publicGetters.put(key, method);
        }
        HashSet<String> elementLocalNames = new HashSet<String>();
        for (Field field : JAXBWrapperAccessor.getAllFields(this.contentClass)) {
            XmlElementWrapper xmlElemWrapper = field.getAnnotation(XmlElementWrapper.class);
            XmlElement xmlElem = field.getAnnotation(XmlElement.class);
            XmlElementRef xmlElemRef = field.getAnnotation(XmlElementRef.class);
            String fieldName = field.getName().toLowerCase();
            String namespace = "";
            String localName = field.getName();
            if (xmlElemWrapper != null) {
                namespace = xmlElemWrapper.namespace();
                if (xmlElemWrapper.name() != null && !xmlElemWrapper.name().equals("") && !xmlElemWrapper.name().equals("##default")) {
                    localName = xmlElemWrapper.name();
                }
            } else if (xmlElem != null) {
                namespace = xmlElem.namespace();
                if (xmlElem.name() != null && !xmlElem.name().equals("") && !xmlElem.name().equals("##default")) {
                    localName = xmlElem.name();
                }
            } else if (xmlElemRef != null) {
                namespace = xmlElemRef.namespace();
                if (xmlElemRef.name() != null && !xmlElemRef.name().equals("") && !xmlElemRef.name().equals("##default")) {
                    localName = xmlElemRef.name();
                }
            }
            if (elementLocalNames.contains(localName)) {
                this.elementLocalNameCollision = true;
            } else {
                elementLocalNames.add(localName);
            }
            QName qname = new QName(namespace, localName);
            if (field.getType().equals(JAXBElement.class) && field.getGenericType() instanceof ParameterizedType) {
                Type componentType;
                Type arg = ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
                if (arg instanceof Class) {
                    elementDeclaredTypesByQName.put(qname, (Class)arg);
                    elementDeclaredTypesByLocalpart.put(localName, (Class)arg);
                } else if (arg instanceof GenericArrayType && (componentType = ((GenericArrayType)arg).getGenericComponentType()) instanceof Class) {
                    Class<?> arrayClass = Array.newInstance((Class)componentType, 0).getClass();
                    elementDeclaredTypesByQName.put(qname, arrayClass);
                    elementDeclaredTypesByLocalpart.put(localName, arrayClass);
                }
            }
            Method setMethod = JAXBWrapperAccessor.accessor(publicSetters, fieldName, localName);
            Method getMethod = JAXBWrapperAccessor.accessor(publicGetters, fieldName, localName);
            if (!JAXBWrapperAccessor.isProperty(field, getMethod, setMethod)) continue;
            PropertySetter setter = JAXBWrapperAccessor.createPropertySetter(field, setMethod);
            PropertyGetter getter = JAXBWrapperAccessor.createPropertyGetter(field, getMethod);
            setByQName.put(qname, setter);
            setByLocalpart.put(localName, setter);
            getByQName.put(qname, getter);
            getByLocalpart.put(localName, getter);
        }
        if (this.elementLocalNameCollision) {
            this.propertySetters = setByQName;
            this.propertyGetters = getByQName;
            this.elementDeclaredTypes = elementDeclaredTypesByQName;
        } else {
            this.propertySetters = setByLocalpart;
            this.propertyGetters = getByLocalpart;
            this.elementDeclaredTypes = elementDeclaredTypesByLocalpart;
        }
    }

    private static Method accessor(HashMap<String, Method> map, String fieldName, String localName) {
        Method a = map.get(fieldName);
        if (a == null) {
            a = map.get(localName);
        }
        if (a == null && fieldName.startsWith("_")) {
            a = map.get(fieldName.substring(1));
        }
        return a;
    }

    private static boolean isProperty(Field field, Method getter, Method setter) {
        if (Modifier.isPublic(field.getModifiers())) {
            return true;
        }
        if (getter == null) {
            return false;
        }
        if (setter == null) {
            return Collection.class.isAssignableFrom(field.getType()) || Map.class.isAssignableFrom(field.getType());
        }
        return true;
    }

    private static List<Field> getAllFields(Class<?> clz) {
        ArrayList<Field> list = new ArrayList<Field>();
        while (!Object.class.equals(clz)) {
            list.addAll(Arrays.asList(JAXBWrapperAccessor.getDeclaredFields(clz)));
            clz = clz.getSuperclass();
        }
        return list;
    }

    private static Field[] getDeclaredFields(final Class<?> clz) {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Field[]>(){

                @Override
                public Field[] run() throws IllegalAccessException {
                    return clz.getDeclaredFields();
                }
            });
        }
        catch (PrivilegedActionException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    private static PropertyGetter createPropertyGetter(Field field, Method getMethod) {
        MethodGetter methodGetter;
        if (!field.isAccessible() && getMethod != null && (methodGetter = new MethodGetter(getMethod)).getType().toString().equals(field.getType().toString())) {
            return methodGetter;
        }
        return new PrivFieldGetter(field);
    }

    private static PropertySetter createPropertySetter(Field field, Method setter) {
        MethodSetter injection;
        if (!field.isAccessible() && setter != null && (injection = new MethodSetter(setter)).getType().toString().equals(field.getType().toString())) {
            return injection;
        }
        return new PrivFieldSetter(field);
    }

    private Class getElementDeclaredType(QName name) {
        Object key = this.elementLocalNameCollision ? name : name.getLocalPart();
        return this.elementDeclaredTypes.get(key);
    }

    @Override
    public PropertyAccessor getPropertyAccessor(String ns, String name) {
        final QName n = new QName(ns, name);
        final PropertySetter setter = this.getPropertySetter(n);
        final PropertyGetter getter = this.getPropertyGetter(n);
        final boolean isJAXBElement = setter.getType().equals(JAXBElement.class);
        final boolean isListType = List.class.isAssignableFrom(setter.getType());
        final Class elementDeclaredType = isJAXBElement ? this.getElementDeclaredType(n) : null;
        return new PropertyAccessor(){

            public Object get(Object bean) throws DatabindingException {
                JAXBElement jaxbElement;
                Object val = isJAXBElement ? ((jaxbElement = (JAXBElement)JAXBWrapperAccessor.get(getter, bean)) == null ? null : jaxbElement.getValue()) : JAXBWrapperAccessor.get(getter, bean);
                if (val == null && isListType) {
                    val = new ArrayList();
                    this.set(bean, val);
                }
                return val;
            }

            public void set(Object bean, Object value) throws DatabindingException {
                if (isJAXBElement) {
                    JAXBElement jaxbElement = new JAXBElement(n, elementDeclaredType, JAXBWrapperAccessor.this.contentClass, value);
                    JAXBWrapperAccessor.set(setter, bean, jaxbElement);
                } else {
                    JAXBWrapperAccessor.set(setter, bean, value);
                }
            }
        };
    }

    private static Object get(PropertyGetter getter, Object wrapperInstance) {
        return getter instanceof PrivFieldGetter ? ((PrivFieldGetter)getter).getPriv(wrapperInstance) : getter.get(wrapperInstance);
    }

    private static void set(PropertySetter setter, Object wrapperInstance, Object value) {
        if (setter instanceof PrivFieldSetter) {
            ((PrivFieldSetter)setter).setPriv(wrapperInstance, value);
        } else {
            setter.set(wrapperInstance, value);
        }
    }

    private static class PrivFieldGetter
    extends FieldGetter {
        private PrivFieldGetter(Field f) {
            super(f);
        }

        private Object getPriv(Object instance) {
            if (this.field.isAccessible()) {
                try {
                    return this.field.get(instance);
                }
                catch (Exception e) {
                    throw new WebServiceException((Throwable)e);
                }
            }
            PrivilegedGetter privilegedGetter = new PrivilegedGetter(this.field, instance);
            try {
                AccessController.doPrivileged(privilegedGetter);
            }
            catch (PrivilegedActionException e) {
                throw new WebServiceException((Throwable)e);
            }
            return privilegedGetter.value;
        }

        private static class PrivilegedGetter
        implements PrivilegedExceptionAction {
            private Object value;
            private Field field;
            private Object instance;

            public PrivilegedGetter(Field field, Object instance) {
                this.field = field;
                this.instance = instance;
            }

            public Object run() throws IllegalAccessException {
                if (!this.field.isAccessible()) {
                    this.field.setAccessible(true);
                }
                this.value = this.field.get(this.instance);
                return null;
            }
        }
    }

    private static class PrivFieldSetter
    extends FieldSetter {
        private PrivFieldSetter(Field f) {
            super(f);
        }

        private void setPriv(final Object instance, Object val) {
            Object resource;
            Object object = resource = this.type.isPrimitive() && val == null ? PrivFieldSetter.uninitializedValue(this.type) : val;
            if (this.field.isAccessible()) {
                try {
                    this.field.set(instance, resource);
                }
                catch (Exception e) {
                    throw new WebServiceException((Throwable)e);
                }
            }
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction<Object>(){

                    @Override
                    public Object run() throws IllegalAccessException {
                        if (!field.isAccessible()) {
                            field.setAccessible(true);
                        }
                        field.set(instance, resource);
                        return null;
                    }
                });
            }
            catch (PrivilegedActionException e) {
                throw new WebServiceException((Throwable)e);
            }
        }
    }
}

