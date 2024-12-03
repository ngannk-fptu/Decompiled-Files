/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.cfgxml.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.hibernate.boot.CacheRegionDefinition;
import org.hibernate.boot.cfgxml.spi.MappingReference;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgCollectionCacheType;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgConfigPropertyType;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgEntityCacheType;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgEventListenerGroupType;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgEventListenerType;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgHibernateConfiguration;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgMappingReferenceType;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.log.DeprecationLogger;
import org.hibernate.secure.spi.GrantedPermission;
import org.hibernate.secure.spi.JaccPermissionDeclarations;
import org.jboss.logging.Logger;

public class LoadedConfig {
    private static final Logger log = Logger.getLogger(LoadedConfig.class);
    private String sessionFactoryName;
    private final Map configurationValues = new ConcurrentHashMap(16, 0.75f, 1);
    private Map<String, JaccPermissionDeclarations> jaccPermissionsByContextId;
    private List<CacheRegionDefinition> cacheRegionDefinitions;
    private List<MappingReference> mappingReferences;
    private Map<EventType, Set<String>> eventListenerMap;

    public LoadedConfig(String sessionFactoryName) {
        this.sessionFactoryName = sessionFactoryName;
    }

    public String getSessionFactoryName() {
        return this.sessionFactoryName;
    }

    public Map getConfigurationValues() {
        return this.configurationValues;
    }

    @Deprecated
    public Map<String, JaccPermissionDeclarations> getJaccPermissionsByContextId() {
        return this.jaccPermissionsByContextId;
    }

    @Deprecated
    public JaccPermissionDeclarations getJaccPermissions(String jaccContextId) {
        return this.jaccPermissionsByContextId.get(jaccContextId);
    }

    public List<CacheRegionDefinition> getCacheRegionDefinitions() {
        return this.cacheRegionDefinitions == null ? Collections.emptyList() : this.cacheRegionDefinitions;
    }

    public List<MappingReference> getMappingReferences() {
        return this.mappingReferences == null ? Collections.emptyList() : this.mappingReferences;
    }

    public Map<EventType, Set<String>> getEventListenerMap() {
        return this.eventListenerMap == null ? Collections.emptyMap() : this.eventListenerMap;
    }

    public static LoadedConfig consume(JaxbCfgHibernateConfiguration jaxbCfg) {
        LoadedConfig cfg = new LoadedConfig(jaxbCfg.getSessionFactory().getName());
        for (JaxbCfgConfigPropertyType jaxbProperty : jaxbCfg.getSessionFactory().getProperty()) {
            cfg.addConfigurationValue(jaxbProperty.getName(), jaxbProperty.getValue());
        }
        for (JaxbCfgMappingReferenceType jaxbMapping : jaxbCfg.getSessionFactory().getMapping()) {
            cfg.addMappingReference(MappingReference.consume(jaxbMapping));
        }
        for (Object cacheDeclaration : jaxbCfg.getSessionFactory().getClassCacheOrCollectionCache()) {
            cfg.addCacheRegionDefinition(LoadedConfig.parseCacheRegionDefinition(cacheDeclaration));
        }
        if (jaxbCfg.getSecurity() != null && !jaxbCfg.getSecurity().getGrant().isEmpty()) {
            DeprecationLogger.DEPRECATION_LOGGER.deprecatedJaccCfgXmlSettings();
            for (JaxbCfgHibernateConfiguration.JaxbCfgSecurity.JaxbCfgGrant grant : jaxbCfg.getSecurity().getGrant()) {
                JaccPermissionDeclarations jaccPermissions = cfg.getOrCreateJaccPermissions(jaxbCfg.getSecurity().getContext());
                jaccPermissions.addPermissionDeclaration(new GrantedPermission(grant.getRole(), grant.getEntityName(), grant.getActions()));
            }
        }
        if (!jaxbCfg.getSessionFactory().getListener().isEmpty()) {
            for (JaxbCfgEventListenerType listener : jaxbCfg.getSessionFactory().getListener()) {
                EventType eventType = EventType.resolveEventTypeByName(listener.getType().value());
                cfg.addEventListener(eventType, listener.getClazz());
            }
        }
        if (!jaxbCfg.getSessionFactory().getEvent().isEmpty()) {
            for (JaxbCfgEventListenerGroupType listenerGroup : jaxbCfg.getSessionFactory().getEvent()) {
                if (listenerGroup.getListener().isEmpty()) continue;
                String eventTypeName = listenerGroup.getType().value();
                EventType eventType = EventType.resolveEventTypeByName(eventTypeName);
                for (JaxbCfgEventListenerType listener : listenerGroup.getListener()) {
                    if (listener.getType() != null) {
                        log.debugf("Listener [%s] defined as part of a group also defined event type", (Object)listener.getClazz());
                    }
                    cfg.addEventListener(eventType, listener.getClazz());
                }
            }
        }
        return cfg;
    }

    private static String trim(String value) {
        if (value == null) {
            return null;
        }
        return value.trim();
    }

    private void addConfigurationValue(String propertyName, String value) {
        value = LoadedConfig.trim(value);
        this.configurationValues.put(propertyName, value);
        if (!propertyName.startsWith("hibernate.")) {
            this.configurationValues.put("hibernate." + propertyName, value);
        }
    }

    private void addMappingReference(MappingReference mappingReference) {
        if (this.mappingReferences == null) {
            this.mappingReferences = new ArrayList<MappingReference>();
        }
        this.mappingReferences.add(mappingReference);
    }

    private static CacheRegionDefinition parseCacheRegionDefinition(Object cacheDeclaration) {
        if (JaxbCfgEntityCacheType.class.isInstance(cacheDeclaration)) {
            JaxbCfgEntityCacheType jaxbClassCache = (JaxbCfgEntityCacheType)cacheDeclaration;
            return new CacheRegionDefinition(CacheRegionDefinition.CacheRegionType.ENTITY, jaxbClassCache.getClazz(), jaxbClassCache.getUsage().value(), jaxbClassCache.getRegion(), "all".equals(jaxbClassCache.getInclude()));
        }
        JaxbCfgCollectionCacheType jaxbCollectionCache = (JaxbCfgCollectionCacheType)cacheDeclaration;
        return new CacheRegionDefinition(CacheRegionDefinition.CacheRegionType.COLLECTION, jaxbCollectionCache.getCollection(), jaxbCollectionCache.getUsage().value(), jaxbCollectionCache.getRegion(), false);
    }

    public void addCacheRegionDefinition(CacheRegionDefinition cacheRegionDefinition) {
        if (this.cacheRegionDefinitions == null) {
            this.cacheRegionDefinitions = new ArrayList<CacheRegionDefinition>();
        }
        this.cacheRegionDefinitions.add(cacheRegionDefinition);
    }

    public void addEventListener(EventType eventType, String listenerClass) {
        Set<String> listenerClasses;
        if (this.eventListenerMap == null) {
            this.eventListenerMap = new HashMap<EventType, Set<String>>();
        }
        if ((listenerClasses = this.eventListenerMap.get(eventType)) == null) {
            listenerClasses = new HashSet<String>();
            this.eventListenerMap.put(eventType, listenerClasses);
        }
        listenerClasses.add(listenerClass);
    }

    public JaccPermissionDeclarations getOrCreateJaccPermissions(String contextId) {
        JaccPermissionDeclarations jaccPermission;
        if (this.jaccPermissionsByContextId == null) {
            this.jaccPermissionsByContextId = new HashMap<String, JaccPermissionDeclarations>();
        }
        if ((jaccPermission = this.jaccPermissionsByContextId.get(contextId)) == null) {
            jaccPermission = new JaccPermissionDeclarations(contextId);
        }
        this.jaccPermissionsByContextId.put(contextId, jaccPermission);
        return jaccPermission;
    }

    public void merge(LoadedConfig incoming) {
        if (this.sessionFactoryName != null) {
            if (incoming.getSessionFactoryName() != null) {
                log.debugf("More than one cfg.xml file attempted to supply SessionFactory name: [%s], [%s].  Keeping initially discovered one [%s]", (Object)this.getSessionFactoryName(), (Object)incoming.getSessionFactoryName(), (Object)this.getSessionFactoryName());
            }
        } else {
            this.sessionFactoryName = incoming.getSessionFactoryName();
        }
        this.addConfigurationValues(incoming.getConfigurationValues());
        this.addMappingReferences(incoming.getMappingReferences());
        this.addCacheRegionDefinitions(incoming.getCacheRegionDefinitions());
        this.addJaccPermissions(incoming.getJaccPermissionsByContextId());
        this.addEventListeners(incoming.getEventListenerMap());
    }

    protected void addConfigurationValues(Map configurationValues) {
        if (configurationValues == null) {
            return;
        }
        this.configurationValues.putAll(configurationValues);
    }

    private void addMappingReferences(List<MappingReference> mappingReferences) {
        if (mappingReferences == null) {
            return;
        }
        if (this.mappingReferences == null) {
            this.mappingReferences = new ArrayList<MappingReference>();
        }
        this.mappingReferences.addAll(mappingReferences);
    }

    private void addCacheRegionDefinitions(List<CacheRegionDefinition> cacheRegionDefinitions) {
        if (cacheRegionDefinitions == null) {
            return;
        }
        if (this.cacheRegionDefinitions == null) {
            this.cacheRegionDefinitions = new ArrayList<CacheRegionDefinition>();
        }
        this.cacheRegionDefinitions.addAll(cacheRegionDefinitions);
    }

    private void addJaccPermissions(Map<String, JaccPermissionDeclarations> jaccPermissionsByContextId) {
        if (jaccPermissionsByContextId == null) {
            return;
        }
        if (this.jaccPermissionsByContextId == null) {
            this.jaccPermissionsByContextId = new HashMap<String, JaccPermissionDeclarations>();
        }
        for (Map.Entry<String, JaccPermissionDeclarations> incomingEntry : jaccPermissionsByContextId.entrySet()) {
            JaccPermissionDeclarations permissions = jaccPermissionsByContextId.get(incomingEntry.getKey());
            if (permissions == null) {
                permissions = new JaccPermissionDeclarations(incomingEntry.getKey());
                this.jaccPermissionsByContextId.put(incomingEntry.getKey(), permissions);
            }
            permissions.addPermissionDeclarations(incomingEntry.getValue().getPermissionDeclarations());
        }
    }

    private void addEventListeners(Map<EventType, Set<String>> eventListenerMap) {
        if (eventListenerMap == null) {
            return;
        }
        if (this.eventListenerMap == null) {
            this.eventListenerMap = new HashMap<EventType, Set<String>>();
        }
        for (Map.Entry<EventType, Set<String>> incomingEntry : eventListenerMap.entrySet()) {
            Set<String> listenerClasses = this.eventListenerMap.get(incomingEntry.getKey());
            if (listenerClasses == null) {
                listenerClasses = new HashSet<String>();
                this.eventListenerMap.put(incomingEntry.getKey(), listenerClasses);
            }
            listenerClasses.addAll((Collection<String>)incomingEntry.getValue());
        }
    }

    public static LoadedConfig baseline() {
        return new LoadedConfig(null);
    }
}

