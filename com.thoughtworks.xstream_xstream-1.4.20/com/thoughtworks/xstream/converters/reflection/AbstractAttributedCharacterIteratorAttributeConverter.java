/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.core.util.Fields;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.AttributedCharacterIterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AbstractAttributedCharacterIteratorAttributeConverter
extends AbstractSingleValueConverter {
    private static final Map instanceMaps = Collections.synchronizedMap(new HashMap());
    private final Class type;
    static /* synthetic */ Class class$java$lang$String;

    public AbstractAttributedCharacterIteratorAttributeConverter(Class type) {
        if (!AttributedCharacterIterator.Attribute.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(type.getName() + " is not a " + AttributedCharacterIterator.Attribute.class.getName());
        }
        this.type = type;
    }

    public boolean canConvert(Class type) {
        return type == this.type && !this.getAttributeMap().isEmpty();
    }

    public String toString(Object source) {
        return this.getName((AttributedCharacterIterator.Attribute)source);
    }

    private String getName(AttributedCharacterIterator.Attribute attribute) {
        String className;
        String s;
        ReflectiveOperationException ex = null;
        if (Reflections.getName != null) {
            try {
                return (String)Reflections.getName.invoke((Object)attribute, (Object[])null);
            }
            catch (IllegalAccessException e) {
                ex = e;
            }
            catch (InvocationTargetException e) {
                ex = e;
            }
        }
        if ((s = attribute.toString()).startsWith(className = attribute.getClass().getName())) {
            return s.substring(className.length() + 1, s.length() - 1);
        }
        ConversionException exception = new ConversionException("Cannot find name of attribute", ex);
        exception.add("attribute-type", className);
        throw exception;
    }

    public Object fromString(String str) {
        Object attr = this.getAttributeMap().get(str);
        if (attr != null) {
            return attr;
        }
        ConversionException exception = new ConversionException("Cannot find attribute");
        exception.add("attribute-type", this.type.getName());
        exception.add("attribute-name", str);
        throw exception;
    }

    private Map getAttributeMap() {
        Map attributeMap = (Map)instanceMaps.get(this.type.getName());
        if (attributeMap == null) {
            attributeMap = this.buildAttributeMap();
            instanceMaps.put(this.type.getName(), attributeMap);
        }
        return attributeMap;
    }

    private Map buildAttributeMap() {
        HashMap<String, AttributedCharacterIterator.Attribute> attributeMap = new HashMap<String, AttributedCharacterIterator.Attribute>();
        Field instanceMap = Fields.locate(this.type, Map.class, true);
        if (instanceMap != null) {
            try {
                Map map = (Map)Fields.read(instanceMap, null);
                if (map != null) {
                    boolean valid = true;
                    Iterator iter = map.entrySet().iterator();
                    while (valid && iter.hasNext()) {
                        Map.Entry entry = iter.next();
                        valid = entry.getKey().getClass() == (class$java$lang$String == null ? AbstractAttributedCharacterIteratorAttributeConverter.class$("java.lang.String") : class$java$lang$String) && entry.getValue().getClass() == this.type;
                    }
                    if (valid) {
                        attributeMap.putAll(map);
                    }
                }
            }
            catch (ObjectAccessException map) {
                // empty catch block
            }
        }
        if (attributeMap.isEmpty()) {
            try {
                Field[] fields = this.type.getDeclaredFields();
                for (int i = 0; i < fields.length; ++i) {
                    if (fields[i].getType() == this.type != Modifier.isStatic(fields[i].getModifiers())) continue;
                    AttributedCharacterIterator.Attribute attribute = (AttributedCharacterIterator.Attribute)Fields.read(fields[i], null);
                    attributeMap.put(this.toString(attribute), attribute);
                }
            }
            catch (SecurityException e) {
                attributeMap.clear();
            }
            catch (ObjectAccessException e) {
                attributeMap.clear();
            }
            catch (NoClassDefFoundError e) {
                attributeMap.clear();
            }
        }
        return attributeMap;
    }

    private static class Reflections {
        private static final Method getName;

        private Reflections() {
        }

        static {
            Method method = null;
            try {
                method = (class$java$text$AttributedCharacterIterator$Attribute == null ? (class$java$text$AttributedCharacterIterator$Attribute = AbstractAttributedCharacterIteratorAttributeConverter.class$("java.text.AttributedCharacterIterator$Attribute")) : class$java$text$AttributedCharacterIterator$Attribute).getDeclaredMethod("getName", null);
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
            }
            catch (SecurityException securityException) {
            }
            catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
            getName = method;
        }
    }
}

