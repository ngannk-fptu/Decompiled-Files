/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.Synchronization
 *  javax.transaction.SystemException
 *  org.jboss.logging.BasicLogger
 *  org.jboss.logging.Logger$Level
 *  org.jboss.logging.annotations.Cause
 *  org.jboss.logging.annotations.LogMessage
 *  org.jboss.logging.annotations.Message
 *  org.jboss.logging.annotations.MessageLogger
 *  org.jboss.logging.annotations.ValidIdRange
 */
package org.hibernate.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Hashtable;
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
import org.hibernate.type.BasicType;
import org.hibernate.type.SerializationException;
import org.hibernate.type.Type;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.ValidIdRange;

@MessageLogger(projectCode="HHH")
@ValidIdRange(min=1, max=10000)
public interface CoreMessageLogger
extends BasicLogger {
    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Already session bound on call to bind(); make sure you clean up your sessions!", id=2)
    public void alreadySessionBound();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Autocommit mode: %s", id=6)
    public void autoCommitMode(boolean var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="JTASessionContext being used with JDBC transactions; auto-flush will not operate correctly with getCurrentSession()", id=8)
    public void autoFlushWillNotWork();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="On release of batch it still contained JDBC statements", id=10)
    public void batchContainedStatementsOnRelease();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Bytecode provider name : %s", id=21)
    public void bytecodeProvider(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="c3p0 properties were encountered, but the %s provider class was not found on the classpath; these properties are going to be ignored.", id=22)
    public void c3p0ProviderClassNotFound(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="I/O reported cached file could not be found : %s : %s", id=23)
    public void cachedFileNotFound(String var1, FileNotFoundException var2);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Cache provider: %s", id=24)
    public void cacheProvider(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Calling joinTransaction() on a non JTA EntityManager", id=27)
    public void callingJoinTransactionOnNonJtaEntityManager();

    @LogMessage(level=Logger.Level.DEBUG)
    @Message(value="Closing", id=31)
    public void closing();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Collections fetched (minimize this): %s", id=32)
    public void collectionsFetched(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Collections loaded: %s", id=33)
    public void collectionsLoaded(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Collections recreated: %s", id=34)
    public void collectionsRecreated(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Collections removed: %s", id=35)
    public void collectionsRemoved(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Collections updated: %s", id=36)
    public void collectionsUpdated(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Columns: %s", id=37)
    public void columns(Set var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Composite-id class does not override equals(): %s", id=38)
    public void compositeIdClassDoesNotOverrideEquals(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Composite-id class does not override hashCode(): %s", id=39)
    public void compositeIdClassDoesNotOverrideHashCode(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Configuration resource: %s", id=40)
    public void configurationResource(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Configured SessionFactory: %s", id=41)
    public void configuredSessionFactory(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Configuring from file: %s", id=42)
    public void configuringFromFile(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Configuring from resource: %s", id=43)
    public void configuringFromResource(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Configuring from URL: %s", id=44)
    public void configuringFromUrl(URL var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Configuring from XML document", id=45)
    public void configuringFromXmlDocument();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Connections obtained: %s", id=48)
    public void connectionsObtained(long var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Container is providing a null PersistenceUnitRootUrl: discovery impossible", id=50)
    public void containerProvidingNullPersistenceUnitRootUrl();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Ignoring bag join fetch [%s] due to prior collection join fetch", id=51)
    public void containsJoinFetchedCollection(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Creating subcontext: %s", id=53)
    public void creatingSubcontextInfo(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Defining %s=true ignored in HEM", id=59)
    public void definingFlushBeforeCompletionIgnoredInHem(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="@ForceDiscriminator is deprecated use @DiscriminatorOptions instead.", id=62)
    public void deprecatedForceDescriminatorAnnotation();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="The Oracle9Dialect dialect has been deprecated; use either Oracle9iDialect or Oracle10gDialect instead", id=63)
    public void deprecatedOracle9Dialect();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="The OracleDialect dialect has been deprecated; use Oracle8iDialect instead", id=64)
    public void deprecatedOracleDialect();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="DEPRECATED : use [%s] instead with custom [%s] implementation", id=65)
    public void deprecatedUuidGenerator(String var1, String var2);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Disallowing insert statement comment for select-identity due to Oracle driver bug", id=67)
    public void disallowingInsertStatementComment();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Duplicate generator name %s", id=69)
    public void duplicateGeneratorName(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Duplicate generator table: %s", id=70)
    public void duplicateGeneratorTable(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Duplicate import: %s -> %s", id=71)
    public void duplicateImport(String var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Duplicate joins for class: %s", id=72)
    public void duplicateJoins(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="entity-listener duplication, first event definition will be used: %s", id=73)
    public void duplicateListener(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Found more than one <persistence-unit-metadata>, subsequent ignored", id=74)
    public void duplicateMetadata();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Entities deleted: %s", id=76)
    public void entitiesDeleted(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Entities fetched (minimize this): %s", id=77)
    public void entitiesFetched(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Entities inserted: %s", id=78)
    public void entitiesInserted(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Entities loaded: %s", id=79)
    public void entitiesLoaded(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Entities updated: %s", id=80)
    public void entitiesUpdated(long var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="@org.hibernate.annotations.Entity used on a non root entity: ignored for %s", id=81)
    public void entityAnnotationOnNonRoot(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Entity Manager closed by someone else (%s must not be used)", id=82)
    public void entityManagerClosedBySomeoneElse(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Entity [%s] is abstract-class/interface explicitly mapped as non-abstract; be sure to supply entity-names", id=84)
    public void entityMappedAsNonAbstract(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="%s %s found", id=85)
    public void exceptionHeaderFound(String var1, String var2);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="%s No %s found", id=86)
    public void exceptionHeaderNotFound(String var1, String var2);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Exception in interceptor afterTransactionCompletion()", id=87)
    public void exceptionInAfterTransactionCompletionInterceptor(@Cause Throwable var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Exception in interceptor beforeTransactionCompletion()", id=88)
    public void exceptionInBeforeTransactionCompletionInterceptor(@Cause Throwable var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Sub-resolver threw unexpected exception, continuing to next : %s", id=89)
    public void exceptionInSubResolver(String var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Expected type: %s, actual value: %s", id=91)
    public void expectedType(String var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="An item was expired by the cache while it was locked (increase your cache timeout): %s", id=92)
    public void expired(Object var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Bound factory to JNDI name: %s", id=94)
    public void factoryBoundToJndiName(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="A factory was renamed from [%s] to [%s] in JNDI", id=96)
    public void factoryJndiRename(String var1, String var2);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Unbound factory from JNDI name: %s", id=97)
    public void factoryUnboundFromJndiName(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="A factory was unbound from name: %s", id=98)
    public void factoryUnboundFromName(String var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="an assertion failure occurred (this may indicate a bug in Hibernate, but is more likely due to unsafe use of the session): %s", id=99)
    public void failed(Throwable var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Fail-safe cleanup (collections) : %s", id=100)
    public void failSafeCollectionsCleanup(CollectionLoadContext var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Fail-safe cleanup (entities) : %s", id=101)
    public void failSafeEntitiesCleanup(EntityLoadContext var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Fetching database metadata", id=102)
    public void fetchingDatabaseMetadata();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="firstResult/maxResults specified with collection fetch; applying in memory!", id=104)
    public void firstOrMaxResultsSpecifiedWithCollectionFetch();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Flushes: %s", id=105)
    public void flushes(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Forcing container resource cleanup on transaction completion", id=106)
    public void forcingContainerResourceCleanup();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Forcing table use for sequence-style generator due to pooled optimizer selection where db does not support pooled sequences", id=107)
    public void forcingTableUse();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Foreign keys: %s", id=108)
    public void foreignKeys(Set var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Found mapping document in jar: %s", id=109)
    public void foundMappingDocument(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="GUID identifier generated: %s", id=113)
    public void guidGenerated(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Handling transient entity in delete processing", id=114)
    public void handlingTransientEntity();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Hibernate connection pool size: %s (min=%s)", id=115)
    public void hibernateConnectionPoolSize(int var1, int var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Config specified explicit optimizer of [%s], but [%s=%s]; using optimizer [%s] increment default of [%s].", id=116)
    public void honoringOptimizerSetting(String var1, String var2, int var3, String var4, int var5);

    @LogMessage(level=Logger.Level.DEBUG)
    @Message(value="HQL: %s, time: %sms, rows: %s", id=117)
    public void hql(String var1, Long var2, Long var3);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="HSQLDB supports only READ_UNCOMMITTED isolation", id=118)
    public void hsqldbSupportsOnlyReadCommittedIsolation();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="On EntityLoadContext#clear, hydratingEntities contained [%s] entries", id=119)
    public void hydratingEntitiesCount(int var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Ignoring unique constraints specified on table generator [%s]", id=120)
    public void ignoringTableGeneratorConstraints(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Ignoring unrecognized query hint [%s]", id=121)
    public void ignoringUnrecognizedQueryHint(String var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="IllegalArgumentException in class: %s, getter method of property: %s", id=122)
    public void illegalPropertyGetterArgument(String var1, String var2);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="IllegalArgumentException in class: %s, setter method of property: %s", id=123)
    public void illegalPropertySetterArgument(String var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="@Immutable used on a non root entity: ignored for %s", id=124)
    public void immutableAnnotationOnNonRoot(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Mapping metadata cache was not completely processed", id=125)
    public void incompleteMappingMetadataCacheProcessing();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Indexes: %s", id=126)
    public void indexes(Set var1);

    @LogMessage(level=Logger.Level.DEBUG)
    @Message(value="Could not bind JNDI listener", id=127)
    public void couldNotBindJndiListener();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Instantiating explicit connection provider: %s", id=130)
    public void instantiatingExplicitConnectionProvider(String var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Array element type error\n%s", id=132)
    public void invalidArrayElementType(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Discriminator column has to be defined in the root entity, it will be ignored in subclass: %s", id=133)
    public void invalidDiscriminatorAnnotation(String var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Application attempted to edit read only item: %s", id=134)
    public void invalidEditOfReadOnlyItem(Object var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Invalid JNDI name: %s", id=135)
    public void invalidJndiName(String var1, @Cause JndiNameException var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Inapropriate use of @OnDelete on entity, annotation ignored: %s", id=136)
    public void invalidOnDeleteAnnotation(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Root entity should not hold a PrimaryKeyJoinColum(s), will be ignored: %s", id=137)
    public void invalidPrimaryKeyJoinColumnAnnotation(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Mixing inheritance strategy in a entity hierarchy is not allowed, ignoring sub strategy in: %s", id=138)
    public void invalidSubStrategy(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Illegal use of @Table in a subclass of a SINGLE_TABLE hierarchy: %s", id=139)
    public void invalidTableAnnotation(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="JACC contextID: %s", id=140)
    public void jaccContextId(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="java.sql.Types mapped the same code [%s] multiple times; was [%s]; now [%s]", id=141)
    public void JavaSqlTypesMappedSameCodeMultipleTimes(int var1, String var2, String var3);

    @Message(value="Bytecode enhancement failed: %s", id=142)
    public String bytecodeEnhancementFailed(String var1);

    @Message(value="Bytecode enhancement failed because no public, protected or package-private default constructor was found for entity: %s. Private constructors don't work with runtime proxies!", id=143)
    public String bytecodeEnhancementFailedBecauseOfDefaultConstructor(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="%s = false breaks the EJB3 specification", id=144)
    public void jdbcAutoCommitFalseBreaksEjb3Spec(String var1);

    @Message(value="JDBC rollback failed", id=151)
    public String jdbcRollbackFailed();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="JNDI InitialContext properties:%s", id=154)
    public void jndiInitialContextProperties(Hashtable var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="JNDI name %s does not handle a session factory reference", id=155)
    public void jndiNameDoesNotHandleSessionFactoryReference(String var1, @Cause ClassCastException var2);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Lazy property fetching available for: %s", id=157)
    public void lazyPropertyFetchingAvailable(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="In CollectionLoadContext#endLoadingCollections, localLoadingCollectionKeys contained [%s], but no LoadingCollectionEntry was found in loadContexts", id=159)
    public void loadingCollectionKeyNotFound(CollectionKey var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="On CollectionLoadContext#cleanup, localLoadingCollectionKeys contained [%s] entries", id=160)
    public void localLoadingCollectionKeysCount(int var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Logging statistics....", id=161)
    public void loggingStatistics();

    @LogMessage(level=Logger.Level.DEBUG)
    @Message(value="*** Logical connection closed ***", id=162)
    public void logicalConnectionClosed();

    @LogMessage(level=Logger.Level.DEBUG)
    @Message(value="Logical connection releasing its physical connection", id=163)
    public void logicalConnectionReleasingPhysicalConnection();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Max query time: %sms", id=173)
    public void maxQueryTime(long var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Function template anticipated %s arguments, but %s arguments encountered", id=174)
    public void missingArguments(int var1, int var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Class annotated @org.hibernate.annotations.Entity but not javax.persistence.Entity (most likely a user error): %s", id=175)
    public void missingEntityAnnotation(String var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Error in named query: %s", id=177)
    public void namedQueryError(String var1, @Cause HibernateException var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Naming exception occurred accessing factory: %s", id=178)
    public void namingExceptionAccessingFactory(NamingException var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Narrowing proxy to %s - this operation breaks ==", id=179)
    public void narrowingProxy(Class var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="FirstResult/maxResults specified on polymorphic query; applying in memory!", id=180)
    public void needsLimit();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="No appropriate connection provider encountered, assuming application will be supplying connections", id=181)
    public void noAppropriateConnectionProvider();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="No default (no-argument) constructor for class: %s (class must be instantiated by Interceptor)", id=182)
    public void noDefaultConstructor(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="no persistent classes found for query class: %s", id=183)
    public void noPersistentClassesFound(String var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="No session factory with JNDI name %s", id=184)
    public void noSessionFactoryWithJndiName(String var1, @Cause NameNotFoundException var2);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Optimistic lock failures: %s", id=187)
    public void optimisticLockFailures(long var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="@OrderBy not allowed for an indexed collection, annotation ignored.", id=189)
    public void orderByAnnotationIndexedCollection();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Overriding %s is dangerous, this might break the EJB3 specification implementation", id=193)
    public void overridingTransactionStrategyDangerous(String var1);

    @LogMessage(level=Logger.Level.DEBUG)
    @Message(value="Package not found or wo package-info.java: %s", id=194)
    public void packageNotFound(String var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Error parsing XML (%s) : %s", id=196)
    public void parsingXmlError(int var1, String var2);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Error parsing XML: %s(%s) %s", id=197)
    public void parsingXmlErrorForFile(String var1, int var2, String var3);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Warning parsing XML (%s) : %s", id=198)
    public void parsingXmlWarning(int var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Warning parsing XML: %s(%s) %s", id=199)
    public void parsingXmlWarningForFile(String var1, int var2, String var3);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Persistence provider caller does not implement the EJB3 spec correctly.PersistenceUnitInfo.getNewTempClassLoader() is null.", id=200)
    public void persistenceProviderCallerDoesNotImplementEjb3SpecCorrectly();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Pooled optimizer source reported [%s] as the initial value; use of 1 or greater highly recommended", id=201)
    public void pooledOptimizerReportedInitialValue(IntegralDataTypeHolder var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="PreparedStatement was already in the batch, [%s].", id=202)
    public void preparedStatementAlreadyInBatch(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="processEqualityExpression() : No expression to process!", id=203)
    public void processEqualityExpression();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Processing PersistenceUnitInfo [name: %s]", id=204)
    public void processingPersistenceUnitInfoName(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Loaded properties from resource hibernate.properties: %s", id=205)
    public void propertiesLoaded(Properties var1);

    @LogMessage(level=Logger.Level.DEBUG)
    @Message(value="hibernate.properties not found", id=206)
    public void propertiesNotFound();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Property %s not found in class but described in <mapping-file/> (possible typo error)", id=207)
    public void propertyNotFound(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="proxool properties were encountered, but the %s provider class was not found on the classpath; these properties are going to be ignored.", id=209)
    public void proxoolProviderClassNotFound(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Queries executed to database: %s", id=210)
    public void queriesExecuted(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Query cache hits: %s", id=213)
    public void queryCacheHits(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Query cache misses: %s", id=214)
    public void queryCacheMisses(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Query cache puts: %s", id=215)
    public void queryCachePuts(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="RDMSOS2200Dialect version: 1.0", id=218)
    public void rdmsOs2200Dialect();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Reading mappings from cache file: %s", id=219)
    public void readingCachedMappings(File var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Reading mappings from file: %s", id=220)
    public void readingMappingsFromFile(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Reading mappings from resource: %s", id=221)
    public void readingMappingsFromResource(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="read-only cache configured for mutable collection [%s]", id=222)
    public void readOnlyCacheConfiguredForMutableCollection(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Recognized obsolete hibernate namespace %s. Use namespace %s instead. Refer to Hibernate 3.6 Migration Guide!", id=223)
    public void recognizedObsoleteHibernateNamespace(String var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Property [%s] has been renamed to [%s]; update your properties appropriately", id=225)
    public void renamedProperty(Object var1, Object var2);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Required a different provider: %s", id=226)
    public void requiredDifferentProvider(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Running hbm2ddl schema export", id=227)
    public void runningHbm2ddlSchemaExport();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Running hbm2ddl schema update", id=228)
    public void runningHbm2ddlSchemaUpdate();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Running schema validator", id=229)
    public void runningSchemaValidator();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Schema export complete", id=230)
    public void schemaExportComplete();

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Schema export unsuccessful", id=231)
    public void schemaExportUnsuccessful(@Cause Exception var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Schema update complete", id=232)
    public void schemaUpdateComplete();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Scoping types to session factory %s after already scoped %s", id=233)
    public void scopingTypesToSessionFactoryAfterAlreadyScoped(SessionFactoryImplementor var1, SessionFactoryImplementor var2);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Searching for mapping documents in jar: %s", id=235)
    public void searchingForMappingDocuments(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Second level cache hits: %s", id=237)
    public void secondLevelCacheHits(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Second level cache misses: %s", id=238)
    public void secondLevelCacheMisses(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Second level cache puts: %s", id=239)
    public void secondLevelCachePuts(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Service properties: %s", id=240)
    public void serviceProperties(Properties var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Sessions closed: %s", id=241)
    public void sessionsClosed(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Sessions opened: %s", id=242)
    public void sessionsOpened(long var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="@Sort not allowed for an indexed collection, annotation ignored.", id=244)
    public void sortAnnotationIndexedCollection();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Manipulation query [%s] resulted in [%s] split queries", id=245)
    public void splitQueries(String var1, int var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="SQL Error: %s, SQLState: %s", id=247)
    public void sqlWarning(int var1, String var2);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Starting query cache at region: %s", id=248)
    public void startingQueryCache(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Starting service at JNDI name: %s", id=249)
    public void startingServiceAtJndiName(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Starting update timestamps cache at region: %s", id=250)
    public void startingUpdateTimestampsCache(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Start time: %s", id=251)
    public void startTime(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Statements closed: %s", id=252)
    public void statementsClosed(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Statements prepared: %s", id=253)
    public void statementsPrepared(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Stopping service", id=255)
    public void stoppingService();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="sub-resolver threw unexpected exception, continuing to next : %s", id=257)
    public void subResolverException(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Successful transactions: %s", id=258)
    public void successfulTransactions(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Synchronization [%s] was already registered", id=259)
    public void synchronizationAlreadyRegistered(Synchronization var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Exception calling user Synchronization [%s] : %s", id=260)
    public void synchronizationFailed(Synchronization var1, Throwable var2);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Table found: %s", id=261)
    public void tableFound(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Table not found: %s", id=262)
    public void tableNotFound(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="More than one table found: %s", id=263)
    public void multipleTablesFound(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Transactions: %s", id=266)
    public void transactions(long var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Transaction started on non-root session", id=267)
    public void transactionStartedOnNonRootSession();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Transaction strategy: %s", id=268)
    public void transactionStrategy(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Type [%s] defined no registration keys; ignoring", id=269)
    public void typeDefinedNoRegistrationKeys(BasicType var1);

    @LogMessage(level=Logger.Level.DEBUG)
    @Message(value="Type registration [%s] overrides previous : %s", id=270)
    public void typeRegistrationOverridesPrevious(String var1, Type var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Naming exception occurred accessing Ejb3Configuration", id=271)
    public void unableToAccessEjb3Configuration(@Cause NamingException var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Error while accessing session factory with JNDI name %s", id=272)
    public void unableToAccessSessionFactory(String var1, @Cause NamingException var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Error accessing type info result set : %s", id=273)
    public void unableToAccessTypeInfoResultSet(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to apply constraints on DDL for %s", id=274)
    public void unableToApplyConstraints(String var1, @Cause Exception var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Could not bind Ejb3Configuration to JNDI", id=276)
    public void unableToBindEjb3ConfigurationToJndi(@Cause JndiException var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Could not bind factory to JNDI", id=277)
    public void unableToBindFactoryToJndi(@Cause JndiException var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Could not bind value '%s' to parameter: %s; %s", id=278)
    public void unableToBindValueToParameter(String var1, int var2, String var3);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Unable to build enhancement metamodel for %s", id=279)
    public void unableToBuildEnhancementMetamodel(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Could not build SessionFactory using the MBean classpath - will try again using client classpath: %s", id=280)
    public void unableToBuildSessionFactoryUsingMBeanClasspath(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to clean up callable statement", id=281)
    public void unableToCleanUpCallableStatement(@Cause SQLException var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to clean up prepared statement", id=282)
    public void unableToCleanUpPreparedStatement(@Cause SQLException var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to cleanup temporary id table after use [%s]", id=283)
    public void unableToCleanupTemporaryIdTable(Throwable var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Error closing connection", id=284)
    public void unableToCloseConnection(@Cause Exception var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Error closing InitialContext [%s]", id=285)
    public void unableToCloseInitialContext(String var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Error closing input files: %s", id=286)
    public void unableToCloseInputFiles(String var1, @Cause IOException var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Could not close input stream", id=287)
    public void unableToCloseInputStream(@Cause IOException var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Could not close input stream for %s", id=288)
    public void unableToCloseInputStreamForResource(String var1, @Cause IOException var2);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Unable to close iterator", id=289)
    public void unableToCloseIterator(@Cause SQLException var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Could not close jar: %s", id=290)
    public void unableToCloseJar(String var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Error closing output file: %s", id=291)
    public void unableToCloseOutputFile(String var1, @Cause IOException var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="IOException occurred closing output stream", id=292)
    public void unableToCloseOutputStream(@Cause IOException var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Could not close session", id=294)
    public void unableToCloseSession(@Cause HibernateException var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Could not close session during rollback", id=295)
    public void unableToCloseSessionDuringRollback(@Cause Exception var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="IOException occurred closing stream", id=296)
    public void unableToCloseStream(@Cause IOException var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Could not close stream on hibernate.properties: %s", id=297)
    public void unableToCloseStreamError(IOException var1);

    @Message(value="JTA commit failed", id=298)
    public String unableToCommitJta();

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Could not complete schema update", id=299)
    public void unableToCompleteSchemaUpdate(@Cause Exception var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Could not complete schema validation", id=300)
    public void unableToCompleteSchemaValidation(@Cause SQLException var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to configure SQLExceptionConverter : %s", id=301)
    public void unableToConfigureSqlExceptionConverter(HibernateException var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Unable to construct current session context [%s]", id=302)
    public void unableToConstructCurrentSessionContext(String var1, @Cause Throwable var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to construct instance of specified SQLExceptionConverter : %s", id=303)
    public void unableToConstructSqlExceptionConverter(Throwable var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Could not copy system properties, system properties will be ignored", id=304)
    public void unableToCopySystemProperties();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Could not create proxy factory for:%s", id=305)
    public void unableToCreateProxyFactory(String var1, @Cause HibernateException var2);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Error creating schema ", id=306)
    public void unableToCreateSchema(@Cause Exception var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Could not deserialize cache file: %s : %s", id=307)
    public void unableToDeserializeCache(String var1, SerializationException var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to destroy cache: %s", id=308)
    public void unableToDestroyCache(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to destroy query cache: %s: %s", id=309)
    public void unableToDestroyQueryCache(String var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to destroy update timestamps cache: %s: %s", id=310)
    public void unableToDestroyUpdateTimestampsCache(String var1, String var2);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Unable to determine lock mode value : %s -> %s", id=311)
    public void unableToDetermineLockModeValue(String var1, Object var2);

    @Message(value="Could not determine transaction status", id=312)
    public String unableToDetermineTransactionStatus();

    @Message(value="Could not determine transaction status after commit", id=313)
    public String unableToDetermineTransactionStatusAfterCommit();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to evictData temporary id table after use [%s]", id=314)
    public void unableToDropTemporaryIdTable(String var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Exception executing batch [%s], SQL: %s", id=315)
    public void unableToExecuteBatch(Exception var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Error executing resolver [%s] : %s", id=316)
    public void unableToExecuteResolver(DialectResolver var1, String var2);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Could not find any META-INF/persistence.xml file in the classpath", id=318)
    public void unableToFindPersistenceXmlInClasspath();

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Could not get database metadata", id=319)
    public void unableToGetDatabaseMetadata(@Cause SQLException var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to instantiate configured schema name resolver [%s] %s", id=320)
    public void unableToInstantiateConfiguredSchemaNameResolver(String var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to interpret specified optimizer [%s], falling back to noop", id=321)
    public void unableToLocateCustomOptimizerClass(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to instantiate specified optimizer [%s], falling back to noop", id=322)
    public void unableToInstantiateOptimizer(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to instantiate UUID generation strategy class : %s", id=325)
    public void unableToInstantiateUuidGenerationStrategy(Exception var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Cannot join transaction: do not override %s", id=326)
    public void unableToJoinTransaction(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Error performing load command", id=327)
    public void unableToLoadCommand(@Cause HibernateException var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to load/access derby driver class sysinfo to check versions : %s", id=328)
    public void unableToLoadDerbyDriver(String var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Problem loading properties from hibernate.properties", id=329)
    public void unableToLoadProperties();

    @Message(value="Unable to locate config file: %s", id=330)
    public String unableToLocateConfigFile(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to locate configured schema name resolver class [%s] %s", id=331)
    public void unableToLocateConfiguredSchemaNameResolver(String var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to locate MBeanServer on JMX service shutdown", id=332)
    public void unableToLocateMBeanServer();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to locate requested UUID generation strategy class : %s", id=334)
    public void unableToLocateUuidGenerationStrategy(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to log SQLWarnings : %s", id=335)
    public void unableToLogSqlWarnings(SQLException var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Could not log warnings", id=336)
    public void unableToLogWarnings(@Cause SQLException var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Unable to mark for rollback on PersistenceException: ", id=337)
    public void unableToMarkForRollbackOnPersistenceException(@Cause Exception var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Unable to mark for rollback on TransientObjectException: ", id=338)
    public void unableToMarkForRollbackOnTransientObjectException(@Cause Exception var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Could not obtain connection metadata: %s", id=339)
    public void unableToObtainConnectionMetadata(SQLException var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Could not obtain connection to query metadata", id=342)
    public void unableToObtainConnectionToQueryMetadata(@Cause Exception var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Could not obtain initial context", id=343)
    public void unableToObtainInitialContext(@Cause NamingException var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Could not parse the package-level metadata [%s]", id=344)
    public void unableToParseMetadata(String var1);

    @Message(value="JDBC commit failed", id=345)
    public String unableToPerformJdbcCommit();

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Error during managed flush [%s]", id=346)
    public void unableToPerformManagedFlush(String var1);

    @Message(value="Unable to query java.sql.DatabaseMetaData", id=347)
    public String unableToQueryDatabaseMetadata();

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Unable to read class: %s", id=348)
    public void unableToReadClass(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Could not read column value from result set: %s; %s", id=349)
    public void unableToReadColumnValueFromResultSet(String var1, String var2);

    @Message(value="Could not read a hi value - you need to populate the table: %s", id=350)
    public String unableToReadHiValue(String var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Could not read or init a hi value", id=351)
    public void unableToReadOrInitHiValue(@Cause SQLException var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Unable to release batch statement...", id=352)
    public void unableToReleaseBatchStatement();

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Could not release a cache lock : %s", id=353)
    public void unableToReleaseCacheLock(CacheException var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Unable to release initial context: %s", id=354)
    public void unableToReleaseContext(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to release created MBeanServer : %s", id=355)
    public void unableToReleaseCreatedMBeanServer(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Unable to release isolated connection [%s]", id=356)
    public void unableToReleaseIsolatedConnection(Throwable var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to release type info result set", id=357)
    public void unableToReleaseTypeInfoResultSet();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to erase previously added bag join fetch", id=358)
    public void unableToRemoveBagJoinFetch();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Could not resolve aggregate function [%s]; using standard definition", id=359)
    public void unableToResolveAggregateFunction(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Unable to resolve mapping file [%s]", id=360)
    public void unableToResolveMappingFile(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Unable to retrieve cache from JNDI [%s]: %s", id=361)
    public void unableToRetrieveCache(String var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to retrieve type info result set : %s", id=362)
    public void unableToRetrieveTypeInfoResultSet(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Unable to rollback connection on exception [%s]", id=363)
    public void unableToRollbackConnection(Exception var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Unable to rollback isolated transaction on error [%s] : [%s]", id=364)
    public void unableToRollbackIsolatedTransaction(Exception var1, Exception var2);

    @Message(value="JTA rollback failed", id=365)
    public String unableToRollbackJta();

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Error running schema update", id=366)
    public void unableToRunSchemaUpdate(@Cause Exception var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Could not set transaction to rollback only", id=367)
    public void unableToSetTransactionToRollbackOnly(@Cause SystemException var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Exception while stopping service", id=368)
    public void unableToStopHibernateService(@Cause Exception var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Error stopping service [%s]", id=369)
    public void unableToStopService(Class var1, @Cause Exception var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Exception switching from method: [%s] to a method using the column index. Reverting to using: [%<s]", id=370)
    public void unableToSwitchToMethodUsingColumnIndex(Method var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Could not synchronize database state with session: %s", id=371)
    public void unableToSynchronizeDatabaseStateWithSession(HibernateException var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Could not toggle autocommit", id=372)
    public void unableToToggleAutoCommit(@Cause Exception var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Unable to transform class: %s", id=373)
    public void unableToTransformClass(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Could not unbind factory from JNDI", id=374)
    public void unableToUnbindFactoryFromJndi(@Cause JndiException var1);

    @Message(value="Could not update hi value in: %s", id=375)
    public String unableToUpdateHiValue(String var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Could not updateQuery hi value in: %s", id=376)
    public void unableToUpdateQueryHiValue(String var1, @Cause SQLException var2);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Error wrapping result set", id=377)
    public void unableToWrapResultSet(@Cause SQLException var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="I/O reported error writing cached file : %s: %s", id=378)
    public void unableToWriteCachedFile(String var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unexpected literal token type [%s] passed for numeric processing", id=380)
    public void unexpectedLiteralTokenType(int var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="JDBC driver did not return the expected number of row counts", id=381)
    public void unexpectedRowCounts();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="unrecognized bytecode provider [%s], using [%s] by default", id=382)
    public void unknownBytecodeProvider(String var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unknown Ingres major version [%s]; using Ingres 9.2 dialect", id=383)
    public void unknownIngresVersion(int var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unknown Oracle major version [%s]", id=384)
    public void unknownOracleVersion(int var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unknown Microsoft SQL Server major version [%s] using [%s] dialect", id=385)
    public void unknownSqlServerVersion(int var1, Class<? extends Dialect> var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="ResultSet had no statement associated with it, but was not yet registered", id=386)
    public void unregisteredResultSetWithoutStatement();

    @LogMessage(level=Logger.Level.DEBUG)
    @Message(value="ResultSet's statement was not registered", id=387)
    public void unregisteredStatement();

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Unsuccessful: %s", id=388)
    public void unsuccessful(String var1);

    @Deprecated
    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Unsuccessful: %s", id=389)
    public void unsuccessfulCreate(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Overriding release mode as connection provider does not support 'after_statement'", id=390)
    public void unsupportedAfterStatement();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Ingres 10 is not yet fully supported; using Ingres 9.3 dialect", id=391)
    public void unsupportedIngresVersion();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Hibernate does not support SequenceGenerator.initialValue() unless '%s' set", id=392)
    public void unsupportedInitialValue(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="The %s.%s.%s version of H2 implements temporary table creation such that it commits current transaction; multi-table, bulk hql/jpaql will not work properly", id=393)
    public void unsupportedMultiTableBulkHqlJpaql(int var1, int var2, int var3);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Oracle 11g is not yet fully supported; using Oracle 10g dialect", id=394)
    public void unsupportedOracleVersion();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Updating schema", id=396)
    public void updatingSchema();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Explicit segment value for id generator [%s.%s] suggested; using default [%s]", id=398)
    public void usingDefaultIdGeneratorSegmentValue(String var1, String var2, String var3);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Using default transaction strategy (direct JDBC transactions)", id=399)
    public void usingDefaultTransactionStrategy();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Using dialect: %s", id=400)
    public void usingDialect(Dialect var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Don't use old DTDs, read the Hibernate 3.x Migration Guide!", id=404)
    public void usingOldDtd();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Using bytecode reflection optimizer", id=406)
    public void usingReflectionOptimizer();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Using java.io streams to persist binary types", id=407)
    public void usingStreams();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Using %s which does not generate IETF RFC 4122 compliant UUID values; consider using %s instead", id=409)
    public void usingUuidHexGenerator(String var1, String var2);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Hibernate Validator not found: ignoring", id=410)
    public void validatorNotFound();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Hibernate ORM core version %s", id=412)
    public void version(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Warnings creating temp table : %s", id=413)
    public void warningsCreatingTempTable(SQLWarning var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Property hibernate.search.autoregister_listeners is set to false. No attempt will be made to register Hibernate Search event listeners.", id=414)
    public void willNotRegisterListeners();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Write locks via update not supported for non-versioned entities [%s]", id=416)
    public void writeLocksNotSupported(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Writing generated schema to file: %s", id=417)
    public void writingGeneratedSchemaToFile(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Adding override for %s: %s", id=418)
    public void addingOverrideFor(String var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Resolved SqlTypeDescriptor is for a different SQL code. %s has sqlCode=%s; type override %s has sqlCode=%s", id=419)
    public void resolvedSqlTypeDescriptorForDifferentSqlCode(String var1, String var2, String var3, String var4);

    @LogMessage(level=Logger.Level.DEBUG)
    @Message(value="Closing un-released batch", id=420)
    public void closingUnreleasedBatch();

    @LogMessage(level=Logger.Level.DEBUG)
    @Message(value="Disabling contextual LOB creation as %s is true", id=421)
    public void disablingContextualLOBCreation(String var1);

    @LogMessage(level=Logger.Level.DEBUG)
    @Message(value="Disabling contextual LOB creation as connection was null", id=422)
    public void disablingContextualLOBCreationSinceConnectionNull();

    @LogMessage(level=Logger.Level.DEBUG)
    @Message(value="Disabling contextual LOB creation as JDBC driver reported JDBC version [%s] less than 4", id=423)
    public void disablingContextualLOBCreationSinceOldJdbcVersion(int var1);

    @LogMessage(level=Logger.Level.DEBUG)
    @Message(value="Disabling contextual LOB creation as createClob() method threw error : %s", id=424)
    public void disablingContextualLOBCreationSinceCreateClobFailed(Throwable var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Could not close session; swallowing exception[%s] as transaction completed", id=425)
    public void unableToCloseSessionButSwallowingError(HibernateException var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="You should set hibernate.transaction.jta.platform if cache is enabled", id=426)
    public void setManagerLookupClass();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Encountered legacy TransactionManagerLookup specified; convert to newer %s contract specified via %s setting", id=428)
    public void legacyTransactionManagerStrategy(String var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Setting entity-identifier value binding where one already existed : %s.", id=429)
    public void entityIdentifierValueBindingExists(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="The DerbyDialect dialect has been deprecated; use one of the version-specific dialects instead", id=430)
    public void deprecatedDerbyDialect();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to determine H2 database version, certain features may not work", id=431)
    public void undeterminedH2Version();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="There were not column names specified for index %s on table %s", id=432)
    public void noColumnsSpecifiedForIndex(String var1, String var2);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="update timestamps cache puts: %s", id=433)
    public void timestampCachePuts(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="update timestamps cache hits: %s", id=434)
    public void timestampCacheHits(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="update timestamps cache misses: %s", id=435)
    public void timestampCacheMisses(long var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Entity manager factory name (%s) is already registered.  If entity manager will be clustered or passivated, specify a unique value for property '%s'", id=436)
    public void entityManagerFactoryAlreadyRegistered(String var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Attempting to save one or more entities that have a non-nullable association with an unsaved transient entity. The unsaved transient entity must be saved in an operation prior to saving these dependent entities.\n\tUnsaved transient entity: (%s)\n\tDependent entities: (%s)\n\tNon-nullable association(s): (%s)", id=437)
    public void cannotResolveNonNullableTransientDependencies(String var1, Set<String> var2, Set<String> var3);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="NaturalId cache puts: %s", id=438)
    public void naturalIdCachePuts(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="NaturalId cache hits: %s", id=439)
    public void naturalIdCacheHits(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="NaturalId cache misses: %s", id=440)
    public void naturalIdCacheMisses(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Max NaturalId query time: %sms", id=441)
    public void naturalIdMaxQueryTime(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="NaturalId queries executed to database: %s", id=442)
    public void naturalIdQueriesExecuted(long var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Dialect [%s] limits the number of elements in an IN predicate to %s entries.  However, the given parameter list [%s] contained %s entries, which will likely cause failures to execute the query in the database", id=443)
    public void tooManyInExpressions(String var1, int var2, String var3, int var4);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Encountered request for locking however dialect reports that database prefers locking be done in a separate select (follow-on locking); results will be locked after initial query executes", id=444)
    public void usingFollowOnLocking();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Alias-specific lock modes requested, which is not currently supported with follow-on locking; all acquired locks will be [%s]", id=445)
    public void aliasSpecificLockingWithFollowOnLocking(LockMode var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="embed-xml attributes were intended to be used for DOM4J entity mode. Since that entity mode has been removed, embed-xml attributes are no longer supported and should be removed from mappings.", id=446)
    public void embedXmlAttributesNoLongerSupported();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Explicit use of UPGRADE_SKIPLOCKED in lock() calls is not recommended; use normal UPGRADE locking instead", id=447)
    public void explicitSkipLockedLockCombo();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="'javax.persistence.validation.mode' named multiple values : %s", id=448)
    public void multipleValidationModes(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=449, value="@Convert annotation applied to Map attribute [%s] did not explicitly specify attributeName using 'key'/'value' as required by spec; attempting to DoTheRightThing")
    public void nonCompliantMapConversion(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=450, value="Encountered request for Service by non-primary service role [%s -> %s]; please update usage")
    public void alternateServiceRole(String var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=451, value="Transaction afterCompletion called by a background thread; delaying afterCompletion processing until the original thread can handle it. [status=%s]")
    public void rollbackFromBackgroundThread(int var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Exception while loading a class or resource found during scanning", id=452)
    public void unableToLoadScannedClassOrResource(@Cause Exception var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Exception while discovering OSGi service implementations : %s", id=453)
    public void unableToDiscoverOsgiService(String var1, @Cause Exception var2);

    @Deprecated
    @LogMessage(level=Logger.Level.WARN)
    @Message(value="The outer-join attribute on <many-to-many> has been deprecated. Instead of outer-join=\"false\", use lazy=\"extra\" with <map>, <set>, <bag>, <idbag>, or <list>, which will only initialize entities (not as a proxy) as needed.", id=454)
    public void deprecatedManyToManyOuterJoin();

    @Deprecated
    @LogMessage(level=Logger.Level.WARN)
    @Message(value="The fetch attribute on <many-to-many> has been deprecated. Instead of fetch=\"select\", use lazy=\"extra\" with <map>, <set>, <bag>, <idbag>, or <list>, which will only initialize entities (not as a proxy) as needed.", id=455)
    public void deprecatedManyToManyFetch();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Named parameters are used for a callable statement, but database metadata indicates named parameters are not supported.", id=456)
    public void unsupportedNamedParameters();

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=457, value="Joined inheritance hierarchy [%1$s] defined explicit @DiscriminatorColumn.  Legacy Hibernate behavior was to ignore the @DiscriminatorColumn.  However, as part of issue HHH-6911 we now apply the explicit @DiscriminatorColumn.  If you would prefer the legacy behavior, enable the `%2$s` setting (%2$s=true)")
    public void applyingExplicitDiscriminatorColumnForJoined(String var1, String var2);

    @LogMessage(level=Logger.Level.DEBUG)
    @Message(value="Creating pooled optimizer (lo) with [incrementSize=%s; returnClass=%s]", id=467)
    public void creatingPooledLoOptimizer(int var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to interpret type [%s] as an AttributeConverter due to an exception : %s", id=468)
    public void logBadHbmAttributeConverterType(String var1, String var2);

    @Message(value="The ClassLoaderService can not be reused. This instance was stopped already.", id=469)
    public HibernateException usingStoppedClassLoaderService();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="An unexpected session is defined for a collection, but the collection is not connected to that session. A persistent collection may only be associated with one session at a time. Overwriting session. %s", id=470)
    public void logUnexpectedSessionInCollectionNotConnected(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Cannot unset session in a collection because an unexpected session is defined. A persistent collection may only be associated with one session at a time. %s", id=471)
    public void logCannotUnsetUnexpectedSessionInCollection(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Hikari properties were encountered, but the Hikari ConnectionProvider was not found on the classpath; these properties are going to be ignored.", id=472)
    public void hikariProviderClassNotFound();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Omitting cached file [%s] as the mapping file is newer", id=473)
    public void cachedFileObsolete(File var1);

    @Message(value="Ambiguous persistent property methods detected on %s; mark one as @Transient : [%s] and [%s]", id=474)
    public String ambiguousPropertyMethods(String var1, String var2, String var3);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Cannot locate column information using identifier [%s]; ignoring index [%s]", id=475)
    public void logCannotLocateIndexColumnInformation(String var1, String var2);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Executing import script '%s'", id=476)
    public void executingImportScript(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Starting delayed evictData of schema as part of SessionFactory shut-down'", id=477)
    public void startingDelayedSchemaDrop();

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Unsuccessful: %s", id=478)
    public void unsuccessfulSchemaManagementCommand(String var1);

    @Message(value="Collection [%s] was not processed by flush(). This is likely due to unsafe use of the session (e.g. used in multiple threads concurrently, updates during entity lifecycle hooks).", id=479)
    public String collectionNotProcessedByFlush(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="A ManagedEntity was associated with a stale PersistenceContext. A ManagedEntity may only be associated with one PersistenceContext at a time; %s", id=480)
    public void stalePersistenceContextInEntityEntry(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=481, value="Encountered Java type [%s] for which we could not locate a JavaTypeDescriptor and which does not appear to implement equals and/or hashCode.  This can lead to significant performance problems when performing equality/dirty checking involving this Java type.  Consider registering a custom JavaTypeDescriptor or at least implementing equals/hashCode.")
    public void unknownJavaTypeNoEqualsHashCode(Class var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="@org.hibernate.annotations.Cache used on a non-root entity: ignored for [%s]. Please see the Hibernate documentation for proper usage.", id=482)
    public void cacheOrCacheableAnnotationOnNonRoot(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=483, value="An experimental feature has been enabled (hibernate.create_empty_composites.enabled=true) that instantiates empty composite/embedded objects when all of its attribute values are null. This feature has known issues and should not be used in production until it is stabilized. See Hibernate Jira issue HHH-11936 for details.")
    public void emptyCompositesEnabled();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Vibur properties were encountered, but the Vibur ConnectionProvider was not found on the classpath; these properties are going to be ignored.", id=484)
    public void viburProviderClassNotFound();

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Illegally attempted to associate a proxy for entity [%s] with id [%s] with two open sessions.", id=485)
    public void attemptToAssociateProxyWithTwoOpenSessions(String var1, Object var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Agroal properties were encountered, but the Agroal ConnectionProvider was not found on the classpath; these properties are going to be ignored.", id=486)
    public void agroalProviderClassNotFound();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="The query: [%s] attempts to update an immutable entity: %s", id=487)
    public void immutableEntityUpdateQuery(String var1, String var2);

    @Message(value="Bytecode enhancement failed for class: %1$s. It might be due to the Java module system preventing Hibernate ORM from defining an enhanced class in the same package as class %1$s. In this case, the class should be opened and exported to Hibernate ORM.", id=488)
    public String bytecodeEnhancementFailedUnableToGetPrivateLookupFor(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Setting hibernate.native_exception_handling_51_compliance=true is not valid with JPA bootstrapping; setting will be ignored.", id=489)
    public void nativeExceptionHandling51ComplianceJpaBootstrapping();

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Using JtaPlatform implementation: [%s]", id=490)
    public void usingJtaPlatform(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="The [%2$s] association in the [%1$s] entity uses both @NotFound(action = NotFoundAction.IGNORE) and FetchType.LAZY. The NotFoundAction.IGNORE @ManyToOne and @OneToOne associations are always fetched eagerly.", id=491)
    public void ignoreNotFoundWithFetchTypeLazy(String var1, String var2);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Query plan cache hits: %s", id=492)
    public void queryPlanCacheHits(long var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Query plan cache misses: %s", id=493)
    public void queryPlanCacheMisses(long var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Attempt to merge an uninitialized collection with queued operations; queued operations will be ignored: %s", id=494)
    public void ignoreQueuedOperationsOnMerge(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Attaching an uninitialized collection with queued operations to a session: %s", id=495)
    public void queuedOperationWhenAttachToSession(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Detaching an uninitialized collection with queued operations from a session: %s", id=496)
    public void queuedOperationWhenDetachFromSession(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="The increment size of the [%s] sequence is set to [%d] in the entity mapping while the associated database sequence increment size is [%d]. The database sequence increment size will take precedence to avoid identifier allocation conflicts.", id=497)
    public void sequenceIncrementSizeMismatch(String var1, int var2, int var3);

    @LogMessage(level=Logger.Level.DEBUG)
    @Message(value="Detaching an uninitialized collection with queued operations from a session due to rollback: %s", id=498)
    public void queuedOperationWhenDetachFromSessionOnRollback(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Using @AttributeOverride or @AttributeOverrides in conjunction with entity inheritance is not supported: %s. The overriding definitions are ignored.", id=499)
    public void unsupportedAttributeOverrideWithEntityInheritance(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="The [%s] property of the [%s] entity was modified, but it won't be updated because the property is immutable.", id=502)
    public void ignoreImmutablePropertyModification(String var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="A class should not be annotated with both @Inheritance and @MappedSuperclass. @Inheritance will be ignored for: %s.", id=503)
    public void unsupportedMappedSuperclassWithEntityInheritance(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Multiple configuration properties defined to create schema. Choose at most one among 'javax.persistence.create-database-schemas', 'hibernate.hbm2ddl.create_namespaces', 'hibernate.hbm2dll.create_namespaces' (this last being deprecated).", id=504)
    public void multipleSchemaCreationSettingsDefined();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Ignoring ServiceConfigurationError caught while trying to instantiate service '%s'.", id=505)
    public void ignoringServiceConfigurationError(Class<?> var1, @Cause ServiceConfigurationError var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Detaching an uninitialized collection with enabled filters from a session: %s", id=506)
    public void enabledFiltersWhenDetachFromSession(String var1);

    @Message(value="The Javassist based BytecodeProvider has been removed: remove the `hibernate.bytecode.provider` configuration property to switch to the default provider", id=508)
    public HibernateException usingRemovedJavassistBytecodeProvider();
}

