/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Lists
 *  javax.inject.Inject
 */
package com.google.inject.spi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.ConfigurationException;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Annotations;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.MoreTypes;
import com.google.inject.internal.Nullability;
import com.google.inject.internal.util.Classes;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.Toolable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class InjectionPoint {
    private static final Logger logger = Logger.getLogger(InjectionPoint.class.getName());
    private final boolean optional;
    private final Member member;
    private final TypeLiteral<?> declaringType;
    private final ImmutableList<Dependency<?>> dependencies;

    InjectionPoint(TypeLiteral<?> declaringType, Method method, boolean optional) {
        this.member = method;
        this.declaringType = declaringType;
        this.optional = optional;
        this.dependencies = this.forMember(method, declaringType, method.getParameterAnnotations());
    }

    InjectionPoint(TypeLiteral<?> declaringType, Constructor<?> constructor) {
        this.member = constructor;
        this.declaringType = declaringType;
        this.optional = false;
        this.dependencies = this.forMember(constructor, declaringType, constructor.getParameterAnnotations());
    }

    InjectionPoint(TypeLiteral<?> declaringType, Field field, boolean optional) {
        this.member = field;
        this.declaringType = declaringType;
        this.optional = optional;
        Annotation[] annotations = field.getAnnotations();
        Errors errors = new Errors(field);
        Key<?> key = null;
        try {
            key = Annotations.getKey(declaringType.getFieldType(field), field, annotations, errors);
        }
        catch (ConfigurationException e) {
            errors.merge(e.getErrorMessages());
        }
        catch (ErrorsException e) {
            errors.merge(e.getErrors());
        }
        errors.throwConfigurationExceptionIfErrorsExist();
        this.dependencies = ImmutableList.of(this.newDependency(key, Nullability.allowsNull(annotations), -1));
    }

    private ImmutableList<Dependency<?>> forMember(Member member, TypeLiteral<?> type, Annotation[][] paramterAnnotations) {
        Errors errors = new Errors(member);
        Iterator annotationsIterator = Arrays.asList(paramterAnnotations).iterator();
        ArrayList dependencies = Lists.newArrayList();
        int index = 0;
        for (TypeLiteral<?> parameterType : type.getParameterTypes(member)) {
            try {
                Annotation[] parameterAnnotations = (Annotation[])annotationsIterator.next();
                Key<?> key = Annotations.getKey(parameterType, member, parameterAnnotations, errors);
                dependencies.add(this.newDependency(key, Nullability.allowsNull(parameterAnnotations), index));
                ++index;
            }
            catch (ConfigurationException e) {
                errors.merge(e.getErrorMessages());
            }
            catch (ErrorsException e) {
                errors.merge(e.getErrors());
            }
        }
        errors.throwConfigurationExceptionIfErrorsExist();
        return ImmutableList.copyOf((Collection)dependencies);
    }

    private <T> Dependency<T> newDependency(Key<T> key, boolean allowsNull, int parameterIndex) {
        return new Dependency<T>(this, key, allowsNull, parameterIndex);
    }

    public Member getMember() {
        return this.member;
    }

    public List<Dependency<?>> getDependencies() {
        return this.dependencies;
    }

    public boolean isOptional() {
        return this.optional;
    }

    public boolean isToolable() {
        return ((AnnotatedElement)((Object)this.member)).isAnnotationPresent(Toolable.class);
    }

    public TypeLiteral<?> getDeclaringType() {
        return this.declaringType;
    }

    public boolean equals(Object o) {
        return o instanceof InjectionPoint && this.member.equals(((InjectionPoint)o).member) && this.declaringType.equals(((InjectionPoint)o).declaringType);
    }

    public int hashCode() {
        return this.member.hashCode() ^ this.declaringType.hashCode();
    }

    public String toString() {
        return Classes.toString(this.member);
    }

    public static <T> InjectionPoint forConstructor(Constructor<T> constructor) {
        return new InjectionPoint(TypeLiteral.get(constructor.getDeclaringClass()), constructor);
    }

    public static <T> InjectionPoint forConstructor(Constructor<T> constructor, TypeLiteral<? extends T> type) {
        if (type.getRawType() != constructor.getDeclaringClass()) {
            new Errors(type).constructorNotDefinedByType(constructor, type).throwConfigurationExceptionIfErrorsExist();
        }
        return new InjectionPoint(type, constructor);
    }

    public static InjectionPoint forConstructorOf(TypeLiteral<?> type) {
        Class<?> rawType = MoreTypes.getRawType(type.getType());
        Errors errors = new Errors(rawType);
        Constructor<?> injectableConstructor = null;
        for (Constructor<?> constructor : rawType.getDeclaredConstructors()) {
            boolean optional;
            Inject guiceInject = constructor.getAnnotation(Inject.class);
            if (guiceInject == null) {
                javax.inject.Inject javaxInject = constructor.getAnnotation(javax.inject.Inject.class);
                if (javaxInject == null) continue;
                optional = false;
            } else {
                optional = guiceInject.optional();
            }
            if (optional) {
                errors.optionalConstructor(constructor);
            }
            if (injectableConstructor != null) {
                errors.tooManyConstructors(rawType);
            }
            injectableConstructor = constructor;
            InjectionPoint.checkForMisplacedBindingAnnotations(injectableConstructor, errors);
        }
        errors.throwConfigurationExceptionIfErrorsExist();
        if (injectableConstructor != null) {
            return new InjectionPoint(type, injectableConstructor);
        }
        try {
            Constructor<?> noArgConstructor = rawType.getDeclaredConstructor(new Class[0]);
            if (Modifier.isPrivate(noArgConstructor.getModifiers()) && !Modifier.isPrivate(rawType.getModifiers())) {
                errors.missingConstructor(rawType);
                throw new ConfigurationException(errors.getMessages());
            }
            InjectionPoint.checkForMisplacedBindingAnnotations(noArgConstructor, errors);
            return new InjectionPoint(type, noArgConstructor);
        }
        catch (NoSuchMethodException e) {
            errors.missingConstructor(rawType);
            throw new ConfigurationException(errors.getMessages());
        }
    }

    public static InjectionPoint forConstructorOf(Class<?> type) {
        return InjectionPoint.forConstructorOf(TypeLiteral.get(type));
    }

    public static Set<InjectionPoint> forStaticMethodsAndFields(TypeLiteral<?> type) {
        Set<InjectionPoint> result;
        Errors errors = new Errors();
        if (type.getRawType().isInterface()) {
            errors.staticInjectionOnInterface(type.getRawType());
            result = null;
        } else {
            result = InjectionPoint.getInjectionPoints(type, true, errors);
        }
        if (errors.hasErrors()) {
            throw new ConfigurationException(errors.getMessages()).withPartialValue(result);
        }
        return result;
    }

    public static Set<InjectionPoint> forStaticMethodsAndFields(Class<?> type) {
        return InjectionPoint.forStaticMethodsAndFields(TypeLiteral.get(type));
    }

    public static Set<InjectionPoint> forInstanceMethodsAndFields(TypeLiteral<?> type) {
        Errors errors = new Errors();
        Set<InjectionPoint> result = InjectionPoint.getInjectionPoints(type, false, errors);
        if (errors.hasErrors()) {
            throw new ConfigurationException(errors.getMessages()).withPartialValue(result);
        }
        return result;
    }

    public static Set<InjectionPoint> forInstanceMethodsAndFields(Class<?> type) {
        return InjectionPoint.forInstanceMethodsAndFields(TypeLiteral.get(type));
    }

    private static boolean checkForMisplacedBindingAnnotations(Member member, Errors errors) {
        Annotation misplacedBindingAnnotation = Annotations.findBindingAnnotation(errors, member, ((AnnotatedElement)((Object)member)).getAnnotations());
        if (misplacedBindingAnnotation == null) {
            return false;
        }
        if (member instanceof Method) {
            try {
                if (member.getDeclaringClass().getDeclaredField(member.getName()) != null) {
                    return false;
                }
            }
            catch (NoSuchFieldException noSuchFieldException) {
                // empty catch block
            }
        }
        errors.misplacedBindingAnnotation(member, misplacedBindingAnnotation);
        return true;
    }

    static Annotation getAtInject(AnnotatedElement member) {
        javax.inject.Inject a = member.getAnnotation(javax.inject.Inject.class);
        return a == null ? member.getAnnotation(Inject.class) : a;
    }

    private static Set<InjectionPoint> getInjectionPoints(TypeLiteral<?> type, boolean statics, Errors errors) {
        int topIndex;
        InjectableMembers injectableMembers = new InjectableMembers();
        OverrideIndex overrideIndex = null;
        List<TypeLiteral<?>> hierarchy = InjectionPoint.hierarchyFor(type);
        for (int i = topIndex = hierarchy.size() - 1; i >= 0; --i) {
            Annotation atInject;
            if (overrideIndex != null && i < topIndex) {
                overrideIndex.position = i == 0 ? Position.BOTTOM : Position.MIDDLE;
            }
            TypeLiteral<?> current = hierarchy.get(i);
            for (Field field : current.getRawType().getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) != statics || (atInject = InjectionPoint.getAtInject(field)) == null) continue;
                InjectableField injectableField = new InjectableField(current, field, atInject);
                if (injectableField.jsr330 && Modifier.isFinal(field.getModifiers())) {
                    errors.cannotInjectFinalField(field);
                }
                injectableMembers.add(injectableField);
            }
            for (AccessibleObject accessibleObject : current.getRawType().getDeclaredMethods()) {
                boolean removed;
                if (Modifier.isStatic(((Method)accessibleObject).getModifiers()) != statics) continue;
                atInject = InjectionPoint.getAtInject(accessibleObject);
                if (atInject != null) {
                    InjectableMethod injectableMethod = new InjectableMethod(current, (Method)accessibleObject, atInject);
                    if (InjectionPoint.checkForMisplacedBindingAnnotations((Member)((Object)accessibleObject), errors) || !InjectionPoint.isValidMethod(injectableMethod, errors)) {
                        boolean removed2;
                        if (overrideIndex == null || !(removed2 = overrideIndex.removeIfOverriddenBy((Method)accessibleObject, false, injectableMethod))) continue;
                        logger.log(Level.WARNING, "Method: {0} is not a valid injectable method (because it either has misplaced binding annotations or specifies type parameters) but is overriding a method that is valid. Because it is not valid, the method will not be injected. To fix this, make the method a valid injectable method.", accessibleObject);
                        continue;
                    }
                    if (statics) {
                        injectableMembers.add(injectableMethod);
                        continue;
                    }
                    if (overrideIndex == null) {
                        overrideIndex = new OverrideIndex(injectableMembers);
                    } else {
                        overrideIndex.removeIfOverriddenBy((Method)accessibleObject, true, injectableMethod);
                    }
                    overrideIndex.add(injectableMethod);
                    continue;
                }
                if (overrideIndex == null || !(removed = overrideIndex.removeIfOverriddenBy((Method)accessibleObject, false, null))) continue;
                logger.log(Level.WARNING, "Method: {0} is not annotated with @Inject but is overriding a method that is annotated with @javax.inject.Inject.  Because it is not annotated with @Inject, the method will not be injected. To fix this, annotate the method with @Inject.", accessibleObject);
            }
        }
        if (injectableMembers.isEmpty()) {
            return Collections.emptySet();
        }
        ImmutableSet.Builder builder = ImmutableSet.builder();
        InjectableMember im = injectableMembers.head;
        while (im != null) {
            block14: {
                try {
                    builder.add((Object)im.toInjectionPoint());
                }
                catch (ConfigurationException ignorable) {
                    if (im.optional) break block14;
                    errors.merge(ignorable.getErrorMessages());
                }
            }
            im = im.next;
        }
        return builder.build();
    }

    private static boolean isValidMethod(InjectableMethod injectableMethod, Errors errors) {
        boolean result = true;
        if (injectableMethod.jsr330) {
            Method method = injectableMethod.method;
            if (Modifier.isAbstract(method.getModifiers())) {
                errors.cannotInjectAbstractMethod(method);
                result = false;
            }
            if (method.getTypeParameters().length > 0) {
                errors.cannotInjectMethodWithTypeParameters(method);
                result = false;
            }
        }
        return result;
    }

    private static List<TypeLiteral<?>> hierarchyFor(TypeLiteral<?> type) {
        ArrayList hierarchy = new ArrayList();
        TypeLiteral<?> current = type;
        while (current.getRawType() != Object.class) {
            hierarchy.add(current);
            current = current.getSupertype(current.getRawType().getSuperclass());
        }
        return hierarchy;
    }

    private static boolean overrides(Method a, Method b) {
        int modifiers = b.getModifiers();
        if (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers)) {
            return true;
        }
        if (Modifier.isPrivate(modifiers)) {
            return false;
        }
        return a.getDeclaringClass().getPackage().equals(b.getDeclaringClass().getPackage());
    }

    static class Signature {
        final String name;
        final Class[] parameterTypes;
        final int hash;

        Signature(Method method) {
            this.name = method.getName();
            this.parameterTypes = method.getParameterTypes();
            int h = this.name.hashCode();
            h = h * 31 + this.parameterTypes.length;
            for (Class parameterType : this.parameterTypes) {
                h = h * 31 + parameterType.hashCode();
            }
            this.hash = h;
        }

        public int hashCode() {
            return this.hash;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Signature)) {
                return false;
            }
            Signature other = (Signature)o;
            if (!this.name.equals(other.name)) {
                return false;
            }
            if (this.parameterTypes.length != other.parameterTypes.length) {
                return false;
            }
            for (int i = 0; i < this.parameterTypes.length; ++i) {
                if (this.parameterTypes[i] == other.parameterTypes[i]) continue;
                return false;
            }
            return true;
        }
    }

    static class OverrideIndex {
        final InjectableMembers injectableMembers;
        Map<Signature, List<InjectableMethod>> bySignature;
        Position position = Position.TOP;
        Method lastMethod;
        Signature lastSignature;

        OverrideIndex(InjectableMembers injectableMembers) {
            this.injectableMembers = injectableMembers;
        }

        boolean removeIfOverriddenBy(Method method, boolean alwaysRemove, InjectableMethod injectableMethod) {
            if (this.position == Position.TOP) {
                return false;
            }
            if (this.bySignature == null) {
                this.bySignature = new HashMap<Signature, List<InjectableMethod>>();
                InjectableMember member = this.injectableMembers.head;
                while (member != null) {
                    InjectableMethod im;
                    if (member instanceof InjectableMethod && !(im = (InjectableMethod)member).isFinal()) {
                        ArrayList<InjectableMethod> methods = new ArrayList<InjectableMethod>();
                        methods.add(im);
                        this.bySignature.put(new Signature(im.method), methods);
                    }
                    member = member.next;
                }
            }
            this.lastMethod = method;
            Signature signature = this.lastSignature = new Signature(method);
            List<InjectableMethod> methods = this.bySignature.get(signature);
            boolean removed = false;
            if (methods != null) {
                Iterator<InjectableMethod> iterator = methods.iterator();
                while (iterator.hasNext()) {
                    boolean wasGuiceInject;
                    InjectableMethod possiblyOverridden = iterator.next();
                    if (!InjectionPoint.overrides(method, possiblyOverridden.method)) continue;
                    boolean bl = wasGuiceInject = !possiblyOverridden.jsr330 || possiblyOverridden.overrodeGuiceInject;
                    if (injectableMethod != null) {
                        injectableMethod.overrodeGuiceInject = wasGuiceInject;
                    }
                    if (!alwaysRemove && wasGuiceInject) continue;
                    removed = true;
                    iterator.remove();
                    this.injectableMembers.remove(possiblyOverridden);
                }
            }
            return removed;
        }

        void add(InjectableMethod injectableMethod) {
            this.injectableMembers.add(injectableMethod);
            if (this.position == Position.BOTTOM || injectableMethod.isFinal()) {
                return;
            }
            if (this.bySignature != null) {
                Signature signature = injectableMethod.method == this.lastMethod ? this.lastSignature : new Signature(injectableMethod.method);
                List<InjectableMethod> methods = this.bySignature.get(signature);
                if (methods == null) {
                    methods = new ArrayList<InjectableMethod>();
                    this.bySignature.put(signature, methods);
                }
                methods.add(injectableMethod);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static enum Position {
        TOP,
        MIDDLE,
        BOTTOM;

    }

    static class InjectableMembers {
        InjectableMember head;
        InjectableMember tail;

        InjectableMembers() {
        }

        void add(InjectableMember member) {
            if (this.head == null) {
                this.head = this.tail = member;
            } else {
                member.previous = this.tail;
                this.tail.next = member;
                this.tail = member;
            }
        }

        void remove(InjectableMember member) {
            if (member.previous != null) {
                member.previous.next = member.next;
            }
            if (member.next != null) {
                member.next.previous = member.previous;
            }
            if (this.head == member) {
                this.head = member.next;
            }
            if (this.tail == member) {
                this.tail = member.previous;
            }
        }

        boolean isEmpty() {
            return this.head == null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class InjectableMethod
    extends InjectableMember {
        final Method method;
        boolean overrodeGuiceInject;

        InjectableMethod(TypeLiteral<?> declaringType, Method method, Annotation atInject) {
            super(declaringType, atInject);
            this.method = method;
        }

        @Override
        InjectionPoint toInjectionPoint() {
            return new InjectionPoint(this.declaringType, this.method, this.optional);
        }

        public boolean isFinal() {
            return Modifier.isFinal(this.method.getModifiers());
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class InjectableField
    extends InjectableMember {
        final Field field;

        InjectableField(TypeLiteral<?> declaringType, Field field, Annotation atInject) {
            super(declaringType, atInject);
            this.field = field;
        }

        @Override
        InjectionPoint toInjectionPoint() {
            return new InjectionPoint(this.declaringType, this.field, this.optional);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static abstract class InjectableMember {
        final TypeLiteral<?> declaringType;
        final boolean optional;
        final boolean jsr330;
        InjectableMember previous;
        InjectableMember next;

        InjectableMember(TypeLiteral<?> declaringType, Annotation atInject) {
            this.declaringType = declaringType;
            if (atInject.annotationType() == javax.inject.Inject.class) {
                this.optional = false;
                this.jsr330 = true;
                return;
            }
            this.jsr330 = false;
            this.optional = ((Inject)atInject).optional();
        }

        abstract InjectionPoint toInjectionPoint();
    }
}

