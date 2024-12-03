/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanUtils
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.ReflectionUtils$FieldFilter
 *  org.springframework.util.ReflectionUtils$MethodFilter
 */
package org.springframework.data.spel;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.beans.BeanUtils;
import org.springframework.data.spel.ExpressionDependencies;
import org.springframework.data.spel.spi.EvaluationContextExtension;
import org.springframework.data.spel.spi.Function;
import org.springframework.data.util.StreamUtils;
import org.springframework.data.util.Streamable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;

class EvaluationContextExtensionInformation {
    private final ExtensionTypeInformation extensionTypeInformation;
    private final Optional<RootObjectInformation> rootObjectInformation;

    public EvaluationContextExtensionInformation(Class<? extends EvaluationContextExtension> type) {
        Assert.notNull(type, (String)"Extension type must not be null!");
        Class<?> rootObjectType = org.springframework.data.util.ReflectionUtils.findRequiredMethod(type, "getRootObject", new Class[0]).getReturnType();
        this.rootObjectInformation = Optional.ofNullable(Object.class.equals(rootObjectType) ? null : new RootObjectInformation(rootObjectType));
        this.extensionTypeInformation = new ExtensionTypeInformation(type);
    }

    public ExtensionTypeInformation getExtensionTypeInformation() {
        return this.extensionTypeInformation;
    }

    public RootObjectInformation getRootObjectInformation(Optional<Object> target) {
        return target.map(it -> this.rootObjectInformation.orElseGet(() -> new RootObjectInformation(it.getClass()))).orElse(RootObjectInformation.NONE);
    }

    public boolean provides(ExpressionDependencies.ExpressionDependency dependency) {
        if (!this.rootObjectInformation.isPresent()) {
            return true;
        }
        if (this.rootObjectInformation.filter(it -> it.provides(dependency)).isPresent()) {
            return true;
        }
        return this.extensionTypeInformation.provides(dependency);
    }

    private static Map<String, Object> discoverDeclaredProperties(Class<?> type) {
        HashMap map = new HashMap();
        ReflectionUtils.doWithFields(type, field -> map.put(field.getName(), field.get(null)), (ReflectionUtils.FieldFilter)ExtensionTypeInformation.PublicMethodAndFieldFilter.STATIC);
        return map.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(map);
    }

    static class Methods {
        private final Collection<Method> methods = new HashSet<Method>();
        private final Set<String> methodNames = new HashSet<String>();

        Methods() {
        }

        void add(Method method) {
            this.methods.add(method);
            this.methodNames.add(method.getName());
        }

        boolean containsMethodName(String name) {
            return this.methodNames.contains(name);
        }

        public Stream<Method> stream() {
            return this.methods.stream();
        }
    }

    static class RootObjectInformation {
        private static final RootObjectInformation NONE = new RootObjectInformation(Object.class);
        private final Map<String, Method> accessors;
        private final Methods methods;
        private final Collection<Field> fields;

        RootObjectInformation(Class<?> type) {
            Assert.notNull(type, (String)"Type must not be null!");
            this.accessors = new HashMap<String, Method>();
            this.methods = new Methods();
            this.fields = new ArrayList<Field>();
            if (Object.class.equals(type)) {
                return;
            }
            Streamable<PropertyDescriptor> descriptors = Streamable.of(BeanUtils.getPropertyDescriptors(type));
            ReflectionUtils.doWithMethods(type, method -> {
                this.methods.add(method);
                descriptors.stream().filter(it -> method.equals(it.getReadMethod())).forEach(it -> this.accessors.put(it.getName(), method));
            }, (ReflectionUtils.MethodFilter)ExtensionTypeInformation.PublicMethodAndFieldFilter.NON_STATIC);
            ReflectionUtils.doWithFields(type, this.fields::add, (ReflectionUtils.FieldFilter)ExtensionTypeInformation.PublicMethodAndFieldFilter.NON_STATIC);
        }

        MultiValueMap<String, Function> getFunctions(Optional<Object> target) {
            return target.map(this::getFunctions).orElseGet(LinkedMultiValueMap::new);
        }

        private MultiValueMap<String, Function> getFunctions(Object target) {
            return this.methods.stream().collect(StreamUtils.toMultiMap(Method::getName, m -> new Function((Method)m, target)));
        }

        Map<String, Object> getProperties(Optional<Object> target) {
            return target.map(it -> {
                HashMap properties = new HashMap();
                this.accessors.entrySet().stream().forEach(method -> properties.put(method.getKey(), new Function((Method)method.getValue(), it)));
                this.fields.stream().forEach(field -> properties.put(field.getName(), ReflectionUtils.getField((Field)field, (Object)it)));
                return Collections.unmodifiableMap(properties);
            }).orElseGet(Collections::emptyMap);
        }

        boolean provides(ExpressionDependencies.ExpressionDependency dependency) {
            if (!dependency.isMethod() && !dependency.isPropertyOrField()) {
                return false;
            }
            if (dependency.isPropertyOrField()) {
                if (this.accessors.containsKey(dependency.getSymbol())) {
                    return true;
                }
                for (Field field : this.fields) {
                    if (!field.getName().equals(dependency.getSymbol())) continue;
                    return true;
                }
                return false;
            }
            if (dependency.isMethod()) {
                return this.methods.containsMethodName(dependency.getSymbol());
            }
            return false;
        }
    }

    public static class ExtensionTypeInformation {
        private final Map<String, Object> properties;
        private final MultiValueMap<String, Function> functions;

        public ExtensionTypeInformation(Class<? extends EvaluationContextExtension> type) {
            Assert.notNull(type, (String)"Extension type must not be null!");
            this.functions = ExtensionTypeInformation.discoverDeclaredFunctions(type);
            this.properties = EvaluationContextExtensionInformation.discoverDeclaredProperties(type);
        }

        public boolean provides(ExpressionDependencies.ExpressionDependency dependency) {
            if (dependency.isPropertyOrField()) {
                return this.properties.containsKey(dependency.getSymbol());
            }
            if (dependency.isMethod()) {
                return this.functions.containsKey((Object)dependency.getSymbol());
            }
            return false;
        }

        private static MultiValueMap<String, Function> discoverDeclaredFunctions(Class<?> type) {
            MultiValueMap map = CollectionUtils.toMultiValueMap(new HashMap());
            ReflectionUtils.doWithMethods(type, method -> map.add((Object)method.getName(), (Object)new Function(method, null)), (ReflectionUtils.MethodFilter)PublicMethodAndFieldFilter.STATIC);
            return CollectionUtils.unmodifiableMultiValueMap((MultiValueMap)map);
        }

        public Map<String, Object> getProperties() {
            return this.properties;
        }

        public MultiValueMap<String, Function> getFunctions() {
            return this.functions;
        }

        static class PublicMethodAndFieldFilter
        implements ReflectionUtils.MethodFilter,
        ReflectionUtils.FieldFilter {
            static final PublicMethodAndFieldFilter STATIC = new PublicMethodAndFieldFilter(true);
            static final PublicMethodAndFieldFilter NON_STATIC = new PublicMethodAndFieldFilter(false);
            private final boolean staticOnly;

            PublicMethodAndFieldFilter(boolean staticOnly) {
                this.staticOnly = staticOnly;
            }

            public boolean matches(Method method) {
                if (ReflectionUtils.isObjectMethod((Method)method)) {
                    return false;
                }
                boolean methodStatic = Modifier.isStatic(method.getModifiers());
                boolean staticMatch = this.staticOnly ? methodStatic : !methodStatic;
                return Modifier.isPublic(method.getModifiers()) && staticMatch;
            }

            public boolean matches(Field field) {
                boolean fieldStatic = Modifier.isStatic(field.getModifiers());
                boolean staticMatch = this.staticOnly ? fieldStatic : !fieldStatic;
                return Modifier.isPublic(field.getModifiers()) && staticMatch;
            }
        }
    }
}

