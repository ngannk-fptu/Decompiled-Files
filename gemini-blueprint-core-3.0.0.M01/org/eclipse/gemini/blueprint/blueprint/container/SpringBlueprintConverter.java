/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.PropertyEditorRegistry
 *  org.springframework.beans.SimpleTypeConverter
 *  org.springframework.beans.TypeConverter
 *  org.springframework.beans.TypeMismatchException
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 */
package org.eclipse.gemini.blueprint.blueprint.container;

import org.osgi.service.blueprint.container.Converter;
import org.osgi.service.blueprint.container.ReifiedType;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

public class SpringBlueprintConverter
implements Converter {
    private final ConfigurableBeanFactory beanFactory;
    private volatile TypeConverter typeConverter;

    public SpringBlueprintConverter(ConfigurableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public boolean canConvert(Object source, ReifiedType targetType) {
        Class required = targetType.getRawClass();
        try {
            this.getConverter().convertIfNecessary(source, required);
            return true;
        }
        catch (TypeMismatchException ex) {
            return false;
        }
    }

    @Override
    public Object convert(Object source, ReifiedType targetType) throws Exception {
        Class target = targetType != null ? targetType.getRawClass() : null;
        return this.getConverter().convertIfNecessary(source, target);
    }

    private TypeConverter getConverter() {
        if (this.typeConverter == null) {
            SimpleTypeConverter simpleConverter = new SimpleTypeConverter();
            this.beanFactory.copyRegisteredEditorsTo((PropertyEditorRegistry)simpleConverter);
            simpleConverter.setConversionService(this.beanFactory.getConversionService());
            this.typeConverter = simpleConverter;
        }
        return this.typeConverter;
    }
}

