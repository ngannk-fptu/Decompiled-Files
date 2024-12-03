/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.spi.application.IdentifiableType
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule
 *  com.atlassian.applinks.spi.util.TypeAccessor
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.tracker.DefaultPluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker$Customizer
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.applinks.core;

import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.applinks.core.plugin.AbstractAppLinksTypeModuleDescriptor;
import com.atlassian.applinks.core.plugin.ApplicationTypeModuleDescriptor;
import com.atlassian.applinks.core.plugin.AuthenticationProviderModuleDescriptor;
import com.atlassian.applinks.core.plugin.EntityTypeModuleDescriptor;
import com.atlassian.applinks.spi.application.IdentifiableType;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule;
import com.atlassian.applinks.spi.util.TypeAccessor;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.tracker.DefaultPluginModuleTracker;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class DefaultTypeAccessor
implements TypeAccessor,
InternalTypeAccessor,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(DefaultTypeAccessor.class);
    private final PluginModuleTracker<AuthenticationProviderPluginModule, AuthenticationProviderModuleDescriptor> authTracker;
    private final PluginModuleTracker<ApplicationType, ApplicationTypeModuleDescriptor> applicationTracker;
    private final PluginModuleTracker<EntityType, EntityTypeModuleDescriptor> entityTracker;
    private final TypeCache typeCache = new TypeCache();

    public DefaultTypeAccessor(PluginAccessor pluginAccessor, PluginEventManager eventManager) {
        this.applicationTracker = new DefaultPluginModuleTracker(pluginAccessor, eventManager, ApplicationTypeModuleDescriptor.class, (PluginModuleTracker.Customizer)this.typeCache);
        this.entityTracker = new DefaultPluginModuleTracker(pluginAccessor, eventManager, EntityTypeModuleDescriptor.class, (PluginModuleTracker.Customizer)this.typeCache);
        this.authTracker = new DefaultPluginModuleTracker(pluginAccessor, eventManager, AuthenticationProviderModuleDescriptor.class, (PluginModuleTracker.Customizer)this.typeCache);
    }

    public <T extends EntityType> T getEntityType(Class<T> typeClass) {
        return (T)((EntityType)this.loadTypeOrDescendant(typeClass));
    }

    public EntityType loadEntityType(TypeId typeId) {
        return (EntityType)this.typeCache.get(typeId.get());
    }

    @Override
    public EntityType loadEntityType(String typeClassName) {
        return (EntityType)this.typeCache.get(typeClassName);
    }

    public <T extends ApplicationType> T getApplicationType(Class<T> typeClass) {
        return (T)((ApplicationType)this.loadTypeOrDescendant(typeClass));
    }

    private <T> T loadTypeOrDescendant(Class<T> typeClass) {
        Iterator types;
        Object type = this.typeCache.get(typeClass);
        if (type == null && (types = this.typeCache.getAll(typeClass).iterator()).hasNext()) {
            type = types.next();
            if (types.hasNext()) {
                throw new IllegalArgumentException("Multiple implementations of " + typeClass.getName() + " installed!");
            }
        }
        return (T)type;
    }

    public ApplicationType loadApplicationType(TypeId typeId) {
        return (ApplicationType)this.typeCache.get(typeId.get());
    }

    @Override
    public ApplicationType loadApplicationType(String typeClassName) {
        return (ApplicationType)this.typeCache.get(typeClassName);
    }

    public Class<? extends AuthenticationProvider> getAuthenticationProviderClass(String className) {
        return (Class)this.typeCache.get(className);
    }

    public Iterable<? extends EntityType> getEnabledEntityTypes() {
        return this.typeCache.getAll(EntityType.class);
    }

    public Iterable<? extends EntityType> getEnabledEntityTypesForApplicationType(ApplicationType applicationType) {
        return StreamSupport.stream(this.getEnabledEntityTypes().spliterator(), false).filter(input -> input.getApplicationType().isAssignableFrom(applicationType.getClass())).collect(Collectors.toList());
    }

    public Iterable<? extends ApplicationType> getEnabledApplicationTypes() {
        return this.typeCache.getAll(ApplicationType.class);
    }

    @Override
    public Iterable<? extends EntityType> getEntityTypesForApplicationType(TypeId typeId) {
        final ApplicationType type = this.loadApplicationType(typeId);
        return Iterables.filter(this.getEnabledEntityTypes(), (Predicate)new Predicate<EntityType>(){

            public boolean apply(EntityType input) {
                return input.getApplicationType().isAssignableFrom(type.getClass());
            }
        });
    }

    public void destroy() {
        this.applicationTracker.close();
        this.entityTracker.close();
        this.authTracker.close();
    }

    private static class TypeCache
    implements PluginModuleTracker.Customizer {
        private final Lock write = new ReentrantLock();
        private final Map<String, Object> cache = new ConcurrentHashMap<String, Object>();
        private final Map<String, Set<String>> moduleClasses = new HashMap<String, Set<String>>();

        private TypeCache() {
        }

        private <T> void put(String completeModuleKey, Class<? extends T> clazz, T instance) {
            this.put(completeModuleKey, clazz.getName(), instance);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void put(String completeModuleKey, String className, Object instance) {
            try {
                this.write.lock();
                Set<String> stored = this.moduleClasses.get(completeModuleKey);
                if (stored == null) {
                    stored = new HashSet<String>();
                    this.moduleClasses.put(completeModuleKey, stored);
                }
                stored.add(className);
                this.cache.put(className, instance);
            }
            finally {
                this.write.unlock();
            }
        }

        private <T> T get(Class<T> clazz) {
            return (T)this.cache.get(clazz.getName());
        }

        private <T> T get(String className) {
            return (T)this.cache.get(className);
        }

        private <T> Iterable<T> getAll(Class<T> type) {
            return Iterables.filter((Iterable)ImmutableSet.copyOf(this.cache.values()), type);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void flush(String moduleCompleteKey) {
            try {
                this.write.lock();
                Set<String> stored = this.moduleClasses.get(moduleCompleteKey);
                if (stored != null) {
                    for (String className : stored) {
                        this.cache.remove(className);
                    }
                }
                this.moduleClasses.remove(moduleCompleteKey);
            }
            finally {
                this.write.unlock();
            }
        }

        public ModuleDescriptor adding(ModuleDescriptor descriptor) {
            String completeKey = descriptor.getCompleteKey();
            if (descriptor instanceof AbstractAppLinksTypeModuleDescriptor) {
                AbstractAppLinksTypeModuleDescriptor typeDescriptor = (AbstractAppLinksTypeModuleDescriptor)descriptor;
                Object moduleInstance = typeDescriptor.getModule();
                if (!IdentifiableType.class.isAssignableFrom(moduleInstance.getClass())) {
                    log.error("{} does not implement {}! This type will not be available.", moduleInstance.getClass(), (Object)IdentifiableType.class.getName());
                    return descriptor;
                }
                this.put(completeKey, typeDescriptor.getModule().getClass(), moduleInstance);
                for (String inyourface : typeDescriptor.getInterfaces()) {
                    this.put(completeKey, inyourface, moduleInstance);
                }
                this.put(completeKey, ((IdentifiableType)moduleInstance).getId().get(), moduleInstance);
            } else if (descriptor instanceof AuthenticationProviderModuleDescriptor) {
                AuthenticationProviderPluginModule module = (AuthenticationProviderPluginModule)descriptor.getModule();
                this.put(completeKey, module.getAuthenticationProviderClass().getName(), module.getAuthenticationProviderClass());
            }
            return descriptor;
        }

        public void removed(ModuleDescriptor descriptor) {
            String completeKey = descriptor.getCompleteKey();
            this.flush(completeKey);
        }
    }
}

