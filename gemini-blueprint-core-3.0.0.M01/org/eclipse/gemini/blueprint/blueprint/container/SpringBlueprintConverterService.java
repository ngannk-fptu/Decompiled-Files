/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.PropertyEditorRegistry
 *  org.springframework.beans.SimpleTypeConverter
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.core.convert.ConversionException
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.TypeDescriptor
 */
package org.eclipse.gemini.blueprint.blueprint.container;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.gemini.blueprint.blueprint.container.TypeFactory;
import org.eclipse.gemini.blueprint.blueprint.container.support.BlueprintEditorRegistrar;
import org.eclipse.gemini.blueprint.context.support.internal.security.SecurityUtils;
import org.osgi.service.blueprint.container.Converter;
import org.osgi.service.blueprint.container.ReifiedType;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

public class SpringBlueprintConverterService
implements ConversionService {
    private final ConversionService delegate;
    private final List<Converter> converters = new ArrayList<Converter>();
    private final SimpleTypeConverter typeConverter;
    private final ConfigurableBeanFactory cbf;
    private volatile boolean converterInitialized = false;

    public SpringBlueprintConverterService(ConversionService delegate, ConfigurableBeanFactory cbf) {
        this.delegate = delegate;
        this.cbf = cbf;
        this.typeConverter = new SimpleTypeConverter();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void add(Converter blueprintConverter) {
        List<Converter> list = this.converters;
        synchronized (list) {
            this.converters.add(blueprintConverter);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void add(Collection<Converter> blueprintConverters) {
        List<Converter> list = this.converters;
        synchronized (list) {
            this.converters.addAll(blueprintConverters);
        }
    }

    public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
        return true;
    }

    public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return true;
    }

    public <T> T convert(Object source, Class<T> targetType) {
        return (T)this.convert(source, TypeDescriptor.forObject((Object)source), TypeDescriptor.valueOf(targetType));
    }

    public Object convert(final Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (targetType == null) {
            return source;
        }
        final ReifiedType type = TypeFactory.getType(targetType);
        boolean hasSecurity = System.getSecurityManager() != null;
        AccessControlContext acc = hasSecurity ? SecurityUtils.getAccFrom((BeanFactory)this.cbf) : null;
        Object result = hasSecurity ? AccessController.doPrivileged(new PrivilegedAction<Object>(){

            @Override
            public Object run() {
                return SpringBlueprintConverterService.this.doConvert(source, type);
            }
        }, acc) : this.doConvert(source, type);
        if (result != null) {
            return result;
        }
        if (!(targetType.isCollection() || targetType.isArray() || targetType.isMap() || type.size() <= 0)) {
            for (int i = 0; i < type.size(); ++i) {
                ReifiedType arg = type.getActualTypeArgument(i);
                if (Object.class.equals((Object)arg.getRawClass())) continue;
                throw new BlueprintConverterException("No conversion found for generic argument(s) for reified type " + arg.getRawClass() + "source type " + sourceType + "| targetType =" + targetType.getType(), null);
            }
        }
        if (this.delegate != null) {
            this.delegate.convert(source, sourceType, targetType);
        }
        this.lazyInitConverter();
        return this.typeConverter.convertIfNecessary(source, targetType.getType());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void lazyInitConverter() {
        if (!this.converterInitialized) {
            SimpleTypeConverter simpleTypeConverter = this.typeConverter;
            synchronized (simpleTypeConverter) {
                if (!this.converterInitialized) {
                    this.converterInitialized = true;
                    if (this.cbf != null) {
                        this.cbf.copyRegisteredEditorsTo((PropertyEditorRegistry)this.typeConverter);
                        new BlueprintEditorRegistrar().registerCustomEditors((PropertyEditorRegistry)this.typeConverter);
                    }
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Object doConvert(Object source, ReifiedType type) {
        List<Converter> list = this.converters;
        synchronized (list) {
            for (Converter converter : this.converters) {
                try {
                    if (!converter.canConvert(source, type)) continue;
                    return converter.convert(source, type);
                }
                catch (Exception ex) {
                    throw new BlueprintConverterException("Conversion between source " + source + " and reified type " + type + " failed", ex);
                }
            }
        }
        return null;
    }

    private static final class BlueprintConverterException
    extends ConversionException {
        public BlueprintConverterException(String message, Throwable cause) {
            super(message, cause);
        }

        public BlueprintConverterException(String message) {
            super(message);
        }
    }
}

