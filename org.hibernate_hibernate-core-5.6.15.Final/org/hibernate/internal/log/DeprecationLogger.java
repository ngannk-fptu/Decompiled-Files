/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.BasicLogger
 *  org.jboss.logging.Logger
 *  org.jboss.logging.Logger$Level
 *  org.jboss.logging.annotations.LogMessage
 *  org.jboss.logging.annotations.Message
 *  org.jboss.logging.annotations.MessageLogger
 *  org.jboss.logging.annotations.ValidIdRange
 */
package org.hibernate.internal.log;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.ValidIdRange;

@MessageLogger(projectCode="HHH")
@ValidIdRange(min=90000001, max=90001000)
public interface DeprecationLogger
extends BasicLogger {
    public static final String CATEGORY = "org.hibernate.orm.deprecation";
    public static final DeprecationLogger DEPRECATION_LOGGER = (DeprecationLogger)Logger.getMessageLogger(DeprecationLogger.class, (String)"org.hibernate.orm.deprecation");

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Found usage of deprecated setting for specifying Scanner [hibernate.ejb.resource_scanner]; use [hibernate.archive.scanner] instead", id=90000001)
    public void logDeprecatedScannerSetting();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Support for an entity defining multiple entity-modes is deprecated", id=90000002)
    public void logDeprecationOfMultipleEntityModeSupport();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Use of DOM4J entity-mode is considered deprecated", id=90000003)
    public void logDeprecationOfDomEntityModeSupport();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="embed-xml attributes were intended to be used for DOM4J entity mode. Since that entity mode has been removed, embed-xml attributes are no longer supported and should be removed from mappings.", id=90000004)
    public void logDeprecationOfEmbedXmlSupport();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Defining an entity [%s] with no physical id attribute is no longer supported; please map the identifier to a physical entity attribute", id=90000005)
    public void logDeprecationOfNonNamedIdAttribute(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Attempted to specify unsupported NamingStrategy via setting [%s]; NamingStrategy has been removed in favor of the split ImplicitNamingStrategy and PhysicalNamingStrategy; use [%s] or [%s], respectively, instead.", id=90000006)
    public void logDeprecatedNamingStrategySetting(String var1, String var2, String var3);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Attempted to specify unsupported NamingStrategy via command-line argument [--naming]. NamingStrategy has been removed in favor of the split ImplicitNamingStrategy and PhysicalNamingStrategy; use [--implicit-naming] or [--physical-naming], respectively, instead.", id=90000007)
    public void logDeprecatedNamingStrategyArgument();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Attempted to specify unsupported NamingStrategy via Ant task argument. NamingStrategy has been removed in favor of the split ImplicitNamingStrategy and PhysicalNamingStrategy.", id=90000008)
    public void logDeprecatedNamingStrategyAntArgument();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="The outer-join attribute on <many-to-many> has been deprecated. Instead of outer-join=\"false\", use lazy=\"extra\" with <map>, <set>, <bag>, <idbag>, or <list>, which will only initialize entities (not as a proxy) as needed.", id=90000009)
    public void deprecatedManyToManyOuterJoin();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="The fetch attribute on <many-to-many> has been deprecated. Instead of fetch=\"select\", use lazy=\"extra\" with <map>, <set>, <bag>, <idbag>, or <list>, which will only initialize entities (not as a proxy) as needed.", id=90000010)
    public void deprecatedManyToManyFetch();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="org.hibernate.hql.spi.TemporaryTableBulkIdStrategy (temporary) has been deprecated in favor of the more specific org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy (local_temporary).", id=90000011)
    public void logDeprecationOfTemporaryTableBulkIdStrategy();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Recognized obsolete hibernate namespace %s. Use namespace %s instead.  Support for obsolete DTD/XSD namespaces may be removed at any time.", id=90000012)
    public void recognizedObsoleteHibernateNamespace(String var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=90000013, value="Named ConnectionProvider [%s] has been deprecated in favor of %s; that provider will be used instead.  Update your settings")
    public void connectionProviderClassDeprecated(String var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=90000014, value="Found use of deprecated [%s] sequence-based id generator; use org.hibernate.id.enhanced.SequenceStyleGenerator instead.  See Hibernate Domain Model Mapping Guide for details.")
    public void deprecatedSequenceGenerator(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=90000015, value="Found use of deprecated [%s] table-based id generator; use org.hibernate.id.enhanced.TableGenerator instead.  See Hibernate Domain Model Mapping Guide for details.")
    public void deprecatedTableGenerator(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=90000016, value="Found use of deprecated 'collection property' syntax in HQL/JPQL query [%2$s.%1$s]; use collection function syntax instead [%1$s(%2$s)].")
    public void logDeprecationOfCollectionPropertiesInHql(String var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=90000017, value="Found use of deprecated entity-type selector syntax in HQL/JPQL query ['%1$s.class']; use TYPE operator instead : type(%1$s)")
    public void logDeprecationOfClassEntityTypeSelector(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=90000018, value="Found use of deprecated transaction factory setting [%s]; use the new TransactionCoordinatorBuilder settings [%s] instead")
    public void logDeprecatedTransactionFactorySetting(String var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=90000020, value="You are using the deprecated legacy bytecode enhancement Ant-task.  This task is left in place for a short-time to aid migrations to 5.1 and the new (vastly improved) bytecode enhancement support.  This task (%s) now delegates to thenew Ant-task (%s) leveraging that new bytecode enhancement.  You should update your build to use the new task explicitly.")
    public void logDeprecatedInstrumentTask(Class var1, Class var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=90000021, value="Encountered deprecated setting [%s], use [%s] instead")
    public void deprecatedSetting(String var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=90000022, value="Hibernate's legacy org.hibernate.Criteria API is deprecated; use the JPA javax.persistence.criteria.CriteriaQuery instead")
    public void deprecatedLegacyCriteria();

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=90000023, value="Encountered use of deprecated Connection handling settings [hibernate.connection.acquisition_mode]or [hibernate.connection.release_mode]; use [hibernate.connection.handling_mode] instead")
    public void logUseOfDeprecatedConnectionHandlingSettings();

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=90000024, value="Application requested zero be used as the base for JDBC-style parameters found in native-queries; this is a *temporary* backwards-compatibility setting to help applications  using versions prior to 5.3 in upgrading.  It will be removed in a later version.")
    public void logUseOfDeprecatedZeroBasedJdbcStyleParams();

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=90000025, value="Encountered multiple component mappings for the same java class [%s] with different property mappings. This is deprecated and will be removed in a future version. Every property mapping combination should have its own java class")
    public void deprecatedComponentMapping(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=90000026, value="JACC integration was enabled.  Support for JACC integration will be removed in version 6.0.  Use of`%s`, `%s` or `%s` settings is discouraged")
    public void deprecatedJaccUsage(String var1, String var2, String var3);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=90000027, value="JACC settings encountered in hibernate `cfg.xml` file.  JACC integration is deprecated and will be removed in version 6.0")
    public void deprecatedJaccCfgXmlSettings();

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=90000028, value="Manageable service was registered with JMX support (`%s`).  JMX support is scheduled for removal in 6.0")
    public void deprecatedJmxManageableServiceRegistration(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=90000029, value="JMX support has been enabled via `%s`.  This feature is scheduled for removal in 6.0")
    public void deprecatedJmxSupport(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=90000030, value="MBean was registered with JMX support (`%s`).  JMX support is scheduled for removal in 6.0")
    public void deprecatedJmxBeanRegistration(String var1);
}

