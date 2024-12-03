/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.matcher;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.ByteCodeElement;
import net.bytebuddy.description.ModifierReviewable;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.annotation.AnnotationSource;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.AccessibilityMatcher;
import net.bytebuddy.matcher.AnnotationTargetMatcher;
import net.bytebuddy.matcher.AnnotationTypeMatcher;
import net.bytebuddy.matcher.ArrayTypeMatcher;
import net.bytebuddy.matcher.BooleanMatcher;
import net.bytebuddy.matcher.CachingMatcher;
import net.bytebuddy.matcher.ClassFileVersionMatcher;
import net.bytebuddy.matcher.ClassLoaderHierarchyMatcher;
import net.bytebuddy.matcher.ClassLoaderParentMatcher;
import net.bytebuddy.matcher.CollectionElementMatcher;
import net.bytebuddy.matcher.CollectionErasureMatcher;
import net.bytebuddy.matcher.CollectionItemMatcher;
import net.bytebuddy.matcher.CollectionOneToOneMatcher;
import net.bytebuddy.matcher.CollectionSizeMatcher;
import net.bytebuddy.matcher.DeclaringAnnotationMatcher;
import net.bytebuddy.matcher.DeclaringFieldMatcher;
import net.bytebuddy.matcher.DeclaringMethodMatcher;
import net.bytebuddy.matcher.DeclaringTypeMatcher;
import net.bytebuddy.matcher.DefinedShapeMatcher;
import net.bytebuddy.matcher.DescriptorMatcher;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.EqualityMatcher;
import net.bytebuddy.matcher.ErasureMatcher;
import net.bytebuddy.matcher.FailSafeMatcher;
import net.bytebuddy.matcher.FieldTypeMatcher;
import net.bytebuddy.matcher.HasSuperClassMatcher;
import net.bytebuddy.matcher.HasSuperTypeMatcher;
import net.bytebuddy.matcher.InheritedAnnotationMatcher;
import net.bytebuddy.matcher.InstanceTypeMatcher;
import net.bytebuddy.matcher.IsNamedMatcher;
import net.bytebuddy.matcher.MethodExceptionTypeMatcher;
import net.bytebuddy.matcher.MethodOverrideMatcher;
import net.bytebuddy.matcher.MethodParameterTypeMatcher;
import net.bytebuddy.matcher.MethodParameterTypesMatcher;
import net.bytebuddy.matcher.MethodParametersMatcher;
import net.bytebuddy.matcher.MethodReturnTypeMatcher;
import net.bytebuddy.matcher.MethodSortMatcher;
import net.bytebuddy.matcher.ModifierMatcher;
import net.bytebuddy.matcher.NameMatcher;
import net.bytebuddy.matcher.NegatingMatcher;
import net.bytebuddy.matcher.NullMatcher;
import net.bytebuddy.matcher.PrimitiveTypeMatcher;
import net.bytebuddy.matcher.RecordMatcher;
import net.bytebuddy.matcher.SignatureTokenMatcher;
import net.bytebuddy.matcher.StringMatcher;
import net.bytebuddy.matcher.StringSetMatcher;
import net.bytebuddy.matcher.SubTypeMatcher;
import net.bytebuddy.matcher.SuperTypeMatcher;
import net.bytebuddy.matcher.TypeSortMatcher;
import net.bytebuddy.matcher.VisibilityMatcher;
import net.bytebuddy.utility.JavaModule;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ElementMatchers {
    private ElementMatchers() {
        throw new UnsupportedOperationException("This class is a utility class and not supposed to be instantiated");
    }

    public static <T> ElementMatcher.Junction<T> failSafe(ElementMatcher<? super T> matcher) {
        return new FailSafeMatcher<T>(matcher, false);
    }

    public static <T> ElementMatcher.Junction<T> cached(ElementMatcher<? super T> matcher, ConcurrentMap<? super T, Boolean> map) {
        return new CachingMatcher<T>(matcher, map);
    }

    public static <T> ElementMatcher.Junction<T> cached(ElementMatcher<? super T> matcher, int evictionSize) {
        if (evictionSize < 1) {
            throw new IllegalArgumentException("Eviction size must be a positive number: " + evictionSize);
        }
        return new CachingMatcher.WithInlineEviction<T>(matcher, new ConcurrentHashMap(), evictionSize);
    }

    public static <T> ElementMatcher.Junction<T> is(@MaybeNull Object value) {
        return value == null ? NullMatcher.make() : new EqualityMatcher(value);
    }

    public static <T extends FieldDescription> ElementMatcher.Junction<T> is(Field field) {
        return ElementMatchers.is(new FieldDescription.ForLoadedField(field));
    }

    public static <T extends FieldDescription> ElementMatcher.Junction<T> is(FieldDescription.InDefinedShape field) {
        return ElementMatchers.definedField(new EqualityMatcher(field));
    }

    public static <T extends FieldDescription> ElementMatcher.Junction<T> definedField(ElementMatcher<? super FieldDescription.InDefinedShape> matcher) {
        return new DefinedShapeMatcher(matcher);
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> is(Method method) {
        return ElementMatchers.is(new MethodDescription.ForLoadedMethod(method));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> is(Constructor<?> constructor) {
        return ElementMatchers.is(new MethodDescription.ForLoadedConstructor(constructor));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> is(MethodDescription.InDefinedShape method) {
        return ElementMatchers.definedMethod(new EqualityMatcher(method));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> definedMethod(ElementMatcher<? super MethodDescription.InDefinedShape> matcher) {
        return new DefinedShapeMatcher(matcher);
    }

    public static <T extends ParameterDescription> ElementMatcher.Junction<T> is(ParameterDescription.InDefinedShape parameter) {
        return ElementMatchers.definedParameter(new EqualityMatcher(parameter));
    }

    public static <T extends ParameterDescription> ElementMatcher.Junction<T> definedParameter(ElementMatcher<? super ParameterDescription.InDefinedShape> matcher) {
        return new DefinedShapeMatcher(matcher);
    }

    public static <T extends ParameterDescription> ElementMatcher.Junction<T> hasType(ElementMatcher<? super TypeDescription> matcher) {
        return ElementMatchers.hasGenericType(ElementMatchers.erasure(matcher));
    }

    public static <T extends ParameterDescription> ElementMatcher.Junction<T> hasGenericType(ElementMatcher<? super TypeDescription.Generic> matcher) {
        return new MethodParameterTypeMatcher(matcher);
    }

    public static <T extends ParameterDescription> ElementMatcher.Junction<T> isMandated() {
        return ModifierMatcher.of(ModifierMatcher.Mode.MANDATED);
    }

    public static <T extends TypeDefinition> ElementMatcher.Junction<T> is(Type type) {
        return ElementMatchers.is(TypeDefinition.Sort.describe(type));
    }

    public static <T extends AnnotationDescription> ElementMatcher.Junction<T> is(Annotation annotation) {
        return ElementMatchers.is(AnnotationDescription.ForLoadedAnnotation.of(annotation));
    }

    public static <T> ElementMatcher.Junction<T> not(ElementMatcher<? super T> matcher) {
        return new NegatingMatcher<T>(matcher);
    }

    public static <T> ElementMatcher.Junction<T> any() {
        return BooleanMatcher.of(true);
    }

    public static <T> ElementMatcher.Junction<T> none() {
        return BooleanMatcher.of(false);
    }

    public static <T> ElementMatcher.Junction<T> anyOf(Object ... value) {
        return ElementMatchers.anyOf(Arrays.asList(value));
    }

    public static <T> ElementMatcher.Junction<T> anyOf(Iterable<?> values) {
        ElementMatcher.Junction<T> matcher = null;
        for (Object value : values) {
            matcher = matcher == null ? ElementMatchers.is(value) : matcher.or(ElementMatchers.is(value));
        }
        return matcher == null ? ElementMatchers.none() : matcher;
    }

    public static <T extends TypeDefinition> ElementMatcher.Junction<T> anyOf(Type ... value) {
        return ElementMatchers.anyOf(new TypeList.Generic.ForLoadedTypes(value));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> anyOf(Constructor<?> ... value) {
        return ElementMatchers.definedMethod(ElementMatchers.anyOf(new MethodList.ForLoadedMethods(value, new Method[0])));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> anyOf(Method ... value) {
        return ElementMatchers.definedMethod(ElementMatchers.anyOf(new MethodList.ForLoadedMethods(new Constructor[0], value)));
    }

    public static <T extends FieldDescription> ElementMatcher.Junction<T> anyOf(Field ... value) {
        return ElementMatchers.definedField(ElementMatchers.anyOf(new FieldList.ForLoadedFields(value)));
    }

    public static <T extends AnnotationDescription> ElementMatcher.Junction<T> anyOf(Annotation ... value) {
        return ElementMatchers.anyOf(new AnnotationList.ForLoadedAnnotations(value));
    }

    public static <T> ElementMatcher.Junction<T> noneOf(Object ... value) {
        return ElementMatchers.noneOf(Arrays.asList(value));
    }

    public static <T> ElementMatcher.Junction<T> noneOf(Iterable<?> values) {
        ElementMatcher.Junction<T> matcher = null;
        for (Object value : values) {
            matcher = matcher == null ? ElementMatchers.not(ElementMatchers.is(value)) : matcher.and(ElementMatchers.not(ElementMatchers.is(value)));
        }
        return matcher == null ? ElementMatchers.any() : matcher;
    }

    public static <T extends TypeDefinition> ElementMatcher.Junction<T> noneOf(Type ... value) {
        return ElementMatchers.noneOf(new TypeList.Generic.ForLoadedTypes(value));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> noneOf(Constructor<?> ... value) {
        return ElementMatchers.definedMethod(ElementMatchers.noneOf(new MethodList.ForLoadedMethods(value, new Method[0])));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> noneOf(Method ... value) {
        return ElementMatchers.definedMethod(ElementMatchers.noneOf(new MethodList.ForLoadedMethods(new Constructor[0], value)));
    }

    public static <T extends FieldDescription> ElementMatcher.Junction<T> noneOf(Field ... value) {
        return ElementMatchers.definedField(ElementMatchers.noneOf(new FieldList.ForLoadedFields(value)));
    }

    public static <T extends AnnotationDescription> ElementMatcher.Junction<T> noneOf(Annotation ... value) {
        return ElementMatchers.noneOf(new AnnotationList.ForLoadedAnnotations(value));
    }

    public static <T> ElementMatcher.Junction<Iterable<? extends T>> whereAny(ElementMatcher<? super T> matcher) {
        return new CollectionItemMatcher<T>(matcher);
    }

    public static <T> ElementMatcher.Junction<Iterable<? extends T>> whereNone(ElementMatcher<? super T> matcher) {
        return ElementMatchers.not(ElementMatchers.whereAny(matcher));
    }

    public static <T extends TypeDescription.Generic> ElementMatcher.Junction<T> erasure(Class<?> type) {
        return ElementMatchers.erasure(ElementMatchers.is(type));
    }

    public static <T extends TypeDescription.Generic> ElementMatcher.Junction<T> erasure(TypeDescription type) {
        return ElementMatchers.erasure(ElementMatchers.is(type));
    }

    public static <T extends TypeDescription.Generic> ElementMatcher.Junction<T> erasure(ElementMatcher<? super TypeDescription> matcher) {
        return new ErasureMatcher(matcher);
    }

    public static <T extends Iterable<? extends TypeDescription.Generic>> ElementMatcher.Junction<T> erasures(Class<?> ... type) {
        return ElementMatchers.erasures(new TypeList.ForLoadedTypes(type));
    }

    public static <T extends Iterable<? extends TypeDescription.Generic>> ElementMatcher.Junction<T> erasures(TypeDescription ... type) {
        return ElementMatchers.erasures(Arrays.asList(type));
    }

    public static <T extends Iterable<? extends TypeDescription.Generic>> ElementMatcher.Junction<T> erasures(Iterable<? extends TypeDescription> types) {
        ArrayList<ElementMatcher.Junction<T>> typeMatchers = new ArrayList<ElementMatcher.Junction<T>>();
        for (TypeDescription typeDescription : types) {
            typeMatchers.add(ElementMatchers.is(typeDescription));
        }
        return ElementMatchers.erasures(new CollectionOneToOneMatcher(typeMatchers));
    }

    public static <T extends Iterable<? extends TypeDescription.Generic>> ElementMatcher.Junction<T> erasures(ElementMatcher<? super Iterable<? extends TypeDescription>> matcher) {
        return new CollectionErasureMatcher(matcher);
    }

    public static <T extends TypeDefinition> ElementMatcher.Junction<T> isVariable(String symbol) {
        return ElementMatchers.isVariable(ElementMatchers.named(symbol));
    }

    public static <T extends TypeDefinition> ElementMatcher.Junction<T> isVariable(ElementMatcher<? super NamedElement> matcher) {
        return new TypeSortMatcher(ElementMatchers.anyOf(new Object[]{TypeDefinition.Sort.VARIABLE, TypeDefinition.Sort.VARIABLE_SYMBOLIC})).and(matcher);
    }

    public static <T extends NamedElement> ElementMatcher.Junction<T> named(String name) {
        return new NameMatcher(new StringMatcher(name, StringMatcher.Mode.EQUALS_FULLY));
    }

    public static <T extends NamedElement> ElementMatcher.Junction<T> namedOneOf(String ... names) {
        return new NameMatcher(new StringSetMatcher(new HashSet<String>(Arrays.asList(names))));
    }

    public static <T extends NamedElement> ElementMatcher.Junction<T> namedIgnoreCase(String name) {
        return new NameMatcher(new StringMatcher(name, StringMatcher.Mode.EQUALS_FULLY_IGNORE_CASE));
    }

    public static <T extends NamedElement> ElementMatcher.Junction<T> nameStartsWith(String prefix) {
        return new NameMatcher(new StringMatcher(prefix, StringMatcher.Mode.STARTS_WITH));
    }

    public static <T extends NamedElement> ElementMatcher.Junction<T> nameStartsWithIgnoreCase(String prefix) {
        return new NameMatcher(new StringMatcher(prefix, StringMatcher.Mode.STARTS_WITH_IGNORE_CASE));
    }

    public static <T extends NamedElement> ElementMatcher.Junction<T> nameEndsWith(String suffix) {
        return new NameMatcher(new StringMatcher(suffix, StringMatcher.Mode.ENDS_WITH));
    }

    public static <T extends NamedElement> ElementMatcher.Junction<T> nameEndsWithIgnoreCase(String suffix) {
        return new NameMatcher(new StringMatcher(suffix, StringMatcher.Mode.ENDS_WITH_IGNORE_CASE));
    }

    public static <T extends NamedElement> ElementMatcher.Junction<T> nameContains(String infix) {
        return new NameMatcher(new StringMatcher(infix, StringMatcher.Mode.CONTAINS));
    }

    public static <T extends NamedElement> ElementMatcher.Junction<T> nameContainsIgnoreCase(String infix) {
        return new NameMatcher(new StringMatcher(infix, StringMatcher.Mode.CONTAINS_IGNORE_CASE));
    }

    public static <T extends NamedElement> ElementMatcher.Junction<T> nameMatches(String regex) {
        return new NameMatcher(new StringMatcher(regex, StringMatcher.Mode.MATCHES));
    }

    public static <T extends NamedElement.WithOptionalName> ElementMatcher.Junction<T> isNamed() {
        return new IsNamedMatcher();
    }

    public static <T extends NamedElement.WithDescriptor> ElementMatcher.Junction<T> hasDescriptor(String descriptor) {
        return new DescriptorMatcher(new StringMatcher(descriptor, StringMatcher.Mode.EQUALS_FULLY));
    }

    public static <T extends ByteCodeElement> ElementMatcher.Junction<T> isDeclaredBy(Class<?> type) {
        return ElementMatchers.isDeclaredBy(TypeDescription.ForLoadedType.of(type));
    }

    public static <T extends ByteCodeElement> ElementMatcher.Junction<T> isDeclaredBy(TypeDescription type) {
        return ElementMatchers.isDeclaredBy(ElementMatchers.is(type));
    }

    public static <T extends ByteCodeElement> ElementMatcher.Junction<T> isDeclaredBy(ElementMatcher<? super TypeDescription> matcher) {
        return ElementMatchers.isDeclaredByGeneric(ElementMatchers.erasure(matcher));
    }

    public static <T extends ByteCodeElement> ElementMatcher.Junction<T> isDeclaredByGeneric(Type type) {
        return ElementMatchers.isDeclaredByGeneric(TypeDefinition.Sort.describe(type));
    }

    public static <T extends ByteCodeElement> ElementMatcher.Junction<T> isDeclaredByGeneric(TypeDescription.Generic type) {
        return ElementMatchers.isDeclaredByGeneric(ElementMatchers.is(type));
    }

    public static <T extends ByteCodeElement> ElementMatcher.Junction<T> isDeclaredByGeneric(ElementMatcher<? super TypeDescription.Generic> matcher) {
        return new DeclaringTypeMatcher(matcher);
    }

    public static <T extends ByteCodeElement> ElementMatcher.Junction<T> isVisibleTo(Class<?> type) {
        return ElementMatchers.isVisibleTo(TypeDescription.ForLoadedType.of(type));
    }

    public static <T extends ByteCodeElement> ElementMatcher.Junction<T> isVisibleTo(TypeDescription type) {
        return new VisibilityMatcher(type);
    }

    public static <T extends ByteCodeElement> ElementMatcher.Junction<T> isAccessibleTo(Class<?> type) {
        return ElementMatchers.isAccessibleTo(TypeDescription.ForLoadedType.of(type));
    }

    public static <T extends ByteCodeElement> ElementMatcher.Junction<T> isAccessibleTo(TypeDescription type) {
        return new AccessibilityMatcher(type);
    }

    public static <T extends ModifierReviewable.OfAbstraction> ElementMatcher.Junction<T> isAbstract() {
        return ModifierMatcher.of(ModifierMatcher.Mode.ABSTRACT);
    }

    public static <T extends ModifierReviewable.OfEnumeration> ElementMatcher.Junction<T> isEnum() {
        return ModifierMatcher.of(ModifierMatcher.Mode.ENUMERATION);
    }

    public static <T extends AnnotationSource> ElementMatcher.Junction<T> isAnnotatedWith(Class<? extends Annotation> type) {
        return ElementMatchers.isAnnotatedWith(TypeDescription.ForLoadedType.of(type));
    }

    public static <T extends AnnotationSource> ElementMatcher.Junction<T> isAnnotatedWith(TypeDescription type) {
        return ElementMatchers.isAnnotatedWith(ElementMatchers.is(type));
    }

    public static <T extends AnnotationSource> ElementMatcher.Junction<T> isAnnotatedWith(ElementMatcher<? super TypeDescription> matcher) {
        return ElementMatchers.declaresAnnotation(ElementMatchers.annotationType(matcher));
    }

    public static <T extends AnnotationSource> ElementMatcher.Junction<T> declaresAnnotation(ElementMatcher<? super AnnotationDescription> matcher) {
        return new DeclaringAnnotationMatcher(new CollectionItemMatcher<AnnotationDescription>(matcher));
    }

    public static <T extends ModifierReviewable.OfByteCodeElement> ElementMatcher.Junction<T> isPublic() {
        return ModifierMatcher.of(ModifierMatcher.Mode.PUBLIC);
    }

    public static <T extends ModifierReviewable.OfByteCodeElement> ElementMatcher.Junction<T> isProtected() {
        return ModifierMatcher.of(ModifierMatcher.Mode.PROTECTED);
    }

    public static <T extends ModifierReviewable.OfByteCodeElement> ElementMatcher.Junction<T> isPackagePrivate() {
        return ElementMatchers.not(ElementMatchers.isPublic().or(ElementMatchers.<T>isProtected()).or(ElementMatchers.<T>isPrivate()));
    }

    public static <T extends ModifierReviewable.OfByteCodeElement> ElementMatcher.Junction<T> isPrivate() {
        return ModifierMatcher.of(ModifierMatcher.Mode.PRIVATE);
    }

    public static <T extends ModifierReviewable.OfByteCodeElement> ElementMatcher.Junction<T> isStatic() {
        return ModifierMatcher.of(ModifierMatcher.Mode.STATIC);
    }

    public static <T extends ModifierReviewable> ElementMatcher.Junction<T> isFinal() {
        return ModifierMatcher.of(ModifierMatcher.Mode.FINAL);
    }

    public static <T extends ModifierReviewable> ElementMatcher.Junction<T> isSynthetic() {
        return ModifierMatcher.of(ModifierMatcher.Mode.SYNTHETIC);
    }

    public static <T extends ModifierReviewable.ForMethodDescription> ElementMatcher.Junction<T> isSynchronized() {
        return ModifierMatcher.of(ModifierMatcher.Mode.SYNCHRONIZED);
    }

    public static <T extends ModifierReviewable.ForMethodDescription> ElementMatcher.Junction<T> isNative() {
        return ModifierMatcher.of(ModifierMatcher.Mode.NATIVE);
    }

    public static <T extends ModifierReviewable.ForMethodDescription> ElementMatcher.Junction<T> isStrict() {
        return ModifierMatcher.of(ModifierMatcher.Mode.STRICT);
    }

    public static <T extends ModifierReviewable.ForMethodDescription> ElementMatcher.Junction<T> isVarArgs() {
        return ModifierMatcher.of(ModifierMatcher.Mode.VAR_ARGS);
    }

    public static <T extends ModifierReviewable.ForMethodDescription> ElementMatcher.Junction<T> isBridge() {
        return ModifierMatcher.of(ModifierMatcher.Mode.BRIDGE);
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> returnsGeneric(Type type) {
        return ElementMatchers.returnsGeneric(TypeDefinition.Sort.describe(type));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> returnsGeneric(TypeDescription.Generic type) {
        return ElementMatchers.returnsGeneric(ElementMatchers.is(type));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> returns(Class<?> type) {
        return ElementMatchers.returnsGeneric(ElementMatchers.erasure(type));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> returns(TypeDescription type) {
        return ElementMatchers.returns(ElementMatchers.is(type));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> returns(ElementMatcher<? super TypeDescription> matcher) {
        return ElementMatchers.returnsGeneric(ElementMatchers.erasure(matcher));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> returnsGeneric(ElementMatcher<? super TypeDescription.Generic> matcher) {
        return new MethodReturnTypeMatcher(matcher);
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> takesGenericArgument(int index, Type type) {
        return ElementMatchers.takesGenericArgument(index, TypeDefinition.Sort.describe(type));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> takesGenericArgument(int index, TypeDescription.Generic type) {
        return ElementMatchers.takesGenericArgument(index, ElementMatchers.is(type));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> takesGenericArgument(int index, ElementMatcher<? super TypeDescription.Generic> matcher) {
        return ElementMatchers.takesGenericArguments(new CollectionElementMatcher<TypeDescription.Generic>(index, matcher));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> takesGenericArguments(Type ... type) {
        return ElementMatchers.takesGenericArguments(new TypeList.Generic.ForLoadedTypes(type));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> takesGenericArguments(TypeDefinition ... type) {
        return ElementMatchers.takesGenericArguments(Arrays.asList(type));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> takesGenericArguments(List<? extends TypeDefinition> types) {
        ArrayList<ElementMatcher.Junction<T>> typeMatchers = new ArrayList<ElementMatcher.Junction<T>>();
        for (TypeDefinition typeDefinition : types) {
            typeMatchers.add(ElementMatchers.is(typeDefinition));
        }
        return ElementMatchers.takesGenericArguments(new CollectionOneToOneMatcher(typeMatchers));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> takesGenericArguments(ElementMatcher<? super Iterable<? extends TypeDescription.Generic>> matchers) {
        return new MethodParametersMatcher(new MethodParameterTypesMatcher(matchers));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> takesArgument(int index, Class<?> type) {
        return ElementMatchers.takesArgument(index, TypeDescription.ForLoadedType.of(type));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> takesArgument(int index, TypeDescription type) {
        return ElementMatchers.takesArgument(index, ElementMatchers.is(type));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> takesArgument(int index, ElementMatcher<? super TypeDescription> matcher) {
        return ElementMatchers.takesGenericArgument(index, ElementMatchers.erasure(matcher));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> takesArguments(Class<?> ... type) {
        return ElementMatchers.takesGenericArguments(ElementMatchers.erasures(type));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> takesArguments(TypeDescription ... type) {
        return ElementMatchers.takesGenericArguments(ElementMatchers.erasures(type));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> takesArguments(Iterable<? extends TypeDescription> types) {
        ArrayList<ElementMatcher.Junction<T>> typeMatchers = new ArrayList<ElementMatcher.Junction<T>>();
        for (TypeDescription typeDescription : types) {
            typeMatchers.add(ElementMatchers.erasure(typeDescription));
        }
        return ElementMatchers.takesGenericArguments(new CollectionOneToOneMatcher(typeMatchers));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> takesArguments(ElementMatcher<? super Iterable<? extends TypeDescription>> matchers) {
        return new MethodParametersMatcher(new MethodParameterTypesMatcher(ElementMatchers.erasures(matchers)));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> takesArguments(int length) {
        return new MethodParametersMatcher(new CollectionSizeMatcher(length));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> takesNoArguments() {
        return ElementMatchers.takesArguments(0);
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> hasParameters(ElementMatcher<? super Iterable<? extends ParameterDescription>> matcher) {
        return new MethodParametersMatcher(matcher);
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> canThrow(Class<? extends Throwable> exceptionType) {
        return ElementMatchers.canThrow(TypeDescription.ForLoadedType.of(exceptionType));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> canThrow(TypeDescription exceptionType) {
        return exceptionType.isAssignableTo(RuntimeException.class) || exceptionType.isAssignableTo(Error.class) ? BooleanMatcher.of(true) : ElementMatchers.declaresGenericException(new CollectionItemMatcher<T>(ElementMatchers.erasure(ElementMatchers.isSuperTypeOf(exceptionType))));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> declaresGenericException(Type exceptionType) {
        return ElementMatchers.declaresGenericException(TypeDefinition.Sort.describe(exceptionType));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> declaresGenericException(TypeDescription.Generic exceptionType) {
        return !exceptionType.getSort().isWildcard() && exceptionType.asErasure().isAssignableTo(Throwable.class) ? ElementMatchers.declaresGenericException(new CollectionItemMatcher<T>(ElementMatchers.is(exceptionType))) : BooleanMatcher.of(false);
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> declaresException(Class<? extends Throwable> exceptionType) {
        return ElementMatchers.declaresException(TypeDescription.ForLoadedType.of(exceptionType));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> declaresException(TypeDescription exceptionType) {
        return exceptionType.isAssignableTo(Throwable.class) ? ElementMatchers.declaresGenericException(new CollectionItemMatcher<T>(ElementMatchers.erasure(exceptionType))) : BooleanMatcher.of(false);
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> declaresGenericException(ElementMatcher<? super Iterable<? extends TypeDescription.Generic>> matcher) {
        return new MethodExceptionTypeMatcher(matcher);
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isOverriddenFrom(Class<?> type) {
        return ElementMatchers.isOverriddenFrom(TypeDescription.ForLoadedType.of(type));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isOverriddenFrom(TypeDescription type) {
        return ElementMatchers.isOverriddenFrom(ElementMatchers.is(type));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isOverriddenFrom(ElementMatcher<? super TypeDescription> matcher) {
        return ElementMatchers.isOverriddenFromGeneric(ElementMatchers.erasure(matcher));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isOverriddenFromGeneric(Type type) {
        return ElementMatchers.isOverriddenFromGeneric(TypeDefinition.Sort.describe(type));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isOverriddenFromGeneric(TypeDescription.Generic type) {
        return ElementMatchers.isOverriddenFromGeneric(ElementMatchers.is(type));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isOverriddenFromGeneric(ElementMatcher<? super TypeDescription.Generic> matcher) {
        return new MethodOverrideMatcher(matcher);
    }

    public static <T extends TypeDescription> ElementMatcher.Junction<T> isInterface() {
        return ModifierMatcher.of(ModifierMatcher.Mode.INTERFACE);
    }

    public static <T extends TypeDescription> ElementMatcher.Junction<T> isAnnotation() {
        return ModifierMatcher.of(ModifierMatcher.Mode.ANNOTATION);
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isMethod() {
        return MethodSortMatcher.of(MethodSortMatcher.Sort.METHOD);
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isConstructor() {
        return MethodSortMatcher.of(MethodSortMatcher.Sort.CONSTRUCTOR);
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isTypeInitializer() {
        return MethodSortMatcher.of(MethodSortMatcher.Sort.TYPE_INITIALIZER);
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isVirtual() {
        return MethodSortMatcher.of(MethodSortMatcher.Sort.VIRTUAL);
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isDefaultMethod() {
        return MethodSortMatcher.of(MethodSortMatcher.Sort.DEFAULT_METHOD);
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isDefaultConstructor() {
        return ElementMatchers.isConstructor().and(ElementMatchers.<T>takesNoArguments());
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isMain() {
        return ElementMatchers.named("main").and(ElementMatchers.takesArguments(String[].class)).and(ElementMatchers.returns(TypeDescription.ForLoadedType.of(Void.TYPE)).and(ElementMatchers.<T>isStatic()).and(ElementMatchers.<T>isPublic()));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isDefaultFinalizer() {
        return ElementMatchers.isFinalizer().and(ElementMatchers.isDeclaredBy(TypeDescription.ForLoadedType.of(Object.class)));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isFinalizer() {
        return ElementMatchers.named("finalize").and(ElementMatchers.<T>takesNoArguments()).and(ElementMatchers.returns(TypeDescription.ForLoadedType.of(Void.TYPE)));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isHashCode() {
        return ElementMatchers.named("hashCode").and(ElementMatchers.<T>takesNoArguments()).and(ElementMatchers.returns(Integer.TYPE));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isEquals() {
        return ElementMatchers.named("equals").and(ElementMatchers.takesArguments(TypeDescription.ForLoadedType.of(Object.class))).and(ElementMatchers.returns(Boolean.TYPE));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isClone() {
        return ElementMatchers.named("clone").and(ElementMatchers.<T>takesNoArguments());
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isToString() {
        return ElementMatchers.named("toString").and(ElementMatchers.<T>takesNoArguments()).and(ElementMatchers.returns(TypeDescription.ForLoadedType.of(String.class)));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isSetter() {
        return ElementMatchers.nameStartsWith("set").and(ElementMatchers.takesArguments(1)).and(ElementMatchers.returns(TypeDescription.ForLoadedType.of(Void.TYPE)));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isSetter(String property) {
        return ElementMatchers.isSetter().and(property.length() == 0 ? ElementMatchers.named("set") : ElementMatchers.named("set" + Character.toUpperCase(property.charAt(0)) + property.substring(1)));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isSetter(Class<?> type) {
        return ElementMatchers.isSetter(TypeDescription.ForLoadedType.of(type));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isGenericSetter(Type type) {
        return ElementMatchers.isGenericSetter(TypeDefinition.Sort.describe(type));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isSetter(TypeDescription type) {
        return ElementMatchers.isSetter(ElementMatchers.is(type));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isGenericSetter(TypeDescription.Generic type) {
        return ElementMatchers.isGenericSetter(ElementMatchers.is(type));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isSetter(ElementMatcher<? super TypeDescription> matcher) {
        return ElementMatchers.isGenericSetter(ElementMatchers.erasure(matcher));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isGenericSetter(ElementMatcher<? super TypeDescription.Generic> matcher) {
        return ElementMatchers.isSetter().and(ElementMatchers.takesGenericArguments(new CollectionOneToOneMatcher<TypeDescription.Generic>(Collections.singletonList(matcher))));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isGetter() {
        return ElementMatchers.takesNoArguments().and(ElementMatchers.not(ElementMatchers.returns(TypeDescription.ForLoadedType.of(Void.TYPE)))).and(ElementMatchers.nameStartsWith("get").or(ElementMatchers.nameStartsWith("is").and(ElementMatchers.returnsGeneric(ElementMatchers.anyOf(new Type[]{Boolean.TYPE, Boolean.class})))));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isGetter(String property) {
        return ElementMatchers.isGetter().and(property.length() == 0 ? ElementMatchers.named("get").or(ElementMatchers.named("is")) : ElementMatchers.named("get" + Character.toUpperCase(property.charAt(0)) + property.substring(1)).or(ElementMatchers.named("is" + Character.toUpperCase(property.charAt(0)) + property.substring(1))));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isGetter(Class<?> type) {
        return ElementMatchers.isGetter(TypeDescription.ForLoadedType.of(type));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isGenericGetter(Type type) {
        return ElementMatchers.isGenericGetter(TypeDefinition.Sort.describe(type));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isGetter(TypeDescription type) {
        return ElementMatchers.isGetter(ElementMatchers.is(type));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isGenericGetter(TypeDescription.Generic type) {
        return ElementMatchers.isGenericGetter(ElementMatchers.is(type));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isGetter(ElementMatcher<? super TypeDescription> matcher) {
        return ElementMatchers.isGenericGetter(ElementMatchers.erasure(matcher));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> isGenericGetter(ElementMatcher<? super TypeDescription.Generic> matcher) {
        return ElementMatchers.isGetter().and(ElementMatchers.returnsGeneric(matcher));
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> hasMethodName(String internalName) {
        if ("<init>".equals(internalName)) {
            return ElementMatchers.isConstructor();
        }
        if ("<clinit>".equals(internalName)) {
            return ElementMatchers.isTypeInitializer();
        }
        return ElementMatchers.named(internalName);
    }

    public static <T extends MethodDescription> ElementMatcher.Junction<T> hasSignature(MethodDescription.SignatureToken token) {
        return new SignatureTokenMatcher(ElementMatchers.is(token));
    }

    public static <T extends TypeDescription> ElementMatcher.Junction<T> isSubTypeOf(Class<?> type) {
        return ElementMatchers.isSubTypeOf(TypeDescription.ForLoadedType.of(type));
    }

    public static <T extends TypeDescription> ElementMatcher.Junction<T> isSubTypeOf(TypeDescription type) {
        return new SubTypeMatcher(type);
    }

    public static <T extends TypeDescription> ElementMatcher.Junction<T> isSuperTypeOf(Class<?> type) {
        return ElementMatchers.isSuperTypeOf(TypeDescription.ForLoadedType.of(type));
    }

    public static <T extends TypeDescription> ElementMatcher.Junction<T> isSuperTypeOf(TypeDescription type) {
        return new SuperTypeMatcher(type);
    }

    public static <T extends TypeDescription> ElementMatcher.Junction<T> hasSuperClass(ElementMatcher<? super TypeDescription> matcher) {
        return ElementMatchers.hasGenericSuperClass(ElementMatchers.erasure(matcher));
    }

    public static <T extends TypeDescription> ElementMatcher.Junction<T> hasGenericSuperClass(ElementMatcher<? super TypeDescription.Generic> matcher) {
        return new HasSuperClassMatcher(matcher);
    }

    public static <T extends TypeDescription> ElementMatcher.Junction<T> hasSuperType(ElementMatcher<? super TypeDescription> matcher) {
        return ElementMatchers.hasGenericSuperType(ElementMatchers.erasure(matcher));
    }

    public static <T extends TypeDescription> ElementMatcher.Junction<T> hasGenericSuperType(ElementMatcher<? super TypeDescription.Generic> matcher) {
        return new HasSuperTypeMatcher(matcher);
    }

    public static <T extends TypeDescription> ElementMatcher.Junction<T> inheritsAnnotation(Class<?> type) {
        return ElementMatchers.inheritsAnnotation(TypeDescription.ForLoadedType.of(type));
    }

    public static <T extends TypeDescription> ElementMatcher.Junction<T> inheritsAnnotation(TypeDescription type) {
        return ElementMatchers.inheritsAnnotation(ElementMatchers.is(type));
    }

    public static <T extends TypeDescription> ElementMatcher.Junction<T> inheritsAnnotation(ElementMatcher<? super TypeDescription> matcher) {
        return ElementMatchers.hasAnnotation(ElementMatchers.annotationType(matcher));
    }

    public static <T extends TypeDescription> ElementMatcher.Junction<T> hasAnnotation(ElementMatcher<? super AnnotationDescription> matcher) {
        return new InheritedAnnotationMatcher(new CollectionItemMatcher<AnnotationDescription>(matcher));
    }

    public static <T extends TypeDescription> ElementMatcher.Junction<T> hasClassFileVersionAtLeast(ClassFileVersion classFileVersion) {
        return new ClassFileVersionMatcher(classFileVersion, false);
    }

    public static <T extends TypeDescription> ElementMatcher.Junction<T> hasClassFileVersionAtMost(ClassFileVersion classFileVersion) {
        return new ClassFileVersionMatcher(classFileVersion, true);
    }

    public static <T extends TypeDefinition> ElementMatcher.Junction<T> declaresField(ElementMatcher<? super FieldDescription> matcher) {
        return new DeclaringFieldMatcher(new CollectionItemMatcher<FieldDescription>(matcher));
    }

    public static <T extends TypeDefinition> ElementMatcher.Junction<T> declaresMethod(ElementMatcher<? super MethodDescription> matcher) {
        return new DeclaringMethodMatcher(new CollectionItemMatcher<MethodDescription>(matcher));
    }

    public static <T extends TypeDefinition> ElementMatcher.Junction<T> ofSort(TypeDefinition.Sort sort) {
        return ElementMatchers.ofSort(ElementMatchers.is((Object)sort));
    }

    public static <T extends TypeDefinition> ElementMatcher.Junction<T> ofSort(ElementMatcher<? super TypeDefinition.Sort> matcher) {
        return new TypeSortMatcher(matcher);
    }

    public static <T extends TypeDefinition> ElementMatcher.Junction<T> isPrimitive() {
        return new PrimitiveTypeMatcher();
    }

    public static <T extends TypeDefinition> ElementMatcher.Junction<T> isArray() {
        return new ArrayTypeMatcher();
    }

    public static <T extends TypeDefinition> ElementMatcher.Junction<T> isRecord() {
        return new RecordMatcher();
    }

    public static <T extends FieldDescription> ElementMatcher.Junction<T> genericFieldType(Type fieldType) {
        return ElementMatchers.genericFieldType(TypeDefinition.Sort.describe(fieldType));
    }

    public static <T extends FieldDescription> ElementMatcher.Junction<T> genericFieldType(TypeDescription.Generic fieldType) {
        return ElementMatchers.genericFieldType(ElementMatchers.is(fieldType));
    }

    public static <T extends FieldDescription> ElementMatcher.Junction<T> genericFieldType(ElementMatcher<? super TypeDescription.Generic> matcher) {
        return new FieldTypeMatcher(matcher);
    }

    public static <T extends FieldDescription> ElementMatcher.Junction<T> fieldType(Class<?> fieldType) {
        return ElementMatchers.fieldType(TypeDescription.ForLoadedType.of(fieldType));
    }

    public static <T extends FieldDescription> ElementMatcher.Junction<T> fieldType(TypeDescription fieldType) {
        return ElementMatchers.fieldType(ElementMatchers.is(fieldType));
    }

    public static <T extends FieldDescription> ElementMatcher.Junction<T> fieldType(ElementMatcher<? super TypeDescription> matcher) {
        return ElementMatchers.genericFieldType(ElementMatchers.erasure(matcher));
    }

    public static <T extends FieldDescription> ElementMatcher.Junction<T> isVolatile() {
        return ModifierMatcher.of(ModifierMatcher.Mode.VOLATILE);
    }

    public static <T extends FieldDescription> ElementMatcher.Junction<T> isTransient() {
        return ModifierMatcher.of(ModifierMatcher.Mode.TRANSIENT);
    }

    public static <T extends AnnotationDescription> ElementMatcher.Junction<T> annotationType(Class<? extends Annotation> type) {
        return ElementMatchers.annotationType(TypeDescription.ForLoadedType.of(type));
    }

    public static <T extends AnnotationDescription> ElementMatcher.Junction<T> annotationType(TypeDescription type) {
        return ElementMatchers.annotationType(ElementMatchers.is(type));
    }

    public static <T extends AnnotationDescription> ElementMatcher.Junction<T> annotationType(ElementMatcher<? super TypeDescription> matcher) {
        return new AnnotationTypeMatcher(matcher);
    }

    public static <T extends AnnotationDescription> ElementMatcher.Junction<T> targetsElement(ElementType elementType) {
        return new AnnotationTargetMatcher(elementType);
    }

    public static <T extends ClassLoader> ElementMatcher.Junction<T> isBootstrapClassLoader() {
        return NullMatcher.make();
    }

    public static <T extends ClassLoader> ElementMatcher.Junction<T> isSystemClassLoader() {
        return new EqualityMatcher(ClassLoader.getSystemClassLoader());
    }

    public static <T extends ClassLoader> ElementMatcher.Junction<T> isExtensionClassLoader() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader().getParent();
        return classLoader == null ? ElementMatchers.none() : new EqualityMatcher(classLoader);
    }

    public static <T extends ClassLoader> ElementMatcher.Junction<T> isChildOf(@MaybeNull ClassLoader classLoader) {
        return classLoader == ClassLoadingStrategy.BOOTSTRAP_LOADER ? BooleanMatcher.of(true) : ElementMatchers.hasChild(ElementMatchers.is(classLoader));
    }

    public static <T extends ClassLoader> ElementMatcher.Junction<T> hasChild(ElementMatcher<? super ClassLoader> matcher) {
        return new ClassLoaderHierarchyMatcher(matcher);
    }

    public static <T extends ClassLoader> ElementMatcher.Junction<T> isParentOf(@MaybeNull ClassLoader classLoader) {
        return classLoader == ClassLoadingStrategy.BOOTSTRAP_LOADER ? ElementMatchers.isBootstrapClassLoader() : new ClassLoaderParentMatcher(classLoader);
    }

    public static <T extends ClassLoader> ElementMatcher.Junction<T> ofType(ElementMatcher<? super TypeDescription> matcher) {
        return new InstanceTypeMatcher(matcher);
    }

    public static <T extends JavaModule> ElementMatcher.Junction<T> supportsModules() {
        return ElementMatchers.not(NullMatcher.make());
    }
}

