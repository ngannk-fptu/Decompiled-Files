/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.DecoratingProxy
 *  org.springframework.core.GenericTypeResolver
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.core.convert.converter.ConditionalGenericConverter
 *  org.springframework.core.convert.converter.GenericConverter
 *  org.springframework.core.convert.converter.GenericConverter$ConvertiblePair
 *  org.springframework.core.convert.support.GenericConversionService
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.util.StringValueResolver
 */
package org.springframework.format.support;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.DecoratingProxy;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

public class FormattingConversionService
extends GenericConversionService
implements FormatterRegistry,
EmbeddedValueResolverAware {
    @Nullable
    private StringValueResolver embeddedValueResolver;
    private final Map<AnnotationConverterKey, GenericConverter> cachedPrinters = new ConcurrentHashMap<AnnotationConverterKey, GenericConverter>(64);
    private final Map<AnnotationConverterKey, GenericConverter> cachedParsers = new ConcurrentHashMap<AnnotationConverterKey, GenericConverter>(64);

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    @Override
    public void addPrinter(Printer<?> printer) {
        Class<?> fieldType = FormattingConversionService.getFieldType(printer, Printer.class);
        this.addConverter(new PrinterConverter(fieldType, printer, (ConversionService)this));
    }

    @Override
    public void addParser(Parser<?> parser) {
        Class<?> fieldType = FormattingConversionService.getFieldType(parser, Parser.class);
        this.addConverter(new ParserConverter(fieldType, parser, (ConversionService)this));
    }

    @Override
    public void addFormatter(Formatter<?> formatter) {
        this.addFormatterForFieldType(FormattingConversionService.getFieldType(formatter), formatter);
    }

    @Override
    public void addFormatterForFieldType(Class<?> fieldType, Formatter<?> formatter) {
        this.addConverter(new PrinterConverter(fieldType, formatter, (ConversionService)this));
        this.addConverter(new ParserConverter(fieldType, formatter, (ConversionService)this));
    }

    @Override
    public void addFormatterForFieldType(Class<?> fieldType, Printer<?> printer, Parser<?> parser) {
        this.addConverter(new PrinterConverter(fieldType, printer, (ConversionService)this));
        this.addConverter(new ParserConverter(fieldType, parser, (ConversionService)this));
    }

    @Override
    public void addFormatterForFieldAnnotation(AnnotationFormatterFactory<? extends Annotation> annotationFormatterFactory) {
        Class<? extends Annotation> annotationType = FormattingConversionService.getAnnotationType(annotationFormatterFactory);
        if (this.embeddedValueResolver != null && annotationFormatterFactory instanceof EmbeddedValueResolverAware) {
            ((EmbeddedValueResolverAware)((Object)annotationFormatterFactory)).setEmbeddedValueResolver(this.embeddedValueResolver);
        }
        Set<Class<?>> fieldTypes = annotationFormatterFactory.getFieldTypes();
        for (Class<?> fieldType : fieldTypes) {
            this.addConverter((GenericConverter)new AnnotationPrinterConverter(annotationType, annotationFormatterFactory, fieldType));
            this.addConverter((GenericConverter)new AnnotationParserConverter(annotationType, annotationFormatterFactory, fieldType));
        }
    }

    static Class<?> getFieldType(Formatter<?> formatter) {
        return FormattingConversionService.getFieldType(formatter, Formatter.class);
    }

    private static <T> Class<?> getFieldType(T instance, Class<T> genericInterface) {
        Class fieldType = GenericTypeResolver.resolveTypeArgument(instance.getClass(), genericInterface);
        if (fieldType == null && instance instanceof DecoratingProxy) {
            fieldType = GenericTypeResolver.resolveTypeArgument((Class)((DecoratingProxy)instance).getDecoratedClass(), genericInterface);
        }
        Assert.notNull((Object)fieldType, () -> "Unable to extract the parameterized field type from " + ClassUtils.getShortName((Class)genericInterface) + " [" + instance.getClass().getName() + "]; does the class parameterize the <T> generic type?");
        return fieldType;
    }

    static Class<? extends Annotation> getAnnotationType(AnnotationFormatterFactory<? extends Annotation> factory) {
        Class annotationType = GenericTypeResolver.resolveTypeArgument(factory.getClass(), AnnotationFormatterFactory.class);
        if (annotationType == null) {
            throw new IllegalArgumentException("Unable to extract parameterized Annotation type argument from AnnotationFormatterFactory [" + factory.getClass().getName() + "]; does the factory parameterize the <A extends Annotation> generic type?");
        }
        return annotationType;
    }

    private static class AnnotationConverterKey {
        private final Annotation annotation;
        private final Class<?> fieldType;

        public AnnotationConverterKey(Annotation annotation, Class<?> fieldType) {
            this.annotation = annotation;
            this.fieldType = fieldType;
        }

        public Annotation getAnnotation() {
            return this.annotation;
        }

        public Class<?> getFieldType() {
            return this.fieldType;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof AnnotationConverterKey)) {
                return false;
            }
            AnnotationConverterKey otherKey = (AnnotationConverterKey)other;
            return this.fieldType == otherKey.fieldType && this.annotation.equals(otherKey.annotation);
        }

        public int hashCode() {
            return this.fieldType.hashCode() * 29 + this.annotation.hashCode();
        }
    }

    private class AnnotationParserConverter
    implements ConditionalGenericConverter {
        private final Class<? extends Annotation> annotationType;
        private final AnnotationFormatterFactory annotationFormatterFactory;
        private final Class<?> fieldType;

        public AnnotationParserConverter(Class<? extends Annotation> annotationType, AnnotationFormatterFactory<?> annotationFormatterFactory, Class<?> fieldType) {
            this.annotationType = annotationType;
            this.annotationFormatterFactory = annotationFormatterFactory;
            this.fieldType = fieldType;
        }

        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new GenericConverter.ConvertiblePair(String.class, this.fieldType));
        }

        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return targetType.hasAnnotation(this.annotationType);
        }

        @Nullable
        public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            Annotation ann = targetType.getAnnotation(this.annotationType);
            if (ann == null) {
                throw new IllegalStateException("Expected [" + this.annotationType.getName() + "] to be present on " + targetType);
            }
            AnnotationConverterKey converterKey = new AnnotationConverterKey(ann, targetType.getObjectType());
            GenericConverter converter = (GenericConverter)FormattingConversionService.this.cachedParsers.get(converterKey);
            if (converter == null) {
                Parser<?> parser = this.annotationFormatterFactory.getParser(converterKey.getAnnotation(), converterKey.getFieldType());
                converter = new ParserConverter(this.fieldType, parser, (ConversionService)FormattingConversionService.this);
                FormattingConversionService.this.cachedParsers.put(converterKey, converter);
            }
            return converter.convert(source, sourceType, targetType);
        }

        public String toString() {
            return String.class.getName() + " -> @" + this.annotationType.getName() + " " + this.fieldType.getName() + ": " + this.annotationFormatterFactory;
        }
    }

    private class AnnotationPrinterConverter
    implements ConditionalGenericConverter {
        private final Class<? extends Annotation> annotationType;
        private final AnnotationFormatterFactory annotationFormatterFactory;
        private final Class<?> fieldType;

        public AnnotationPrinterConverter(Class<? extends Annotation> annotationType, AnnotationFormatterFactory<?> annotationFormatterFactory, Class<?> fieldType) {
            this.annotationType = annotationType;
            this.annotationFormatterFactory = annotationFormatterFactory;
            this.fieldType = fieldType;
        }

        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new GenericConverter.ConvertiblePair(this.fieldType, String.class));
        }

        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return sourceType.hasAnnotation(this.annotationType);
        }

        @Nullable
        public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            Annotation ann = sourceType.getAnnotation(this.annotationType);
            if (ann == null) {
                throw new IllegalStateException("Expected [" + this.annotationType.getName() + "] to be present on " + sourceType);
            }
            AnnotationConverterKey converterKey = new AnnotationConverterKey(ann, sourceType.getObjectType());
            GenericConverter converter = (GenericConverter)FormattingConversionService.this.cachedPrinters.get(converterKey);
            if (converter == null) {
                Printer<?> printer = this.annotationFormatterFactory.getPrinter(converterKey.getAnnotation(), converterKey.getFieldType());
                converter = new PrinterConverter(this.fieldType, printer, (ConversionService)FormattingConversionService.this);
                FormattingConversionService.this.cachedPrinters.put(converterKey, converter);
            }
            return converter.convert(source, sourceType, targetType);
        }

        public String toString() {
            return "@" + this.annotationType.getName() + " " + this.fieldType.getName() + " -> " + String.class.getName() + ": " + this.annotationFormatterFactory;
        }
    }

    private static class ParserConverter
    implements GenericConverter {
        private final Class<?> fieldType;
        private final Parser<?> parser;
        private final ConversionService conversionService;

        public ParserConverter(Class<?> fieldType, Parser<?> parser, ConversionService conversionService) {
            this.fieldType = fieldType;
            this.parser = parser;
            this.conversionService = conversionService;
        }

        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new GenericConverter.ConvertiblePair(String.class, this.fieldType));
        }

        @Nullable
        public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            Object result;
            String text = (String)source;
            if (!StringUtils.hasText((String)text)) {
                return null;
            }
            try {
                result = this.parser.parse(text, LocaleContextHolder.getLocale());
            }
            catch (IllegalArgumentException ex) {
                throw ex;
            }
            catch (Throwable ex) {
                throw new IllegalArgumentException("Parse attempt failed for value [" + text + "]", ex);
            }
            TypeDescriptor resultType = TypeDescriptor.valueOf(result.getClass());
            if (!resultType.isAssignableTo(targetType)) {
                result = this.conversionService.convert(result, resultType, targetType);
            }
            return result;
        }

        public String toString() {
            return String.class.getName() + " -> " + this.fieldType.getName() + ": " + this.parser;
        }
    }

    private static class PrinterConverter
    implements GenericConverter {
        private final Class<?> fieldType;
        private final TypeDescriptor printerObjectType;
        private final Printer printer;
        private final ConversionService conversionService;

        public PrinterConverter(Class<?> fieldType, Printer<?> printer, ConversionService conversionService) {
            this.fieldType = fieldType;
            this.printerObjectType = TypeDescriptor.valueOf(this.resolvePrinterObjectType(printer));
            this.printer = printer;
            this.conversionService = conversionService;
        }

        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new GenericConverter.ConvertiblePair(this.fieldType, String.class));
        }

        public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            if (!sourceType.isAssignableTo(this.printerObjectType)) {
                source = this.conversionService.convert(source, sourceType, this.printerObjectType);
            }
            if (source == null) {
                return "";
            }
            return this.printer.print(source, LocaleContextHolder.getLocale());
        }

        @Nullable
        private Class<?> resolvePrinterObjectType(Printer<?> printer) {
            return GenericTypeResolver.resolveTypeArgument(printer.getClass(), Printer.class);
        }

        public String toString() {
            return this.fieldType.getName() + " -> " + String.class.getName() + " : " + this.printer;
        }
    }
}

