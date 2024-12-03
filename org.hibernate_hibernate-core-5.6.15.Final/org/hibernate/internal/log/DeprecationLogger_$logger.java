/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.BasicLogger
 *  org.jboss.logging.DelegatingBasicLogger
 *  org.jboss.logging.Logger
 *  org.jboss.logging.Logger$Level
 */
package org.hibernate.internal.log;

import java.io.Serializable;
import java.util.Locale;
import org.hibernate.internal.log.DeprecationLogger;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.DelegatingBasicLogger;
import org.jboss.logging.Logger;

public class DeprecationLogger_$logger
extends DelegatingBasicLogger
implements DeprecationLogger,
BasicLogger,
Serializable {
    private static final long serialVersionUID = 1L;
    private static final String FQCN = DeprecationLogger_$logger.class.getName();
    private static final Locale LOCALE = Locale.ROOT;

    public DeprecationLogger_$logger(Logger log) {
        super(log);
    }

    protected Locale getLoggingLocale() {
        return LOCALE;
    }

    @Override
    public final void logDeprecatedScannerSetting() {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.logDeprecatedScannerSetting$str(), new Object[0]);
    }

    protected String logDeprecatedScannerSetting$str() {
        return "HHH90000001: Found usage of deprecated setting for specifying Scanner [hibernate.ejb.resource_scanner]; use [hibernate.archive.scanner] instead";
    }

    @Override
    public final void logDeprecationOfMultipleEntityModeSupport() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.logDeprecationOfMultipleEntityModeSupport$str(), new Object[0]);
    }

    protected String logDeprecationOfMultipleEntityModeSupport$str() {
        return "HHH90000002: Support for an entity defining multiple entity-modes is deprecated";
    }

    @Override
    public final void logDeprecationOfDomEntityModeSupport() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.logDeprecationOfDomEntityModeSupport$str(), new Object[0]);
    }

    protected String logDeprecationOfDomEntityModeSupport$str() {
        return "HHH90000003: Use of DOM4J entity-mode is considered deprecated";
    }

    @Override
    public final void logDeprecationOfEmbedXmlSupport() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.logDeprecationOfEmbedXmlSupport$str(), new Object[0]);
    }

    protected String logDeprecationOfEmbedXmlSupport$str() {
        return "HHH90000004: embed-xml attributes were intended to be used for DOM4J entity mode. Since that entity mode has been removed, embed-xml attributes are no longer supported and should be removed from mappings.";
    }

    @Override
    public final void logDeprecationOfNonNamedIdAttribute(String entityName) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.logDeprecationOfNonNamedIdAttribute$str(), (Object)entityName);
    }

    protected String logDeprecationOfNonNamedIdAttribute$str() {
        return "HHH90000005: Defining an entity [%s] with no physical id attribute is no longer supported; please map the identifier to a physical entity attribute";
    }

    @Override
    public final void logDeprecatedNamingStrategySetting(String setting, String implicitInstead, String physicalInstead) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.logDeprecatedNamingStrategySetting$str(), (Object)setting, (Object)implicitInstead, (Object)physicalInstead);
    }

    protected String logDeprecatedNamingStrategySetting$str() {
        return "HHH90000006: Attempted to specify unsupported NamingStrategy via setting [%s]; NamingStrategy has been removed in favor of the split ImplicitNamingStrategy and PhysicalNamingStrategy; use [%s] or [%s], respectively, instead.";
    }

    @Override
    public final void logDeprecatedNamingStrategyArgument() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.logDeprecatedNamingStrategyArgument$str(), new Object[0]);
    }

    protected String logDeprecatedNamingStrategyArgument$str() {
        return "HHH90000007: Attempted to specify unsupported NamingStrategy via command-line argument [--naming]. NamingStrategy has been removed in favor of the split ImplicitNamingStrategy and PhysicalNamingStrategy; use [--implicit-naming] or [--physical-naming], respectively, instead.";
    }

    @Override
    public final void logDeprecatedNamingStrategyAntArgument() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.logDeprecatedNamingStrategyAntArgument$str(), new Object[0]);
    }

    protected String logDeprecatedNamingStrategyAntArgument$str() {
        return "HHH90000008: Attempted to specify unsupported NamingStrategy via Ant task argument. NamingStrategy has been removed in favor of the split ImplicitNamingStrategy and PhysicalNamingStrategy.";
    }

    @Override
    public final void deprecatedManyToManyOuterJoin() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.deprecatedManyToManyOuterJoin$str(), new Object[0]);
    }

    protected String deprecatedManyToManyOuterJoin$str() {
        return "HHH90000009: The outer-join attribute on <many-to-many> has been deprecated. Instead of outer-join=\"false\", use lazy=\"extra\" with <map>, <set>, <bag>, <idbag>, or <list>, which will only initialize entities (not as a proxy) as needed.";
    }

    @Override
    public final void deprecatedManyToManyFetch() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.deprecatedManyToManyFetch$str(), new Object[0]);
    }

    protected String deprecatedManyToManyFetch$str() {
        return "HHH90000010: The fetch attribute on <many-to-many> has been deprecated. Instead of fetch=\"select\", use lazy=\"extra\" with <map>, <set>, <bag>, <idbag>, or <list>, which will only initialize entities (not as a proxy) as needed.";
    }

    @Override
    public final void logDeprecationOfTemporaryTableBulkIdStrategy() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.logDeprecationOfTemporaryTableBulkIdStrategy$str(), new Object[0]);
    }

    protected String logDeprecationOfTemporaryTableBulkIdStrategy$str() {
        return "HHH90000011: org.hibernate.hql.spi.TemporaryTableBulkIdStrategy (temporary) has been deprecated in favor of the more specific org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy (local_temporary).";
    }

    @Override
    public final void recognizedObsoleteHibernateNamespace(String oldHibernateNamespace, String hibernateNamespace) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.recognizedObsoleteHibernateNamespace$str(), (Object)oldHibernateNamespace, (Object)hibernateNamespace);
    }

    protected String recognizedObsoleteHibernateNamespace$str() {
        return "HHH90000012: Recognized obsolete hibernate namespace %s. Use namespace %s instead.  Support for obsolete DTD/XSD namespaces may be removed at any time.";
    }

    @Override
    public final void connectionProviderClassDeprecated(String providerClassName, String actualProviderClassName) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.connectionProviderClassDeprecated$str(), (Object)providerClassName, (Object)actualProviderClassName);
    }

    protected String connectionProviderClassDeprecated$str() {
        return "HHH90000013: Named ConnectionProvider [%s] has been deprecated in favor of %s; that provider will be used instead.  Update your settings";
    }

    @Override
    public final void deprecatedSequenceGenerator(String generatorImpl) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.deprecatedSequenceGenerator$str(), (Object)generatorImpl);
    }

    protected String deprecatedSequenceGenerator$str() {
        return "HHH90000014: Found use of deprecated [%s] sequence-based id generator; use org.hibernate.id.enhanced.SequenceStyleGenerator instead.  See Hibernate Domain Model Mapping Guide for details.";
    }

    @Override
    public final void deprecatedTableGenerator(String generatorImpl) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.deprecatedTableGenerator$str(), (Object)generatorImpl);
    }

    protected String deprecatedTableGenerator$str() {
        return "HHH90000015: Found use of deprecated [%s] table-based id generator; use org.hibernate.id.enhanced.TableGenerator instead.  See Hibernate Domain Model Mapping Guide for details.";
    }

    @Override
    public final void logDeprecationOfCollectionPropertiesInHql(String collectionPropertyName, String alias) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.logDeprecationOfCollectionPropertiesInHql$str(), (Object)collectionPropertyName, (Object)alias);
    }

    protected String logDeprecationOfCollectionPropertiesInHql$str() {
        return "HHH90000016: Found use of deprecated 'collection property' syntax in HQL/JPQL query [%2$s.%1$s]; use collection function syntax instead [%1$s(%2$s)].";
    }

    @Override
    public final void logDeprecationOfClassEntityTypeSelector(String path) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.logDeprecationOfClassEntityTypeSelector$str(), (Object)path);
    }

    protected String logDeprecationOfClassEntityTypeSelector$str() {
        return "HHH90000017: Found use of deprecated entity-type selector syntax in HQL/JPQL query ['%1$s.class']; use TYPE operator instead : type(%1$s)";
    }

    @Override
    public final void logDeprecatedTransactionFactorySetting(String legacySettingName, String updatedSettingName) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.logDeprecatedTransactionFactorySetting$str(), (Object)legacySettingName, (Object)updatedSettingName);
    }

    protected String logDeprecatedTransactionFactorySetting$str() {
        return "HHH90000018: Found use of deprecated transaction factory setting [%s]; use the new TransactionCoordinatorBuilder settings [%s] instead";
    }

    @Override
    public final void logDeprecatedInstrumentTask(Class taskClass, Class newTaskClass) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.logDeprecatedInstrumentTask$str(), (Object)taskClass, (Object)newTaskClass);
    }

    protected String logDeprecatedInstrumentTask$str() {
        return "HHH90000020: You are using the deprecated legacy bytecode enhancement Ant-task.  This task is left in place for a short-time to aid migrations to 5.1 and the new (vastly improved) bytecode enhancement support.  This task (%s) now delegates to thenew Ant-task (%s) leveraging that new bytecode enhancement.  You should update your build to use the new task explicitly.";
    }

    @Override
    public final void deprecatedSetting(String oldSettingName, String newSettingName) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.deprecatedSetting$str(), (Object)oldSettingName, (Object)newSettingName);
    }

    protected String deprecatedSetting$str() {
        return "HHH90000021: Encountered deprecated setting [%s], use [%s] instead";
    }

    @Override
    public final void deprecatedLegacyCriteria() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.deprecatedLegacyCriteria$str(), new Object[0]);
    }

    protected String deprecatedLegacyCriteria$str() {
        return "HHH90000022: Hibernate's legacy org.hibernate.Criteria API is deprecated; use the JPA javax.persistence.criteria.CriteriaQuery instead";
    }

    @Override
    public final void logUseOfDeprecatedConnectionHandlingSettings() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.logUseOfDeprecatedConnectionHandlingSettings$str(), new Object[0]);
    }

    protected String logUseOfDeprecatedConnectionHandlingSettings$str() {
        return "HHH90000023: Encountered use of deprecated Connection handling settings [hibernate.connection.acquisition_mode]or [hibernate.connection.release_mode]; use [hibernate.connection.handling_mode] instead";
    }

    @Override
    public final void logUseOfDeprecatedZeroBasedJdbcStyleParams() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.logUseOfDeprecatedZeroBasedJdbcStyleParams$str(), new Object[0]);
    }

    protected String logUseOfDeprecatedZeroBasedJdbcStyleParams$str() {
        return "HHH90000024: Application requested zero be used as the base for JDBC-style parameters found in native-queries; this is a *temporary* backwards-compatibility setting to help applications  using versions prior to 5.3 in upgrading.  It will be removed in a later version.";
    }

    @Override
    public final void deprecatedComponentMapping(String name) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.deprecatedComponentMapping$str(), (Object)name);
    }

    protected String deprecatedComponentMapping$str() {
        return "HHH90000025: Encountered multiple component mappings for the same java class [%s] with different property mappings. This is deprecated and will be removed in a future version. Every property mapping combination should have its own java class";
    }

    @Override
    public final void deprecatedJaccUsage(String jaccEnabled, String jaccContextId, String jaccPrefix) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.deprecatedJaccUsage$str(), (Object)jaccEnabled, (Object)jaccContextId, (Object)jaccPrefix);
    }

    protected String deprecatedJaccUsage$str() {
        return "HHH90000026: JACC integration was enabled.  Support for JACC integration will be removed in version 6.0.  Use of`%s`, `%s` or `%s` settings is discouraged";
    }

    @Override
    public final void deprecatedJaccCfgXmlSettings() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.deprecatedJaccCfgXmlSettings$str(), new Object[0]);
    }

    protected String deprecatedJaccCfgXmlSettings$str() {
        return "HHH90000027: JACC settings encountered in hibernate `cfg.xml` file.  JACC integration is deprecated and will be removed in version 6.0";
    }

    @Override
    public final void deprecatedJmxManageableServiceRegistration(String jmxEnabledSetting) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.deprecatedJmxManageableServiceRegistration$str(), (Object)jmxEnabledSetting);
    }

    protected String deprecatedJmxManageableServiceRegistration$str() {
        return "HHH90000028: Manageable service was registered with JMX support (`%s`).  JMX support is scheduled for removal in 6.0";
    }

    @Override
    public final void deprecatedJmxSupport(String jmxEnabledSetting) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.deprecatedJmxSupport$str(), (Object)jmxEnabledSetting);
    }

    protected String deprecatedJmxSupport$str() {
        return "HHH90000029: JMX support has been enabled via `%s`.  This feature is scheduled for removal in 6.0";
    }

    @Override
    public final void deprecatedJmxBeanRegistration(String name) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.deprecatedJmxBeanRegistration$str(), (Object)name);
    }

    protected String deprecatedJmxBeanRegistration$str() {
        return "HHH90000030: MBean was registered with JMX support (`%s`).  JMX support is scheduled for removal in 6.0";
    }
}

