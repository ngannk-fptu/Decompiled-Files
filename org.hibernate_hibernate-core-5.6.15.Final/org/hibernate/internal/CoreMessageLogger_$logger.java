/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.Synchronization
 *  javax.transaction.SystemException
 *  org.jboss.logging.BasicLogger
 *  org.jboss.logging.DelegatingBasicLogger
 *  org.jboss.logging.Logger
 *  org.jboss.logging.Logger$Level
 */
package org.hibernate.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.ServiceConfigurationError;
import java.util.Set;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.cache.CacheException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;
import org.hibernate.engine.jndi.JndiException;
import org.hibernate.engine.jndi.JndiNameException;
import org.hibernate.engine.loading.internal.CollectionLoadContext;
import org.hibernate.engine.loading.internal.EntityLoadContext;
import org.hibernate.engine.spi.CollectionKey;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.id.IntegralDataTypeHolder;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.type.BasicType;
import org.hibernate.type.SerializationException;
import org.hibernate.type.Type;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.DelegatingBasicLogger;
import org.jboss.logging.Logger;

public class CoreMessageLogger_$logger
extends DelegatingBasicLogger
implements CoreMessageLogger,
BasicLogger,
Serializable {
    private static final long serialVersionUID = 1L;
    private static final String FQCN = CoreMessageLogger_$logger.class.getName();
    private static final Locale LOCALE = Locale.ROOT;

    public CoreMessageLogger_$logger(Logger log) {
        super(log);
    }

    protected Locale getLoggingLocale() {
        return LOCALE;
    }

    @Override
    public final void alreadySessionBound() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.alreadySessionBound$str(), new Object[0]);
    }

    protected String alreadySessionBound$str() {
        return "HHH000002: Already session bound on call to bind(); make sure you clean up your sessions!";
    }

    @Override
    public final void autoCommitMode(boolean autocommit) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.autoCommitMode$str(), (Object)autocommit);
    }

    protected String autoCommitMode$str() {
        return "HHH000006: Autocommit mode: %s";
    }

    @Override
    public final void autoFlushWillNotWork() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.autoFlushWillNotWork$str(), new Object[0]);
    }

    protected String autoFlushWillNotWork$str() {
        return "HHH000008: JTASessionContext being used with JDBC transactions; auto-flush will not operate correctly with getCurrentSession()";
    }

    @Override
    public final void batchContainedStatementsOnRelease() {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.batchContainedStatementsOnRelease$str(), new Object[0]);
    }

    protected String batchContainedStatementsOnRelease$str() {
        return "HHH000010: On release of batch it still contained JDBC statements";
    }

    @Override
    public final void bytecodeProvider(String provider) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.bytecodeProvider$str(), (Object)provider);
    }

    protected String bytecodeProvider$str() {
        return "HHH000021: Bytecode provider name : %s";
    }

    @Override
    public final void c3p0ProviderClassNotFound(String c3p0ProviderClassName) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.c3p0ProviderClassNotFound$str(), (Object)c3p0ProviderClassName);
    }

    protected String c3p0ProviderClassNotFound$str() {
        return "HHH000022: c3p0 properties were encountered, but the %s provider class was not found on the classpath; these properties are going to be ignored.";
    }

    @Override
    public final void cachedFileNotFound(String path, FileNotFoundException error) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.cachedFileNotFound$str(), (Object)path, (Object)error);
    }

    protected String cachedFileNotFound$str() {
        return "HHH000023: I/O reported cached file could not be found : %s : %s";
    }

    @Override
    public final void cacheProvider(String name) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.cacheProvider$str(), (Object)name);
    }

    protected String cacheProvider$str() {
        return "HHH000024: Cache provider: %s";
    }

    @Override
    public final void callingJoinTransactionOnNonJtaEntityManager() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.callingJoinTransactionOnNonJtaEntityManager$str(), new Object[0]);
    }

    protected String callingJoinTransactionOnNonJtaEntityManager$str() {
        return "HHH000027: Calling joinTransaction() on a non JTA EntityManager";
    }

    @Override
    public final void closing() {
        this.log.logf(FQCN, Logger.Level.DEBUG, null, this.closing$str(), new Object[0]);
    }

    protected String closing$str() {
        return "HHH000031: Closing";
    }

    @Override
    public final void collectionsFetched(long collectionFetchCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.collectionsFetched$str(), (Object)collectionFetchCount);
    }

    protected String collectionsFetched$str() {
        return "HHH000032: Collections fetched (minimize this): %s";
    }

    @Override
    public final void collectionsLoaded(long collectionLoadCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.collectionsLoaded$str(), (Object)collectionLoadCount);
    }

    protected String collectionsLoaded$str() {
        return "HHH000033: Collections loaded: %s";
    }

    @Override
    public final void collectionsRecreated(long collectionRecreateCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.collectionsRecreated$str(), (Object)collectionRecreateCount);
    }

    protected String collectionsRecreated$str() {
        return "HHH000034: Collections recreated: %s";
    }

    @Override
    public final void collectionsRemoved(long collectionRemoveCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.collectionsRemoved$str(), (Object)collectionRemoveCount);
    }

    protected String collectionsRemoved$str() {
        return "HHH000035: Collections removed: %s";
    }

    @Override
    public final void collectionsUpdated(long collectionUpdateCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.collectionsUpdated$str(), (Object)collectionUpdateCount);
    }

    protected String collectionsUpdated$str() {
        return "HHH000036: Collections updated: %s";
    }

    @Override
    public final void columns(Set keySet) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.columns$str(), (Object)keySet);
    }

    protected String columns$str() {
        return "HHH000037: Columns: %s";
    }

    @Override
    public final void compositeIdClassDoesNotOverrideEquals(String name) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.compositeIdClassDoesNotOverrideEquals$str(), (Object)name);
    }

    protected String compositeIdClassDoesNotOverrideEquals$str() {
        return "HHH000038: Composite-id class does not override equals(): %s";
    }

    @Override
    public final void compositeIdClassDoesNotOverrideHashCode(String name) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.compositeIdClassDoesNotOverrideHashCode$str(), (Object)name);
    }

    protected String compositeIdClassDoesNotOverrideHashCode$str() {
        return "HHH000039: Composite-id class does not override hashCode(): %s";
    }

    @Override
    public final void configurationResource(String resource) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.configurationResource$str(), (Object)resource);
    }

    protected String configurationResource$str() {
        return "HHH000040: Configuration resource: %s";
    }

    @Override
    public final void configuredSessionFactory(String name) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.configuredSessionFactory$str(), (Object)name);
    }

    protected String configuredSessionFactory$str() {
        return "HHH000041: Configured SessionFactory: %s";
    }

    @Override
    public final void configuringFromFile(String file) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.configuringFromFile$str(), (Object)file);
    }

    protected String configuringFromFile$str() {
        return "HHH000042: Configuring from file: %s";
    }

    @Override
    public final void configuringFromResource(String resource) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.configuringFromResource$str(), (Object)resource);
    }

    protected String configuringFromResource$str() {
        return "HHH000043: Configuring from resource: %s";
    }

    @Override
    public final void configuringFromUrl(URL url) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.configuringFromUrl$str(), (Object)url);
    }

    protected String configuringFromUrl$str() {
        return "HHH000044: Configuring from URL: %s";
    }

    @Override
    public final void configuringFromXmlDocument() {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.configuringFromXmlDocument$str(), new Object[0]);
    }

    protected String configuringFromXmlDocument$str() {
        return "HHH000045: Configuring from XML document";
    }

    @Override
    public final void connectionsObtained(long connectCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.connectionsObtained$str(), (Object)connectCount);
    }

    protected String connectionsObtained$str() {
        return "HHH000048: Connections obtained: %s";
    }

    @Override
    public final void containerProvidingNullPersistenceUnitRootUrl() {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.containerProvidingNullPersistenceUnitRootUrl$str(), new Object[0]);
    }

    protected String containerProvidingNullPersistenceUnitRootUrl$str() {
        return "HHH000050: Container is providing a null PersistenceUnitRootUrl: discovery impossible";
    }

    @Override
    public final void containsJoinFetchedCollection(String role) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.containsJoinFetchedCollection$str(), (Object)role);
    }

    protected String containsJoinFetchedCollection$str() {
        return "HHH000051: Ignoring bag join fetch [%s] due to prior collection join fetch";
    }

    @Override
    public final void creatingSubcontextInfo(String intermediateContextName) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.creatingSubcontextInfo$str(), (Object)intermediateContextName);
    }

    protected String creatingSubcontextInfo$str() {
        return "HHH000053: Creating subcontext: %s";
    }

    @Override
    public final void definingFlushBeforeCompletionIgnoredInHem(String flushBeforeCompletion) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.definingFlushBeforeCompletionIgnoredInHem$str(), (Object)flushBeforeCompletion);
    }

    protected String definingFlushBeforeCompletionIgnoredInHem$str() {
        return "HHH000059: Defining %s=true ignored in HEM";
    }

    @Override
    public final void deprecatedForceDescriminatorAnnotation() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.deprecatedForceDescriminatorAnnotation$str(), new Object[0]);
    }

    protected String deprecatedForceDescriminatorAnnotation$str() {
        return "HHH000062: @ForceDiscriminator is deprecated use @DiscriminatorOptions instead.";
    }

    @Override
    public final void deprecatedOracle9Dialect() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.deprecatedOracle9Dialect$str(), new Object[0]);
    }

    protected String deprecatedOracle9Dialect$str() {
        return "HHH000063: The Oracle9Dialect dialect has been deprecated; use either Oracle9iDialect or Oracle10gDialect instead";
    }

    @Override
    public final void deprecatedOracleDialect() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.deprecatedOracleDialect$str(), new Object[0]);
    }

    protected String deprecatedOracleDialect$str() {
        return "HHH000064: The OracleDialect dialect has been deprecated; use Oracle8iDialect instead";
    }

    @Override
    public final void deprecatedUuidGenerator(String name, String name2) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.deprecatedUuidGenerator$str(), (Object)name, (Object)name2);
    }

    protected String deprecatedUuidGenerator$str() {
        return "HHH000065: DEPRECATED : use [%s] instead with custom [%s] implementation";
    }

    @Override
    public final void disallowingInsertStatementComment() {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.disallowingInsertStatementComment$str(), new Object[0]);
    }

    protected String disallowingInsertStatementComment$str() {
        return "HHH000067: Disallowing insert statement comment for select-identity due to Oracle driver bug";
    }

    @Override
    public final void duplicateGeneratorName(String name) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.duplicateGeneratorName$str(), (Object)name);
    }

    protected String duplicateGeneratorName$str() {
        return "HHH000069: Duplicate generator name %s";
    }

    @Override
    public final void duplicateGeneratorTable(String name) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.duplicateGeneratorTable$str(), (Object)name);
    }

    protected String duplicateGeneratorTable$str() {
        return "HHH000070: Duplicate generator table: %s";
    }

    @Override
    public final void duplicateImport(String entityName, String rename) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.duplicateImport$str(), (Object)entityName, (Object)rename);
    }

    protected String duplicateImport$str() {
        return "HHH000071: Duplicate import: %s -> %s";
    }

    @Override
    public final void duplicateJoins(String entityName) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.duplicateJoins$str(), (Object)entityName);
    }

    protected String duplicateJoins$str() {
        return "HHH000072: Duplicate joins for class: %s";
    }

    @Override
    public final void duplicateListener(String className) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.duplicateListener$str(), (Object)className);
    }

    protected String duplicateListener$str() {
        return "HHH000073: entity-listener duplication, first event definition will be used: %s";
    }

    @Override
    public final void duplicateMetadata() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.duplicateMetadata$str(), new Object[0]);
    }

    protected String duplicateMetadata$str() {
        return "HHH000074: Found more than one <persistence-unit-metadata>, subsequent ignored";
    }

    @Override
    public final void entitiesDeleted(long entityDeleteCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.entitiesDeleted$str(), (Object)entityDeleteCount);
    }

    protected String entitiesDeleted$str() {
        return "HHH000076: Entities deleted: %s";
    }

    @Override
    public final void entitiesFetched(long entityFetchCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.entitiesFetched$str(), (Object)entityFetchCount);
    }

    protected String entitiesFetched$str() {
        return "HHH000077: Entities fetched (minimize this): %s";
    }

    @Override
    public final void entitiesInserted(long entityInsertCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.entitiesInserted$str(), (Object)entityInsertCount);
    }

    protected String entitiesInserted$str() {
        return "HHH000078: Entities inserted: %s";
    }

    @Override
    public final void entitiesLoaded(long entityLoadCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.entitiesLoaded$str(), (Object)entityLoadCount);
    }

    protected String entitiesLoaded$str() {
        return "HHH000079: Entities loaded: %s";
    }

    @Override
    public final void entitiesUpdated(long entityUpdateCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.entitiesUpdated$str(), (Object)entityUpdateCount);
    }

    protected String entitiesUpdated$str() {
        return "HHH000080: Entities updated: %s";
    }

    @Override
    public final void entityAnnotationOnNonRoot(String className) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.entityAnnotationOnNonRoot$str(), (Object)className);
    }

    protected String entityAnnotationOnNonRoot$str() {
        return "HHH000081: @org.hibernate.annotations.Entity used on a non root entity: ignored for %s";
    }

    @Override
    public final void entityManagerClosedBySomeoneElse(String autoCloseSession) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.entityManagerClosedBySomeoneElse$str(), (Object)autoCloseSession);
    }

    protected String entityManagerClosedBySomeoneElse$str() {
        return "HHH000082: Entity Manager closed by someone else (%s must not be used)";
    }

    @Override
    public final void entityMappedAsNonAbstract(String name) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.entityMappedAsNonAbstract$str(), (Object)name);
    }

    protected String entityMappedAsNonAbstract$str() {
        return "HHH000084: Entity [%s] is abstract-class/interface explicitly mapped as non-abstract; be sure to supply entity-names";
    }

    @Override
    public final void exceptionHeaderFound(String exceptionHeader, String metaInfOrmXml) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.exceptionHeaderFound$str(), (Object)exceptionHeader, (Object)metaInfOrmXml);
    }

    protected String exceptionHeaderFound$str() {
        return "HHH000085: %s %s found";
    }

    @Override
    public final void exceptionHeaderNotFound(String exceptionHeader, String metaInfOrmXml) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.exceptionHeaderNotFound$str(), (Object)exceptionHeader, (Object)metaInfOrmXml);
    }

    protected String exceptionHeaderNotFound$str() {
        return "HHH000086: %s No %s found";
    }

    @Override
    public final void exceptionInAfterTransactionCompletionInterceptor(Throwable e) {
        this.log.logf(FQCN, Logger.Level.ERROR, e, this.exceptionInAfterTransactionCompletionInterceptor$str(), new Object[0]);
    }

    protected String exceptionInAfterTransactionCompletionInterceptor$str() {
        return "HHH000087: Exception in interceptor afterTransactionCompletion()";
    }

    @Override
    public final void exceptionInBeforeTransactionCompletionInterceptor(Throwable e) {
        this.log.logf(FQCN, Logger.Level.ERROR, e, this.exceptionInBeforeTransactionCompletionInterceptor$str(), new Object[0]);
    }

    protected String exceptionInBeforeTransactionCompletionInterceptor$str() {
        return "HHH000088: Exception in interceptor beforeTransactionCompletion()";
    }

    @Override
    public final void exceptionInSubResolver(String message) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.exceptionInSubResolver$str(), (Object)message);
    }

    protected String exceptionInSubResolver$str() {
        return "HHH000089: Sub-resolver threw unexpected exception, continuing to next : %s";
    }

    @Override
    public final void expectedType(String name, String string) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.expectedType$str(), (Object)name, (Object)string);
    }

    protected String expectedType$str() {
        return "HHH000091: Expected type: %s, actual value: %s";
    }

    @Override
    public final void expired(Object key) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.expired$str(), key);
    }

    protected String expired$str() {
        return "HHH000092: An item was expired by the cache while it was locked (increase your cache timeout): %s";
    }

    @Override
    public final void factoryBoundToJndiName(String name) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.factoryBoundToJndiName$str(), (Object)name);
    }

    protected String factoryBoundToJndiName$str() {
        return "HHH000094: Bound factory to JNDI name: %s";
    }

    @Override
    public final void factoryJndiRename(String oldName, String newName) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.factoryJndiRename$str(), (Object)oldName, (Object)newName);
    }

    protected String factoryJndiRename$str() {
        return "HHH000096: A factory was renamed from [%s] to [%s] in JNDI";
    }

    @Override
    public final void factoryUnboundFromJndiName(String name) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.factoryUnboundFromJndiName$str(), (Object)name);
    }

    protected String factoryUnboundFromJndiName$str() {
        return "HHH000097: Unbound factory from JNDI name: %s";
    }

    @Override
    public final void factoryUnboundFromName(String name) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.factoryUnboundFromName$str(), (Object)name);
    }

    protected String factoryUnboundFromName$str() {
        return "HHH000098: A factory was unbound from name: %s";
    }

    @Override
    public final void failed(Throwable throwable) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.failed$str(), (Object)throwable);
    }

    protected String failed$str() {
        return "HHH000099: an assertion failure occurred (this may indicate a bug in Hibernate, but is more likely due to unsafe use of the session): %s";
    }

    @Override
    public final void failSafeCollectionsCleanup(CollectionLoadContext collectionLoadContext) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.failSafeCollectionsCleanup$str(), (Object)collectionLoadContext);
    }

    protected String failSafeCollectionsCleanup$str() {
        return "HHH000100: Fail-safe cleanup (collections) : %s";
    }

    @Override
    public final void failSafeEntitiesCleanup(EntityLoadContext entityLoadContext) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.failSafeEntitiesCleanup$str(), (Object)entityLoadContext);
    }

    protected String failSafeEntitiesCleanup$str() {
        return "HHH000101: Fail-safe cleanup (entities) : %s";
    }

    @Override
    public final void fetchingDatabaseMetadata() {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.fetchingDatabaseMetadata$str(), new Object[0]);
    }

    protected String fetchingDatabaseMetadata$str() {
        return "HHH000102: Fetching database metadata";
    }

    @Override
    public final void firstOrMaxResultsSpecifiedWithCollectionFetch() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.firstOrMaxResultsSpecifiedWithCollectionFetch$str(), new Object[0]);
    }

    protected String firstOrMaxResultsSpecifiedWithCollectionFetch$str() {
        return "HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!";
    }

    @Override
    public final void flushes(long flushCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.flushes$str(), (Object)flushCount);
    }

    protected String flushes$str() {
        return "HHH000105: Flushes: %s";
    }

    @Override
    public final void forcingContainerResourceCleanup() {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.forcingContainerResourceCleanup$str(), new Object[0]);
    }

    protected String forcingContainerResourceCleanup$str() {
        return "HHH000106: Forcing container resource cleanup on transaction completion";
    }

    @Override
    public final void forcingTableUse() {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.forcingTableUse$str(), new Object[0]);
    }

    protected String forcingTableUse$str() {
        return "HHH000107: Forcing table use for sequence-style generator due to pooled optimizer selection where db does not support pooled sequences";
    }

    @Override
    public final void foreignKeys(Set keySet) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.foreignKeys$str(), (Object)keySet);
    }

    protected String foreignKeys$str() {
        return "HHH000108: Foreign keys: %s";
    }

    @Override
    public final void foundMappingDocument(String name) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.foundMappingDocument$str(), (Object)name);
    }

    protected String foundMappingDocument$str() {
        return "HHH000109: Found mapping document in jar: %s";
    }

    @Override
    public final void guidGenerated(String result) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.guidGenerated$str(), (Object)result);
    }

    protected String guidGenerated$str() {
        return "HHH000113: GUID identifier generated: %s";
    }

    @Override
    public final void handlingTransientEntity() {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.handlingTransientEntity$str(), new Object[0]);
    }

    protected String handlingTransientEntity$str() {
        return "HHH000114: Handling transient entity in delete processing";
    }

    @Override
    public final void hibernateConnectionPoolSize(int poolSize, int minSize) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.hibernateConnectionPoolSize$str(), (Object)poolSize, (Object)minSize);
    }

    protected String hibernateConnectionPoolSize$str() {
        return "HHH000115: Hibernate connection pool size: %s (min=%s)";
    }

    @Override
    public final void honoringOptimizerSetting(String none, String incrementParam, int incrementSize, String positiveOrNegative, int defaultIncrementSize) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.honoringOptimizerSetting$str(), new Object[]{none, incrementParam, incrementSize, positiveOrNegative, defaultIncrementSize});
    }

    protected String honoringOptimizerSetting$str() {
        return "HHH000116: Config specified explicit optimizer of [%s], but [%s=%s]; using optimizer [%s] increment default of [%s].";
    }

    @Override
    public final void hql(String hql, Long valueOf, Long valueOf2) {
        this.log.logf(FQCN, Logger.Level.DEBUG, null, this.hql$str(), (Object)hql, (Object)valueOf, (Object)valueOf2);
    }

    protected String hql$str() {
        return "HHH000117: HQL: %s, time: %sms, rows: %s";
    }

    @Override
    public final void hsqldbSupportsOnlyReadCommittedIsolation() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.hsqldbSupportsOnlyReadCommittedIsolation$str(), new Object[0]);
    }

    protected String hsqldbSupportsOnlyReadCommittedIsolation$str() {
        return "HHH000118: HSQLDB supports only READ_UNCOMMITTED isolation";
    }

    @Override
    public final void hydratingEntitiesCount(int size) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.hydratingEntitiesCount$str(), (Object)size);
    }

    protected String hydratingEntitiesCount$str() {
        return "HHH000119: On EntityLoadContext#clear, hydratingEntities contained [%s] entries";
    }

    @Override
    public final void ignoringTableGeneratorConstraints(String name) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.ignoringTableGeneratorConstraints$str(), (Object)name);
    }

    protected String ignoringTableGeneratorConstraints$str() {
        return "HHH000120: Ignoring unique constraints specified on table generator [%s]";
    }

    @Override
    public final void ignoringUnrecognizedQueryHint(String hintName) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.ignoringUnrecognizedQueryHint$str(), (Object)hintName);
    }

    protected String ignoringUnrecognizedQueryHint$str() {
        return "HHH000121: Ignoring unrecognized query hint [%s]";
    }

    @Override
    public final void illegalPropertyGetterArgument(String name, String propertyName) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.illegalPropertyGetterArgument$str(), (Object)name, (Object)propertyName);
    }

    protected String illegalPropertyGetterArgument$str() {
        return "HHH000122: IllegalArgumentException in class: %s, getter method of property: %s";
    }

    @Override
    public final void illegalPropertySetterArgument(String name, String propertyName) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.illegalPropertySetterArgument$str(), (Object)name, (Object)propertyName);
    }

    protected String illegalPropertySetterArgument$str() {
        return "HHH000123: IllegalArgumentException in class: %s, setter method of property: %s";
    }

    @Override
    public final void immutableAnnotationOnNonRoot(String className) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.immutableAnnotationOnNonRoot$str(), (Object)className);
    }

    protected String immutableAnnotationOnNonRoot$str() {
        return "HHH000124: @Immutable used on a non root entity: ignored for %s";
    }

    @Override
    public final void incompleteMappingMetadataCacheProcessing() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.incompleteMappingMetadataCacheProcessing$str(), new Object[0]);
    }

    protected String incompleteMappingMetadataCacheProcessing$str() {
        return "HHH000125: Mapping metadata cache was not completely processed";
    }

    @Override
    public final void indexes(Set keySet) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.indexes$str(), (Object)keySet);
    }

    protected String indexes$str() {
        return "HHH000126: Indexes: %s";
    }

    @Override
    public final void couldNotBindJndiListener() {
        this.log.logf(FQCN, Logger.Level.DEBUG, null, this.couldNotBindJndiListener$str(), new Object[0]);
    }

    protected String couldNotBindJndiListener$str() {
        return "HHH000127: Could not bind JNDI listener";
    }

    @Override
    public final void instantiatingExplicitConnectionProvider(String providerClassName) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.instantiatingExplicitConnectionProvider$str(), (Object)providerClassName);
    }

    protected String instantiatingExplicitConnectionProvider$str() {
        return "HHH000130: Instantiating explicit connection provider: %s";
    }

    @Override
    public final void invalidArrayElementType(String message) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.invalidArrayElementType$str(), (Object)message);
    }

    protected String invalidArrayElementType$str() {
        return "HHH000132: Array element type error\n%s";
    }

    @Override
    public final void invalidDiscriminatorAnnotation(String className) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.invalidDiscriminatorAnnotation$str(), (Object)className);
    }

    protected String invalidDiscriminatorAnnotation$str() {
        return "HHH000133: Discriminator column has to be defined in the root entity, it will be ignored in subclass: %s";
    }

    @Override
    public final void invalidEditOfReadOnlyItem(Object key) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.invalidEditOfReadOnlyItem$str(), key);
    }

    protected String invalidEditOfReadOnlyItem$str() {
        return "HHH000134: Application attempted to edit read only item: %s";
    }

    @Override
    public final void invalidJndiName(String name, JndiNameException e) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)((Object)e), this.invalidJndiName$str(), (Object)name);
    }

    protected String invalidJndiName$str() {
        return "HHH000135: Invalid JNDI name: %s";
    }

    @Override
    public final void invalidOnDeleteAnnotation(String entityName) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.invalidOnDeleteAnnotation$str(), (Object)entityName);
    }

    protected String invalidOnDeleteAnnotation$str() {
        return "HHH000136: Inapropriate use of @OnDelete on entity, annotation ignored: %s";
    }

    @Override
    public final void invalidPrimaryKeyJoinColumnAnnotation(String className) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.invalidPrimaryKeyJoinColumnAnnotation$str(), (Object)className);
    }

    protected String invalidPrimaryKeyJoinColumnAnnotation$str() {
        return "HHH000137: Root entity should not hold a PrimaryKeyJoinColum(s), will be ignored: %s";
    }

    @Override
    public final void invalidSubStrategy(String className) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.invalidSubStrategy$str(), (Object)className);
    }

    protected String invalidSubStrategy$str() {
        return "HHH000138: Mixing inheritance strategy in a entity hierarchy is not allowed, ignoring sub strategy in: %s";
    }

    @Override
    public final void invalidTableAnnotation(String className) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.invalidTableAnnotation$str(), (Object)className);
    }

    protected String invalidTableAnnotation$str() {
        return "HHH000139: Illegal use of @Table in a subclass of a SINGLE_TABLE hierarchy: %s";
    }

    @Override
    public final void jaccContextId(String contextId) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.jaccContextId$str(), (Object)contextId);
    }

    protected String jaccContextId$str() {
        return "HHH000140: JACC contextID: %s";
    }

    @Override
    public final void JavaSqlTypesMappedSameCodeMultipleTimes(int code, String old, String name) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.JavaSqlTypesMappedSameCodeMultipleTimes$str(), (Object)code, (Object)old, (Object)name);
    }

    protected String JavaSqlTypesMappedSameCodeMultipleTimes$str() {
        return "HHH000141: java.sql.Types mapped the same code [%s] multiple times; was [%s]; now [%s]";
    }

    protected String bytecodeEnhancementFailed$str() {
        return "HHH000142: Bytecode enhancement failed: %s";
    }

    @Override
    public final String bytecodeEnhancementFailed(String entityName) {
        return String.format(this.getLoggingLocale(), this.bytecodeEnhancementFailed$str(), entityName);
    }

    protected String bytecodeEnhancementFailedBecauseOfDefaultConstructor$str() {
        return "HHH000143: Bytecode enhancement failed because no public, protected or package-private default constructor was found for entity: %s. Private constructors don't work with runtime proxies!";
    }

    @Override
    public final String bytecodeEnhancementFailedBecauseOfDefaultConstructor(String entityName) {
        return String.format(this.getLoggingLocale(), this.bytecodeEnhancementFailedBecauseOfDefaultConstructor$str(), entityName);
    }

    @Override
    public final void jdbcAutoCommitFalseBreaksEjb3Spec(String autocommit) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.jdbcAutoCommitFalseBreaksEjb3Spec$str(), (Object)autocommit);
    }

    protected String jdbcAutoCommitFalseBreaksEjb3Spec$str() {
        return "HHH000144: %s = false breaks the EJB3 specification";
    }

    protected String jdbcRollbackFailed$str() {
        return "HHH000151: JDBC rollback failed";
    }

    @Override
    public final String jdbcRollbackFailed() {
        return String.format(this.getLoggingLocale(), this.jdbcRollbackFailed$str(), new Object[0]);
    }

    @Override
    public final void jndiInitialContextProperties(Hashtable hash) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.jndiInitialContextProperties$str(), (Object)hash);
    }

    protected String jndiInitialContextProperties$str() {
        return "HHH000154: JNDI InitialContext properties:%s";
    }

    @Override
    public final void jndiNameDoesNotHandleSessionFactoryReference(String sfJNDIName, ClassCastException e) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)e, this.jndiNameDoesNotHandleSessionFactoryReference$str(), (Object)sfJNDIName);
    }

    protected String jndiNameDoesNotHandleSessionFactoryReference$str() {
        return "HHH000155: JNDI name %s does not handle a session factory reference";
    }

    @Override
    public final void lazyPropertyFetchingAvailable(String name) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.lazyPropertyFetchingAvailable$str(), (Object)name);
    }

    protected String lazyPropertyFetchingAvailable$str() {
        return "HHH000157: Lazy property fetching available for: %s";
    }

    @Override
    public final void loadingCollectionKeyNotFound(CollectionKey collectionKey) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.loadingCollectionKeyNotFound$str(), (Object)collectionKey);
    }

    protected String loadingCollectionKeyNotFound$str() {
        return "HHH000159: In CollectionLoadContext#endLoadingCollections, localLoadingCollectionKeys contained [%s], but no LoadingCollectionEntry was found in loadContexts";
    }

    @Override
    public final void localLoadingCollectionKeysCount(int size) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.localLoadingCollectionKeysCount$str(), (Object)size);
    }

    protected String localLoadingCollectionKeysCount$str() {
        return "HHH000160: On CollectionLoadContext#cleanup, localLoadingCollectionKeys contained [%s] entries";
    }

    @Override
    public final void loggingStatistics() {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.loggingStatistics$str(), new Object[0]);
    }

    protected String loggingStatistics$str() {
        return "HHH000161: Logging statistics....";
    }

    @Override
    public final void logicalConnectionClosed() {
        this.log.logf(FQCN, Logger.Level.DEBUG, null, this.logicalConnectionClosed$str(), new Object[0]);
    }

    protected String logicalConnectionClosed$str() {
        return "HHH000162: *** Logical connection closed ***";
    }

    @Override
    public final void logicalConnectionReleasingPhysicalConnection() {
        this.log.logf(FQCN, Logger.Level.DEBUG, null, this.logicalConnectionReleasingPhysicalConnection$str(), new Object[0]);
    }

    protected String logicalConnectionReleasingPhysicalConnection$str() {
        return "HHH000163: Logical connection releasing its physical connection";
    }

    @Override
    public final void maxQueryTime(long queryExecutionMaxTime) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.maxQueryTime$str(), (Object)queryExecutionMaxTime);
    }

    protected String maxQueryTime$str() {
        return "HHH000173: Max query time: %sms";
    }

    @Override
    public final void missingArguments(int anticipatedNumberOfArguments, int numberOfArguments) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.missingArguments$str(), (Object)anticipatedNumberOfArguments, (Object)numberOfArguments);
    }

    protected String missingArguments$str() {
        return "HHH000174: Function template anticipated %s arguments, but %s arguments encountered";
    }

    @Override
    public final void missingEntityAnnotation(String className) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.missingEntityAnnotation$str(), (Object)className);
    }

    protected String missingEntityAnnotation$str() {
        return "HHH000175: Class annotated @org.hibernate.annotations.Entity but not javax.persistence.Entity (most likely a user error): %s";
    }

    @Override
    public final void namedQueryError(String queryName, HibernateException e) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)((Object)e), this.namedQueryError$str(), (Object)queryName);
    }

    protected String namedQueryError$str() {
        return "HHH000177: Error in named query: %s";
    }

    @Override
    public final void namingExceptionAccessingFactory(NamingException exception) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.namingExceptionAccessingFactory$str(), (Object)exception);
    }

    protected String namingExceptionAccessingFactory$str() {
        return "HHH000178: Naming exception occurred accessing factory: %s";
    }

    @Override
    public final void narrowingProxy(Class concreteProxyClass) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.narrowingProxy$str(), (Object)concreteProxyClass);
    }

    protected String narrowingProxy$str() {
        return "HHH000179: Narrowing proxy to %s - this operation breaks ==";
    }

    @Override
    public final void needsLimit() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.needsLimit$str(), new Object[0]);
    }

    protected String needsLimit$str() {
        return "HHH000180: FirstResult/maxResults specified on polymorphic query; applying in memory!";
    }

    @Override
    public final void noAppropriateConnectionProvider() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.noAppropriateConnectionProvider$str(), new Object[0]);
    }

    protected String noAppropriateConnectionProvider$str() {
        return "HHH000181: No appropriate connection provider encountered, assuming application will be supplying connections";
    }

    @Override
    public final void noDefaultConstructor(String name) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.noDefaultConstructor$str(), (Object)name);
    }

    protected String noDefaultConstructor$str() {
        return "HHH000182: No default (no-argument) constructor for class: %s (class must be instantiated by Interceptor)";
    }

    @Override
    public final void noPersistentClassesFound(String query) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.noPersistentClassesFound$str(), (Object)query);
    }

    protected String noPersistentClassesFound$str() {
        return "HHH000183: no persistent classes found for query class: %s";
    }

    @Override
    public final void noSessionFactoryWithJndiName(String sfJNDIName, NameNotFoundException e) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)e, this.noSessionFactoryWithJndiName$str(), (Object)sfJNDIName);
    }

    protected String noSessionFactoryWithJndiName$str() {
        return "HHH000184: No session factory with JNDI name %s";
    }

    @Override
    public final void optimisticLockFailures(long optimisticFailureCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.optimisticLockFailures$str(), (Object)optimisticFailureCount);
    }

    protected String optimisticLockFailures$str() {
        return "HHH000187: Optimistic lock failures: %s";
    }

    @Override
    public final void orderByAnnotationIndexedCollection() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.orderByAnnotationIndexedCollection$str(), new Object[0]);
    }

    protected String orderByAnnotationIndexedCollection$str() {
        return "HHH000189: @OrderBy not allowed for an indexed collection, annotation ignored.";
    }

    @Override
    public final void overridingTransactionStrategyDangerous(String transactionStrategy) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.overridingTransactionStrategyDangerous$str(), (Object)transactionStrategy);
    }

    protected String overridingTransactionStrategyDangerous$str() {
        return "HHH000193: Overriding %s is dangerous, this might break the EJB3 specification implementation";
    }

    @Override
    public final void packageNotFound(String packageName) {
        this.log.logf(FQCN, Logger.Level.DEBUG, null, this.packageNotFound$str(), (Object)packageName);
    }

    protected String packageNotFound$str() {
        return "HHH000194: Package not found or wo package-info.java: %s";
    }

    @Override
    public final void parsingXmlError(int lineNumber, String message) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.parsingXmlError$str(), (Object)lineNumber, (Object)message);
    }

    protected String parsingXmlError$str() {
        return "HHH000196: Error parsing XML (%s) : %s";
    }

    @Override
    public final void parsingXmlErrorForFile(String file, int lineNumber, String message) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.parsingXmlErrorForFile$str(), (Object)file, (Object)lineNumber, (Object)message);
    }

    protected String parsingXmlErrorForFile$str() {
        return "HHH000197: Error parsing XML: %s(%s) %s";
    }

    @Override
    public final void parsingXmlWarning(int lineNumber, String message) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.parsingXmlWarning$str(), (Object)lineNumber, (Object)message);
    }

    protected String parsingXmlWarning$str() {
        return "HHH000198: Warning parsing XML (%s) : %s";
    }

    @Override
    public final void parsingXmlWarningForFile(String file, int lineNumber, String message) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.parsingXmlWarningForFile$str(), (Object)file, (Object)lineNumber, (Object)message);
    }

    protected String parsingXmlWarningForFile$str() {
        return "HHH000199: Warning parsing XML: %s(%s) %s";
    }

    @Override
    public final void persistenceProviderCallerDoesNotImplementEjb3SpecCorrectly() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.persistenceProviderCallerDoesNotImplementEjb3SpecCorrectly$str(), new Object[0]);
    }

    protected String persistenceProviderCallerDoesNotImplementEjb3SpecCorrectly$str() {
        return "HHH000200: Persistence provider caller does not implement the EJB3 spec correctly.PersistenceUnitInfo.getNewTempClassLoader() is null.";
    }

    @Override
    public final void pooledOptimizerReportedInitialValue(IntegralDataTypeHolder value) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.pooledOptimizerReportedInitialValue$str(), (Object)value);
    }

    protected String pooledOptimizerReportedInitialValue$str() {
        return "HHH000201: Pooled optimizer source reported [%s] as the initial value; use of 1 or greater highly recommended";
    }

    @Override
    public final void preparedStatementAlreadyInBatch(String sql) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.preparedStatementAlreadyInBatch$str(), (Object)sql);
    }

    protected String preparedStatementAlreadyInBatch$str() {
        return "HHH000202: PreparedStatement was already in the batch, [%s].";
    }

    @Override
    public final void processEqualityExpression() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.processEqualityExpression$str(), new Object[0]);
    }

    protected String processEqualityExpression$str() {
        return "HHH000203: processEqualityExpression() : No expression to process!";
    }

    @Override
    public final void processingPersistenceUnitInfoName(String persistenceUnitName) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.processingPersistenceUnitInfoName$str(), (Object)persistenceUnitName);
    }

    protected String processingPersistenceUnitInfoName$str() {
        return "HHH000204: Processing PersistenceUnitInfo [name: %s]";
    }

    @Override
    public final void propertiesLoaded(Properties maskOut) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.propertiesLoaded$str(), (Object)maskOut);
    }

    protected String propertiesLoaded$str() {
        return "HHH000205: Loaded properties from resource hibernate.properties: %s";
    }

    @Override
    public final void propertiesNotFound() {
        this.log.logf(FQCN, Logger.Level.DEBUG, null, this.propertiesNotFound$str(), new Object[0]);
    }

    protected String propertiesNotFound$str() {
        return "HHH000206: hibernate.properties not found";
    }

    @Override
    public final void propertyNotFound(String property) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.propertyNotFound$str(), (Object)property);
    }

    protected String propertyNotFound$str() {
        return "HHH000207: Property %s not found in class but described in <mapping-file/> (possible typo error)";
    }

    @Override
    public final void proxoolProviderClassNotFound(String proxoolProviderClassName) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.proxoolProviderClassNotFound$str(), (Object)proxoolProviderClassName);
    }

    protected String proxoolProviderClassNotFound$str() {
        return "HHH000209: proxool properties were encountered, but the %s provider class was not found on the classpath; these properties are going to be ignored.";
    }

    @Override
    public final void queriesExecuted(long queryExecutionCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.queriesExecuted$str(), (Object)queryExecutionCount);
    }

    protected String queriesExecuted$str() {
        return "HHH000210: Queries executed to database: %s";
    }

    @Override
    public final void queryCacheHits(long queryCacheHitCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.queryCacheHits$str(), (Object)queryCacheHitCount);
    }

    protected String queryCacheHits$str() {
        return "HHH000213: Query cache hits: %s";
    }

    @Override
    public final void queryCacheMisses(long queryCacheMissCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.queryCacheMisses$str(), (Object)queryCacheMissCount);
    }

    protected String queryCacheMisses$str() {
        return "HHH000214: Query cache misses: %s";
    }

    @Override
    public final void queryCachePuts(long queryCachePutCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.queryCachePuts$str(), (Object)queryCachePutCount);
    }

    protected String queryCachePuts$str() {
        return "HHH000215: Query cache puts: %s";
    }

    @Override
    public final void rdmsOs2200Dialect() {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.rdmsOs2200Dialect$str(), new Object[0]);
    }

    protected String rdmsOs2200Dialect$str() {
        return "HHH000218: RDMSOS2200Dialect version: 1.0";
    }

    @Override
    public final void readingCachedMappings(File cachedFile) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.readingCachedMappings$str(), (Object)cachedFile);
    }

    protected String readingCachedMappings$str() {
        return "HHH000219: Reading mappings from cache file: %s";
    }

    @Override
    public final void readingMappingsFromFile(String path) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.readingMappingsFromFile$str(), (Object)path);
    }

    protected String readingMappingsFromFile$str() {
        return "HHH000220: Reading mappings from file: %s";
    }

    @Override
    public final void readingMappingsFromResource(String resourceName) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.readingMappingsFromResource$str(), (Object)resourceName);
    }

    protected String readingMappingsFromResource$str() {
        return "HHH000221: Reading mappings from resource: %s";
    }

    @Override
    public final void readOnlyCacheConfiguredForMutableCollection(String name) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.readOnlyCacheConfiguredForMutableCollection$str(), (Object)name);
    }

    protected String readOnlyCacheConfiguredForMutableCollection$str() {
        return "HHH000222: read-only cache configured for mutable collection [%s]";
    }

    @Override
    public final void recognizedObsoleteHibernateNamespace(String oldHibernateNamespace, String hibernateNamespace) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.recognizedObsoleteHibernateNamespace$str(), (Object)oldHibernateNamespace, (Object)hibernateNamespace);
    }

    protected String recognizedObsoleteHibernateNamespace$str() {
        return "HHH000223: Recognized obsolete hibernate namespace %s. Use namespace %s instead. Refer to Hibernate 3.6 Migration Guide!";
    }

    @Override
    public final void renamedProperty(Object propertyName, Object newPropertyName) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.renamedProperty$str(), propertyName, newPropertyName);
    }

    protected String renamedProperty$str() {
        return "HHH000225: Property [%s] has been renamed to [%s]; update your properties appropriately";
    }

    @Override
    public final void requiredDifferentProvider(String provider) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.requiredDifferentProvider$str(), (Object)provider);
    }

    protected String requiredDifferentProvider$str() {
        return "HHH000226: Required a different provider: %s";
    }

    @Override
    public final void runningHbm2ddlSchemaExport() {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.runningHbm2ddlSchemaExport$str(), new Object[0]);
    }

    protected String runningHbm2ddlSchemaExport$str() {
        return "HHH000227: Running hbm2ddl schema export";
    }

    @Override
    public final void runningHbm2ddlSchemaUpdate() {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.runningHbm2ddlSchemaUpdate$str(), new Object[0]);
    }

    protected String runningHbm2ddlSchemaUpdate$str() {
        return "HHH000228: Running hbm2ddl schema update";
    }

    @Override
    public final void runningSchemaValidator() {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.runningSchemaValidator$str(), new Object[0]);
    }

    protected String runningSchemaValidator$str() {
        return "HHH000229: Running schema validator";
    }

    @Override
    public final void schemaExportComplete() {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.schemaExportComplete$str(), new Object[0]);
    }

    protected String schemaExportComplete$str() {
        return "HHH000230: Schema export complete";
    }

    @Override
    public final void schemaExportUnsuccessful(Exception e) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)e, this.schemaExportUnsuccessful$str(), new Object[0]);
    }

    protected String schemaExportUnsuccessful$str() {
        return "HHH000231: Schema export unsuccessful";
    }

    @Override
    public final void schemaUpdateComplete() {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.schemaUpdateComplete$str(), new Object[0]);
    }

    protected String schemaUpdateComplete$str() {
        return "HHH000232: Schema update complete";
    }

    @Override
    public final void scopingTypesToSessionFactoryAfterAlreadyScoped(SessionFactoryImplementor factory, SessionFactoryImplementor factory2) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.scopingTypesToSessionFactoryAfterAlreadyScoped$str(), (Object)factory, (Object)factory2);
    }

    protected String scopingTypesToSessionFactoryAfterAlreadyScoped$str() {
        return "HHH000233: Scoping types to session factory %s after already scoped %s";
    }

    @Override
    public final void searchingForMappingDocuments(String name) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.searchingForMappingDocuments$str(), (Object)name);
    }

    protected String searchingForMappingDocuments$str() {
        return "HHH000235: Searching for mapping documents in jar: %s";
    }

    @Override
    public final void secondLevelCacheHits(long secondLevelCacheHitCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.secondLevelCacheHits$str(), (Object)secondLevelCacheHitCount);
    }

    protected String secondLevelCacheHits$str() {
        return "HHH000237: Second level cache hits: %s";
    }

    @Override
    public final void secondLevelCacheMisses(long secondLevelCacheMissCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.secondLevelCacheMisses$str(), (Object)secondLevelCacheMissCount);
    }

    protected String secondLevelCacheMisses$str() {
        return "HHH000238: Second level cache misses: %s";
    }

    @Override
    public final void secondLevelCachePuts(long secondLevelCachePutCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.secondLevelCachePuts$str(), (Object)secondLevelCachePutCount);
    }

    protected String secondLevelCachePuts$str() {
        return "HHH000239: Second level cache puts: %s";
    }

    @Override
    public final void serviceProperties(Properties properties) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.serviceProperties$str(), (Object)properties);
    }

    protected String serviceProperties$str() {
        return "HHH000240: Service properties: %s";
    }

    @Override
    public final void sessionsClosed(long sessionCloseCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.sessionsClosed$str(), (Object)sessionCloseCount);
    }

    protected String sessionsClosed$str() {
        return "HHH000241: Sessions closed: %s";
    }

    @Override
    public final void sessionsOpened(long sessionOpenCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.sessionsOpened$str(), (Object)sessionOpenCount);
    }

    protected String sessionsOpened$str() {
        return "HHH000242: Sessions opened: %s";
    }

    @Override
    public final void sortAnnotationIndexedCollection() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.sortAnnotationIndexedCollection$str(), new Object[0]);
    }

    protected String sortAnnotationIndexedCollection$str() {
        return "HHH000244: @Sort not allowed for an indexed collection, annotation ignored.";
    }

    @Override
    public final void splitQueries(String sourceQuery, int length) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.splitQueries$str(), (Object)sourceQuery, (Object)length);
    }

    protected String splitQueries$str() {
        return "HHH000245: Manipulation query [%s] resulted in [%s] split queries";
    }

    @Override
    public final void sqlWarning(int errorCode, String sqlState) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.sqlWarning$str(), (Object)errorCode, (Object)sqlState);
    }

    protected String sqlWarning$str() {
        return "HHH000247: SQL Error: %s, SQLState: %s";
    }

    @Override
    public final void startingQueryCache(String region) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.startingQueryCache$str(), (Object)region);
    }

    protected String startingQueryCache$str() {
        return "HHH000248: Starting query cache at region: %s";
    }

    @Override
    public final void startingServiceAtJndiName(String boundName) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.startingServiceAtJndiName$str(), (Object)boundName);
    }

    protected String startingServiceAtJndiName$str() {
        return "HHH000249: Starting service at JNDI name: %s";
    }

    @Override
    public final void startingUpdateTimestampsCache(String region) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.startingUpdateTimestampsCache$str(), (Object)region);
    }

    protected String startingUpdateTimestampsCache$str() {
        return "HHH000250: Starting update timestamps cache at region: %s";
    }

    @Override
    public final void startTime(long startTime) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.startTime$str(), (Object)startTime);
    }

    protected String startTime$str() {
        return "HHH000251: Start time: %s";
    }

    @Override
    public final void statementsClosed(long closeStatementCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.statementsClosed$str(), (Object)closeStatementCount);
    }

    protected String statementsClosed$str() {
        return "HHH000252: Statements closed: %s";
    }

    @Override
    public final void statementsPrepared(long prepareStatementCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.statementsPrepared$str(), (Object)prepareStatementCount);
    }

    protected String statementsPrepared$str() {
        return "HHH000253: Statements prepared: %s";
    }

    @Override
    public final void stoppingService() {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.stoppingService$str(), new Object[0]);
    }

    protected String stoppingService$str() {
        return "HHH000255: Stopping service";
    }

    @Override
    public final void subResolverException(String message) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.subResolverException$str(), (Object)message);
    }

    protected String subResolverException$str() {
        return "HHH000257: sub-resolver threw unexpected exception, continuing to next : %s";
    }

    @Override
    public final void successfulTransactions(long committedTransactionCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.successfulTransactions$str(), (Object)committedTransactionCount);
    }

    protected String successfulTransactions$str() {
        return "HHH000258: Successful transactions: %s";
    }

    @Override
    public final void synchronizationAlreadyRegistered(Synchronization synchronization) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.synchronizationAlreadyRegistered$str(), (Object)synchronization);
    }

    protected String synchronizationAlreadyRegistered$str() {
        return "HHH000259: Synchronization [%s] was already registered";
    }

    @Override
    public final void synchronizationFailed(Synchronization synchronization, Throwable t) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.synchronizationFailed$str(), (Object)synchronization, (Object)t);
    }

    protected String synchronizationFailed$str() {
        return "HHH000260: Exception calling user Synchronization [%s] : %s";
    }

    @Override
    public final void tableFound(String string) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.tableFound$str(), (Object)string);
    }

    protected String tableFound$str() {
        return "HHH000261: Table found: %s";
    }

    @Override
    public final void tableNotFound(String name) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.tableNotFound$str(), (Object)name);
    }

    protected String tableNotFound$str() {
        return "HHH000262: Table not found: %s";
    }

    @Override
    public final void multipleTablesFound(String name) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.multipleTablesFound$str(), (Object)name);
    }

    protected String multipleTablesFound$str() {
        return "HHH000263: More than one table found: %s";
    }

    @Override
    public final void transactions(long transactionCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.transactions$str(), (Object)transactionCount);
    }

    protected String transactions$str() {
        return "HHH000266: Transactions: %s";
    }

    @Override
    public final void transactionStartedOnNonRootSession() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.transactionStartedOnNonRootSession$str(), new Object[0]);
    }

    protected String transactionStartedOnNonRootSession$str() {
        return "HHH000267: Transaction started on non-root session";
    }

    @Override
    public final void transactionStrategy(String strategyClassName) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.transactionStrategy$str(), (Object)strategyClassName);
    }

    protected String transactionStrategy$str() {
        return "HHH000268: Transaction strategy: %s";
    }

    @Override
    public final void typeDefinedNoRegistrationKeys(BasicType type) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.typeDefinedNoRegistrationKeys$str(), (Object)type);
    }

    protected String typeDefinedNoRegistrationKeys$str() {
        return "HHH000269: Type [%s] defined no registration keys; ignoring";
    }

    @Override
    public final void typeRegistrationOverridesPrevious(String key, Type old) {
        this.log.logf(FQCN, Logger.Level.DEBUG, null, this.typeRegistrationOverridesPrevious$str(), (Object)key, (Object)old);
    }

    protected String typeRegistrationOverridesPrevious$str() {
        return "HHH000270: Type registration [%s] overrides previous : %s";
    }

    @Override
    public final void unableToAccessEjb3Configuration(NamingException e) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable)e, this.unableToAccessEjb3Configuration$str(), new Object[0]);
    }

    protected String unableToAccessEjb3Configuration$str() {
        return "HHH000271: Naming exception occurred accessing Ejb3Configuration";
    }

    @Override
    public final void unableToAccessSessionFactory(String sfJNDIName, NamingException e) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)e, this.unableToAccessSessionFactory$str(), (Object)sfJNDIName);
    }

    protected String unableToAccessSessionFactory$str() {
        return "HHH000272: Error while accessing session factory with JNDI name %s";
    }

    @Override
    public final void unableToAccessTypeInfoResultSet(String string) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToAccessTypeInfoResultSet$str(), (Object)string);
    }

    protected String unableToAccessTypeInfoResultSet$str() {
        return "HHH000273: Error accessing type info result set : %s";
    }

    @Override
    public final void unableToApplyConstraints(String className, Exception e) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable)e, this.unableToApplyConstraints$str(), (Object)className);
    }

    protected String unableToApplyConstraints$str() {
        return "HHH000274: Unable to apply constraints on DDL for %s";
    }

    @Override
    public final void unableToBindEjb3ConfigurationToJndi(JndiException e) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable)((Object)e), this.unableToBindEjb3ConfigurationToJndi$str(), new Object[0]);
    }

    protected String unableToBindEjb3ConfigurationToJndi$str() {
        return "HHH000276: Could not bind Ejb3Configuration to JNDI";
    }

    @Override
    public final void unableToBindFactoryToJndi(JndiException e) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable)((Object)e), this.unableToBindFactoryToJndi$str(), new Object[0]);
    }

    protected String unableToBindFactoryToJndi$str() {
        return "HHH000277: Could not bind factory to JNDI";
    }

    @Override
    public final void unableToBindValueToParameter(String nullSafeToString, int index, String message) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.unableToBindValueToParameter$str(), (Object)nullSafeToString, (Object)index, (Object)message);
    }

    protected String unableToBindValueToParameter$str() {
        return "HHH000278: Could not bind value '%s' to parameter: %s; %s";
    }

    @Override
    public final void unableToBuildEnhancementMetamodel(String className) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.unableToBuildEnhancementMetamodel$str(), (Object)className);
    }

    protected String unableToBuildEnhancementMetamodel$str() {
        return "HHH000279: Unable to build enhancement metamodel for %s";
    }

    @Override
    public final void unableToBuildSessionFactoryUsingMBeanClasspath(String message) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.unableToBuildSessionFactoryUsingMBeanClasspath$str(), (Object)message);
    }

    protected String unableToBuildSessionFactoryUsingMBeanClasspath$str() {
        return "HHH000280: Could not build SessionFactory using the MBean classpath - will try again using client classpath: %s";
    }

    @Override
    public final void unableToCleanUpCallableStatement(SQLException e) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable)e, this.unableToCleanUpCallableStatement$str(), new Object[0]);
    }

    protected String unableToCleanUpCallableStatement$str() {
        return "HHH000281: Unable to clean up callable statement";
    }

    @Override
    public final void unableToCleanUpPreparedStatement(SQLException e) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable)e, this.unableToCleanUpPreparedStatement$str(), new Object[0]);
    }

    protected String unableToCleanUpPreparedStatement$str() {
        return "HHH000282: Unable to clean up prepared statement";
    }

    @Override
    public final void unableToCleanupTemporaryIdTable(Throwable t) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToCleanupTemporaryIdTable$str(), (Object)t);
    }

    protected String unableToCleanupTemporaryIdTable$str() {
        return "HHH000283: Unable to cleanup temporary id table after use [%s]";
    }

    @Override
    public final void unableToCloseConnection(Exception e) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)e, this.unableToCloseConnection$str(), new Object[0]);
    }

    protected String unableToCloseConnection$str() {
        return "HHH000284: Error closing connection";
    }

    @Override
    public final void unableToCloseInitialContext(String string) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.unableToCloseInitialContext$str(), (Object)string);
    }

    protected String unableToCloseInitialContext$str() {
        return "HHH000285: Error closing InitialContext [%s]";
    }

    @Override
    public final void unableToCloseInputFiles(String name, IOException e) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)e, this.unableToCloseInputFiles$str(), (Object)name);
    }

    protected String unableToCloseInputFiles$str() {
        return "HHH000286: Error closing input files: %s";
    }

    @Override
    public final void unableToCloseInputStream(IOException e) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable)e, this.unableToCloseInputStream$str(), new Object[0]);
    }

    protected String unableToCloseInputStream$str() {
        return "HHH000287: Could not close input stream";
    }

    @Override
    public final void unableToCloseInputStreamForResource(String resourceName, IOException e) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable)e, this.unableToCloseInputStreamForResource$str(), (Object)resourceName);
    }

    protected String unableToCloseInputStreamForResource$str() {
        return "HHH000288: Could not close input stream for %s";
    }

    @Override
    public final void unableToCloseIterator(SQLException e) {
        this.log.logf(FQCN, Logger.Level.INFO, (Throwable)e, this.unableToCloseIterator$str(), new Object[0]);
    }

    protected String unableToCloseIterator$str() {
        return "HHH000289: Unable to close iterator";
    }

    @Override
    public final void unableToCloseJar(String message) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.unableToCloseJar$str(), (Object)message);
    }

    protected String unableToCloseJar$str() {
        return "HHH000290: Could not close jar: %s";
    }

    @Override
    public final void unableToCloseOutputFile(String outputFile, IOException e) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)e, this.unableToCloseOutputFile$str(), (Object)outputFile);
    }

    protected String unableToCloseOutputFile$str() {
        return "HHH000291: Error closing output file: %s";
    }

    @Override
    public final void unableToCloseOutputStream(IOException e) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable)e, this.unableToCloseOutputStream$str(), new Object[0]);
    }

    protected String unableToCloseOutputStream$str() {
        return "HHH000292: IOException occurred closing output stream";
    }

    @Override
    public final void unableToCloseSession(HibernateException e) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)((Object)e), this.unableToCloseSession$str(), new Object[0]);
    }

    protected String unableToCloseSession$str() {
        return "HHH000294: Could not close session";
    }

    @Override
    public final void unableToCloseSessionDuringRollback(Exception e) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)e, this.unableToCloseSessionDuringRollback$str(), new Object[0]);
    }

    protected String unableToCloseSessionDuringRollback$str() {
        return "HHH000295: Could not close session during rollback";
    }

    @Override
    public final void unableToCloseStream(IOException e) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable)e, this.unableToCloseStream$str(), new Object[0]);
    }

    protected String unableToCloseStream$str() {
        return "HHH000296: IOException occurred closing stream";
    }

    @Override
    public final void unableToCloseStreamError(IOException error) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.unableToCloseStreamError$str(), (Object)error);
    }

    protected String unableToCloseStreamError$str() {
        return "HHH000297: Could not close stream on hibernate.properties: %s";
    }

    protected String unableToCommitJta$str() {
        return "HHH000298: JTA commit failed";
    }

    @Override
    public final String unableToCommitJta() {
        return String.format(this.getLoggingLocale(), this.unableToCommitJta$str(), new Object[0]);
    }

    @Override
    public final void unableToCompleteSchemaUpdate(Exception e) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)e, this.unableToCompleteSchemaUpdate$str(), new Object[0]);
    }

    protected String unableToCompleteSchemaUpdate$str() {
        return "HHH000299: Could not complete schema update";
    }

    @Override
    public final void unableToCompleteSchemaValidation(SQLException e) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)e, this.unableToCompleteSchemaValidation$str(), new Object[0]);
    }

    protected String unableToCompleteSchemaValidation$str() {
        return "HHH000300: Could not complete schema validation";
    }

    @Override
    public final void unableToConfigureSqlExceptionConverter(HibernateException e) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToConfigureSqlExceptionConverter$str(), (Object)e);
    }

    protected String unableToConfigureSqlExceptionConverter$str() {
        return "HHH000301: Unable to configure SQLExceptionConverter : %s";
    }

    @Override
    public final void unableToConstructCurrentSessionContext(String impl, Throwable e) {
        this.log.logf(FQCN, Logger.Level.ERROR, e, this.unableToConstructCurrentSessionContext$str(), (Object)impl);
    }

    protected String unableToConstructCurrentSessionContext$str() {
        return "HHH000302: Unable to construct current session context [%s]";
    }

    @Override
    public final void unableToConstructSqlExceptionConverter(Throwable t) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToConstructSqlExceptionConverter$str(), (Object)t);
    }

    protected String unableToConstructSqlExceptionConverter$str() {
        return "HHH000303: Unable to construct instance of specified SQLExceptionConverter : %s";
    }

    @Override
    public final void unableToCopySystemProperties() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToCopySystemProperties$str(), new Object[0]);
    }

    protected String unableToCopySystemProperties$str() {
        return "HHH000304: Could not copy system properties, system properties will be ignored";
    }

    @Override
    public final void unableToCreateProxyFactory(String entityName, HibernateException e) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable)((Object)e), this.unableToCreateProxyFactory$str(), (Object)entityName);
    }

    protected String unableToCreateProxyFactory$str() {
        return "HHH000305: Could not create proxy factory for:%s";
    }

    @Override
    public final void unableToCreateSchema(Exception e) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)e, this.unableToCreateSchema$str(), new Object[0]);
    }

    protected String unableToCreateSchema$str() {
        return "HHH000306: Error creating schema ";
    }

    @Override
    public final void unableToDeserializeCache(String path, SerializationException error) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToDeserializeCache$str(), (Object)path, (Object)error);
    }

    protected String unableToDeserializeCache$str() {
        return "HHH000307: Could not deserialize cache file: %s : %s";
    }

    @Override
    public final void unableToDestroyCache(String message) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToDestroyCache$str(), (Object)message);
    }

    protected String unableToDestroyCache$str() {
        return "HHH000308: Unable to destroy cache: %s";
    }

    @Override
    public final void unableToDestroyQueryCache(String region, String message) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToDestroyQueryCache$str(), (Object)region, (Object)message);
    }

    protected String unableToDestroyQueryCache$str() {
        return "HHH000309: Unable to destroy query cache: %s: %s";
    }

    @Override
    public final void unableToDestroyUpdateTimestampsCache(String region, String message) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToDestroyUpdateTimestampsCache$str(), (Object)region, (Object)message);
    }

    protected String unableToDestroyUpdateTimestampsCache$str() {
        return "HHH000310: Unable to destroy update timestamps cache: %s: %s";
    }

    @Override
    public final void unableToDetermineLockModeValue(String hintName, Object value) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.unableToDetermineLockModeValue$str(), (Object)hintName, value);
    }

    protected String unableToDetermineLockModeValue$str() {
        return "HHH000311: Unable to determine lock mode value : %s -> %s";
    }

    protected String unableToDetermineTransactionStatus$str() {
        return "HHH000312: Could not determine transaction status";
    }

    @Override
    public final String unableToDetermineTransactionStatus() {
        return String.format(this.getLoggingLocale(), this.unableToDetermineTransactionStatus$str(), new Object[0]);
    }

    protected String unableToDetermineTransactionStatusAfterCommit$str() {
        return "HHH000313: Could not determine transaction status after commit";
    }

    @Override
    public final String unableToDetermineTransactionStatusAfterCommit() {
        return String.format(this.getLoggingLocale(), this.unableToDetermineTransactionStatusAfterCommit$str(), new Object[0]);
    }

    @Override
    public final void unableToDropTemporaryIdTable(String message) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToDropTemporaryIdTable$str(), (Object)message);
    }

    protected String unableToDropTemporaryIdTable$str() {
        return "HHH000314: Unable to evictData temporary id table after use [%s]";
    }

    @Override
    public final void unableToExecuteBatch(Exception e, String sql) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.unableToExecuteBatch$str(), (Object)e, (Object)sql);
    }

    protected String unableToExecuteBatch$str() {
        return "HHH000315: Exception executing batch [%s], SQL: %s";
    }

    @Override
    public final void unableToExecuteResolver(DialectResolver abstractDialectResolver, String message) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToExecuteResolver$str(), (Object)abstractDialectResolver, (Object)message);
    }

    protected String unableToExecuteResolver$str() {
        return "HHH000316: Error executing resolver [%s] : %s";
    }

    @Override
    public final void unableToFindPersistenceXmlInClasspath() {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.unableToFindPersistenceXmlInClasspath$str(), new Object[0]);
    }

    protected String unableToFindPersistenceXmlInClasspath$str() {
        return "HHH000318: Could not find any META-INF/persistence.xml file in the classpath";
    }

    @Override
    public final void unableToGetDatabaseMetadata(SQLException e) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)e, this.unableToGetDatabaseMetadata$str(), new Object[0]);
    }

    protected String unableToGetDatabaseMetadata$str() {
        return "HHH000319: Could not get database metadata";
    }

    @Override
    public final void unableToInstantiateConfiguredSchemaNameResolver(String resolverClassName, String message) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToInstantiateConfiguredSchemaNameResolver$str(), (Object)resolverClassName, (Object)message);
    }

    protected String unableToInstantiateConfiguredSchemaNameResolver$str() {
        return "HHH000320: Unable to instantiate configured schema name resolver [%s] %s";
    }

    @Override
    public final void unableToLocateCustomOptimizerClass(String type) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToLocateCustomOptimizerClass$str(), (Object)type);
    }

    protected String unableToLocateCustomOptimizerClass$str() {
        return "HHH000321: Unable to interpret specified optimizer [%s], falling back to noop";
    }

    @Override
    public final void unableToInstantiateOptimizer(String type) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToInstantiateOptimizer$str(), (Object)type);
    }

    protected String unableToInstantiateOptimizer$str() {
        return "HHH000322: Unable to instantiate specified optimizer [%s], falling back to noop";
    }

    @Override
    public final void unableToInstantiateUuidGenerationStrategy(Exception ignore) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToInstantiateUuidGenerationStrategy$str(), (Object)ignore);
    }

    protected String unableToInstantiateUuidGenerationStrategy$str() {
        return "HHH000325: Unable to instantiate UUID generation strategy class : %s";
    }

    @Override
    public final void unableToJoinTransaction(String transactionStrategy) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToJoinTransaction$str(), (Object)transactionStrategy);
    }

    protected String unableToJoinTransaction$str() {
        return "HHH000326: Cannot join transaction: do not override %s";
    }

    @Override
    public final void unableToLoadCommand(HibernateException e) {
        this.log.logf(FQCN, Logger.Level.INFO, (Throwable)((Object)e), this.unableToLoadCommand$str(), new Object[0]);
    }

    protected String unableToLoadCommand$str() {
        return "HHH000327: Error performing load command";
    }

    @Override
    public final void unableToLoadDerbyDriver(String message) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToLoadDerbyDriver$str(), (Object)message);
    }

    protected String unableToLoadDerbyDriver$str() {
        return "HHH000328: Unable to load/access derby driver class sysinfo to check versions : %s";
    }

    @Override
    public final void unableToLoadProperties() {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.unableToLoadProperties$str(), new Object[0]);
    }

    protected String unableToLoadProperties$str() {
        return "HHH000329: Problem loading properties from hibernate.properties";
    }

    protected String unableToLocateConfigFile$str() {
        return "HHH000330: Unable to locate config file: %s";
    }

    @Override
    public final String unableToLocateConfigFile(String path) {
        return String.format(this.getLoggingLocale(), this.unableToLocateConfigFile$str(), path);
    }

    @Override
    public final void unableToLocateConfiguredSchemaNameResolver(String resolverClassName, String message) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToLocateConfiguredSchemaNameResolver$str(), (Object)resolverClassName, (Object)message);
    }

    protected String unableToLocateConfiguredSchemaNameResolver$str() {
        return "HHH000331: Unable to locate configured schema name resolver class [%s] %s";
    }

    @Override
    public final void unableToLocateMBeanServer() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToLocateMBeanServer$str(), new Object[0]);
    }

    protected String unableToLocateMBeanServer$str() {
        return "HHH000332: Unable to locate MBeanServer on JMX service shutdown";
    }

    @Override
    public final void unableToLocateUuidGenerationStrategy(String strategyClassName) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToLocateUuidGenerationStrategy$str(), (Object)strategyClassName);
    }

    protected String unableToLocateUuidGenerationStrategy$str() {
        return "HHH000334: Unable to locate requested UUID generation strategy class : %s";
    }

    @Override
    public final void unableToLogSqlWarnings(SQLException sqle) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToLogSqlWarnings$str(), (Object)sqle);
    }

    protected String unableToLogSqlWarnings$str() {
        return "HHH000335: Unable to log SQLWarnings : %s";
    }

    @Override
    public final void unableToLogWarnings(SQLException e) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable)e, this.unableToLogWarnings$str(), new Object[0]);
    }

    protected String unableToLogWarnings$str() {
        return "HHH000336: Could not log warnings";
    }

    @Override
    public final void unableToMarkForRollbackOnPersistenceException(Exception e) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)e, this.unableToMarkForRollbackOnPersistenceException$str(), new Object[0]);
    }

    protected String unableToMarkForRollbackOnPersistenceException$str() {
        return "HHH000337: Unable to mark for rollback on PersistenceException: ";
    }

    @Override
    public final void unableToMarkForRollbackOnTransientObjectException(Exception e) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)e, this.unableToMarkForRollbackOnTransientObjectException$str(), new Object[0]);
    }

    protected String unableToMarkForRollbackOnTransientObjectException$str() {
        return "HHH000338: Unable to mark for rollback on TransientObjectException: ";
    }

    @Override
    public final void unableToObtainConnectionMetadata(SQLException error) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToObtainConnectionMetadata$str(), (Object)error);
    }

    protected String unableToObtainConnectionMetadata$str() {
        return "HHH000339: Could not obtain connection metadata: %s";
    }

    @Override
    public final void unableToObtainConnectionToQueryMetadata(Exception e) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable)e, this.unableToObtainConnectionToQueryMetadata$str(), new Object[0]);
    }

    protected String unableToObtainConnectionToQueryMetadata$str() {
        return "HHH000342: Could not obtain connection to query metadata";
    }

    @Override
    public final void unableToObtainInitialContext(NamingException e) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)e, this.unableToObtainInitialContext$str(), new Object[0]);
    }

    protected String unableToObtainInitialContext$str() {
        return "HHH000343: Could not obtain initial context";
    }

    @Override
    public final void unableToParseMetadata(String packageName) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.unableToParseMetadata$str(), (Object)packageName);
    }

    protected String unableToParseMetadata$str() {
        return "HHH000344: Could not parse the package-level metadata [%s]";
    }

    protected String unableToPerformJdbcCommit$str() {
        return "HHH000345: JDBC commit failed";
    }

    @Override
    public final String unableToPerformJdbcCommit() {
        return String.format(this.getLoggingLocale(), this.unableToPerformJdbcCommit$str(), new Object[0]);
    }

    @Override
    public final void unableToPerformManagedFlush(String message) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.unableToPerformManagedFlush$str(), (Object)message);
    }

    protected String unableToPerformManagedFlush$str() {
        return "HHH000346: Error during managed flush [%s]";
    }

    protected String unableToQueryDatabaseMetadata$str() {
        return "HHH000347: Unable to query java.sql.DatabaseMetaData";
    }

    @Override
    public final String unableToQueryDatabaseMetadata() {
        return String.format(this.getLoggingLocale(), this.unableToQueryDatabaseMetadata$str(), new Object[0]);
    }

    @Override
    public final void unableToReadClass(String message) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.unableToReadClass$str(), (Object)message);
    }

    protected String unableToReadClass$str() {
        return "HHH000348: Unable to read class: %s";
    }

    @Override
    public final void unableToReadColumnValueFromResultSet(String name, String message) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.unableToReadColumnValueFromResultSet$str(), (Object)name, (Object)message);
    }

    protected String unableToReadColumnValueFromResultSet$str() {
        return "HHH000349: Could not read column value from result set: %s; %s";
    }

    protected String unableToReadHiValue$str() {
        return "HHH000350: Could not read a hi value - you need to populate the table: %s";
    }

    @Override
    public final String unableToReadHiValue(String tableName) {
        return String.format(this.getLoggingLocale(), this.unableToReadHiValue$str(), tableName);
    }

    @Override
    public final void unableToReadOrInitHiValue(SQLException e) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)e, this.unableToReadOrInitHiValue$str(), new Object[0]);
    }

    protected String unableToReadOrInitHiValue$str() {
        return "HHH000351: Could not read or init a hi value";
    }

    @Override
    public final void unableToReleaseBatchStatement() {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.unableToReleaseBatchStatement$str(), new Object[0]);
    }

    protected String unableToReleaseBatchStatement$str() {
        return "HHH000352: Unable to release batch statement...";
    }

    @Override
    public final void unableToReleaseCacheLock(CacheException ce) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.unableToReleaseCacheLock$str(), (Object)ce);
    }

    protected String unableToReleaseCacheLock$str() {
        return "HHH000353: Could not release a cache lock : %s";
    }

    @Override
    public final void unableToReleaseContext(String message) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.unableToReleaseContext$str(), (Object)message);
    }

    protected String unableToReleaseContext$str() {
        return "HHH000354: Unable to release initial context: %s";
    }

    @Override
    public final void unableToReleaseCreatedMBeanServer(String string) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToReleaseCreatedMBeanServer$str(), (Object)string);
    }

    protected String unableToReleaseCreatedMBeanServer$str() {
        return "HHH000355: Unable to release created MBeanServer : %s";
    }

    @Override
    public final void unableToReleaseIsolatedConnection(Throwable ignore) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.unableToReleaseIsolatedConnection$str(), (Object)ignore);
    }

    protected String unableToReleaseIsolatedConnection$str() {
        return "HHH000356: Unable to release isolated connection [%s]";
    }

    @Override
    public final void unableToReleaseTypeInfoResultSet() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToReleaseTypeInfoResultSet$str(), new Object[0]);
    }

    protected String unableToReleaseTypeInfoResultSet$str() {
        return "HHH000357: Unable to release type info result set";
    }

    @Override
    public final void unableToRemoveBagJoinFetch() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToRemoveBagJoinFetch$str(), new Object[0]);
    }

    protected String unableToRemoveBagJoinFetch$str() {
        return "HHH000358: Unable to erase previously added bag join fetch";
    }

    @Override
    public final void unableToResolveAggregateFunction(String name) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.unableToResolveAggregateFunction$str(), (Object)name);
    }

    protected String unableToResolveAggregateFunction$str() {
        return "HHH000359: Could not resolve aggregate function [%s]; using standard definition";
    }

    @Override
    public final void unableToResolveMappingFile(String xmlFile) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.unableToResolveMappingFile$str(), (Object)xmlFile);
    }

    protected String unableToResolveMappingFile$str() {
        return "HHH000360: Unable to resolve mapping file [%s]";
    }

    @Override
    public final void unableToRetrieveCache(String namespace, String message) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.unableToRetrieveCache$str(), (Object)namespace, (Object)message);
    }

    protected String unableToRetrieveCache$str() {
        return "HHH000361: Unable to retrieve cache from JNDI [%s]: %s";
    }

    @Override
    public final void unableToRetrieveTypeInfoResultSet(String string) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToRetrieveTypeInfoResultSet$str(), (Object)string);
    }

    protected String unableToRetrieveTypeInfoResultSet$str() {
        return "HHH000362: Unable to retrieve type info result set : %s";
    }

    @Override
    public final void unableToRollbackConnection(Exception ignore) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.unableToRollbackConnection$str(), (Object)ignore);
    }

    protected String unableToRollbackConnection$str() {
        return "HHH000363: Unable to rollback connection on exception [%s]";
    }

    @Override
    public final void unableToRollbackIsolatedTransaction(Exception e, Exception ignore) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.unableToRollbackIsolatedTransaction$str(), (Object)e, (Object)ignore);
    }

    protected String unableToRollbackIsolatedTransaction$str() {
        return "HHH000364: Unable to rollback isolated transaction on error [%s] : [%s]";
    }

    protected String unableToRollbackJta$str() {
        return "HHH000365: JTA rollback failed";
    }

    @Override
    public final String unableToRollbackJta() {
        return String.format(this.getLoggingLocale(), this.unableToRollbackJta$str(), new Object[0]);
    }

    @Override
    public final void unableToRunSchemaUpdate(Exception e) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)e, this.unableToRunSchemaUpdate$str(), new Object[0]);
    }

    protected String unableToRunSchemaUpdate$str() {
        return "HHH000366: Error running schema update";
    }

    @Override
    public final void unableToSetTransactionToRollbackOnly(SystemException e) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)e, this.unableToSetTransactionToRollbackOnly$str(), new Object[0]);
    }

    protected String unableToSetTransactionToRollbackOnly$str() {
        return "HHH000367: Could not set transaction to rollback only";
    }

    @Override
    public final void unableToStopHibernateService(Exception e) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable)e, this.unableToStopHibernateService$str(), new Object[0]);
    }

    protected String unableToStopHibernateService$str() {
        return "HHH000368: Exception while stopping service";
    }

    @Override
    public final void unableToStopService(Class class1, Exception e) {
        this.log.logf(FQCN, Logger.Level.INFO, (Throwable)e, this.unableToStopService$str(), (Object)class1);
    }

    protected String unableToStopService$str() {
        return "HHH000369: Error stopping service [%s]";
    }

    @Override
    public final void unableToSwitchToMethodUsingColumnIndex(Method method) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToSwitchToMethodUsingColumnIndex$str(), (Object)method);
    }

    protected String unableToSwitchToMethodUsingColumnIndex$str() {
        return "HHH000370: Exception switching from method: [%s] to a method using the column index. Reverting to using: [%<s]";
    }

    @Override
    public final void unableToSynchronizeDatabaseStateWithSession(HibernateException he) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.unableToSynchronizeDatabaseStateWithSession$str(), (Object)he);
    }

    protected String unableToSynchronizeDatabaseStateWithSession$str() {
        return "HHH000371: Could not synchronize database state with session: %s";
    }

    @Override
    public final void unableToToggleAutoCommit(Exception e) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)e, this.unableToToggleAutoCommit$str(), new Object[0]);
    }

    protected String unableToToggleAutoCommit$str() {
        return "HHH000372: Could not toggle autocommit";
    }

    @Override
    public final void unableToTransformClass(String message) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.unableToTransformClass$str(), (Object)message);
    }

    protected String unableToTransformClass$str() {
        return "HHH000373: Unable to transform class: %s";
    }

    @Override
    public final void unableToUnbindFactoryFromJndi(JndiException e) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable)((Object)e), this.unableToUnbindFactoryFromJndi$str(), new Object[0]);
    }

    protected String unableToUnbindFactoryFromJndi$str() {
        return "HHH000374: Could not unbind factory from JNDI";
    }

    protected String unableToUpdateHiValue$str() {
        return "HHH000375: Could not update hi value in: %s";
    }

    @Override
    public final String unableToUpdateHiValue(String tableName) {
        return String.format(this.getLoggingLocale(), this.unableToUpdateHiValue$str(), tableName);
    }

    @Override
    public final void unableToUpdateQueryHiValue(String tableName, SQLException e) {
        this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)e, this.unableToUpdateQueryHiValue$str(), (Object)tableName);
    }

    protected String unableToUpdateQueryHiValue$str() {
        return "HHH000376: Could not updateQuery hi value in: %s";
    }

    @Override
    public final void unableToWrapResultSet(SQLException e) {
        this.log.logf(FQCN, Logger.Level.INFO, (Throwable)e, this.unableToWrapResultSet$str(), new Object[0]);
    }

    protected String unableToWrapResultSet$str() {
        return "HHH000377: Error wrapping result set";
    }

    @Override
    public final void unableToWriteCachedFile(String path, String message) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unableToWriteCachedFile$str(), (Object)path, (Object)message);
    }

    protected String unableToWriteCachedFile$str() {
        return "HHH000378: I/O reported error writing cached file : %s: %s";
    }

    @Override
    public final void unexpectedLiteralTokenType(int type) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unexpectedLiteralTokenType$str(), (Object)type);
    }

    protected String unexpectedLiteralTokenType$str() {
        return "HHH000380: Unexpected literal token type [%s] passed for numeric processing";
    }

    @Override
    public final void unexpectedRowCounts() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unexpectedRowCounts$str(), new Object[0]);
    }

    protected String unexpectedRowCounts$str() {
        return "HHH000381: JDBC driver did not return the expected number of row counts";
    }

    @Override
    public final void unknownBytecodeProvider(String providerName, String defaultProvider) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unknownBytecodeProvider$str(), (Object)providerName, (Object)defaultProvider);
    }

    protected String unknownBytecodeProvider$str() {
        return "HHH000382: unrecognized bytecode provider [%s], using [%s] by default";
    }

    @Override
    public final void unknownIngresVersion(int databaseMajorVersion) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unknownIngresVersion$str(), (Object)databaseMajorVersion);
    }

    protected String unknownIngresVersion$str() {
        return "HHH000383: Unknown Ingres major version [%s]; using Ingres 9.2 dialect";
    }

    @Override
    public final void unknownOracleVersion(int databaseMajorVersion) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unknownOracleVersion$str(), (Object)databaseMajorVersion);
    }

    protected String unknownOracleVersion$str() {
        return "HHH000384: Unknown Oracle major version [%s]";
    }

    @Override
    public final void unknownSqlServerVersion(int databaseMajorVersion, Class<? extends Dialect> dialectClass) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unknownSqlServerVersion$str(), (Object)databaseMajorVersion, dialectClass);
    }

    protected String unknownSqlServerVersion$str() {
        return "HHH000385: Unknown Microsoft SQL Server major version [%s] using [%s] dialect";
    }

    @Override
    public final void unregisteredResultSetWithoutStatement() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unregisteredResultSetWithoutStatement$str(), new Object[0]);
    }

    protected String unregisteredResultSetWithoutStatement$str() {
        return "HHH000386: ResultSet had no statement associated with it, but was not yet registered";
    }

    @Override
    public final void unregisteredStatement() {
        this.log.logf(FQCN, Logger.Level.DEBUG, null, this.unregisteredStatement$str(), new Object[0]);
    }

    protected String unregisteredStatement$str() {
        return "HHH000387: ResultSet's statement was not registered";
    }

    @Override
    public final void unsuccessful(String sql) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.unsuccessful$str(), (Object)sql);
    }

    protected String unsuccessful$str() {
        return "HHH000388: Unsuccessful: %s";
    }

    @Override
    public final void unsuccessfulCreate(String string) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.unsuccessfulCreate$str(), (Object)string);
    }

    protected String unsuccessfulCreate$str() {
        return "HHH000389: Unsuccessful: %s";
    }

    @Override
    public final void unsupportedAfterStatement() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unsupportedAfterStatement$str(), new Object[0]);
    }

    protected String unsupportedAfterStatement$str() {
        return "HHH000390: Overriding release mode as connection provider does not support 'after_statement'";
    }

    @Override
    public final void unsupportedIngresVersion() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unsupportedIngresVersion$str(), new Object[0]);
    }

    protected String unsupportedIngresVersion$str() {
        return "HHH000391: Ingres 10 is not yet fully supported; using Ingres 9.3 dialect";
    }

    @Override
    public final void unsupportedInitialValue(String propertyName) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unsupportedInitialValue$str(), (Object)propertyName);
    }

    protected String unsupportedInitialValue$str() {
        return "HHH000392: Hibernate does not support SequenceGenerator.initialValue() unless '%s' set";
    }

    @Override
    public final void unsupportedMultiTableBulkHqlJpaql(int majorVersion, int minorVersion, int buildId) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unsupportedMultiTableBulkHqlJpaql$str(), (Object)majorVersion, (Object)minorVersion, (Object)buildId);
    }

    protected String unsupportedMultiTableBulkHqlJpaql$str() {
        return "HHH000393: The %s.%s.%s version of H2 implements temporary table creation such that it commits current transaction; multi-table, bulk hql/jpaql will not work properly";
    }

    @Override
    public final void unsupportedOracleVersion() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unsupportedOracleVersion$str(), new Object[0]);
    }

    protected String unsupportedOracleVersion$str() {
        return "HHH000394: Oracle 11g is not yet fully supported; using Oracle 10g dialect";
    }

    @Override
    public final void updatingSchema() {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.updatingSchema$str(), new Object[0]);
    }

    protected String updatingSchema$str() {
        return "HHH000396: Updating schema";
    }

    @Override
    public final void usingDefaultIdGeneratorSegmentValue(String tableName, String segmentColumnName, String defaultToUse) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.usingDefaultIdGeneratorSegmentValue$str(), (Object)tableName, (Object)segmentColumnName, (Object)defaultToUse);
    }

    protected String usingDefaultIdGeneratorSegmentValue$str() {
        return "HHH000398: Explicit segment value for id generator [%s.%s] suggested; using default [%s]";
    }

    @Override
    public final void usingDefaultTransactionStrategy() {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.usingDefaultTransactionStrategy$str(), new Object[0]);
    }

    protected String usingDefaultTransactionStrategy$str() {
        return "HHH000399: Using default transaction strategy (direct JDBC transactions)";
    }

    @Override
    public final void usingDialect(Dialect dialect) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.usingDialect$str(), (Object)dialect);
    }

    protected String usingDialect$str() {
        return "HHH000400: Using dialect: %s";
    }

    @Override
    public final void usingOldDtd() {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.usingOldDtd$str(), new Object[0]);
    }

    protected String usingOldDtd$str() {
        return "HHH000404: Don't use old DTDs, read the Hibernate 3.x Migration Guide!";
    }

    @Override
    public final void usingReflectionOptimizer() {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.usingReflectionOptimizer$str(), new Object[0]);
    }

    protected String usingReflectionOptimizer$str() {
        return "HHH000406: Using bytecode reflection optimizer";
    }

    @Override
    public final void usingStreams() {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.usingStreams$str(), new Object[0]);
    }

    protected String usingStreams$str() {
        return "HHH000407: Using java.io streams to persist binary types";
    }

    @Override
    public final void usingUuidHexGenerator(String name, String name2) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.usingUuidHexGenerator$str(), (Object)name, (Object)name2);
    }

    protected String usingUuidHexGenerator$str() {
        return "HHH000409: Using %s which does not generate IETF RFC 4122 compliant UUID values; consider using %s instead";
    }

    @Override
    public final void validatorNotFound() {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.validatorNotFound$str(), new Object[0]);
    }

    protected String validatorNotFound$str() {
        return "HHH000410: Hibernate Validator not found: ignoring";
    }

    @Override
    public final void version(String versionString) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.version$str(), (Object)versionString);
    }

    protected String version$str() {
        return "HHH000412: Hibernate ORM core version %s";
    }

    @Override
    public final void warningsCreatingTempTable(SQLWarning warning) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.warningsCreatingTempTable$str(), (Object)warning);
    }

    protected String warningsCreatingTempTable$str() {
        return "HHH000413: Warnings creating temp table : %s";
    }

    @Override
    public final void willNotRegisterListeners() {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.willNotRegisterListeners$str(), new Object[0]);
    }

    protected String willNotRegisterListeners$str() {
        return "HHH000414: Property hibernate.search.autoregister_listeners is set to false. No attempt will be made to register Hibernate Search event listeners.";
    }

    @Override
    public final void writeLocksNotSupported(String entityName) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.writeLocksNotSupported$str(), (Object)entityName);
    }

    protected String writeLocksNotSupported$str() {
        return "HHH000416: Write locks via update not supported for non-versioned entities [%s]";
    }

    @Override
    public final void writingGeneratedSchemaToFile(String outputFile) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.writingGeneratedSchemaToFile$str(), (Object)outputFile);
    }

    protected String writingGeneratedSchemaToFile$str() {
        return "HHH000417: Writing generated schema to file: %s";
    }

    @Override
    public final void addingOverrideFor(String name, String name2) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.addingOverrideFor$str(), (Object)name, (Object)name2);
    }

    protected String addingOverrideFor$str() {
        return "HHH000418: Adding override for %s: %s";
    }

    @Override
    public final void resolvedSqlTypeDescriptorForDifferentSqlCode(String name, String valueOf, String name2, String valueOf2) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.resolvedSqlTypeDescriptorForDifferentSqlCode$str(), new Object[]{name, valueOf, name2, valueOf2});
    }

    protected String resolvedSqlTypeDescriptorForDifferentSqlCode$str() {
        return "HHH000419: Resolved SqlTypeDescriptor is for a different SQL code. %s has sqlCode=%s; type override %s has sqlCode=%s";
    }

    @Override
    public final void closingUnreleasedBatch() {
        this.log.logf(FQCN, Logger.Level.DEBUG, null, this.closingUnreleasedBatch$str(), new Object[0]);
    }

    protected String closingUnreleasedBatch$str() {
        return "HHH000420: Closing un-released batch";
    }

    @Override
    public final void disablingContextualLOBCreation(String nonContextualLobCreation) {
        this.log.logf(FQCN, Logger.Level.DEBUG, null, this.disablingContextualLOBCreation$str(), (Object)nonContextualLobCreation);
    }

    protected String disablingContextualLOBCreation$str() {
        return "HHH000421: Disabling contextual LOB creation as %s is true";
    }

    @Override
    public final void disablingContextualLOBCreationSinceConnectionNull() {
        this.log.logf(FQCN, Logger.Level.DEBUG, null, this.disablingContextualLOBCreationSinceConnectionNull$str(), new Object[0]);
    }

    protected String disablingContextualLOBCreationSinceConnectionNull$str() {
        return "HHH000422: Disabling contextual LOB creation as connection was null";
    }

    @Override
    public final void disablingContextualLOBCreationSinceOldJdbcVersion(int jdbcMajorVersion) {
        this.log.logf(FQCN, Logger.Level.DEBUG, null, this.disablingContextualLOBCreationSinceOldJdbcVersion$str(), (Object)jdbcMajorVersion);
    }

    protected String disablingContextualLOBCreationSinceOldJdbcVersion$str() {
        return "HHH000423: Disabling contextual LOB creation as JDBC driver reported JDBC version [%s] less than 4";
    }

    @Override
    public final void disablingContextualLOBCreationSinceCreateClobFailed(Throwable t) {
        this.log.logf(FQCN, Logger.Level.DEBUG, null, this.disablingContextualLOBCreationSinceCreateClobFailed$str(), (Object)t);
    }

    protected String disablingContextualLOBCreationSinceCreateClobFailed$str() {
        return "HHH000424: Disabling contextual LOB creation as createClob() method threw error : %s";
    }

    @Override
    public final void unableToCloseSessionButSwallowingError(HibernateException e) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.unableToCloseSessionButSwallowingError$str(), (Object)e);
    }

    protected String unableToCloseSessionButSwallowingError$str() {
        return "HHH000425: Could not close session; swallowing exception[%s] as transaction completed";
    }

    @Override
    public final void setManagerLookupClass() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.setManagerLookupClass$str(), new Object[0]);
    }

    protected String setManagerLookupClass$str() {
        return "HHH000426: You should set hibernate.transaction.jta.platform if cache is enabled";
    }

    @Override
    public final void legacyTransactionManagerStrategy(String name, String jtaPlatform) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.legacyTransactionManagerStrategy$str(), (Object)name, (Object)jtaPlatform);
    }

    protected String legacyTransactionManagerStrategy$str() {
        return "HHH000428: Encountered legacy TransactionManagerLookup specified; convert to newer %s contract specified via %s setting";
    }

    @Override
    public final void entityIdentifierValueBindingExists(String name) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.entityIdentifierValueBindingExists$str(), (Object)name);
    }

    protected String entityIdentifierValueBindingExists$str() {
        return "HHH000429: Setting entity-identifier value binding where one already existed : %s.";
    }

    @Override
    public final void deprecatedDerbyDialect() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.deprecatedDerbyDialect$str(), new Object[0]);
    }

    protected String deprecatedDerbyDialect$str() {
        return "HHH000430: The DerbyDialect dialect has been deprecated; use one of the version-specific dialects instead";
    }

    @Override
    public final void undeterminedH2Version() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.undeterminedH2Version$str(), new Object[0]);
    }

    protected String undeterminedH2Version$str() {
        return "HHH000431: Unable to determine H2 database version, certain features may not work";
    }

    @Override
    public final void noColumnsSpecifiedForIndex(String indexName, String tableName) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.noColumnsSpecifiedForIndex$str(), (Object)indexName, (Object)tableName);
    }

    protected String noColumnsSpecifiedForIndex$str() {
        return "HHH000432: There were not column names specified for index %s on table %s";
    }

    @Override
    public final void timestampCachePuts(long updateTimestampsCachePutCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.timestampCachePuts$str(), (Object)updateTimestampsCachePutCount);
    }

    protected String timestampCachePuts$str() {
        return "HHH000433: update timestamps cache puts: %s";
    }

    @Override
    public final void timestampCacheHits(long updateTimestampsCachePutCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.timestampCacheHits$str(), (Object)updateTimestampsCachePutCount);
    }

    protected String timestampCacheHits$str() {
        return "HHH000434: update timestamps cache hits: %s";
    }

    @Override
    public final void timestampCacheMisses(long updateTimestampsCachePutCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.timestampCacheMisses$str(), (Object)updateTimestampsCachePutCount);
    }

    protected String timestampCacheMisses$str() {
        return "HHH000435: update timestamps cache misses: %s";
    }

    @Override
    public final void entityManagerFactoryAlreadyRegistered(String emfName, String propertyName) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.entityManagerFactoryAlreadyRegistered$str(), (Object)emfName, (Object)propertyName);
    }

    protected String entityManagerFactoryAlreadyRegistered$str() {
        return "HHH000436: Entity manager factory name (%s) is already registered.  If entity manager will be clustered or passivated, specify a unique value for property '%s'";
    }

    @Override
    public final void cannotResolveNonNullableTransientDependencies(String transientEntityString, Set<String> dependentEntityStrings, Set<String> nonNullableAssociationPaths) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.cannotResolveNonNullableTransientDependencies$str(), (Object)transientEntityString, dependentEntityStrings, nonNullableAssociationPaths);
    }

    protected String cannotResolveNonNullableTransientDependencies$str() {
        return "HHH000437: Attempting to save one or more entities that have a non-nullable association with an unsaved transient entity. The unsaved transient entity must be saved in an operation prior to saving these dependent entities.\n\tUnsaved transient entity: (%s)\n\tDependent entities: (%s)\n\tNon-nullable association(s): (%s)";
    }

    @Override
    public final void naturalIdCachePuts(long naturalIdCachePutCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.naturalIdCachePuts$str(), (Object)naturalIdCachePutCount);
    }

    protected String naturalIdCachePuts$str() {
        return "HHH000438: NaturalId cache puts: %s";
    }

    @Override
    public final void naturalIdCacheHits(long naturalIdCacheHitCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.naturalIdCacheHits$str(), (Object)naturalIdCacheHitCount);
    }

    protected String naturalIdCacheHits$str() {
        return "HHH000439: NaturalId cache hits: %s";
    }

    @Override
    public final void naturalIdCacheMisses(long naturalIdCacheMissCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.naturalIdCacheMisses$str(), (Object)naturalIdCacheMissCount);
    }

    protected String naturalIdCacheMisses$str() {
        return "HHH000440: NaturalId cache misses: %s";
    }

    @Override
    public final void naturalIdMaxQueryTime(long naturalIdQueryExecutionMaxTime) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.naturalIdMaxQueryTime$str(), (Object)naturalIdQueryExecutionMaxTime);
    }

    protected String naturalIdMaxQueryTime$str() {
        return "HHH000441: Max NaturalId query time: %sms";
    }

    @Override
    public final void naturalIdQueriesExecuted(long naturalIdQueriesExecutionCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.naturalIdQueriesExecuted$str(), (Object)naturalIdQueriesExecutionCount);
    }

    protected String naturalIdQueriesExecuted$str() {
        return "HHH000442: NaturalId queries executed to database: %s";
    }

    @Override
    public final void tooManyInExpressions(String dialectName, int limit, String paramName, int size) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.tooManyInExpressions$str(), new Object[]{dialectName, limit, paramName, size});
    }

    protected String tooManyInExpressions$str() {
        return "HHH000443: Dialect [%s] limits the number of elements in an IN predicate to %s entries.  However, the given parameter list [%s] contained %s entries, which will likely cause failures to execute the query in the database";
    }

    @Override
    public final void usingFollowOnLocking() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.usingFollowOnLocking$str(), new Object[0]);
    }

    protected String usingFollowOnLocking$str() {
        return "HHH000444: Encountered request for locking however dialect reports that database prefers locking be done in a separate select (follow-on locking); results will be locked after initial query executes";
    }

    @Override
    public final void aliasSpecificLockingWithFollowOnLocking(LockMode lockMode) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.aliasSpecificLockingWithFollowOnLocking$str(), (Object)lockMode);
    }

    protected String aliasSpecificLockingWithFollowOnLocking$str() {
        return "HHH000445: Alias-specific lock modes requested, which is not currently supported with follow-on locking; all acquired locks will be [%s]";
    }

    @Override
    public final void embedXmlAttributesNoLongerSupported() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.embedXmlAttributesNoLongerSupported$str(), new Object[0]);
    }

    protected String embedXmlAttributesNoLongerSupported$str() {
        return "HHH000446: embed-xml attributes were intended to be used for DOM4J entity mode. Since that entity mode has been removed, embed-xml attributes are no longer supported and should be removed from mappings.";
    }

    @Override
    public final void explicitSkipLockedLockCombo() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.explicitSkipLockedLockCombo$str(), new Object[0]);
    }

    protected String explicitSkipLockedLockCombo$str() {
        return "HHH000447: Explicit use of UPGRADE_SKIPLOCKED in lock() calls is not recommended; use normal UPGRADE locking instead";
    }

    @Override
    public final void multipleValidationModes(String modes) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.multipleValidationModes$str(), (Object)modes);
    }

    protected String multipleValidationModes$str() {
        return "HHH000448: 'javax.persistence.validation.mode' named multiple values : %s";
    }

    @Override
    public final void nonCompliantMapConversion(String collectionRole) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.nonCompliantMapConversion$str(), (Object)collectionRole);
    }

    protected String nonCompliantMapConversion$str() {
        return "HHH000449: @Convert annotation applied to Map attribute [%s] did not explicitly specify attributeName using 'key'/'value' as required by spec; attempting to DoTheRightThing";
    }

    @Override
    public final void alternateServiceRole(String requestedRole, String targetRole) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.alternateServiceRole$str(), (Object)requestedRole, (Object)targetRole);
    }

    protected String alternateServiceRole$str() {
        return "HHH000450: Encountered request for Service by non-primary service role [%s -> %s]; please update usage";
    }

    @Override
    public final void rollbackFromBackgroundThread(int status) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.rollbackFromBackgroundThread$str(), (Object)status);
    }

    protected String rollbackFromBackgroundThread$str() {
        return "HHH000451: Transaction afterCompletion called by a background thread; delaying afterCompletion processing until the original thread can handle it. [status=%s]";
    }

    @Override
    public final void unableToLoadScannedClassOrResource(Exception e) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable)e, this.unableToLoadScannedClassOrResource$str(), new Object[0]);
    }

    protected String unableToLoadScannedClassOrResource$str() {
        return "HHH000452: Exception while loading a class or resource found during scanning";
    }

    @Override
    public final void unableToDiscoverOsgiService(String service, Exception e) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable)e, this.unableToDiscoverOsgiService$str(), (Object)service);
    }

    protected String unableToDiscoverOsgiService$str() {
        return "HHH000453: Exception while discovering OSGi service implementations : %s";
    }

    @Override
    public final void deprecatedManyToManyOuterJoin() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.deprecatedManyToManyOuterJoin$str(), new Object[0]);
    }

    protected String deprecatedManyToManyOuterJoin$str() {
        return "HHH000454: The outer-join attribute on <many-to-many> has been deprecated. Instead of outer-join=\"false\", use lazy=\"extra\" with <map>, <set>, <bag>, <idbag>, or <list>, which will only initialize entities (not as a proxy) as needed.";
    }

    @Override
    public final void deprecatedManyToManyFetch() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.deprecatedManyToManyFetch$str(), new Object[0]);
    }

    protected String deprecatedManyToManyFetch$str() {
        return "HHH000455: The fetch attribute on <many-to-many> has been deprecated. Instead of fetch=\"select\", use lazy=\"extra\" with <map>, <set>, <bag>, <idbag>, or <list>, which will only initialize entities (not as a proxy) as needed.";
    }

    @Override
    public final void unsupportedNamedParameters() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unsupportedNamedParameters$str(), new Object[0]);
    }

    protected String unsupportedNamedParameters$str() {
        return "HHH000456: Named parameters are used for a callable statement, but database metadata indicates named parameters are not supported.";
    }

    @Override
    public final void applyingExplicitDiscriminatorColumnForJoined(String className, String overrideSetting) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.applyingExplicitDiscriminatorColumnForJoined$str(), (Object)className, (Object)overrideSetting);
    }

    protected String applyingExplicitDiscriminatorColumnForJoined$str() {
        return "HHH000457: Joined inheritance hierarchy [%1$s] defined explicit @DiscriminatorColumn.  Legacy Hibernate behavior was to ignore the @DiscriminatorColumn.  However, as part of issue HHH-6911 we now apply the explicit @DiscriminatorColumn.  If you would prefer the legacy behavior, enable the `%2$s` setting (%2$s=true)";
    }

    @Override
    public final void creatingPooledLoOptimizer(int incrementSize, String name) {
        this.log.logf(FQCN, Logger.Level.DEBUG, null, this.creatingPooledLoOptimizer$str(), (Object)incrementSize, (Object)name);
    }

    protected String creatingPooledLoOptimizer$str() {
        return "HHH000467: Creating pooled optimizer (lo) with [incrementSize=%s; returnClass=%s]";
    }

    @Override
    public final void logBadHbmAttributeConverterType(String type, String exceptionMessage) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.logBadHbmAttributeConverterType$str(), (Object)type, (Object)exceptionMessage);
    }

    protected String logBadHbmAttributeConverterType$str() {
        return "HHH000468: Unable to interpret type [%s] as an AttributeConverter due to an exception : %s";
    }

    protected String usingStoppedClassLoaderService$str() {
        return "HHH000469: The ClassLoaderService can not be reused. This instance was stopped already.";
    }

    @Override
    public final HibernateException usingStoppedClassLoaderService() {
        HibernateException result = new HibernateException(String.format(this.getLoggingLocale(), this.usingStoppedClassLoaderService$str(), new Object[0]));
        CoreMessageLogger_$logger._copyStackTraceMinusOne((Throwable)((Object)result));
        return result;
    }

    private static void _copyStackTraceMinusOne(Throwable e) {
        StackTraceElement[] st = e.getStackTrace();
        e.setStackTrace(Arrays.copyOfRange(st, 1, st.length));
    }

    @Override
    public final void logUnexpectedSessionInCollectionNotConnected(String msg) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.logUnexpectedSessionInCollectionNotConnected$str(), (Object)msg);
    }

    protected String logUnexpectedSessionInCollectionNotConnected$str() {
        return "HHH000470: An unexpected session is defined for a collection, but the collection is not connected to that session. A persistent collection may only be associated with one session at a time. Overwriting session. %s";
    }

    @Override
    public final void logCannotUnsetUnexpectedSessionInCollection(String msg) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.logCannotUnsetUnexpectedSessionInCollection$str(), (Object)msg);
    }

    protected String logCannotUnsetUnexpectedSessionInCollection$str() {
        return "HHH000471: Cannot unset session in a collection because an unexpected session is defined. A persistent collection may only be associated with one session at a time. %s";
    }

    @Override
    public final void hikariProviderClassNotFound() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.hikariProviderClassNotFound$str(), new Object[0]);
    }

    protected String hikariProviderClassNotFound$str() {
        return "HHH000472: Hikari properties were encountered, but the Hikari ConnectionProvider was not found on the classpath; these properties are going to be ignored.";
    }

    @Override
    public final void cachedFileObsolete(File cachedFile) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.cachedFileObsolete$str(), (Object)cachedFile);
    }

    protected String cachedFileObsolete$str() {
        return "HHH000473: Omitting cached file [%s] as the mapping file is newer";
    }

    protected String ambiguousPropertyMethods$str() {
        return "HHH000474: Ambiguous persistent property methods detected on %s; mark one as @Transient : [%s] and [%s]";
    }

    @Override
    public final String ambiguousPropertyMethods(String entityName, String oneMethodSig, String secondMethodSig) {
        return String.format(this.getLoggingLocale(), this.ambiguousPropertyMethods$str(), entityName, oneMethodSig, secondMethodSig);
    }

    @Override
    public final void logCannotLocateIndexColumnInformation(String columnIdentifierText, String indexIdentifierText) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.logCannotLocateIndexColumnInformation$str(), (Object)columnIdentifierText, (Object)indexIdentifierText);
    }

    protected String logCannotLocateIndexColumnInformation$str() {
        return "HHH000475: Cannot locate column information using identifier [%s]; ignoring index [%s]";
    }

    @Override
    public final void executingImportScript(String scriptName) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.executingImportScript$str(), (Object)scriptName);
    }

    protected String executingImportScript$str() {
        return "HHH000476: Executing import script '%s'";
    }

    @Override
    public final void startingDelayedSchemaDrop() {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.startingDelayedSchemaDrop$str(), new Object[0]);
    }

    protected String startingDelayedSchemaDrop$str() {
        return "HHH000477: Starting delayed evictData of schema as part of SessionFactory shut-down'";
    }

    @Override
    public final void unsuccessfulSchemaManagementCommand(String command) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.unsuccessfulSchemaManagementCommand$str(), (Object)command);
    }

    protected String unsuccessfulSchemaManagementCommand$str() {
        return "HHH000478: Unsuccessful: %s";
    }

    protected String collectionNotProcessedByFlush$str() {
        return "HHH000479: Collection [%s] was not processed by flush(). This is likely due to unsafe use of the session (e.g. used in multiple threads concurrently, updates during entity lifecycle hooks).";
    }

    @Override
    public final String collectionNotProcessedByFlush(String role) {
        return String.format(this.getLoggingLocale(), this.collectionNotProcessedByFlush$str(), role);
    }

    @Override
    public final void stalePersistenceContextInEntityEntry(String msg) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.stalePersistenceContextInEntityEntry$str(), (Object)msg);
    }

    protected String stalePersistenceContextInEntityEntry$str() {
        return "HHH000480: A ManagedEntity was associated with a stale PersistenceContext. A ManagedEntity may only be associated with one PersistenceContext at a time; %s";
    }

    @Override
    public final void unknownJavaTypeNoEqualsHashCode(Class javaType) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unknownJavaTypeNoEqualsHashCode$str(), (Object)javaType);
    }

    protected String unknownJavaTypeNoEqualsHashCode$str() {
        return "HHH000481: Encountered Java type [%s] for which we could not locate a JavaTypeDescriptor and which does not appear to implement equals and/or hashCode.  This can lead to significant performance problems when performing equality/dirty checking involving this Java type.  Consider registering a custom JavaTypeDescriptor or at least implementing equals/hashCode.";
    }

    @Override
    public final void cacheOrCacheableAnnotationOnNonRoot(String className) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.cacheOrCacheableAnnotationOnNonRoot$str(), (Object)className);
    }

    protected String cacheOrCacheableAnnotationOnNonRoot$str() {
        return "HHH000482: @org.hibernate.annotations.Cache used on a non-root entity: ignored for [%s]. Please see the Hibernate documentation for proper usage.";
    }

    @Override
    public final void emptyCompositesEnabled() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.emptyCompositesEnabled$str(), new Object[0]);
    }

    protected String emptyCompositesEnabled$str() {
        return "HHH000483: An experimental feature has been enabled (hibernate.create_empty_composites.enabled=true) that instantiates empty composite/embedded objects when all of its attribute values are null. This feature has known issues and should not be used in production until it is stabilized. See Hibernate Jira issue HHH-11936 for details.";
    }

    @Override
    public final void viburProviderClassNotFound() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.viburProviderClassNotFound$str(), new Object[0]);
    }

    protected String viburProviderClassNotFound$str() {
        return "HHH000484: Vibur properties were encountered, but the Vibur ConnectionProvider was not found on the classpath; these properties are going to be ignored.";
    }

    @Override
    public final void attemptToAssociateProxyWithTwoOpenSessions(String entityName, Object id) {
        this.log.logf(FQCN, Logger.Level.ERROR, null, this.attemptToAssociateProxyWithTwoOpenSessions$str(), (Object)entityName, id);
    }

    protected String attemptToAssociateProxyWithTwoOpenSessions$str() {
        return "HHH000485: Illegally attempted to associate a proxy for entity [%s] with id [%s] with two open sessions.";
    }

    @Override
    public final void agroalProviderClassNotFound() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.agroalProviderClassNotFound$str(), new Object[0]);
    }

    protected String agroalProviderClassNotFound$str() {
        return "HHH000486: Agroal properties were encountered, but the Agroal ConnectionProvider was not found on the classpath; these properties are going to be ignored.";
    }

    @Override
    public final void immutableEntityUpdateQuery(String sourceQuery, String querySpaces) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.immutableEntityUpdateQuery$str(), (Object)sourceQuery, (Object)querySpaces);
    }

    protected String immutableEntityUpdateQuery$str() {
        return "HHH000487: The query: [%s] attempts to update an immutable entity: %s";
    }

    protected String bytecodeEnhancementFailedUnableToGetPrivateLookupFor$str() {
        return "HHH000488: Bytecode enhancement failed for class: %1$s. It might be due to the Java module system preventing Hibernate ORM from defining an enhanced class in the same package as class %1$s. In this case, the class should be opened and exported to Hibernate ORM.";
    }

    @Override
    public final String bytecodeEnhancementFailedUnableToGetPrivateLookupFor(String className) {
        return String.format(this.getLoggingLocale(), this.bytecodeEnhancementFailedUnableToGetPrivateLookupFor$str(), className);
    }

    @Override
    public final void nativeExceptionHandling51ComplianceJpaBootstrapping() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.nativeExceptionHandling51ComplianceJpaBootstrapping$str(), new Object[0]);
    }

    protected String nativeExceptionHandling51ComplianceJpaBootstrapping$str() {
        return "HHH000489: Setting hibernate.native_exception_handling_51_compliance=true is not valid with JPA bootstrapping; setting will be ignored.";
    }

    @Override
    public final void usingJtaPlatform(String jtaPlatformClassName) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.usingJtaPlatform$str(), (Object)jtaPlatformClassName);
    }

    protected String usingJtaPlatform$str() {
        return "HHH000490: Using JtaPlatform implementation: [%s]";
    }

    @Override
    public final void ignoreNotFoundWithFetchTypeLazy(String entity, String association) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.ignoreNotFoundWithFetchTypeLazy$str(), (Object)entity, (Object)association);
    }

    protected String ignoreNotFoundWithFetchTypeLazy$str() {
        return "HHH000491: The [%2$s] association in the [%1$s] entity uses both @NotFound(action = NotFoundAction.IGNORE) and FetchType.LAZY. The NotFoundAction.IGNORE @ManyToOne and @OneToOne associations are always fetched eagerly.";
    }

    @Override
    public final void queryPlanCacheHits(long queryPlanCacheHitCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.queryPlanCacheHits$str(), (Object)queryPlanCacheHitCount);
    }

    protected String queryPlanCacheHits$str() {
        return "HHH000492: Query plan cache hits: %s";
    }

    @Override
    public final void queryPlanCacheMisses(long queryPlanCacheMissCount) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.queryPlanCacheMisses$str(), (Object)queryPlanCacheMissCount);
    }

    protected String queryPlanCacheMisses$str() {
        return "HHH000493: Query plan cache misses: %s";
    }

    @Override
    public final void ignoreQueuedOperationsOnMerge(String collectionInfoString) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.ignoreQueuedOperationsOnMerge$str(), (Object)collectionInfoString);
    }

    protected String ignoreQueuedOperationsOnMerge$str() {
        return "HHH000494: Attempt to merge an uninitialized collection with queued operations; queued operations will be ignored: %s";
    }

    @Override
    public final void queuedOperationWhenAttachToSession(String collectionInfoString) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.queuedOperationWhenAttachToSession$str(), (Object)collectionInfoString);
    }

    protected String queuedOperationWhenAttachToSession$str() {
        return "HHH000495: Attaching an uninitialized collection with queued operations to a session: %s";
    }

    @Override
    public final void queuedOperationWhenDetachFromSession(String collectionInfoString) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.queuedOperationWhenDetachFromSession$str(), (Object)collectionInfoString);
    }

    protected String queuedOperationWhenDetachFromSession$str() {
        return "HHH000496: Detaching an uninitialized collection with queued operations from a session: %s";
    }

    @Override
    public final void sequenceIncrementSizeMismatch(String sequenceName, int incrementSize, int databaseIncrementSize) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.sequenceIncrementSizeMismatch$str(), (Object)sequenceName, (Object)incrementSize, (Object)databaseIncrementSize);
    }

    protected String sequenceIncrementSizeMismatch$str() {
        return "HHH000497: The increment size of the [%s] sequence is set to [%d] in the entity mapping while the associated database sequence increment size is [%d]. The database sequence increment size will take precedence to avoid identifier allocation conflicts.";
    }

    @Override
    public final void queuedOperationWhenDetachFromSessionOnRollback(String collectionInfoString) {
        this.log.logf(FQCN, Logger.Level.DEBUG, null, this.queuedOperationWhenDetachFromSessionOnRollback$str(), (Object)collectionInfoString);
    }

    protected String queuedOperationWhenDetachFromSessionOnRollback$str() {
        return "HHH000498: Detaching an uninitialized collection with queued operations from a session due to rollback: %s";
    }

    @Override
    public final void unsupportedAttributeOverrideWithEntityInheritance(String entityName) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unsupportedAttributeOverrideWithEntityInheritance$str(), (Object)entityName);
    }

    protected String unsupportedAttributeOverrideWithEntityInheritance$str() {
        return "HHH000499: Using @AttributeOverride or @AttributeOverrides in conjunction with entity inheritance is not supported: %s. The overriding definitions are ignored.";
    }

    @Override
    public final void ignoreImmutablePropertyModification(String propertyName, String entityName) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.ignoreImmutablePropertyModification$str(), (Object)propertyName, (Object)entityName);
    }

    protected String ignoreImmutablePropertyModification$str() {
        return "HHH000502: The [%s] property of the [%s] entity was modified, but it won't be updated because the property is immutable.";
    }

    @Override
    public final void unsupportedMappedSuperclassWithEntityInheritance(String entityName) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.unsupportedMappedSuperclassWithEntityInheritance$str(), (Object)entityName);
    }

    protected String unsupportedMappedSuperclassWithEntityInheritance$str() {
        return "HHH000503: A class should not be annotated with both @Inheritance and @MappedSuperclass. @Inheritance will be ignored for: %s.";
    }

    @Override
    public final void multipleSchemaCreationSettingsDefined() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.multipleSchemaCreationSettingsDefined$str(), new Object[0]);
    }

    protected String multipleSchemaCreationSettingsDefined$str() {
        return "HHH000504: Multiple configuration properties defined to create schema. Choose at most one among 'javax.persistence.create-database-schemas', 'hibernate.hbm2ddl.create_namespaces', 'hibernate.hbm2dll.create_namespaces' (this last being deprecated).";
    }

    @Override
    public final void ignoringServiceConfigurationError(Class<?> serviceContract, ServiceConfigurationError error) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable)error, this.ignoringServiceConfigurationError$str(), serviceContract);
    }

    protected String ignoringServiceConfigurationError$str() {
        return "HHH000505: Ignoring ServiceConfigurationError caught while trying to instantiate service '%s'.";
    }

    @Override
    public final void enabledFiltersWhenDetachFromSession(String collectionInfoString) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.enabledFiltersWhenDetachFromSession$str(), (Object)collectionInfoString);
    }

    protected String enabledFiltersWhenDetachFromSession$str() {
        return "HHH000506: Detaching an uninitialized collection with enabled filters from a session: %s";
    }

    protected String usingRemovedJavassistBytecodeProvider$str() {
        return "HHH000508: The Javassist based BytecodeProvider has been removed: remove the `hibernate.bytecode.provider` configuration property to switch to the default provider";
    }

    @Override
    public final HibernateException usingRemovedJavassistBytecodeProvider() {
        HibernateException result = new HibernateException(String.format(this.getLoggingLocale(), this.usingRemovedJavassistBytecodeProvider$str(), new Object[0]));
        CoreMessageLogger_$logger._copyStackTraceMinusOne((Throwable)((Object)result));
        return result;
    }
}

