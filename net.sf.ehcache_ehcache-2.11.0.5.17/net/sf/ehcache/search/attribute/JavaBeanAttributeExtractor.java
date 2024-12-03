/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.attribute;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.sf.ehcache.Element;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.search.attribute.AttributeExtractorException;

public class JavaBeanAttributeExtractor
implements AttributeExtractor {
    private static final Object NO_VALUE = new Object();
    private volatile transient MethodRef lastKeyMethod;
    private volatile transient MethodRef lastValueMethod;
    private final String beanProperty;

    public JavaBeanAttributeExtractor(String beanProperty) {
        if (beanProperty == null) {
            throw new NullPointerException();
        }
        if ((beanProperty = beanProperty.trim()).length() == 0) {
            throw new IllegalArgumentException("bean property empty");
        }
        this.beanProperty = beanProperty;
    }

    @Override
    public Object attributeFor(Element element, String attributeName) throws AttributeExtractorException {
        Object value;
        Object attribute = NO_VALUE;
        Object key = element.getObjectKey();
        if (key != null) {
            MethodRef keyMethod = this.lastKeyMethod;
            if (keyMethod == null || keyMethod.targetClass != key.getClass()) {
                this.lastKeyMethod = keyMethod = this.findMethod(key);
            }
            if (keyMethod.method != null) {
                attribute = this.getValue(keyMethod.method, key);
            }
        }
        if ((value = element.getObjectValue()) != null) {
            MethodRef valueMethod = this.lastValueMethod;
            if (valueMethod == null || valueMethod.targetClass != value.getClass()) {
                this.lastValueMethod = valueMethod = this.findMethod(value);
            }
            if (valueMethod.method != null) {
                if (attribute != NO_VALUE) {
                    throw new AttributeExtractorException("Bean property [" + this.beanProperty + "] present on both key and value");
                }
                return this.getValue(valueMethod.method, value);
            }
        }
        if (attribute != NO_VALUE) {
            return attribute;
        }
        throw new AttributeExtractorException("Bean property [" + this.beanProperty + "] not present on either key or value");
    }

    private MethodRef findMethod(Object obj) {
        String upperFirstProp = "" + Character.toUpperCase(this.beanProperty.charAt(0));
        if (this.beanProperty.length() > 1) {
            upperFirstProp = upperFirstProp + this.beanProperty.substring(1);
        }
        Class<?> target = obj.getClass();
        try {
            return new MethodRef(target, target.getMethod("get" + upperFirstProp, new Class[0]));
        }
        catch (SecurityException e) {
            throw new AttributeExtractorException(e);
        }
        catch (NoSuchMethodException e) {
            try {
                Method m = target.getMethod("is" + upperFirstProp, new Class[0]);
                if (m.getReturnType().equals(Boolean.class) || m.getReturnType().equals(Boolean.TYPE)) {
                    return new MethodRef(target, m);
                }
            }
            catch (SecurityException e2) {
                throw new AttributeExtractorException(e2);
            }
            catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
            return new MethodRef(target, null);
        }
    }

    private Object getValue(Method method, Object key) {
        try {
            return method.invoke(key, new Object[0]);
        }
        catch (Throwable t) {
            if (t instanceof InvocationTargetException) {
                t = t.getCause();
            }
            if (t instanceof Error) {
                throw (Error)t;
            }
            throw new AttributeExtractorException("Error getting bean property [" + this.beanProperty + "] on instance of " + key.getClass().getName(), t);
        }
    }

    private static class MethodRef {
        private final Class targetClass;
        private final Method method;

        MethodRef(Class target, Method method) {
            this.targetClass = target;
            this.method = method;
        }
    }
}

