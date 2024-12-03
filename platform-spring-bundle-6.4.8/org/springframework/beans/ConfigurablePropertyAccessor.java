/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans;

import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.TypeConverter;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;

public interface ConfigurablePropertyAccessor
extends PropertyAccessor,
PropertyEditorRegistry,
TypeConverter {
    public void setConversionService(@Nullable ConversionService var1);

    @Nullable
    public ConversionService getConversionService();

    public void setExtractOldValueForEditor(boolean var1);

    public boolean isExtractOldValueForEditor();

    public void setAutoGrowNestedPaths(boolean var1);

    public boolean isAutoGrowNestedPaths();
}

