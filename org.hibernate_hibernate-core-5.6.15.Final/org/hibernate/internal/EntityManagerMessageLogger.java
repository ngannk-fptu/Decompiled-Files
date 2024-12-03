/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger$Level
 *  org.jboss.logging.annotations.Cause
 *  org.jboss.logging.annotations.LogMessage
 *  org.jboss.logging.annotations.Message
 *  org.jboss.logging.annotations.MessageLogger
 */
package org.hibernate.internal;

import java.net.URISyntaxException;
import java.net.URL;
import org.hibernate.internal.CoreMessageLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

@MessageLogger(projectCode="HHH")
public interface EntityManagerMessageLogger
extends CoreMessageLogger {
    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Bound Ejb3Configuration to JNDI name: %s", id=15001)
    public void boundEjb3ConfigurationToJndiName(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Ejb3Configuration name: %s", id=15002)
    public void ejb3ConfigurationName(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="An Ejb3Configuration was renamed from name: %s", id=15003)
    public void ejb3ConfigurationRenamedFromName(String var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="An Ejb3Configuration was unbound from name: %s", id=15004)
    public void ejb3ConfigurationUnboundFromName(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Exploded jar file does not exist (ignored): %s", id=15005)
    public void explodedJarDoesNotExist(URL var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Exploded jar file not a directory (ignored): %s", id=15006)
    public void explodedJarNotDirectory(URL var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Illegal argument on static metamodel field injection : %s#%s; expected type :  %s; encountered type : %s", id=15007)
    public void illegalArgumentOnStaticMetamodelFieldInjection(String var1, String var2, String var3, String var4);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(value="Malformed URL: %s", id=15008)
    public void malformedUrl(URL var1, @Cause URISyntaxException var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Malformed URL: %s", id=15009)
    public void malformedUrlWarning(URL var1, @Cause URISyntaxException var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to find file (ignored): %s", id=15010)
    public void unableToFindFile(URL var1, @Cause Exception var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Unable to locate static metamodel field : %s#%s; this may or may not indicate a problem with the static metamodel", id=15011)
    public void unableToLocateStaticMetamodelField(String var1, String var2);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Using provided datasource", id=15012)
    public void usingProvidedDataSource();

    @LogMessage(level=Logger.Level.DEBUG)
    @Message(value="Returning null (as required by JPA spec) rather than throwing EntityNotFoundException, as the entity (type=%s, id=%s) does not exist", id=15013)
    public void ignoringEntityNotFound(String var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="DEPRECATION - attempt to refer to JPA positional parameter [?%1$s] using String name [\"%1$s\"] rather than int position [%1$s] (generally in Query#setParameter, Query#getParameter or Query#getParameterValue calls).  Hibernate previously allowed such usage, but it is considered deprecated.", id=15014)
    public void deprecatedJpaPositionalParameterAccess(Integer var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(id=15015, value="Encountered a MappedSuperclass [%s] not used in any entity hierarchy")
    public void unusedMappedSuperclass(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=15016, value="Encountered a deprecated javax.persistence.spi.PersistenceProvider [%s]; [%s] will be used instead.")
    public void deprecatedPersistenceProvider(String var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=15017, value="'hibernate.ejb.use_class_enhancer' property is deprecated. Use 'hibernate.enhance.enable[...]' properties instead to enable each individual feature.")
    public void deprecatedInstrumentationProperty();

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=15018, value="Encountered multiple persistence-unit stanzas defining same name [%s]; persistence-unit names must be unique")
    public void duplicatedPersistenceUnitName(String var1);
}

