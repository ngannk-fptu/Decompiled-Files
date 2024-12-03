/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.AbstractPropertyAccessor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.MethodInvocationException;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.TypeConverterDelegate;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.CollectionFactory;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public abstract class AbstractNestablePropertyAccessor
extends AbstractPropertyAccessor {
    private static final Log logger = LogFactory.getLog(AbstractNestablePropertyAccessor.class);
    private int autoGrowCollectionLimit = Integer.MAX_VALUE;
    @Nullable
    Object wrappedObject;
    private String nestedPath = "";
    @Nullable
    Object rootObject;
    @Nullable
    private Map<String, AbstractNestablePropertyAccessor> nestedPropertyAccessors;

    protected AbstractNestablePropertyAccessor() {
        this(true);
    }

    protected AbstractNestablePropertyAccessor(boolean registerDefaultEditors) {
        if (registerDefaultEditors) {
            this.registerDefaultEditors();
        }
        this.typeConverterDelegate = new TypeConverterDelegate(this);
    }

    protected AbstractNestablePropertyAccessor(Object object) {
        this.registerDefaultEditors();
        this.setWrappedInstance(object);
    }

    protected AbstractNestablePropertyAccessor(Class<?> clazz) {
        this.registerDefaultEditors();
        this.setWrappedInstance(BeanUtils.instantiateClass(clazz));
    }

    protected AbstractNestablePropertyAccessor(Object object, String nestedPath, Object rootObject) {
        this.registerDefaultEditors();
        this.setWrappedInstance(object, nestedPath, rootObject);
    }

    protected AbstractNestablePropertyAccessor(Object object, String nestedPath, AbstractNestablePropertyAccessor parent) {
        this.setWrappedInstance(object, nestedPath, parent.getWrappedInstance());
        this.setExtractOldValueForEditor(parent.isExtractOldValueForEditor());
        this.setAutoGrowNestedPaths(parent.isAutoGrowNestedPaths());
        this.setAutoGrowCollectionLimit(parent.getAutoGrowCollectionLimit());
        this.setConversionService(parent.getConversionService());
    }

    public void setAutoGrowCollectionLimit(int autoGrowCollectionLimit) {
        this.autoGrowCollectionLimit = autoGrowCollectionLimit;
    }

    public int getAutoGrowCollectionLimit() {
        return this.autoGrowCollectionLimit;
    }

    public void setWrappedInstance(Object object) {
        this.setWrappedInstance(object, "", null);
    }

    public void setWrappedInstance(Object object, @Nullable String nestedPath, @Nullable Object rootObject) {
        this.wrappedObject = ObjectUtils.unwrapOptional(object);
        Assert.notNull(this.wrappedObject, "Target object must not be null");
        this.nestedPath = nestedPath != null ? nestedPath : "";
        this.rootObject = !this.nestedPath.isEmpty() ? rootObject : this.wrappedObject;
        this.nestedPropertyAccessors = null;
        this.typeConverterDelegate = new TypeConverterDelegate(this, this.wrappedObject);
    }

    public final Object getWrappedInstance() {
        Assert.state(this.wrappedObject != null, "No wrapped object");
        return this.wrappedObject;
    }

    public final Class<?> getWrappedClass() {
        return this.getWrappedInstance().getClass();
    }

    public final String getNestedPath() {
        return this.nestedPath;
    }

    public final Object getRootInstance() {
        Assert.state(this.rootObject != null, "No root object");
        return this.rootObject;
    }

    public final Class<?> getRootClass() {
        return this.getRootInstance().getClass();
    }

    @Override
    public void setPropertyValue(String propertyName, @Nullable Object value) throws BeansException {
        AbstractNestablePropertyAccessor nestedPa;
        try {
            nestedPa = this.getPropertyAccessorForPropertyPath(propertyName);
        }
        catch (NotReadablePropertyException ex) {
            throw new NotWritablePropertyException(this.getRootClass(), this.nestedPath + propertyName, "Nested property in path '" + propertyName + "' does not exist", ex);
        }
        PropertyTokenHolder tokens = this.getPropertyNameTokens(this.getFinalPath(nestedPa, propertyName));
        nestedPa.setPropertyValue(tokens, new PropertyValue(propertyName, value));
    }

    @Override
    public void setPropertyValue(PropertyValue pv) throws BeansException {
        PropertyTokenHolder tokens = (PropertyTokenHolder)pv.resolvedTokens;
        if (tokens == null) {
            AbstractNestablePropertyAccessor nestedPa;
            String propertyName = pv.getName();
            try {
                nestedPa = this.getPropertyAccessorForPropertyPath(propertyName);
            }
            catch (NotReadablePropertyException ex) {
                throw new NotWritablePropertyException(this.getRootClass(), this.nestedPath + propertyName, "Nested property in path '" + propertyName + "' does not exist", ex);
            }
            tokens = this.getPropertyNameTokens(this.getFinalPath(nestedPa, propertyName));
            if (nestedPa == this) {
                pv.getOriginalPropertyValue().resolvedTokens = tokens;
            }
            nestedPa.setPropertyValue(tokens, pv);
        } else {
            this.setPropertyValue(tokens, pv);
        }
    }

    protected void setPropertyValue(PropertyTokenHolder tokens, PropertyValue pv) throws BeansException {
        if (tokens.keys != null) {
            this.processKeyedProperty(tokens, pv);
        } else {
            this.processLocalProperty(tokens, pv);
        }
    }

    private void processKeyedProperty(PropertyTokenHolder tokens, PropertyValue pv) {
        Object propValue = this.getPropertyHoldingValue(tokens);
        PropertyHandler ph = this.getLocalPropertyHandler(tokens.actualName);
        if (ph == null) {
            throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + tokens.actualName, "No property handler found");
        }
        Assert.state(tokens.keys != null, "No token keys");
        String lastKey = tokens.keys[tokens.keys.length - 1];
        if (propValue.getClass().isArray()) {
            Class<?> requiredType = propValue.getClass().getComponentType();
            int arrayIndex = Integer.parseInt(lastKey);
            Object oldValue = null;
            try {
                if (this.isExtractOldValueForEditor() && arrayIndex < Array.getLength(propValue)) {
                    oldValue = Array.get(propValue, arrayIndex);
                }
                Object convertedValue = this.convertIfNecessary(tokens.canonicalName, oldValue, pv.getValue(), requiredType, ph.nested(tokens.keys.length));
                int length = Array.getLength(propValue);
                if (arrayIndex >= length && arrayIndex < this.autoGrowCollectionLimit) {
                    Class<?> componentType = propValue.getClass().getComponentType();
                    Object newArray = Array.newInstance(componentType, arrayIndex + 1);
                    System.arraycopy(propValue, 0, newArray, 0, length);
                    int lastKeyIndex = tokens.canonicalName.lastIndexOf(91);
                    String propName = tokens.canonicalName.substring(0, lastKeyIndex);
                    this.setPropertyValue(propName, newArray);
                    propValue = this.getPropertyValue(propName);
                }
                Array.set(propValue, arrayIndex, convertedValue);
            }
            catch (IndexOutOfBoundsException ex) {
                throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + tokens.canonicalName, "Invalid array index in property path '" + tokens.canonicalName + "'", ex);
            }
        } else if (propValue instanceof List) {
            Class<?> requiredType = ph.getCollectionType(tokens.keys.length);
            List list = (List)propValue;
            int index = Integer.parseInt(lastKey);
            Object oldValue = null;
            if (this.isExtractOldValueForEditor() && index < list.size()) {
                oldValue = list.get(index);
            }
            Object convertedValue = this.convertIfNecessary(tokens.canonicalName, oldValue, pv.getValue(), requiredType, ph.nested(tokens.keys.length));
            int size = list.size();
            if (index >= size && index < this.autoGrowCollectionLimit) {
                for (int i = size; i < index; ++i) {
                    try {
                        list.add(null);
                        continue;
                    }
                    catch (NullPointerException ex) {
                        throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + tokens.canonicalName, "Cannot set element with index " + index + " in List of size " + size + ", accessed using property path '" + tokens.canonicalName + "': List does not support filling up gaps with null elements");
                    }
                }
                list.add(convertedValue);
            } else {
                try {
                    list.set(index, convertedValue);
                }
                catch (IndexOutOfBoundsException ex) {
                    throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + tokens.canonicalName, "Invalid list index in property path '" + tokens.canonicalName + "'", ex);
                }
            }
        } else if (propValue instanceof Map) {
            Class<?> mapKeyType = ph.getMapKeyType(tokens.keys.length);
            Class<?> mapValueType = ph.getMapValueType(tokens.keys.length);
            Map map = (Map)propValue;
            TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(mapKeyType);
            Object convertedMapKey = this.convertIfNecessary(null, null, lastKey, mapKeyType, typeDescriptor);
            Object oldValue = null;
            if (this.isExtractOldValueForEditor()) {
                oldValue = map.get(convertedMapKey);
            }
            Object convertedMapValue = this.convertIfNecessary(tokens.canonicalName, oldValue, pv.getValue(), mapValueType, ph.nested(tokens.keys.length));
            map.put(convertedMapKey, convertedMapValue);
        } else {
            throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + tokens.canonicalName, "Property referenced in indexed property path '" + tokens.canonicalName + "' is neither an array nor a List nor a Map; returned value was [" + propValue + "]");
        }
    }

    private Object getPropertyHoldingValue(PropertyTokenHolder tokens) {
        Object propValue;
        Assert.state(tokens.keys != null, "No token keys");
        PropertyTokenHolder getterTokens = new PropertyTokenHolder(tokens.actualName);
        getterTokens.canonicalName = tokens.canonicalName;
        getterTokens.keys = new String[tokens.keys.length - 1];
        System.arraycopy(tokens.keys, 0, getterTokens.keys, 0, tokens.keys.length - 1);
        try {
            propValue = this.getPropertyValue(getterTokens);
        }
        catch (NotReadablePropertyException ex) {
            throw new NotWritablePropertyException(this.getRootClass(), this.nestedPath + tokens.canonicalName, "Cannot access indexed value in property referenced in indexed property path '" + tokens.canonicalName + "'", ex);
        }
        if (propValue == null) {
            if (this.isAutoGrowNestedPaths()) {
                int lastKeyIndex = tokens.canonicalName.lastIndexOf(91);
                getterTokens.canonicalName = tokens.canonicalName.substring(0, lastKeyIndex);
                propValue = this.setDefaultValue(getterTokens);
            } else {
                throw new NullValueInNestedPathException(this.getRootClass(), this.nestedPath + tokens.canonicalName, "Cannot access indexed value in property referenced in indexed property path '" + tokens.canonicalName + "': returned null");
            }
        }
        return propValue;
    }

    private void processLocalProperty(PropertyTokenHolder tokens, PropertyValue pv) {
        PropertyHandler ph = this.getLocalPropertyHandler(tokens.actualName);
        if (ph == null || !ph.isWritable()) {
            if (pv.isOptional()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Ignoring optional value for property '" + tokens.actualName + "' - property not found on bean class [" + this.getRootClass().getName() + "]");
                }
                return;
            }
            if (this.suppressNotWritablePropertyException) {
                return;
            }
            throw this.createNotWritablePropertyException(tokens.canonicalName);
        }
        Object oldValue = null;
        try {
            Object originalValue;
            Object valueToApply = originalValue = pv.getValue();
            if (!Boolean.FALSE.equals(pv.conversionNecessary)) {
                if (pv.isConverted()) {
                    valueToApply = pv.getConvertedValue();
                } else {
                    block17: {
                        if (this.isExtractOldValueForEditor() && ph.isReadable()) {
                            try {
                                oldValue = ph.getValue();
                            }
                            catch (Exception ex) {
                                if (ex instanceof PrivilegedActionException) {
                                    ex = ((PrivilegedActionException)ex).getException();
                                }
                                if (!logger.isDebugEnabled()) break block17;
                                logger.debug("Could not read previous value of property '" + this.nestedPath + tokens.canonicalName + "'", ex);
                            }
                        }
                    }
                    valueToApply = this.convertForProperty(tokens.canonicalName, oldValue, originalValue, ph.toTypeDescriptor());
                }
                pv.getOriginalPropertyValue().conversionNecessary = valueToApply != originalValue;
            }
            ph.setValue(valueToApply);
        }
        catch (TypeMismatchException ex) {
            throw ex;
        }
        catch (InvocationTargetException ex) {
            PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(this.getRootInstance(), this.nestedPath + tokens.canonicalName, oldValue, pv.getValue());
            if (ex.getTargetException() instanceof ClassCastException) {
                throw new TypeMismatchException(propertyChangeEvent, ph.getPropertyType(), ex.getTargetException());
            }
            Throwable cause = ex.getTargetException();
            if (cause instanceof UndeclaredThrowableException) {
                cause = cause.getCause();
            }
            throw new MethodInvocationException(propertyChangeEvent, cause);
        }
        catch (Exception ex) {
            PropertyChangeEvent pce = new PropertyChangeEvent(this.getRootInstance(), this.nestedPath + tokens.canonicalName, oldValue, pv.getValue());
            throw new MethodInvocationException(pce, (Throwable)ex);
        }
    }

    @Override
    @Nullable
    public Class<?> getPropertyType(String propertyName) throws BeansException {
        try {
            PropertyHandler ph = this.getPropertyHandler(propertyName);
            if (ph != null) {
                return ph.getPropertyType();
            }
            Object value = this.getPropertyValue(propertyName);
            if (value != null) {
                return value.getClass();
            }
            Class<?> editorType = this.guessPropertyTypeFromEditors(propertyName);
            if (editorType != null) {
                return editorType;
            }
        }
        catch (InvalidPropertyException invalidPropertyException) {
            // empty catch block
        }
        return null;
    }

    @Override
    @Nullable
    public TypeDescriptor getPropertyTypeDescriptor(String propertyName) throws BeansException {
        try {
            AbstractNestablePropertyAccessor nestedPa = this.getPropertyAccessorForPropertyPath(propertyName);
            String finalPath = this.getFinalPath(nestedPa, propertyName);
            PropertyTokenHolder tokens = this.getPropertyNameTokens(finalPath);
            PropertyHandler ph = nestedPa.getLocalPropertyHandler(tokens.actualName);
            if (ph != null) {
                if (tokens.keys != null) {
                    if (ph.isReadable() || ph.isWritable()) {
                        return ph.nested(tokens.keys.length);
                    }
                } else if (ph.isReadable() || ph.isWritable()) {
                    return ph.toTypeDescriptor();
                }
            }
        }
        catch (InvalidPropertyException invalidPropertyException) {
            // empty catch block
        }
        return null;
    }

    @Override
    public boolean isReadableProperty(String propertyName) {
        try {
            PropertyHandler ph = this.getPropertyHandler(propertyName);
            if (ph != null) {
                return ph.isReadable();
            }
            this.getPropertyValue(propertyName);
            return true;
        }
        catch (InvalidPropertyException invalidPropertyException) {
            return false;
        }
    }

    @Override
    public boolean isWritableProperty(String propertyName) {
        try {
            PropertyHandler ph = this.getPropertyHandler(propertyName);
            if (ph != null) {
                return ph.isWritable();
            }
            this.getPropertyValue(propertyName);
            return true;
        }
        catch (InvalidPropertyException invalidPropertyException) {
            return false;
        }
    }

    @Nullable
    private Object convertIfNecessary(@Nullable String propertyName, @Nullable Object oldValue, @Nullable Object newValue, @Nullable Class<?> requiredType, @Nullable TypeDescriptor td) throws TypeMismatchException {
        Assert.state(this.typeConverterDelegate != null, "No TypeConverterDelegate");
        try {
            return this.typeConverterDelegate.convertIfNecessary(propertyName, oldValue, newValue, requiredType, td);
        }
        catch (IllegalStateException | ConverterNotFoundException ex) {
            PropertyChangeEvent pce = new PropertyChangeEvent(this.getRootInstance(), this.nestedPath + propertyName, oldValue, newValue);
            throw new ConversionNotSupportedException(pce, requiredType, (Throwable)ex);
        }
        catch (IllegalArgumentException | ConversionException ex) {
            PropertyChangeEvent pce = new PropertyChangeEvent(this.getRootInstance(), this.nestedPath + propertyName, oldValue, newValue);
            throw new TypeMismatchException(pce, requiredType, (Throwable)ex);
        }
    }

    @Nullable
    protected Object convertForProperty(String propertyName, @Nullable Object oldValue, @Nullable Object newValue, TypeDescriptor td) throws TypeMismatchException {
        return this.convertIfNecessary(propertyName, oldValue, newValue, td.getType(), td);
    }

    @Override
    @Nullable
    public Object getPropertyValue(String propertyName) throws BeansException {
        AbstractNestablePropertyAccessor nestedPa = this.getPropertyAccessorForPropertyPath(propertyName);
        PropertyTokenHolder tokens = this.getPropertyNameTokens(this.getFinalPath(nestedPa, propertyName));
        return nestedPa.getPropertyValue(tokens);
    }

    @Nullable
    protected Object getPropertyValue(PropertyTokenHolder tokens) throws BeansException {
        String propertyName = tokens.canonicalName;
        String actualName = tokens.actualName;
        PropertyHandler ph = this.getLocalPropertyHandler(actualName);
        if (ph == null || !ph.isReadable()) {
            throw new NotReadablePropertyException(this.getRootClass(), this.nestedPath + propertyName);
        }
        try {
            Object value = ph.getValue();
            if (tokens.keys != null) {
                if (value == null) {
                    if (this.isAutoGrowNestedPaths()) {
                        value = this.setDefaultValue(new PropertyTokenHolder(tokens.actualName));
                    } else {
                        throw new NullValueInNestedPathException(this.getRootClass(), this.nestedPath + propertyName, "Cannot access indexed value of property referenced in indexed property path '" + propertyName + "': returned null");
                    }
                }
                StringBuilder indexedPropertyName = new StringBuilder(tokens.actualName);
                for (int i = 0; i < tokens.keys.length; ++i) {
                    String key = tokens.keys[i];
                    if (value == null) {
                        throw new NullValueInNestedPathException(this.getRootClass(), this.nestedPath + propertyName, "Cannot access indexed value of property referenced in indexed property path '" + propertyName + "': returned null");
                    }
                    if (value.getClass().isArray()) {
                        int index = Integer.parseInt(key);
                        value = this.growArrayIfNecessary(value, index, indexedPropertyName.toString());
                        value = Array.get(value, index);
                    } else if (value instanceof List) {
                        int index = Integer.parseInt(key);
                        List list = (List)value;
                        this.growCollectionIfNecessary(list, index, indexedPropertyName.toString(), ph, i + 1);
                        value = list.get(index);
                    } else if (value instanceof Set) {
                        Set set = (Set)value;
                        int index = Integer.parseInt(key);
                        if (index < 0 || index >= set.size()) {
                            throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "Cannot get element with index " + index + " from Set of size " + set.size() + ", accessed using property path '" + propertyName + "'");
                        }
                        Iterator it = set.iterator();
                        int j = 0;
                        while (it.hasNext()) {
                            Object elem = it.next();
                            if (j == index) {
                                value = elem;
                                break;
                            }
                            ++j;
                        }
                    } else if (value instanceof Map) {
                        Map map = (Map)value;
                        Class<?> mapKeyType = ph.getResolvableType().getNested(i + 1).asMap().resolveGeneric(0);
                        TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(mapKeyType);
                        Object convertedMapKey = this.convertIfNecessary(null, null, key, mapKeyType, typeDescriptor);
                        value = map.get(convertedMapKey);
                    } else {
                        throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "Property referenced in indexed property path '" + propertyName + "' is neither an array nor a List nor a Set nor a Map; returned value was [" + value + "]");
                    }
                    indexedPropertyName.append("[").append(key).append("]");
                }
            }
            return value;
        }
        catch (IndexOutOfBoundsException ex) {
            throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "Index of out of bounds in property path '" + propertyName + "'", ex);
        }
        catch (NumberFormatException | TypeMismatchException ex) {
            throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "Invalid index in property path '" + propertyName + "'", ex);
        }
        catch (InvocationTargetException ex) {
            throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "Getter for property '" + actualName + "' threw exception", ex);
        }
        catch (Exception ex) {
            throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "Illegal attempt to get property '" + actualName + "' threw exception", ex);
        }
    }

    @Nullable
    protected PropertyHandler getPropertyHandler(String propertyName) throws BeansException {
        Assert.notNull((Object)propertyName, "Property name must not be null");
        AbstractNestablePropertyAccessor nestedPa = this.getPropertyAccessorForPropertyPath(propertyName);
        return nestedPa.getLocalPropertyHandler(this.getFinalPath(nestedPa, propertyName));
    }

    @Nullable
    protected abstract PropertyHandler getLocalPropertyHandler(String var1);

    protected abstract AbstractNestablePropertyAccessor newNestedPropertyAccessor(Object var1, String var2);

    protected abstract NotWritablePropertyException createNotWritablePropertyException(String var1);

    private Object growArrayIfNecessary(Object array, int index, String name) {
        if (!this.isAutoGrowNestedPaths()) {
            return array;
        }
        int length = Array.getLength(array);
        if (index >= length && index < this.autoGrowCollectionLimit) {
            Class<?> componentType = array.getClass().getComponentType();
            Object newArray = Array.newInstance(componentType, index + 1);
            System.arraycopy(array, 0, newArray, 0, length);
            for (int i = length; i < Array.getLength(newArray); ++i) {
                Array.set(newArray, i, this.newValue(componentType, null, name));
            }
            this.setPropertyValue(name, newArray);
            Object defaultValue = this.getPropertyValue(name);
            Assert.state(defaultValue != null, "Default value must not be null");
            return defaultValue;
        }
        return array;
    }

    private void growCollectionIfNecessary(Collection<Object> collection, int index, String name, PropertyHandler ph, int nestingLevel) {
        Class<?> elementType;
        if (!this.isAutoGrowNestedPaths()) {
            return;
        }
        int size = collection.size();
        if (index >= size && index < this.autoGrowCollectionLimit && (elementType = ph.getResolvableType().getNested(nestingLevel).asCollection().resolveGeneric(new int[0])) != null) {
            for (int i = collection.size(); i < index + 1; ++i) {
                collection.add(this.newValue(elementType, null, name));
            }
        }
    }

    protected String getFinalPath(AbstractNestablePropertyAccessor pa, String nestedPath) {
        if (pa == this) {
            return nestedPath;
        }
        return nestedPath.substring(PropertyAccessorUtils.getLastNestedPropertySeparatorIndex(nestedPath) + 1);
    }

    protected AbstractNestablePropertyAccessor getPropertyAccessorForPropertyPath(String propertyPath) {
        int pos = PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(propertyPath);
        if (pos > -1) {
            String nestedProperty = propertyPath.substring(0, pos);
            String nestedPath = propertyPath.substring(pos + 1);
            AbstractNestablePropertyAccessor nestedPa = this.getNestedPropertyAccessor(nestedProperty);
            return nestedPa.getPropertyAccessorForPropertyPath(nestedPath);
        }
        return this;
    }

    private AbstractNestablePropertyAccessor getNestedPropertyAccessor(String nestedProperty) {
        AbstractNestablePropertyAccessor nestedPa;
        if (this.nestedPropertyAccessors == null) {
            this.nestedPropertyAccessors = new HashMap<String, AbstractNestablePropertyAccessor>();
        }
        PropertyTokenHolder tokens = this.getPropertyNameTokens(nestedProperty);
        String canonicalName = tokens.canonicalName;
        Object value = this.getPropertyValue(tokens);
        if (value == null || value instanceof Optional && !((Optional)value).isPresent()) {
            if (this.isAutoGrowNestedPaths()) {
                value = this.setDefaultValue(tokens);
            } else {
                throw new NullValueInNestedPathException(this.getRootClass(), this.nestedPath + canonicalName);
            }
        }
        if ((nestedPa = this.nestedPropertyAccessors.get(canonicalName)) == null || nestedPa.getWrappedInstance() != ObjectUtils.unwrapOptional(value)) {
            if (logger.isTraceEnabled()) {
                logger.trace("Creating new nested " + this.getClass().getSimpleName() + " for property '" + canonicalName + "'");
            }
            nestedPa = this.newNestedPropertyAccessor(value, this.nestedPath + canonicalName + ".");
            this.copyDefaultEditorsTo(nestedPa);
            this.copyCustomEditorsTo(nestedPa, canonicalName);
            this.nestedPropertyAccessors.put(canonicalName, nestedPa);
        } else if (logger.isTraceEnabled()) {
            logger.trace("Using cached nested property accessor for property '" + canonicalName + "'");
        }
        return nestedPa;
    }

    private Object setDefaultValue(PropertyTokenHolder tokens) {
        PropertyValue pv = this.createDefaultPropertyValue(tokens);
        this.setPropertyValue(tokens, pv);
        Object defaultValue = this.getPropertyValue(tokens);
        Assert.state(defaultValue != null, "Default value must not be null");
        return defaultValue;
    }

    private PropertyValue createDefaultPropertyValue(PropertyTokenHolder tokens) {
        TypeDescriptor desc = this.getPropertyTypeDescriptor(tokens.canonicalName);
        if (desc == null) {
            throw new NullValueInNestedPathException(this.getRootClass(), this.nestedPath + tokens.canonicalName, "Could not determine property type for auto-growing a default value");
        }
        Object defaultValue = this.newValue(desc.getType(), desc, tokens.canonicalName);
        return new PropertyValue(tokens.canonicalName, defaultValue);
    }

    private Object newValue(Class<?> type, @Nullable TypeDescriptor desc, String name) {
        try {
            if (type.isArray()) {
                Class<?> componentType = type.getComponentType();
                if (componentType.isArray()) {
                    Object array = Array.newInstance(componentType, 1);
                    Array.set(array, 0, Array.newInstance(componentType.getComponentType(), 0));
                    return array;
                }
                return Array.newInstance(componentType, 0);
            }
            if (Collection.class.isAssignableFrom(type)) {
                TypeDescriptor elementDesc = desc != null ? desc.getElementTypeDescriptor() : null;
                return CollectionFactory.createCollection(type, elementDesc != null ? elementDesc.getType() : null, 16);
            }
            if (Map.class.isAssignableFrom(type)) {
                TypeDescriptor keyDesc = desc != null ? desc.getMapKeyTypeDescriptor() : null;
                return CollectionFactory.createMap(type, keyDesc != null ? keyDesc.getType() : null, 16);
            }
            Constructor<?> ctor = type.getDeclaredConstructor(new Class[0]);
            if (Modifier.isPrivate(ctor.getModifiers())) {
                throw new IllegalAccessException("Auto-growing not allowed with private constructor: " + ctor);
            }
            return BeanUtils.instantiateClass(ctor, new Object[0]);
        }
        catch (Throwable ex) {
            throw new NullValueInNestedPathException(this.getRootClass(), this.nestedPath + name, "Could not instantiate property type [" + type.getName() + "] to auto-grow nested property path", ex);
        }
    }

    private PropertyTokenHolder getPropertyNameTokens(String propertyName) {
        String actualName = null;
        ArrayList<String> keys = new ArrayList<String>(2);
        int searchIndex = 0;
        while (searchIndex != -1) {
            String key;
            int keyEnd;
            int keyStart = propertyName.indexOf("[", searchIndex);
            searchIndex = -1;
            if (keyStart == -1 || (keyEnd = this.getPropertyNameKeyEnd(propertyName, keyStart + "[".length())) == -1) continue;
            if (actualName == null) {
                actualName = propertyName.substring(0, keyStart);
            }
            if ((key = propertyName.substring(keyStart + "[".length(), keyEnd)).length() > 1 && key.startsWith("'") && key.endsWith("'") || key.startsWith("\"") && key.endsWith("\"")) {
                key = key.substring(1, key.length() - 1);
            }
            keys.add(key);
            searchIndex = keyEnd + "]".length();
        }
        PropertyTokenHolder tokens = new PropertyTokenHolder(actualName != null ? actualName : propertyName);
        if (!keys.isEmpty()) {
            tokens.canonicalName = tokens.canonicalName + "[" + StringUtils.collectionToDelimitedString(keys, "][") + "]";
            tokens.keys = StringUtils.toStringArray(keys);
        }
        return tokens;
    }

    private int getPropertyNameKeyEnd(String propertyName, int startIndex) {
        int unclosedPrefixes = 0;
        int length = propertyName.length();
        block4: for (int i = startIndex; i < length; ++i) {
            switch (propertyName.charAt(i)) {
                case '[': {
                    ++unclosedPrefixes;
                    continue block4;
                }
                case ']': {
                    if (unclosedPrefixes == 0) {
                        return i;
                    }
                    --unclosedPrefixes;
                }
            }
        }
        return -1;
    }

    public String toString() {
        String className = this.getClass().getName();
        if (this.wrappedObject == null) {
            return className + ": no wrapped object set";
        }
        return className + ": wrapping object [" + ObjectUtils.identityToString(this.wrappedObject) + ']';
    }

    protected static class PropertyTokenHolder {
        public String actualName;
        public String canonicalName;
        @Nullable
        public String[] keys;

        public PropertyTokenHolder(String name) {
            this.actualName = name;
            this.canonicalName = name;
        }
    }

    protected static abstract class PropertyHandler {
        private final Class<?> propertyType;
        private final boolean readable;
        private final boolean writable;

        public PropertyHandler(Class<?> propertyType, boolean readable, boolean writable) {
            this.propertyType = propertyType;
            this.readable = readable;
            this.writable = writable;
        }

        public Class<?> getPropertyType() {
            return this.propertyType;
        }

        public boolean isReadable() {
            return this.readable;
        }

        public boolean isWritable() {
            return this.writable;
        }

        public abstract TypeDescriptor toTypeDescriptor();

        public abstract ResolvableType getResolvableType();

        @Nullable
        public Class<?> getMapKeyType(int nestingLevel) {
            return this.getResolvableType().getNested(nestingLevel).asMap().resolveGeneric(0);
        }

        @Nullable
        public Class<?> getMapValueType(int nestingLevel) {
            return this.getResolvableType().getNested(nestingLevel).asMap().resolveGeneric(1);
        }

        @Nullable
        public Class<?> getCollectionType(int nestingLevel) {
            return this.getResolvableType().getNested(nestingLevel).asCollection().resolveGeneric(new int[0]);
        }

        @Nullable
        public abstract TypeDescriptor nested(int var1);

        @Nullable
        public abstract Object getValue() throws Exception;

        public abstract void setValue(@Nullable Object var1) throws Exception;
    }
}

