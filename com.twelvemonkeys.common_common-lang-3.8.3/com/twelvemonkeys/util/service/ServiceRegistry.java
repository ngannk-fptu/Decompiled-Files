/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util.service;

import com.twelvemonkeys.lang.Validate;
import com.twelvemonkeys.util.FilterIterator;
import com.twelvemonkeys.util.service.RegisterableService;
import com.twelvemonkeys.util.service.ServiceConfigurationError;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.spi.CharsetProvider;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageReaderWriterSpi;
import javax.imageio.spi.ImageWriterSpi;

public class ServiceRegistry {
    public static final String SERVICES = "META-INF/services/";
    private final Map<Class<?>, CategoryRegistry> categoryMap;

    public ServiceRegistry(Iterator<? extends Class<?>> iterator) {
        Validate.notNull(iterator, "categories");
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        while (iterator.hasNext()) {
            this.putCategory(linkedHashMap, iterator.next());
        }
        this.categoryMap = Collections.unmodifiableMap(linkedHashMap);
    }

    private <T> void putCategory(Map<Class<?>, CategoryRegistry> map, Class<T> clazz) {
        CategoryRegistry<T> categoryRegistry = new CategoryRegistry<T>(clazz);
        map.put(clazz, categoryRegistry);
    }

    public void registerApplicationClasspathSPIs() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Iterator<Class<?>> iterator = this.categories();
        while (iterator.hasNext()) {
            Class<?> clazz = iterator.next();
            try {
                String string = SERVICES + clazz.getName();
                Enumeration<URL> enumeration = classLoader.getResources(string);
                while (enumeration.hasMoreElements()) {
                    URL uRL = enumeration.nextElement();
                    this.registerSPIs(uRL, clazz, classLoader);
                }
            }
            catch (IOException iOException) {
                throw new ServiceConfigurationError(iOException);
            }
        }
    }

    <T> void registerSPIs(URL uRL, Class<T> clazz, ClassLoader classLoader) {
        Properties properties = new Properties();
        try {
            properties.load(uRL.openStream());
        }
        catch (IOException iOException) {
            throw new ServiceConfigurationError(iOException);
        }
        if (!properties.isEmpty()) {
            CategoryRegistry categoryRegistry = this.categoryMap.get(clazz);
            Set<Object> set = properties.keySet();
            for (Object object : set) {
                String string = (String)object;
                try {
                    Class<?> clazz2 = Class.forName(string, true, classLoader);
                    Object obj = clazz2.newInstance();
                    categoryRegistry.register(obj);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    throw new ServiceConfigurationError(classNotFoundException);
                }
                catch (IllegalAccessException illegalAccessException) {
                    throw new ServiceConfigurationError(illegalAccessException);
                }
                catch (InstantiationException instantiationException) {
                    throw new ServiceConfigurationError(instantiationException);
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    throw new ServiceConfigurationError(illegalArgumentException);
                }
            }
        }
    }

    protected <T> Iterator<T> providers(Class<T> clazz) {
        return this.getRegistry(clazz).providers();
    }

    protected Iterator<Class<?>> categories() {
        return this.categoryMap.keySet().iterator();
    }

    protected Iterator<Class<?>> compatibleCategories(final Object object) {
        return new FilterIterator(this.categories(), new FilterIterator.Filter<Class<?>>(){

            @Override
            public boolean accept(Class<?> clazz) {
                return clazz.isInstance(object);
            }
        });
    }

    protected Iterator<Class<?>> containingCategories(final Object object) {
        return new FilterIterator<Class<?>>(this.categories(), new FilterIterator.Filter<Class<?>>(){

            @Override
            public boolean accept(Class<?> clazz) {
                return ServiceRegistry.this.getRegistry(clazz).contains(object);
            }
        }){
            Class<?> current;

            @Override
            public Class next() {
                this.current = (Class)super.next();
                return this.current;
            }

            @Override
            public void remove() {
                if (this.current == null) {
                    throw new IllegalStateException("No current element");
                }
                ServiceRegistry.this.getRegistry(this.current).deregister(object);
                this.current = null;
            }
        };
    }

    private <T> CategoryRegistry<T> getRegistry(Class<T> clazz) {
        CategoryRegistry categoryRegistry = this.categoryMap.get(clazz);
        if (categoryRegistry == null) {
            throw new IllegalArgumentException("No such category: " + clazz.getName());
        }
        return categoryRegistry;
    }

    public boolean register(Object object) {
        Iterator<Class<?>> iterator = this.compatibleCategories(object);
        boolean bl = false;
        while (iterator.hasNext()) {
            Class<?> clazz = iterator.next();
            if (!this.registerImpl(object, clazz) || bl) continue;
            bl = true;
        }
        return bl;
    }

    private <T> boolean registerImpl(Object object, Class<T> clazz) {
        return this.getRegistry(clazz).register(clazz.cast(object));
    }

    public <T> boolean register(T t, Class<? super T> clazz) {
        return this.registerImpl(t, clazz);
    }

    public boolean deregister(Object object) {
        Iterator<Class<?>> iterator = this.containingCategories(object);
        boolean bl = false;
        while (iterator.hasNext()) {
            Class<?> clazz = iterator.next();
            if (!this.deregister(object, clazz) || bl) continue;
            bl = true;
        }
        return bl;
    }

    public boolean deregister(Object object, Class<?> clazz) {
        return this.getRegistry(clazz).deregister(object);
    }

    public static void main(String[] stringArray) {
        abstract class Spi {
            Spi() {
            }
        }
        ServiceRegistry serviceRegistry = new ServiceRegistry(Arrays.asList(CharsetProvider.class, SelectorProvider.class, ImageReaderSpi.class, ImageWriterSpi.class, Spi.class).iterator());
        serviceRegistry.registerApplicationClasspathSPIs();
        class One
        extends Spi {
            One() {
            }
        }
        One one = new One();
        class Two
        extends Spi {
            Two() {
            }
        }
        Two two = new Two();
        serviceRegistry.register(one, Spi.class);
        serviceRegistry.register(two, Spi.class);
        serviceRegistry.deregister(one);
        serviceRegistry.deregister(one, Spi.class);
        serviceRegistry.deregister(two, Spi.class);
        serviceRegistry.deregister(two);
        Iterator<Class<?>> iterator = serviceRegistry.categories();
        System.out.println("Categories: ");
        while (iterator.hasNext()) {
            Class<?> clazz = iterator.next();
            System.out.println("  " + clazz.getName() + ":");
            Iterator<?> iterator2 = serviceRegistry.providers(clazz);
            Object var7_7 = null;
            while (iterator2.hasNext()) {
                var7_7 = iterator2.next();
                System.out.println("    " + var7_7);
                if (var7_7 instanceof ImageReaderWriterSpi) {
                    System.out.println("    - " + ((ImageReaderWriterSpi)var7_7).getDescription(null));
                }
                if (!iterator2.hasNext()) continue;
                iterator2.remove();
            }
            if (var7_7 != null) {
                Iterator<Class<?>> iterator3 = serviceRegistry.containingCategories(var7_7);
                int n = 0;
                while (iterator3.hasNext()) {
                    if (clazz != iterator3.next()) continue;
                    iterator3.remove();
                    ++n;
                }
                if (n != 1) {
                    System.err.println("Removed " + var7_7 + " from " + n + " categories");
                }
            }
            if (!(iterator2 = serviceRegistry.providers(clazz)).hasNext()) {
                System.out.println("All providers successfully deregistered");
            }
            while (iterator2.hasNext()) {
                System.err.println("Not removed: " + iterator2.next());
            }
        }
    }

    class CategoryRegistry<T> {
        private final Class<T> category;
        private final Map<Class, T> providers = new LinkedHashMap<Class, T>();

        CategoryRegistry(Class<T> clazz) {
            Validate.notNull(clazz, "category");
            this.category = clazz;
        }

        private void checkCategory(Object object) {
            if (!this.category.isInstance(object)) {
                throw new IllegalArgumentException(object + " not instance of category " + this.category.getName());
            }
        }

        public boolean register(T t) {
            this.checkCategory(t);
            if (!this.contains(t)) {
                this.providers.put(t.getClass(), t);
                this.processRegistration(t);
                return true;
            }
            return false;
        }

        void processRegistration(T t) {
            if (t instanceof RegisterableService) {
                RegisterableService registerableService = (RegisterableService)t;
                registerableService.onRegistration(ServiceRegistry.this, this.category);
            }
        }

        public boolean deregister(Object object) {
            this.checkCategory(object);
            T t = this.providers.remove(object.getClass());
            if (t != null) {
                this.processDeregistration(t);
                return true;
            }
            return false;
        }

        void processDeregistration(T t) {
            if (t instanceof RegisterableService) {
                RegisterableService registerableService = (RegisterableService)t;
                registerableService.onDeregistration(ServiceRegistry.this, this.category);
            }
        }

        public boolean contains(Object object) {
            return this.providers.containsKey(object != null ? object.getClass() : null);
        }

        public Iterator<T> providers() {
            final Iterator<T> iterator = this.providers.values().iterator();
            return new Iterator<T>(){
                T current;

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public T next() {
                    this.current = iterator.next();
                    return this.current;
                }

                @Override
                public void remove() {
                    iterator.remove();
                    CategoryRegistry.this.processDeregistration(this.current);
                }
            };
        }
    }
}

