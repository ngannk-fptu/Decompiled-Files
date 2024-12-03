/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.valueextraction.ExtractedValue
 *  javax.validation.valueextraction.UnwrapByDefault
 *  javax.validation.valueextraction.ValueExtractor
 */
package org.hibernate.validator.internal.engine.valueextraction;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.valueextraction.ExtractedValue;
import javax.validation.valueextraction.UnwrapByDefault;
import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.internal.engine.valueextraction.AnnotatedObject;
import org.hibernate.validator.internal.engine.valueextraction.ArrayElement;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.StringHelper;
import org.hibernate.validator.internal.util.TypeHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public class ValueExtractorDescriptor {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final Key key;
    private final ValueExtractor<?> valueExtractor;
    private final boolean unwrapByDefault;
    private final Optional<Class<?>> extractedType;

    public ValueExtractorDescriptor(ValueExtractor<?> valueExtractor) {
        AnnotatedParameterizedType valueExtractorDefinition = ValueExtractorDescriptor.getValueExtractorDefinition(valueExtractor.getClass());
        this.key = new Key(ValueExtractorDescriptor.getContainerType(valueExtractorDefinition, valueExtractor.getClass()), ValueExtractorDescriptor.getExtractedTypeParameter(valueExtractorDefinition, valueExtractor.getClass()));
        this.valueExtractor = valueExtractor;
        this.unwrapByDefault = ValueExtractorDescriptor.hasUnwrapByDefaultAnnotation(valueExtractor.getClass());
        this.extractedType = ValueExtractorDescriptor.getExtractedType(valueExtractorDefinition);
    }

    private static TypeVariable<?> getExtractedTypeParameter(AnnotatedParameterizedType valueExtractorDefinition, Class<? extends ValueExtractor> extractorImplementationType) {
        AnnotatedType containerType = valueExtractorDefinition.getAnnotatedActualTypeArguments()[0];
        Class containerTypeRaw = (Class)TypeHelper.getErasedType(containerType.getType());
        TypeVariable<Class<Object>> extractedTypeParameter = null;
        if (containerType.isAnnotationPresent(ExtractedValue.class)) {
            extractedTypeParameter = containerType instanceof AnnotatedArrayType ? new ArrayElement((AnnotatedArrayType)containerType) : AnnotatedObject.INSTANCE;
        }
        if (containerType instanceof AnnotatedParameterizedType) {
            AnnotatedParameterizedType parameterizedExtractedType = (AnnotatedParameterizedType)containerType;
            int i = 0;
            for (AnnotatedType typeArgument : parameterizedExtractedType.getAnnotatedActualTypeArguments()) {
                if (!TypeHelper.isUnboundWildcard(typeArgument.getType())) {
                    throw LOG.getOnlyUnboundWildcardTypeArgumentsSupportedForContainerTypeOfValueExtractorException(extractorImplementationType);
                }
                if (typeArgument.isAnnotationPresent(ExtractedValue.class)) {
                    if (extractedTypeParameter != null) {
                        throw LOG.getValueExtractorDeclaresExtractedValueMultipleTimesException(extractorImplementationType);
                    }
                    if (!Void.TYPE.equals(typeArgument.getAnnotation(ExtractedValue.class).type())) {
                        throw LOG.getExtractedValueOnTypeParameterOfContainerTypeMayNotDefineTypeAttributeException(extractorImplementationType);
                    }
                    extractedTypeParameter = containerTypeRaw.getTypeParameters()[i];
                }
                ++i;
            }
        }
        if (extractedTypeParameter == null) {
            throw LOG.getValueExtractorFailsToDeclareExtractedValueException(extractorImplementationType);
        }
        return extractedTypeParameter;
    }

    private static Optional<Class<?>> getExtractedType(AnnotatedParameterizedType valueExtractorDefinition) {
        Class extractedType;
        AnnotatedType containerType = valueExtractorDefinition.getAnnotatedActualTypeArguments()[0];
        if (containerType.isAnnotationPresent(ExtractedValue.class) && !Void.TYPE.equals(extractedType = containerType.getAnnotation(ExtractedValue.class).type())) {
            return Optional.of(ReflectionHelper.boxedType(extractedType));
        }
        return Optional.empty();
    }

    private static Class<?> getContainerType(AnnotatedParameterizedType valueExtractorDefinition, Class<? extends ValueExtractor> extractorImplementationType) {
        AnnotatedType containerType = valueExtractorDefinition.getAnnotatedActualTypeArguments()[0];
        return TypeHelper.getErasedReferenceType(containerType.getType());
    }

    private static AnnotatedParameterizedType getValueExtractorDefinition(Class<?> extractorImplementationType) {
        ArrayList<AnnotatedType> valueExtractorAnnotatedTypes = new ArrayList<AnnotatedType>();
        ValueExtractorDescriptor.determineValueExtractorDefinitions(valueExtractorAnnotatedTypes, extractorImplementationType);
        if (valueExtractorAnnotatedTypes.size() == 1) {
            return (AnnotatedParameterizedType)valueExtractorAnnotatedTypes.get(0);
        }
        if (valueExtractorAnnotatedTypes.size() > 1) {
            throw LOG.getParallelDefinitionsOfValueExtractorsException(extractorImplementationType);
        }
        throw new AssertionError((Object)(extractorImplementationType.getName() + " should be a subclass of " + ValueExtractor.class.getSimpleName()));
    }

    private static void determineValueExtractorDefinitions(List<AnnotatedType> valueExtractorDefinitions, Class<?> extractorImplementationType) {
        if (!ValueExtractor.class.isAssignableFrom(extractorImplementationType)) {
            return;
        }
        Class<?> superClass = extractorImplementationType.getSuperclass();
        if (superClass != null && !Object.class.equals(superClass)) {
            ValueExtractorDescriptor.determineValueExtractorDefinitions(valueExtractorDefinitions, superClass);
        }
        for (Class<?> clazz : extractorImplementationType.getInterfaces()) {
            if (ValueExtractor.class.equals(clazz)) continue;
            ValueExtractorDescriptor.determineValueExtractorDefinitions(valueExtractorDefinitions, clazz);
        }
        for (AnnotatedElement annotatedElement : extractorImplementationType.getAnnotatedInterfaces()) {
            if (!ValueExtractor.class.equals(ReflectionHelper.getClassFromType(annotatedElement.getType()))) continue;
            valueExtractorDefinitions.add((AnnotatedType)annotatedElement);
        }
    }

    private static boolean hasUnwrapByDefaultAnnotation(Class<?> extractorImplementationType) {
        return extractorImplementationType.isAnnotationPresent(UnwrapByDefault.class);
    }

    public Key getKey() {
        return this.key;
    }

    public Class<?> getContainerType() {
        return this.key.containerType;
    }

    public TypeVariable<?> getExtractedTypeParameter() {
        return this.key.extractedTypeParameter;
    }

    public Optional<Class<?>> getExtractedType() {
        return this.extractedType;
    }

    public ValueExtractor<?> getValueExtractor() {
        return this.valueExtractor;
    }

    public boolean isUnwrapByDefault() {
        return this.unwrapByDefault;
    }

    public String toString() {
        return "ValueExtractorDescriptor [key=" + this.key + ", valueExtractor=" + this.valueExtractor + ", unwrapByDefault=" + this.unwrapByDefault + "]";
    }

    public static class Key {
        private final Class<?> containerType;
        private final TypeVariable<?> extractedTypeParameter;
        private final int hashCode;

        public Key(Class<?> containerType, TypeVariable<?> extractedTypeParameter) {
            this.containerType = containerType;
            this.extractedTypeParameter = extractedTypeParameter;
            this.hashCode = Key.buildHashCode(containerType, extractedTypeParameter);
        }

        private static int buildHashCode(Type containerType, TypeVariable<?> extractedTypeParameter) {
            int prime = 31;
            int result = 1;
            result = 31 * result + containerType.hashCode();
            result = 31 * result + extractedTypeParameter.hashCode();
            return result;
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key)obj;
            return this.containerType.equals(other.containerType) && this.extractedTypeParameter.equals(other.extractedTypeParameter);
        }

        public String toString() {
            return "Key [containerType=" + StringHelper.toShortString(this.containerType) + ", extractedTypeParameter=" + this.extractedTypeParameter + "]";
        }
    }
}

