/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.security.auth.message.config.AuthConfigFactory
 *  javax.security.auth.message.config.AuthConfigFactory$RegistrationContext
 *  javax.security.auth.message.config.AuthConfigProvider
 *  javax.security.auth.message.config.RegistrationListener
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.authenticator.jaspic;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.security.auth.message.config.AuthConfigFactory;
import javax.security.auth.message.config.AuthConfigProvider;
import javax.security.auth.message.config.RegistrationListener;
import org.apache.catalina.authenticator.jaspic.PersistentProviderRegistrations;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class AuthConfigFactoryImpl
extends AuthConfigFactory {
    private final Log log = LogFactory.getLog(AuthConfigFactoryImpl.class);
    private static final StringManager sm = StringManager.getManager(AuthConfigFactoryImpl.class);
    private static final String CONFIG_PATH = "conf/jaspic-providers.xml";
    private static final File CONFIG_FILE = new File(System.getProperty("catalina.base"), "conf/jaspic-providers.xml");
    private static final Object CONFIG_FILE_LOCK = new Object();
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static String DEFAULT_REGISTRATION_ID = AuthConfigFactoryImpl.getRegistrationID(null, null);
    private final Map<String, RegistrationContextImpl> layerAppContextRegistrations = new ConcurrentHashMap<String, RegistrationContextImpl>();
    private final Map<String, RegistrationContextImpl> appContextRegistrations = new ConcurrentHashMap<String, RegistrationContextImpl>();
    private final Map<String, RegistrationContextImpl> layerRegistrations = new ConcurrentHashMap<String, RegistrationContextImpl>();
    private final Map<String, RegistrationContextImpl> defaultRegistration = new ConcurrentHashMap<String, RegistrationContextImpl>(1);

    public AuthConfigFactoryImpl() {
        this.loadPersistentRegistrations();
    }

    public AuthConfigProvider getConfigProvider(String layer, String appContext, RegistrationListener listener) {
        RegistrationContextImpl registrationContext = this.findRegistrationContextImpl(layer, appContext);
        if (registrationContext != null) {
            if (listener != null) {
                RegistrationListenerWrapper wrapper = new RegistrationListenerWrapper(layer, appContext, listener);
                registrationContext.addListener(wrapper);
            }
            return registrationContext.getProvider();
        }
        return null;
    }

    public String registerConfigProvider(String className, Map properties, String layer, String appContext, String description) {
        String registrationID = this.doRegisterConfigProvider(className, properties, layer, appContext, description);
        this.savePersistentRegistrations();
        return registrationID;
    }

    private String doRegisterConfigProvider(String className, Map properties, String layer, String appContext, String description) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("authConfigFactoryImpl.registerClass", new Object[]{className, layer, appContext}));
        }
        AuthConfigProvider provider = null;
        if (className != null) {
            provider = this.createAuthConfigProvider(className, properties);
        }
        String registrationID = AuthConfigFactoryImpl.getRegistrationID(layer, appContext);
        RegistrationContextImpl registrationContextImpl = new RegistrationContextImpl(layer, appContext, description, true, provider, properties);
        this.addRegistrationContextImpl(layer, appContext, registrationID, registrationContextImpl);
        return registrationID;
    }

    private AuthConfigProvider createAuthConfigProvider(String className, Map properties) throws SecurityException {
        Class<?> clazz = null;
        AuthConfigProvider provider = null;
        try {
            clazz = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        try {
            if (clazz == null) {
                clazz = Class.forName(className);
            }
            Constructor<?> constructor = clazz.getConstructor(Map.class, AuthConfigFactory.class);
            provider = (AuthConfigProvider)constructor.newInstance(properties, null);
        }
        catch (IllegalArgumentException | ReflectiveOperationException e) {
            throw new SecurityException(e);
        }
        return provider;
    }

    public String registerConfigProvider(AuthConfigProvider provider, String layer, String appContext, String description) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("authConfigFactoryImpl.registerInstance", new Object[]{provider.getClass().getName(), layer, appContext}));
        }
        String registrationID = AuthConfigFactoryImpl.getRegistrationID(layer, appContext);
        RegistrationContextImpl registrationContextImpl = new RegistrationContextImpl(layer, appContext, description, false, provider, null);
        this.addRegistrationContextImpl(layer, appContext, registrationID, registrationContextImpl);
        return registrationID;
    }

    private void addRegistrationContextImpl(String layer, String appContext, String registrationID, RegistrationContextImpl registrationContextImpl) {
        block9: {
            RegistrationContextImpl previous;
            block8: {
                RegistrationContextImpl registration;
                previous = null;
                previous = layer != null && appContext != null ? this.layerAppContextRegistrations.put(registrationID, registrationContextImpl) : (layer == null && appContext != null ? this.appContextRegistrations.put(registrationID, registrationContextImpl) : (layer != null && appContext == null ? this.layerRegistrations.put(registrationID, registrationContextImpl) : this.defaultRegistration.put(registrationID, registrationContextImpl)));
                if (previous != null) break block8;
                if (layer != null && appContext != null && (registration = this.appContextRegistrations.get(AuthConfigFactoryImpl.getRegistrationID(null, appContext))) != null) {
                    for (RegistrationListenerWrapper wrapper : registration.listeners) {
                        if (!layer.equals(wrapper.getMessageLayer()) || !appContext.equals(wrapper.getAppContext())) continue;
                        registration.listeners.remove(wrapper);
                        wrapper.listener.notify(wrapper.messageLayer, wrapper.appContext);
                    }
                }
                if (appContext != null) {
                    for (RegistrationContextImpl registration2 : this.layerRegistrations.values()) {
                        for (RegistrationListenerWrapper wrapper : registration2.listeners) {
                            if (!appContext.equals(wrapper.getAppContext())) continue;
                            registration2.listeners.remove(wrapper);
                            wrapper.listener.notify(wrapper.messageLayer, wrapper.appContext);
                        }
                    }
                }
                if (layer == null && appContext == null) break block9;
                for (RegistrationContextImpl registration2 : this.defaultRegistration.values()) {
                    for (RegistrationListenerWrapper wrapper : registration2.listeners) {
                        if ((appContext == null || !appContext.equals(wrapper.getAppContext())) && (layer == null || !layer.equals(wrapper.getMessageLayer()))) continue;
                        registration2.listeners.remove(wrapper);
                        wrapper.listener.notify(wrapper.messageLayer, wrapper.appContext);
                    }
                }
                break block9;
            }
            for (RegistrationListenerWrapper wrapper : previous.listeners) {
                previous.listeners.remove(wrapper);
                wrapper.listener.notify(wrapper.messageLayer, wrapper.appContext);
            }
        }
    }

    public boolean removeRegistration(String registrationID) {
        RegistrationContextImpl registration = null;
        if (DEFAULT_REGISTRATION_ID.equals(registrationID)) {
            registration = this.defaultRegistration.remove(registrationID);
        }
        if (registration == null) {
            registration = this.layerAppContextRegistrations.remove(registrationID);
        }
        if (registration == null) {
            registration = this.appContextRegistrations.remove(registrationID);
        }
        if (registration == null) {
            registration = this.layerRegistrations.remove(registrationID);
        }
        if (registration == null) {
            return false;
        }
        for (RegistrationListenerWrapper wrapper : registration.listeners) {
            wrapper.getListener().notify(wrapper.getMessageLayer(), wrapper.getAppContext());
        }
        if (registration.isPersistent()) {
            this.savePersistentRegistrations();
        }
        return true;
    }

    public String[] detachListener(RegistrationListener listener, String layer, String appContext) {
        String registrationID = AuthConfigFactoryImpl.getRegistrationID(layer, appContext);
        RegistrationContextImpl registrationContext = this.findRegistrationContextImpl(layer, appContext);
        if (registrationContext != null && registrationContext.removeListener(listener)) {
            return new String[]{registrationID};
        }
        return EMPTY_STRING_ARRAY;
    }

    public String[] getRegistrationIDs(AuthConfigProvider provider) {
        ArrayList<String> result = new ArrayList<String>();
        if (provider == null) {
            result.addAll(this.layerAppContextRegistrations.keySet());
            result.addAll(this.appContextRegistrations.keySet());
            result.addAll(this.layerRegistrations.keySet());
            if (!this.defaultRegistration.isEmpty()) {
                result.add(DEFAULT_REGISTRATION_ID);
            }
        } else {
            this.findProvider(provider, this.layerAppContextRegistrations, result);
            this.findProvider(provider, this.appContextRegistrations, result);
            this.findProvider(provider, this.layerRegistrations, result);
            this.findProvider(provider, this.defaultRegistration, result);
        }
        return result.toArray(EMPTY_STRING_ARRAY);
    }

    private void findProvider(AuthConfigProvider provider, Map<String, RegistrationContextImpl> registrations, List<String> result) {
        for (Map.Entry<String, RegistrationContextImpl> entry : registrations.entrySet()) {
            if (!provider.equals(entry.getValue().getProvider())) continue;
            result.add(entry.getKey());
        }
    }

    public AuthConfigFactory.RegistrationContext getRegistrationContext(String registrationID) {
        AuthConfigFactory.RegistrationContext result = this.defaultRegistration.get(registrationID);
        if (result == null) {
            result = this.layerAppContextRegistrations.get(registrationID);
        }
        if (result == null) {
            result = this.appContextRegistrations.get(registrationID);
        }
        if (result == null) {
            result = this.layerRegistrations.get(registrationID);
        }
        return result;
    }

    public void refresh() {
        this.loadPersistentRegistrations();
    }

    private static String getRegistrationID(String layer, String appContext) {
        if (layer != null && layer.length() == 0) {
            throw new IllegalArgumentException(sm.getString("authConfigFactoryImpl.zeroLengthMessageLayer"));
        }
        if (appContext != null && appContext.length() == 0) {
            throw new IllegalArgumentException(sm.getString("authConfigFactoryImpl.zeroLengthAppContext"));
        }
        return (layer == null ? "" : layer) + ":" + (appContext == null ? "" : appContext);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadPersistentRegistrations() {
        Object object = CONFIG_FILE_LOCK;
        synchronized (object) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("authConfigFactoryImpl.load", new Object[]{CONFIG_FILE.getAbsolutePath()}));
            }
            if (!CONFIG_FILE.isFile()) {
                return;
            }
            PersistentProviderRegistrations.Providers providers = PersistentProviderRegistrations.loadProviders(CONFIG_FILE);
            for (PersistentProviderRegistrations.Provider provider : providers.getProviders()) {
                this.doRegisterConfigProvider(provider.getClassName(), provider.getProperties(), provider.getLayer(), provider.getAppContext(), provider.getDescription());
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void savePersistentRegistrations() {
        Object object = CONFIG_FILE_LOCK;
        synchronized (object) {
            PersistentProviderRegistrations.Providers providers = new PersistentProviderRegistrations.Providers();
            this.savePersistentProviders(providers, this.layerAppContextRegistrations);
            this.savePersistentProviders(providers, this.appContextRegistrations);
            this.savePersistentProviders(providers, this.layerRegistrations);
            this.savePersistentProviders(providers, this.defaultRegistration);
            PersistentProviderRegistrations.writeProviders(providers, CONFIG_FILE);
        }
    }

    private void savePersistentProviders(PersistentProviderRegistrations.Providers providers, Map<String, RegistrationContextImpl> registrations) {
        for (Map.Entry<String, RegistrationContextImpl> entry : registrations.entrySet()) {
            this.savePersistentProvider(providers, entry.getValue());
        }
    }

    private void savePersistentProvider(PersistentProviderRegistrations.Providers providers, RegistrationContextImpl registrationContextImpl) {
        if (registrationContextImpl != null && registrationContextImpl.isPersistent()) {
            PersistentProviderRegistrations.Provider provider = new PersistentProviderRegistrations.Provider();
            provider.setAppContext(registrationContextImpl.getAppContext());
            if (registrationContextImpl.getProvider() != null) {
                provider.setClassName(registrationContextImpl.getProvider().getClass().getName());
            }
            provider.setDescription(registrationContextImpl.getDescription());
            provider.setLayer(registrationContextImpl.getMessageLayer());
            for (Map.Entry property : registrationContextImpl.getProperties().entrySet()) {
                provider.addProperty((String)property.getKey(), (String)property.getValue());
            }
            providers.addProvider(provider);
        }
    }

    private RegistrationContextImpl findRegistrationContextImpl(String layer, String appContext) {
        RegistrationContextImpl result = this.layerAppContextRegistrations.get(AuthConfigFactoryImpl.getRegistrationID(layer, appContext));
        if (result == null) {
            result = this.appContextRegistrations.get(AuthConfigFactoryImpl.getRegistrationID(null, appContext));
        }
        if (result == null) {
            result = this.layerRegistrations.get(AuthConfigFactoryImpl.getRegistrationID(layer, null));
        }
        if (result == null) {
            result = this.defaultRegistration.get(DEFAULT_REGISTRATION_ID);
        }
        return result;
    }

    private static class RegistrationContextImpl
    implements AuthConfigFactory.RegistrationContext {
        private final String messageLayer;
        private final String appContext;
        private final String description;
        private final boolean persistent;
        private final AuthConfigProvider provider;
        private final Map<String, String> properties;
        private final List<RegistrationListenerWrapper> listeners = new CopyOnWriteArrayList<RegistrationListenerWrapper>();

        private RegistrationContextImpl(String messageLayer, String appContext, String description, boolean persistent, AuthConfigProvider provider, Map<String, String> properties) {
            this.messageLayer = messageLayer;
            this.appContext = appContext;
            this.description = description;
            this.persistent = persistent;
            this.provider = provider;
            HashMap<String, String> propertiesCopy = new HashMap<String, String>();
            if (properties != null) {
                propertiesCopy.putAll(properties);
            }
            this.properties = Collections.unmodifiableMap(propertiesCopy);
        }

        public String getMessageLayer() {
            return this.messageLayer;
        }

        public String getAppContext() {
            return this.appContext;
        }

        public String getDescription() {
            return this.description;
        }

        public boolean isPersistent() {
            return this.persistent;
        }

        private AuthConfigProvider getProvider() {
            return this.provider;
        }

        private void addListener(RegistrationListenerWrapper listener) {
            if (listener != null) {
                this.listeners.add(listener);
            }
        }

        private Map<String, String> getProperties() {
            return this.properties;
        }

        private boolean removeListener(RegistrationListener listener) {
            boolean result = false;
            for (RegistrationListenerWrapper wrapper : this.listeners) {
                if (!wrapper.getListener().equals(listener)) continue;
                this.listeners.remove(wrapper);
                result = true;
            }
            return result;
        }
    }

    private static class RegistrationListenerWrapper {
        private final String messageLayer;
        private final String appContext;
        private final RegistrationListener listener;

        RegistrationListenerWrapper(String messageLayer, String appContext, RegistrationListener listener) {
            this.messageLayer = messageLayer;
            this.appContext = appContext;
            this.listener = listener;
        }

        public String getMessageLayer() {
            return this.messageLayer;
        }

        public String getAppContext() {
            return this.appContext;
        }

        public RegistrationListener getListener() {
            return this.listener;
        }
    }
}

