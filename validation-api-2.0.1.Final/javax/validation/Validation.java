/*
 * Decompiled with CFR 0.152.
 */
package javax.validation;

import java.lang.ref.SoftReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.WeakHashMap;
import javax.validation.Configuration;
import javax.validation.NoProviderFoundException;
import javax.validation.ValidationException;
import javax.validation.ValidationProviderResolver;
import javax.validation.ValidatorFactory;
import javax.validation.bootstrap.GenericBootstrap;
import javax.validation.bootstrap.ProviderSpecificBootstrap;
import javax.validation.spi.BootstrapState;
import javax.validation.spi.ValidationProvider;

public class Validation {
    public static ValidatorFactory buildDefaultValidatorFactory() {
        return Validation.byDefaultProvider().configure().buildValidatorFactory();
    }

    public static GenericBootstrap byDefaultProvider() {
        return new GenericBootstrapImpl();
    }

    public static <T extends Configuration<T>, U extends ValidationProvider<T>> ProviderSpecificBootstrap<T> byProvider(Class<U> providerType) {
        return new ProviderSpecificBootstrapImpl(providerType);
    }

    private static void clearDefaultValidationProviderResolverCache() {
        GetValidationProviderListAction.clearCache();
    }

    private static class NewProviderInstance<T extends ValidationProvider<?>>
    implements PrivilegedAction<T> {
        private final Class<T> clazz;

        public static <T extends ValidationProvider<?>> NewProviderInstance<T> action(Class<T> clazz) {
            return new NewProviderInstance<T>(clazz);
        }

        private NewProviderInstance(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public T run() {
            try {
                return (T)((ValidationProvider)this.clazz.newInstance());
            }
            catch (IllegalAccessException | InstantiationException | RuntimeException e) {
                throw new ValidationException("Cannot instantiate provider type: " + this.clazz, e);
            }
        }
    }

    private static class GetValidationProviderListAction
    implements PrivilegedAction<List<ValidationProvider<?>>> {
        private static final GetValidationProviderListAction INSTANCE = new GetValidationProviderListAction();
        private final WeakHashMap<ClassLoader, SoftReference<List<ValidationProvider<?>>>> providersPerClassloader = new WeakHashMap();

        private GetValidationProviderListAction() {
        }

        public static synchronized List<ValidationProvider<?>> getValidationProviderList() {
            if (System.getSecurityManager() != null) {
                return (List)AccessController.doPrivileged(INSTANCE);
            }
            return INSTANCE.run();
        }

        public static synchronized void clearCache() {
            GetValidationProviderListAction.INSTANCE.providersPerClassloader.clear();
        }

        @Override
        public List<ValidationProvider<?>> run() {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            List<ValidationProvider<?>> cachedContextClassLoaderProviderList = this.getCachedValidationProviders(classloader);
            if (cachedContextClassLoaderProviderList != null) {
                return cachedContextClassLoaderProviderList;
            }
            List<ValidationProvider<?>> validationProviderList = this.loadProviders(classloader);
            if (validationProviderList.isEmpty()) {
                classloader = DefaultValidationProviderResolver.class.getClassLoader();
                List<ValidationProvider<?>> cachedCurrentClassLoaderProviderList = this.getCachedValidationProviders(classloader);
                if (cachedCurrentClassLoaderProviderList != null) {
                    return cachedCurrentClassLoaderProviderList;
                }
                validationProviderList = this.loadProviders(classloader);
            }
            this.cacheValidationProviders(classloader, validationProviderList);
            return validationProviderList;
        }

        private List<ValidationProvider<?>> loadProviders(ClassLoader classloader) {
            ServiceLoader<ValidationProvider> loader = ServiceLoader.load(ValidationProvider.class, classloader);
            Iterator<ValidationProvider> providerIterator = loader.iterator();
            ArrayList validationProviderList = new ArrayList();
            while (providerIterator.hasNext()) {
                try {
                    validationProviderList.add(providerIterator.next());
                }
                catch (ServiceConfigurationError serviceConfigurationError) {}
            }
            return validationProviderList;
        }

        private synchronized List<ValidationProvider<?>> getCachedValidationProviders(ClassLoader classLoader) {
            SoftReference<List<ValidationProvider<?>>> ref = this.providersPerClassloader.get(classLoader);
            return ref != null ? ref.get() : null;
        }

        private synchronized void cacheValidationProviders(ClassLoader classLoader, List<ValidationProvider<?>> providers) {
            this.providersPerClassloader.put(classLoader, new SoftReference(providers));
        }
    }

    private static class DefaultValidationProviderResolver
    implements ValidationProviderResolver {
        private DefaultValidationProviderResolver() {
        }

        @Override
        public List<ValidationProvider<?>> getValidationProviders() {
            return GetValidationProviderListAction.getValidationProviderList();
        }
    }

    private static class GenericBootstrapImpl
    implements GenericBootstrap,
    BootstrapState {
        private ValidationProviderResolver resolver;
        private ValidationProviderResolver defaultResolver;

        private GenericBootstrapImpl() {
        }

        @Override
        public GenericBootstrap providerResolver(ValidationProviderResolver resolver) {
            this.resolver = resolver;
            return this;
        }

        @Override
        public ValidationProviderResolver getValidationProviderResolver() {
            return this.resolver;
        }

        @Override
        public ValidationProviderResolver getDefaultValidationProviderResolver() {
            if (this.defaultResolver == null) {
                this.defaultResolver = new DefaultValidationProviderResolver();
            }
            return this.defaultResolver;
        }

        @Override
        public Configuration<?> configure() {
            Configuration<?> config;
            List<ValidationProvider<?>> validationProviders;
            ValidationProviderResolver resolver = this.resolver == null ? this.getDefaultValidationProviderResolver() : this.resolver;
            try {
                validationProviders = resolver.getValidationProviders();
            }
            catch (ValidationException e) {
                throw e;
            }
            catch (RuntimeException re) {
                throw new ValidationException("Unable to get available provider resolvers.", re);
            }
            if (validationProviders.isEmpty()) {
                String msg = "Unable to create a Configuration, because no Bean Validation provider could be found. Add a provider like Hibernate Validator (RI) to your classpath.";
                throw new NoProviderFoundException(msg);
            }
            try {
                config = resolver.getValidationProviders().get(0).createGenericConfiguration(this);
            }
            catch (RuntimeException re) {
                throw new ValidationException("Unable to instantiate Configuration.", re);
            }
            return config;
        }
    }

    private static class ProviderSpecificBootstrapImpl<T extends Configuration<T>, U extends ValidationProvider<T>>
    implements ProviderSpecificBootstrap<T> {
        private final Class<U> validationProviderClass;
        private ValidationProviderResolver resolver;

        public ProviderSpecificBootstrapImpl(Class<U> validationProviderClass) {
            this.validationProviderClass = validationProviderClass;
        }

        @Override
        public ProviderSpecificBootstrap<T> providerResolver(ValidationProviderResolver resolver) {
            this.resolver = resolver;
            return this;
        }

        @Override
        public T configure() {
            List<ValidationProvider<?>> resolvers;
            if (this.validationProviderClass == null) {
                throw new ValidationException("builder is mandatory. Use Validation.byDefaultProvider() to use the generic provider discovery mechanism");
            }
            GenericBootstrapImpl state = new GenericBootstrapImpl();
            if (this.resolver == null) {
                ValidationProvider provider = (ValidationProvider)this.run(NewProviderInstance.action(this.validationProviderClass));
                return provider.createSpecializedConfiguration(state);
            }
            state.providerResolver(this.resolver);
            try {
                resolvers = this.resolver.getValidationProviders();
            }
            catch (RuntimeException re) {
                throw new ValidationException("Unable to get available provider resolvers.", re);
            }
            for (ValidationProvider<?> provider : resolvers) {
                if (!this.validationProviderClass.isAssignableFrom(provider.getClass())) continue;
                ValidationProvider specificProvider = (ValidationProvider)this.validationProviderClass.cast(provider);
                return specificProvider.createSpecializedConfiguration(state);
            }
            throw new ValidationException("Unable to find provider: " + this.validationProviderClass);
        }

        private <P> P run(PrivilegedAction<P> action) {
            return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
        }
    }
}

