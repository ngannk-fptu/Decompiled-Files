/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.CollectionFactory
 *  org.springframework.core.convert.ConversionFailedException
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.NumberUtils
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.beans;

import java.beans.PropertyEditor;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyEditorRegistrySupport;
import org.springframework.core.CollectionFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.NumberUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

class TypeConverterDelegate {
    private static final Log logger = LogFactory.getLog(TypeConverterDelegate.class);
    private final PropertyEditorRegistrySupport propertyEditorRegistry;
    @Nullable
    private final Object targetObject;

    public TypeConverterDelegate(PropertyEditorRegistrySupport propertyEditorRegistry) {
        this(propertyEditorRegistry, null);
    }

    public TypeConverterDelegate(PropertyEditorRegistrySupport propertyEditorRegistry, @Nullable Object targetObject) {
        this.propertyEditorRegistry = propertyEditorRegistry;
        this.targetObject = targetObject;
    }

    @Nullable
    public <T> T convertIfNecessary(@Nullable String propertyName, @Nullable Object oldValue, Object newValue, @Nullable Class<T> requiredType) throws IllegalArgumentException {
        return this.convertIfNecessary(propertyName, oldValue, newValue, requiredType, TypeDescriptor.valueOf(requiredType));
    }

    @Nullable
    public <T> T convertIfNecessary(@Nullable String propertyName, @Nullable Object oldValue, @Nullable Object newValue, @Nullable Class<T> requiredType, @Nullable TypeDescriptor typeDescriptor) throws IllegalArgumentException {
        TypeDescriptor sourceTypeDesc;
        PropertyEditor editor = this.propertyEditorRegistry.findCustomEditor(requiredType, propertyName);
        ConversionFailedException conversionAttemptEx = null;
        ConversionService conversionService = this.propertyEditorRegistry.getConversionService();
        if (editor == null && conversionService != null && newValue != null && typeDescriptor != null && conversionService.canConvert(sourceTypeDesc = TypeDescriptor.forObject((Object)newValue), typeDescriptor)) {
            try {
                return (T)conversionService.convert(newValue, sourceTypeDesc, typeDescriptor);
            }
            catch (ConversionFailedException ex) {
                conversionAttemptEx = ex;
            }
        }
        Object convertedValue = newValue;
        if (editor != null || requiredType != null && !ClassUtils.isAssignableValue(requiredType, (Object)convertedValue)) {
            Class elementType;
            TypeDescriptor elementTypeDesc;
            if (typeDescriptor != null && requiredType != null && Collection.class.isAssignableFrom(requiredType) && convertedValue instanceof String && (elementTypeDesc = typeDescriptor.getElementTypeDescriptor()) != null && (Class.class == (elementType = elementTypeDesc.getType()) || Enum.class.isAssignableFrom(elementType))) {
                convertedValue = StringUtils.commaDelimitedListToStringArray((String)((String)convertedValue));
            }
            if (editor == null) {
                editor = this.findDefaultEditor(requiredType);
            }
            convertedValue = this.doConvertValue(oldValue, convertedValue, requiredType, editor);
        }
        boolean standardConversion = false;
        if (requiredType != null) {
            if (convertedValue != null) {
                if (Object.class == requiredType) {
                    return (T)convertedValue;
                }
                if (requiredType.isArray()) {
                    if (convertedValue instanceof String && Enum.class.isAssignableFrom(requiredType.getComponentType())) {
                        convertedValue = StringUtils.commaDelimitedListToStringArray((String)((String)convertedValue));
                    }
                    return (T)this.convertToTypedArray(convertedValue, propertyName, requiredType.getComponentType());
                }
                if (convertedValue instanceof Collection) {
                    convertedValue = this.convertToTypedCollection((Collection)convertedValue, propertyName, requiredType, typeDescriptor);
                    standardConversion = true;
                } else if (convertedValue instanceof Map) {
                    convertedValue = this.convertToTypedMap((Map)convertedValue, propertyName, requiredType, typeDescriptor);
                    standardConversion = true;
                }
                if (convertedValue.getClass().isArray() && Array.getLength(convertedValue) == 1) {
                    convertedValue = Array.get(convertedValue, 0);
                    standardConversion = true;
                }
                if (String.class == requiredType && ClassUtils.isPrimitiveOrWrapper(convertedValue.getClass())) {
                    return (T)convertedValue.toString();
                }
                if (convertedValue instanceof String && !requiredType.isInstance(convertedValue)) {
                    block34: {
                        if (conversionAttemptEx == null && !requiredType.isInterface() && !requiredType.isEnum()) {
                            try {
                                Constructor<T> strCtor = requiredType.getConstructor(String.class);
                                return BeanUtils.instantiateClass(strCtor, convertedValue);
                            }
                            catch (NoSuchMethodException ex) {
                                if (logger.isTraceEnabled()) {
                                    logger.trace((Object)("No String constructor found on type [" + requiredType.getName() + "]"), (Throwable)ex);
                                }
                            }
                            catch (Exception ex) {
                                if (!logger.isDebugEnabled()) break block34;
                                logger.debug((Object)("Construction via String failed for type [" + requiredType.getName() + "]"), (Throwable)ex);
                            }
                        }
                    }
                    String trimmedValue = ((String)convertedValue).trim();
                    if (requiredType.isEnum() && trimmedValue.isEmpty()) {
                        return null;
                    }
                    convertedValue = this.attemptToConvertStringToEnum(requiredType, trimmedValue, convertedValue);
                    standardConversion = true;
                } else if (convertedValue instanceof Number && Number.class.isAssignableFrom(requiredType)) {
                    convertedValue = NumberUtils.convertNumberToTargetClass((Number)((Number)convertedValue), requiredType);
                    standardConversion = true;
                }
            } else if (requiredType == Optional.class) {
                convertedValue = Optional.empty();
            }
            if (!ClassUtils.isAssignableValue(requiredType, convertedValue)) {
                TypeDescriptor sourceTypeDesc2;
                if (conversionAttemptEx != null) {
                    throw conversionAttemptEx;
                }
                if (conversionService != null && typeDescriptor != null && conversionService.canConvert(sourceTypeDesc2 = TypeDescriptor.forObject(newValue), typeDescriptor)) {
                    return (T)conversionService.convert(newValue, sourceTypeDesc2, typeDescriptor);
                }
                StringBuilder msg = new StringBuilder();
                msg.append("Cannot convert value of type '").append(ClassUtils.getDescriptiveType(newValue));
                msg.append("' to required type '").append(ClassUtils.getQualifiedName(requiredType)).append('\'');
                if (propertyName != null) {
                    msg.append(" for property '").append(propertyName).append('\'');
                }
                if (editor != null) {
                    msg.append(": PropertyEditor [").append(editor.getClass().getName()).append("] returned inappropriate value of type '").append(ClassUtils.getDescriptiveType((Object)convertedValue)).append('\'');
                    throw new IllegalArgumentException(msg.toString());
                }
                msg.append(": no matching editors or conversion strategy found");
                throw new IllegalStateException(msg.toString());
            }
        }
        if (conversionAttemptEx != null) {
            if (editor == null && !standardConversion && requiredType != null && Object.class != requiredType) {
                throw conversionAttemptEx;
            }
            logger.debug((Object)"Original ConversionService attempt failed - ignored since PropertyEditor based conversion eventually succeeded", conversionAttemptEx);
        }
        return (T)convertedValue;
    }

    private Object attemptToConvertStringToEnum(Class<?> requiredType, String trimmedValue, Object currentConvertedValue) {
        Object convertedValue;
        block9: {
            block8: {
                int index;
                convertedValue = currentConvertedValue;
                if (Enum.class == requiredType && this.targetObject != null && (index = trimmedValue.lastIndexOf(46)) > -1) {
                    String enumType = trimmedValue.substring(0, index);
                    String fieldName = trimmedValue.substring(index + 1);
                    ClassLoader cl = this.targetObject.getClass().getClassLoader();
                    try {
                        Class enumValueType = ClassUtils.forName((String)enumType, (ClassLoader)cl);
                        Field enumField = enumValueType.getField(fieldName);
                        convertedValue = enumField.get(null);
                    }
                    catch (ClassNotFoundException ex) {
                        if (logger.isTraceEnabled()) {
                            logger.trace((Object)("Enum class [" + enumType + "] cannot be loaded"), (Throwable)ex);
                        }
                    }
                    catch (Throwable ex) {
                        if (!logger.isTraceEnabled()) break block8;
                        logger.trace((Object)("Field [" + fieldName + "] isn't an enum value for type [" + enumType + "]"), ex);
                    }
                }
            }
            if (convertedValue == currentConvertedValue) {
                try {
                    Field enumField = requiredType.getField(trimmedValue);
                    ReflectionUtils.makeAccessible((Field)enumField);
                    convertedValue = enumField.get(null);
                }
                catch (Throwable ex) {
                    if (!logger.isTraceEnabled()) break block9;
                    logger.trace((Object)("Field [" + convertedValue + "] isn't an enum value"), ex);
                }
            }
        }
        return convertedValue;
    }

    @Nullable
    private PropertyEditor findDefaultEditor(@Nullable Class<?> requiredType) {
        PropertyEditor editor = null;
        if (requiredType != null && (editor = this.propertyEditorRegistry.getDefaultEditor(requiredType)) == null && String.class != requiredType) {
            editor = BeanUtils.findEditorByConvention(requiredType);
        }
        return editor;
    }

    @Nullable
    private Object doConvertValue(@Nullable Object oldValue, @Nullable Object newValue, @Nullable Class<?> requiredType, @Nullable PropertyEditor editor) {
        Object convertedValue;
        block10: {
            convertedValue = newValue;
            if (editor != null && !(convertedValue instanceof String)) {
                try {
                    editor.setValue(convertedValue);
                    Object newConvertedValue = editor.getValue();
                    if (newConvertedValue != convertedValue) {
                        convertedValue = newConvertedValue;
                        editor = null;
                    }
                }
                catch (Exception ex) {
                    if (!logger.isDebugEnabled()) break block10;
                    logger.debug((Object)("PropertyEditor [" + editor.getClass().getName() + "] does not support setValue call"), (Throwable)ex);
                }
            }
        }
        Object returnValue = convertedValue;
        if (requiredType != null && !requiredType.isArray() && convertedValue instanceof String[]) {
            if (logger.isTraceEnabled()) {
                logger.trace((Object)("Converting String array to comma-delimited String [" + convertedValue + "]"));
            }
            convertedValue = StringUtils.arrayToCommaDelimitedString((Object[])((String[])convertedValue));
        }
        if (convertedValue instanceof String) {
            if (editor != null) {
                if (logger.isTraceEnabled()) {
                    logger.trace((Object)("Converting String to [" + requiredType + "] using property editor [" + editor + "]"));
                }
                String newTextValue = (String)convertedValue;
                return this.doConvertTextValue(oldValue, newTextValue, editor);
            }
            if (String.class == requiredType) {
                returnValue = convertedValue;
            }
        }
        return returnValue;
    }

    private Object doConvertTextValue(@Nullable Object oldValue, String newTextValue, PropertyEditor editor) {
        block2: {
            try {
                editor.setValue(oldValue);
            }
            catch (Exception ex) {
                if (!logger.isDebugEnabled()) break block2;
                logger.debug((Object)("PropertyEditor [" + editor.getClass().getName() + "] does not support setValue call"), (Throwable)ex);
            }
        }
        editor.setAsText(newTextValue);
        return editor.getValue();
    }

    private Object convertToTypedArray(Object input, @Nullable String propertyName, Class<?> componentType) {
        if (input instanceof Collection) {
            Collection coll = (Collection)input;
            Object result = Array.newInstance(componentType, coll.size());
            int i = 0;
            Iterator it = coll.iterator();
            while (it.hasNext()) {
                Object value = this.convertIfNecessary(this.buildIndexedPropertyName(propertyName, i), null, it.next(), componentType);
                Array.set(result, i, value);
                ++i;
            }
            return result;
        }
        if (input.getClass().isArray()) {
            if (componentType.equals(input.getClass().getComponentType()) && !this.propertyEditorRegistry.hasCustomEditorForElement(componentType, propertyName)) {
                return input;
            }
            int arrayLength = Array.getLength(input);
            Object result = Array.newInstance(componentType, arrayLength);
            for (int i = 0; i < arrayLength; ++i) {
                Object value = this.convertIfNecessary(this.buildIndexedPropertyName(propertyName, i), null, Array.get(input, i), componentType);
                Array.set(result, i, value);
            }
            return result;
        }
        Object result = Array.newInstance(componentType, 1);
        Object value = this.convertIfNecessary(this.buildIndexedPropertyName(propertyName, 0), null, input, componentType);
        Array.set(result, 0, value);
        return result;
    }

    private Collection<?> convertToTypedCollection(Collection<?> original, @Nullable String propertyName, Class<?> requiredType, @Nullable TypeDescriptor typeDescriptor) {
        Collection convertedCopy;
        Iterator<?> it;
        TypeDescriptor elementType;
        if (!Collection.class.isAssignableFrom(requiredType)) {
            return original;
        }
        boolean approximable = CollectionFactory.isApproximableCollectionType(requiredType);
        if (!approximable && !this.canCreateCopy(requiredType)) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Custom Collection type [" + original.getClass().getName() + "] does not allow for creating a copy - injecting original Collection as-is"));
            }
            return original;
        }
        boolean originalAllowed = requiredType.isInstance(original);
        TypeDescriptor typeDescriptor2 = elementType = typeDescriptor != null ? typeDescriptor.getElementTypeDescriptor() : null;
        if (elementType == null && originalAllowed && !this.propertyEditorRegistry.hasCustomEditorForElement(null, propertyName)) {
            return original;
        }
        try {
            it = original.iterator();
        }
        catch (Throwable ex) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Cannot access Collection of type [" + original.getClass().getName() + "] - injecting original Collection as-is: " + ex));
            }
            return original;
        }
        try {
            convertedCopy = approximable ? CollectionFactory.createApproximateCollection(original, (int)original.size()) : (Collection)ReflectionUtils.accessibleConstructor(requiredType, (Class[])new Class[0]).newInstance(new Object[0]);
        }
        catch (Throwable ex) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Cannot create copy of Collection type [" + original.getClass().getName() + "] - injecting original Collection as-is: " + ex));
            }
            return original;
        }
        int i = 0;
        while (it.hasNext()) {
            Object element = it.next();
            String indexedPropertyName = this.buildIndexedPropertyName(propertyName, i);
            Object convertedElement = this.convertIfNecessary(indexedPropertyName, null, element, elementType != null ? elementType.getType() : null, elementType);
            try {
                convertedCopy.add(convertedElement);
            }
            catch (Throwable ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug((Object)("Collection type [" + original.getClass().getName() + "] seems to be read-only - injecting original Collection as-is: " + ex));
                }
                return original;
            }
            originalAllowed = originalAllowed && element == convertedElement;
            ++i;
        }
        return originalAllowed ? original : convertedCopy;
    }

    private Map<?, ?> convertToTypedMap(Map<?, ?> original, @Nullable String propertyName, Class<?> requiredType, @Nullable TypeDescriptor typeDescriptor) {
        Map convertedCopy;
        Iterator<Map.Entry<?, ?>> it;
        TypeDescriptor valueType;
        if (!Map.class.isAssignableFrom(requiredType)) {
            return original;
        }
        boolean approximable = CollectionFactory.isApproximableMapType(requiredType);
        if (!approximable && !this.canCreateCopy(requiredType)) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Custom Map type [" + original.getClass().getName() + "] does not allow for creating a copy - injecting original Map as-is"));
            }
            return original;
        }
        boolean originalAllowed = requiredType.isInstance(original);
        TypeDescriptor keyType = typeDescriptor != null ? typeDescriptor.getMapKeyTypeDescriptor() : null;
        TypeDescriptor typeDescriptor2 = valueType = typeDescriptor != null ? typeDescriptor.getMapValueTypeDescriptor() : null;
        if (keyType == null && valueType == null && originalAllowed && !this.propertyEditorRegistry.hasCustomEditorForElement(null, propertyName)) {
            return original;
        }
        try {
            it = original.entrySet().iterator();
        }
        catch (Throwable ex) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Cannot access Map of type [" + original.getClass().getName() + "] - injecting original Map as-is: " + ex));
            }
            return original;
        }
        try {
            convertedCopy = approximable ? CollectionFactory.createApproximateMap(original, (int)original.size()) : (Map)ReflectionUtils.accessibleConstructor(requiredType, (Class[])new Class[0]).newInstance(new Object[0]);
        }
        catch (Throwable ex) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Cannot create copy of Map type [" + original.getClass().getName() + "] - injecting original Map as-is: " + ex));
            }
            return original;
        }
        while (it.hasNext()) {
            Map.Entry<?, ?> entry = it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            String keyedPropertyName = this.buildKeyedPropertyName(propertyName, key);
            Object convertedKey = this.convertIfNecessary(keyedPropertyName, null, key, keyType != null ? keyType.getType() : null, keyType);
            Object convertedValue = this.convertIfNecessary(keyedPropertyName, null, value, valueType != null ? valueType.getType() : null, valueType);
            try {
                convertedCopy.put(convertedKey, convertedValue);
            }
            catch (Throwable ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug((Object)("Map type [" + original.getClass().getName() + "] seems to be read-only - injecting original Map as-is: " + ex));
                }
                return original;
            }
            originalAllowed = originalAllowed && key == convertedKey && value == convertedValue;
        }
        return originalAllowed ? original : convertedCopy;
    }

    @Nullable
    private String buildIndexedPropertyName(@Nullable String propertyName, int index) {
        return propertyName != null ? propertyName + "[" + index + "]" : null;
    }

    @Nullable
    private String buildKeyedPropertyName(@Nullable String propertyName, Object key) {
        return propertyName != null ? propertyName + "[" + key + "]" : null;
    }

    private boolean canCreateCopy(Class<?> requiredType) {
        return !requiredType.isInterface() && !Modifier.isAbstract(requiredType.getModifiers()) && Modifier.isPublic(requiredType.getModifiers()) && ClassUtils.hasConstructor(requiredType, (Class[])new Class[0]);
    }
}

