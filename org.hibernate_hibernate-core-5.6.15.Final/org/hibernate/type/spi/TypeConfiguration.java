/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.spi;

import java.io.InvalidObjectException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.hibernate.HibernateException;
import org.hibernate.Incubating;
import org.hibernate.SessionFactory;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.boot.cfgxml.spi.CfgXmlAccessService;
import org.hibernate.boot.spi.BasicTypeRegistration;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.id.uuid.LocalObjectUuidHelper;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.SessionFactoryRegistry;
import org.hibernate.metamodel.internal.MetamodelImpl;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.BasicType;
import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.TypeFactory;
import org.hibernate.type.TypeResolver;
import org.hibernate.type.descriptor.java.spi.JavaTypeDescriptorRegistry;
import org.hibernate.type.descriptor.sql.spi.SqlTypeDescriptorRegistry;

@Incubating
public class TypeConfiguration
implements SessionFactoryObserver,
Serializable {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(Scope.class);
    private final String uuid = LocalObjectUuidHelper.generateLocalObjectUuid();
    private final Scope scope;
    private final transient TypeFactory typeFactory;
    private final transient JavaTypeDescriptorRegistry javaTypeDescriptorRegistry;
    private final transient SqlTypeDescriptorRegistry sqlTypeDescriptorRegistry;
    private final transient BasicTypeRegistry basicTypeRegistry;
    private final transient Map<String, String> importMap = new ConcurrentHashMap<String, String>();
    private final transient Map<Integer, Set<String>> jdbcToHibernateTypeContributionMap = new HashMap<Integer, Set<String>>();
    private final transient TypeResolver typeResolver;

    public TypeConfiguration() {
        this.scope = new Scope();
        this.javaTypeDescriptorRegistry = new JavaTypeDescriptorRegistry(this);
        this.sqlTypeDescriptorRegistry = new SqlTypeDescriptorRegistry(this);
        this.basicTypeRegistry = new BasicTypeRegistry();
        this.typeFactory = new TypeFactory(this);
        this.typeResolver = new TypeResolver(this, this.typeFactory);
    }

    public String getUuid() {
        return this.uuid;
    }

    @Deprecated
    public TypeResolver getTypeResolver() {
        return this.typeResolver;
    }

    public BasicTypeRegistry getBasicTypeRegistry() {
        return this.basicTypeRegistry;
    }

    public JavaTypeDescriptorRegistry getJavaTypeDescriptorRegistry() {
        return this.javaTypeDescriptorRegistry;
    }

    public SqlTypeDescriptorRegistry getSqlTypeDescriptorRegistry() {
        return this.sqlTypeDescriptorRegistry;
    }

    public Map<String, String> getImportMap() {
        return Collections.unmodifiableMap(this.importMap);
    }

    public Map<Integer, Set<String>> getJdbcToHibernateTypeContributionMap() {
        return this.jdbcToHibernateTypeContributionMap;
    }

    public MetadataBuildingContext getMetadataBuildingContext() {
        return this.scope.getMetadataBuildingContext();
    }

    public void scope(MetadataBuildingContext metadataBuildingContext) {
        log.debugf("Scoping TypeConfiguration [%s] to MetadataBuildingContext [%s]", this, metadataBuildingContext);
        this.scope.setMetadataBuildingContext(metadataBuildingContext);
    }

    public MetamodelImplementor scope(SessionFactoryImplementor sessionFactory) {
        log.debugf("Scoping TypeConfiguration [%s] to SessionFactoryImpl [%s]", this, sessionFactory);
        for (Map.Entry<String, String> importEntry : this.scope.metadataBuildingContext.getMetadataCollector().getImports().entrySet()) {
            if (this.importMap.containsKey(importEntry.getKey())) continue;
            this.importMap.put(importEntry.getKey(), importEntry.getValue());
        }
        this.scope.setSessionFactory(sessionFactory);
        sessionFactory.addObserver(this);
        return new MetamodelImpl(sessionFactory, this);
    }

    public SessionFactoryImplementor getSessionFactory() {
        return this.scope.getSessionFactory();
    }

    public ServiceRegistry getServiceRegistry() {
        return this.scope.getServiceRegistry();
    }

    @Override
    public void sessionFactoryCreated(SessionFactory factory) {
        log.tracef("Handling #sessionFactoryCreated from [%s] for TypeConfiguration", factory);
        this.scope.setMetadataBuildingContext(null);
    }

    @Override
    public void sessionFactoryClosed(SessionFactory factory) {
        log.tracef("Handling #sessionFactoryClosed from [%s] for TypeConfiguration", factory);
        this.scope.unsetSessionFactory(factory);
    }

    public void addBasicTypeRegistrationContributions(List<BasicTypeRegistration> contributions) {
        for (BasicTypeRegistration basicTypeRegistration : contributions) {
            BasicType basicType = basicTypeRegistration.getBasicType();
            this.basicTypeRegistry.register(basicType, basicTypeRegistration.getRegistrationKeys());
            try {
                int[] jdbcTypes = basicType.sqlTypes(null);
                if (jdbcTypes.length != 1) continue;
                int jdbcType = jdbcTypes[0];
                Set hibernateTypes = this.jdbcToHibernateTypeContributionMap.computeIfAbsent(jdbcType, k -> new HashSet());
                hibernateTypes.add(basicType.getName());
            }
            catch (Exception e) {
                log.errorf(e, "Cannot register [%s] Hibernate Type contribution", basicType.getName());
            }
        }
    }

    private static class Scope
    implements Serializable {
        private transient MetadataBuildingContext metadataBuildingContext;
        private transient SessionFactoryImplementor sessionFactory;
        private String sessionFactoryName;
        private String sessionFactoryUuid;

        private Scope() {
        }

        public MetadataBuildingContext getMetadataBuildingContext() {
            if (this.metadataBuildingContext == null) {
                throw new HibernateException("TypeConfiguration is not currently scoped to MetadataBuildingContext");
            }
            return this.metadataBuildingContext;
        }

        public ServiceRegistry getServiceRegistry() {
            if (this.metadataBuildingContext != null) {
                return this.metadataBuildingContext.getBootstrapContext().getServiceRegistry();
            }
            if (this.sessionFactory != null) {
                return this.sessionFactory.getServiceRegistry();
            }
            return null;
        }

        public void setMetadataBuildingContext(MetadataBuildingContext metadataBuildingContext) {
            this.metadataBuildingContext = metadataBuildingContext;
        }

        public SessionFactoryImplementor getSessionFactory() {
            if (this.sessionFactory == null) {
                if (this.sessionFactoryName == null && this.sessionFactoryUuid == null) {
                    throw new HibernateException("TypeConfiguration was not yet scoped to SessionFactory");
                }
                this.sessionFactory = (SessionFactoryImplementor)SessionFactoryRegistry.INSTANCE.findSessionFactory(this.sessionFactoryUuid, this.sessionFactoryName);
                if (this.sessionFactory == null) {
                    throw new HibernateException("Could not find a SessionFactory [uuid=" + this.sessionFactoryUuid + ",name=" + this.sessionFactoryName + "]");
                }
            }
            return this.sessionFactory;
        }

        void setSessionFactory(SessionFactoryImplementor factory) {
            if (this.sessionFactory != null) {
                log.scopingTypesToSessionFactoryAfterAlreadyScoped(this.sessionFactory, factory);
            } else {
                CfgXmlAccessService cfgXmlAccessService;
                this.sessionFactoryUuid = factory.getUuid();
                String sfName = factory.getSessionFactoryOptions().getSessionFactoryName();
                if (sfName == null && (cfgXmlAccessService = factory.getServiceRegistry().getService(CfgXmlAccessService.class)).getAggregatedConfig() != null) {
                    sfName = cfgXmlAccessService.getAggregatedConfig().getSessionFactoryName();
                }
                this.sessionFactoryName = sfName;
            }
            this.sessionFactory = factory;
        }

        public void unsetSessionFactory(SessionFactory factory) {
            log.debugf("Un-scoping TypeConfiguration [%s] from SessionFactory [%s]", this, factory);
            this.sessionFactory = null;
        }

        private Object readResolve() throws InvalidObjectException {
            if (this.sessionFactory == null && (this.sessionFactoryName != null || this.sessionFactoryUuid != null)) {
                this.sessionFactory = (SessionFactoryImplementor)SessionFactoryRegistry.INSTANCE.findSessionFactory(this.sessionFactoryUuid, this.sessionFactoryName);
                if (this.sessionFactory == null) {
                    throw new HibernateException("Could not find a SessionFactory [uuid=" + this.sessionFactoryUuid + ",name=" + this.sessionFactoryName + "]");
                }
            }
            return this;
        }
    }
}

