/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.context.expression.BeanFactoryResolver
 *  org.springframework.core.annotation.AnnotationAwareOrderComparator
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.expression.AccessException
 *  org.springframework.expression.BeanResolver
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.MethodExecutor
 *  org.springframework.expression.MethodResolver
 *  org.springframework.expression.PropertyAccessor
 *  org.springframework.expression.TypedValue
 *  org.springframework.expression.spel.SpelEvaluationException
 *  org.springframework.expression.spel.SpelMessage
 *  org.springframework.expression.spel.support.ReflectivePropertyAccessor
 *  org.springframework.expression.spel.support.StandardEvaluationContext
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.data.spel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.spel.EvaluationContextExtensionInformation;
import org.springframework.data.spel.EvaluationContextProvider;
import org.springframework.data.spel.ExpressionDependencies;
import org.springframework.data.spel.Functions;
import org.springframework.data.spel.spi.EvaluationContextExtension;
import org.springframework.data.spel.spi.ExtensionIdAware;
import org.springframework.data.spel.spi.Function;
import org.springframework.data.util.Lazy;
import org.springframework.data.util.Optionals;
import org.springframework.expression.AccessException;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.MethodExecutor;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class ExtensionAwareEvaluationContextProvider
implements EvaluationContextProvider {
    private final Map<String, EvaluationContextExtensionInformation> extensionInformationCache = new ConcurrentHashMap<String, EvaluationContextExtensionInformation>();
    private final Lazy<? extends Collection<? extends ExtensionIdAware>> extensions;
    private ListableBeanFactory beanFactory;

    ExtensionAwareEvaluationContextProvider() {
        this(Collections.emptyList());
    }

    public ExtensionAwareEvaluationContextProvider(ListableBeanFactory beanFactory) {
        this(Lazy.of(() -> beanFactory.getBeansOfType(ExtensionIdAware.class, true, false).values()));
        this.beanFactory = beanFactory;
    }

    public ExtensionAwareEvaluationContextProvider(Collection<? extends ExtensionIdAware> extensions) {
        this(Lazy.of(extensions));
    }

    public ExtensionAwareEvaluationContextProvider(Lazy<? extends Collection<? extends ExtensionIdAware>> extensions) {
        this.extensions = extensions;
    }

    public StandardEvaluationContext getEvaluationContext(Object rootObject) {
        return this.doGetEvaluationContext(rootObject, this.getExtensions(it -> true));
    }

    public StandardEvaluationContext getEvaluationContext(Object rootObject, ExpressionDependencies dependencies) {
        return this.doGetEvaluationContext(rootObject, this.getExtensions(it -> dependencies.stream().anyMatch(it::provides)));
    }

    StandardEvaluationContext doGetEvaluationContext(Object rootObject, Collection<? extends EvaluationContextExtension> extensions) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        if (this.beanFactory != null) {
            context.setBeanResolver((BeanResolver)new BeanFactoryResolver((BeanFactory)this.beanFactory));
        }
        ExtensionAwarePropertyAccessor accessor = new ExtensionAwarePropertyAccessor(extensions);
        context.addPropertyAccessor((PropertyAccessor)accessor);
        context.addPropertyAccessor((PropertyAccessor)new ReflectivePropertyAccessor());
        context.addMethodResolver((MethodResolver)accessor);
        if (rootObject != null) {
            context.setRootObject(rootObject);
        }
        return context;
    }

    Collection<? extends ExtensionIdAware> getExtensions() {
        return this.extensions.get();
    }

    private Collection<? extends EvaluationContextExtension> getExtensions(Predicate<EvaluationContextExtensionInformation> extensionFilter) {
        ArrayList<EvaluationContextExtension> extensionsToUse = new ArrayList<EvaluationContextExtension>();
        for (ExtensionIdAware extensionIdAware : this.getExtensions()) {
            EvaluationContextExtension extension;
            if (!(extensionIdAware instanceof EvaluationContextExtension) || !extensionFilter.test(this.getOrCreateInformation(extension = (EvaluationContextExtension)extensionIdAware))) continue;
            extensionsToUse.add(extension);
        }
        return extensionsToUse;
    }

    EvaluationContextExtensionInformation getOrCreateInformation(EvaluationContextExtension extension) {
        return this.getOrCreateInformation(extension.getClass());
    }

    EvaluationContextExtensionInformation getOrCreateInformation(Class<? extends EvaluationContextExtension> extension) {
        return this.extensionInformationCache.computeIfAbsent(ClassUtils.getUserClass(extension).getName(), type -> new EvaluationContextExtensionInformation(extension));
    }

    private List<EvaluationContextExtensionAdapter> toAdapters(Collection<? extends EvaluationContextExtension> extensions) {
        return extensions.stream().sorted((Comparator<? extends EvaluationContextExtension>)AnnotationAwareOrderComparator.INSTANCE).map(it -> new EvaluationContextExtensionAdapter((EvaluationContextExtension)it, this.getOrCreateInformation((EvaluationContextExtension)it))).collect(Collectors.toList());
    }

    private static class EvaluationContextExtensionAdapter {
        private final EvaluationContextExtension extension;
        private final Functions functions = new Functions();
        private final Map<String, Object> properties;

        public EvaluationContextExtensionAdapter(EvaluationContextExtension extension, EvaluationContextExtensionInformation information) {
            Assert.notNull((Object)extension, (String)"Extension must not be null!");
            Assert.notNull((Object)information, (String)"Extension information must not be null!");
            Optional<Object> target = Optional.ofNullable(extension.getRootObject());
            EvaluationContextExtensionInformation.ExtensionTypeInformation extensionTypeInformation = information.getExtensionTypeInformation();
            EvaluationContextExtensionInformation.RootObjectInformation rootObjectInformation = information.getRootObjectInformation(target);
            this.functions.addAll(extension.getFunctions());
            this.functions.addAll(rootObjectInformation.getFunctions(target));
            this.functions.addAll(extensionTypeInformation.getFunctions());
            this.properties = new HashMap<String, Object>();
            this.properties.putAll(extensionTypeInformation.getProperties());
            this.properties.putAll(rootObjectInformation.getProperties(target));
            this.properties.putAll(extension.getProperties());
            this.extension = extension;
        }

        String getExtensionId() {
            return this.extension.getExtensionId();
        }

        Functions getFunctions() {
            return this.functions;
        }

        public Map<String, Object> getProperties() {
            return this.properties;
        }

        public String toString() {
            return String.format("EvaluationContextExtensionAdapter for '%s'", this.getExtensionId());
        }
    }

    private static class FunctionMethodExecutor
    implements MethodExecutor {
        private final Function function;

        public FunctionMethodExecutor(Function function) {
            this.function = function;
        }

        public TypedValue execute(EvaluationContext context, Object target, Object ... arguments) throws AccessException {
            try {
                return new TypedValue(this.function.invoke(arguments));
            }
            catch (Exception e) {
                throw new SpelEvaluationException((Throwable)e, SpelMessage.FUNCTION_REFERENCE_CANNOT_BE_INVOKED, new Object[]{this.function.getName(), this.function.getDeclaringClass()});
            }
        }
    }

    class ExtensionAwarePropertyAccessor
    implements PropertyAccessor,
    MethodResolver {
        private final List<EvaluationContextExtensionAdapter> adapters;
        private final Map<String, EvaluationContextExtensionAdapter> adapterMap;

        public ExtensionAwarePropertyAccessor(Collection<? extends EvaluationContextExtension> extensions) {
            Assert.notNull(extensions, (String)"Extensions must not be null!");
            this.adapters = ExtensionAwareEvaluationContextProvider.this.toAdapters(extensions);
            this.adapterMap = this.adapters.stream().collect(Collectors.toMap(EvaluationContextExtensionAdapter::getExtensionId, it -> it));
            Collections.reverse(this.adapters);
        }

        public boolean canRead(EvaluationContext context, @Nullable Object target, String name) {
            if (target instanceof EvaluationContextExtension) {
                return true;
            }
            if (this.adapterMap.containsKey(name)) {
                return true;
            }
            return this.adapters.stream().anyMatch(it -> it.getProperties().containsKey(name));
        }

        public TypedValue read(EvaluationContext context, @Nullable Object target, String name) {
            if (target instanceof EvaluationContextExtensionAdapter) {
                return this.lookupPropertyFrom((EvaluationContextExtensionAdapter)target, name);
            }
            if (this.adapterMap.containsKey(name)) {
                return new TypedValue((Object)this.adapterMap.get(name));
            }
            return this.adapters.stream().filter(it -> it.getProperties().containsKey(name)).map(it -> this.lookupPropertyFrom((EvaluationContextExtensionAdapter)it, name)).findFirst().orElse(TypedValue.NULL);
        }

        @Nullable
        public MethodExecutor resolve(EvaluationContext context, @Nullable Object target, String name, List<TypeDescriptor> argumentTypes) {
            if (target instanceof EvaluationContextExtensionAdapter) {
                return this.getMethodExecutor((EvaluationContextExtensionAdapter)target, name, argumentTypes).orElse(null);
            }
            return this.adapters.stream().flatMap(it -> Optionals.toStream(this.getMethodExecutor((EvaluationContextExtensionAdapter)it, name, argumentTypes))).findFirst().orElse(null);
        }

        public boolean canWrite(EvaluationContext context, @Nullable Object target, String name) {
            return false;
        }

        public void write(EvaluationContext context, @Nullable Object target, String name, @Nullable Object newValue) {
        }

        @Nullable
        public Class<?>[] getSpecificTargetClasses() {
            return null;
        }

        private Optional<MethodExecutor> getMethodExecutor(EvaluationContextExtensionAdapter adapter, String name, List<TypeDescriptor> argumentTypes) {
            return adapter.getFunctions().get(name, argumentTypes).map(FunctionMethodExecutor::new);
        }

        private TypedValue lookupPropertyFrom(EvaluationContextExtensionAdapter extension, String name) {
            Object value = extension.getProperties().get(name);
            if (!(value instanceof Function)) {
                return new TypedValue(value);
            }
            Function function = (Function)value;
            try {
                return new TypedValue(function.invoke(new Object[0]));
            }
            catch (Exception e) {
                throw new SpelEvaluationException((Throwable)e, SpelMessage.FUNCTION_REFERENCE_CANNOT_BE_INVOKED, new Object[]{name, function.getDeclaringClass()});
            }
        }
    }
}

