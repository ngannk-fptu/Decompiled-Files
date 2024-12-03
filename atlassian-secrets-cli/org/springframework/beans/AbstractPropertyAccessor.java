/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.beans.PropertyAccessException;
import org.springframework.beans.PropertyBatchUpdateException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.TypeConverterSupport;
import org.springframework.lang.Nullable;

public abstract class AbstractPropertyAccessor
extends TypeConverterSupport
implements ConfigurablePropertyAccessor {
    private boolean extractOldValueForEditor = false;
    private boolean autoGrowNestedPaths = false;
    boolean suppressNotWritablePropertyException = false;

    @Override
    public void setExtractOldValueForEditor(boolean extractOldValueForEditor) {
        this.extractOldValueForEditor = extractOldValueForEditor;
    }

    @Override
    public boolean isExtractOldValueForEditor() {
        return this.extractOldValueForEditor;
    }

    @Override
    public void setAutoGrowNestedPaths(boolean autoGrowNestedPaths) {
        this.autoGrowNestedPaths = autoGrowNestedPaths;
    }

    @Override
    public boolean isAutoGrowNestedPaths() {
        return this.autoGrowNestedPaths;
    }

    @Override
    public void setPropertyValue(PropertyValue pv) throws BeansException {
        this.setPropertyValue(pv.getName(), pv.getValue());
    }

    @Override
    public void setPropertyValues(Map<?, ?> map) throws BeansException {
        this.setPropertyValues(new MutablePropertyValues(map));
    }

    @Override
    public void setPropertyValues(PropertyValues pvs) throws BeansException {
        this.setPropertyValues(pvs, false, false);
    }

    @Override
    public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown) throws BeansException {
        this.setPropertyValues(pvs, ignoreUnknown, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown, boolean ignoreInvalid) throws BeansException {
        List<PropertyValue> propertyValues;
        ArrayList<PropertyAccessException> propertyAccessExceptions = null;
        List<PropertyValue> list = propertyValues = pvs instanceof MutablePropertyValues ? ((MutablePropertyValues)pvs).getPropertyValueList() : Arrays.asList(pvs.getPropertyValues());
        if (ignoreUnknown) {
            this.suppressNotWritablePropertyException = true;
        }
        try {
            for (PropertyValue pv : propertyValues) {
                try {
                    this.setPropertyValue(pv);
                }
                catch (NotWritablePropertyException ex) {
                    if (ignoreUnknown) continue;
                    throw ex;
                }
                catch (NullValueInNestedPathException ex) {
                    if (ignoreInvalid) continue;
                    throw ex;
                }
                catch (PropertyAccessException ex) {
                    if (propertyAccessExceptions == null) {
                        propertyAccessExceptions = new ArrayList<PropertyAccessException>();
                    }
                    propertyAccessExceptions.add(ex);
                }
            }
        }
        finally {
            if (ignoreUnknown) {
                this.suppressNotWritablePropertyException = false;
            }
        }
        if (propertyAccessExceptions != null) {
            PropertyAccessException[] paeArray = propertyAccessExceptions.toArray(new PropertyAccessException[0]);
            throw new PropertyBatchUpdateException(paeArray);
        }
    }

    @Override
    @Nullable
    public Class<?> getPropertyType(String propertyPath) {
        return null;
    }

    @Override
    @Nullable
    public abstract Object getPropertyValue(String var1) throws BeansException;

    @Override
    public abstract void setPropertyValue(String var1, @Nullable Object var2) throws BeansException;
}

