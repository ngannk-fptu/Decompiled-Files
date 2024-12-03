/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa;

@Deprecated
public interface AvailableSettings {
    @Deprecated
    public static final String PROVIDER = "javax.persistence.provider";
    @Deprecated
    public static final String TRANSACTION_TYPE = "javax.persistence.transactionType";
    @Deprecated
    public static final String JTA_DATASOURCE = "javax.persistence.jtaDataSource";
    @Deprecated
    public static final String NON_JTA_DATASOURCE = "javax.persistence.nonJtaDataSource";
    @Deprecated
    public static final String JDBC_DRIVER = "javax.persistence.jdbc.driver";
    @Deprecated
    public static final String JDBC_URL = "javax.persistence.jdbc.url";
    @Deprecated
    public static final String JDBC_USER = "javax.persistence.jdbc.user";
    @Deprecated
    public static final String JDBC_PASSWORD = "javax.persistence.jdbc.password";
    @Deprecated
    public static final String SHARED_CACHE_MODE = "javax.persistence.sharedCache.mode";
    @Deprecated
    public static final String SHARED_CACHE_RETRIEVE_MODE = "javax.persistence.cache.retrieveMode";
    @Deprecated
    public static final String SHARED_CACHE_STORE_MODE = "javax.persistence.cache.storeMode";
    @Deprecated
    public static final String VALIDATION_MODE = "javax.persistence.validation.mode";
    @Deprecated
    public static final String VALIDATION_FACTORY = "javax.persistence.validation.factory";
    @Deprecated
    public static final String PERSIST_VALIDATION_GROUP = "javax.persistence.validation.group.pre-persist";
    @Deprecated
    public static final String UPDATE_VALIDATION_GROUP = "javax.persistence.validation.group.pre-update";
    @Deprecated
    public static final String REMOVE_VALIDATION_GROUP = "javax.persistence.validation.group.pre-remove";
    @Deprecated
    public static final String LOCK_SCOPE = "javax.persistence.lock.scope";
    @Deprecated
    public static final String LOCK_TIMEOUT = "javax.persistence.lock.timeout";
    @Deprecated
    public static final String CDI_BEAN_MANAGER = "javax.persistence.bean.manager";
    @Deprecated
    public static final String SCHEMA_GEN_CREATE_SOURCE = "javax.persistence.schema-generation.create-source";
    @Deprecated
    public static final String SCHEMA_GEN_DROP_SOURCE = "javax.persistence.schema-generation.drop-source";
    @Deprecated
    public static final String SCHEMA_GEN_CREATE_SCRIPT_SOURCE = "javax.persistence.schema-generation.create-script-source";
    @Deprecated
    public static final String SCHEMA_GEN_DROP_SCRIPT_SOURCE = "javax.persistence.schema-generation.drop-script-source";
    @Deprecated
    public static final String SCHEMA_GEN_DATABASE_ACTION = "javax.persistence.schema-generation.database.action";
    @Deprecated
    public static final String SCHEMA_GEN_SCRIPTS_ACTION = "javax.persistence.schema-generation.scripts.action";
    @Deprecated
    public static final String SCHEMA_GEN_SCRIPTS_CREATE_TARGET = "javax.persistence.schema-generation.scripts.create-target";
    @Deprecated
    public static final String SCHEMA_GEN_SCRIPTS_DROP_TARGET = "javax.persistence.schema-generation.scripts.drop-target";
    @Deprecated
    public static final String SCHEMA_GEN_CREATE_SCHEMAS = "hibernate.hbm2ddl.create_namespaces";
    @Deprecated
    public static final String SCHEMA_GEN_CONNECTION = "javax.persistence.schema-generation-connection";
    @Deprecated
    public static final String SCHEMA_GEN_DB_NAME = "javax.persistence.database-product-name";
    @Deprecated
    public static final String SCHEMA_GEN_DB_MAJOR_VERSION = "javax.persistence.database-major-version";
    @Deprecated
    public static final String SCHEMA_GEN_DB_MINOR_VERSION = "javax.persistence.database-minor-version";
    @Deprecated
    public static final String SCHEMA_GEN_LOAD_SCRIPT_SOURCE = "javax.persistence.sql-load-script-source";
    @Deprecated
    public static final String JPA_METAMODEL_POPULATION = "hibernate.ejb.metamodel.population";
    @Deprecated
    public static final String INTERCEPTOR = "hibernate.ejb.interceptor";
    @Deprecated
    public static final String SESSION_INTERCEPTOR = "hibernate.ejb.interceptor.session_scoped";
    public static final String ALIAS_SPECIFIC_LOCK_MODE = "org.hibernate.lockMode";
    @Deprecated
    public static final String CFG_FILE = "hibernate.ejb.cfgfile";
    @Deprecated
    public static final String CLASS_CACHE_PREFIX = "hibernate.ejb.classcache";
    @Deprecated
    public static final String COLLECTION_CACHE_PREFIX = "hibernate.ejb.collectioncache";
    @Deprecated
    public static final String SESSION_FACTORY_OBSERVER = "hibernate.ejb.session_factory_observer";
    @Deprecated
    public static final String IDENTIFIER_GENERATOR_STRATEGY_PROVIDER = "hibernate.ejb.identifier_generator_strategy_provider";
    @Deprecated
    public static final String EVENT_LISTENER_PREFIX = "hibernate.ejb.event";
    public static final String ENHANCER_ENABLE_DIRTY_TRACKING = "hibernate.enhancer.enableDirtyTracking";
    public static final String ENHANCER_ENABLE_LAZY_INITIALIZATION = "hibernate.enhancer.enableLazyInitialization";
    public static final String ENHANCER_ENABLE_ASSOCIATION_MANAGEMENT = "hibernate.enhancer.enableAssociationManagement";
    @Deprecated
    public static final String DISCARD_PC_ON_CLOSE = "hibernate.ejb.discard_pc_on_close";
    public static final String FLUSH_MODE = "org.hibernate.flushMode";
    @Deprecated
    public static final String ENTITY_MANAGER_FACTORY_NAME = "hibernate.ejb.entitymanager_factory_name";
    @Deprecated
    public static final String XML_FILE_NAMES = "hibernate.ejb.xml_files";
    @Deprecated
    public static final String HBXML_FILES = "hibernate.hbmxml.files";
    @Deprecated
    public static final String LOADED_CLASSES = "hibernate.ejb.loaded.classes";
    @Deprecated
    public static final String PERSISTENCE_UNIT_NAME = "hibernate.ejb.persistenceUnitName";
    public static final String DELAY_CDI_ACCESS = "hibernate.delay_cdi_access";
    public static final String ALLOW_JTA_TRANSACTION_ACCESS = "hibernate.jta.allowTransactionAccess";
}

