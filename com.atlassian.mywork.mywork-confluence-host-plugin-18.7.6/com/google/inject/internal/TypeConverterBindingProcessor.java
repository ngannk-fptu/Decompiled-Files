/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.TypeLiteral;
import com.google.inject.internal.AbstractProcessor;
import com.google.inject.internal.Errors;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.util.$SourceProvider;
import com.google.inject.internal.util.$Strings;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeConverter;
import com.google.inject.spi.TypeConverterBinding;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class TypeConverterBindingProcessor
extends AbstractProcessor {
    TypeConverterBindingProcessor(Errors errors) {
        super(errors);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void prepareBuiltInConverters(InjectorImpl injector) {
        this.injector = injector;
        try {
            this.convertToPrimitiveType(Integer.TYPE, Integer.class);
            this.convertToPrimitiveType(Long.TYPE, Long.class);
            this.convertToPrimitiveType(Boolean.TYPE, Boolean.class);
            this.convertToPrimitiveType(Byte.TYPE, Byte.class);
            this.convertToPrimitiveType(Short.TYPE, Short.class);
            this.convertToPrimitiveType(Float.TYPE, Float.class);
            this.convertToPrimitiveType(Double.TYPE, Double.class);
            this.convertToClass(Character.class, new TypeConverter(){

                @Override
                public Object convert(String value, TypeLiteral<?> toType) {
                    if ((value = value.trim()).length() != 1) {
                        throw new RuntimeException("Length != 1.");
                    }
                    return Character.valueOf(value.charAt(0));
                }

                public String toString() {
                    return "TypeConverter<Character>";
                }
            });
            this.convertToClasses(Matchers.subclassesOf(Enum.class), new TypeConverter(){

                @Override
                public Object convert(String value, TypeLiteral<?> toType) {
                    return Enum.valueOf(toType.getRawType(), value);
                }

                public String toString() {
                    return "TypeConverter<E extends Enum<E>>";
                }
            });
            this.internalConvertToTypes(new AbstractMatcher<TypeLiteral<?>>(){

                @Override
                public boolean matches(TypeLiteral<?> typeLiteral) {
                    return typeLiteral.getRawType() == Class.class;
                }

                public String toString() {
                    return "Class<?>";
                }
            }, new TypeConverter(){

                @Override
                public Object convert(String value, TypeLiteral<?> toType) {
                    try {
                        return Class.forName(value);
                    }
                    catch (ClassNotFoundException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }

                public String toString() {
                    return "TypeConverter<Class<?>>";
                }
            });
        }
        finally {
            this.injector = null;
        }
    }

    private <T> void convertToPrimitiveType(Class<T> primitiveType, final Class<T> wrapperType) {
        try {
            final Method parser = wrapperType.getMethod("parse" + $Strings.capitalize(primitiveType.getName()), String.class);
            TypeConverter typeConverter = new TypeConverter(){

                @Override
                public Object convert(String value, TypeLiteral<?> toType) {
                    try {
                        return parser.invoke(null, value);
                    }
                    catch (IllegalAccessException e) {
                        throw new AssertionError((Object)e);
                    }
                    catch (InvocationTargetException e) {
                        throw new RuntimeException(e.getTargetException().getMessage());
                    }
                }

                public String toString() {
                    return "TypeConverter<" + wrapperType.getSimpleName() + ">";
                }
            };
            this.convertToClass(wrapperType, typeConverter);
        }
        catch (NoSuchMethodException e) {
            throw new AssertionError((Object)e);
        }
    }

    private <T> void convertToClass(Class<T> type, TypeConverter converter) {
        this.convertToClasses(Matchers.identicalTo(type), converter);
    }

    private void convertToClasses(final Matcher<? super Class<?>> typeMatcher, TypeConverter converter) {
        this.internalConvertToTypes(new AbstractMatcher<TypeLiteral<?>>(){

            @Override
            public boolean matches(TypeLiteral<?> typeLiteral) {
                Type type = typeLiteral.getType();
                if (!(type instanceof Class)) {
                    return false;
                }
                Class clazz = (Class)type;
                return typeMatcher.matches(clazz);
            }

            public String toString() {
                return typeMatcher.toString();
            }
        }, converter);
    }

    private void internalConvertToTypes(Matcher<? super TypeLiteral<?>> typeMatcher, TypeConverter converter) {
        this.injector.state.addConverter(new TypeConverterBinding($SourceProvider.UNKNOWN_SOURCE, typeMatcher, converter));
    }

    @Override
    public Boolean visit(TypeConverterBinding command) {
        this.injector.state.addConverter(new TypeConverterBinding(command.getSource(), command.getTypeMatcher(), command.getTypeConverter()));
        return true;
    }
}

