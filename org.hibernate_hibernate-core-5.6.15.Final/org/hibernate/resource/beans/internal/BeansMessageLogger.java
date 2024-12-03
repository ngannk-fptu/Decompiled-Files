/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 *  org.jboss.logging.Logger$Level
 *  org.jboss.logging.annotations.LogMessage
 *  org.jboss.logging.annotations.Message
 *  org.jboss.logging.annotations.MessageLogger
 *  org.jboss.logging.annotations.ValidIdRange
 */
package org.hibernate.resource.beans.internal;

import org.hibernate.resource.beans.container.spi.BeanContainer;
import org.hibernate.resource.beans.spi.ManagedBeanRegistry;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.ValidIdRange;

@MessageLogger(projectCode="HHH")
@ValidIdRange(min=10005001, max=10010000)
public interface BeansMessageLogger {
    public static final BeansMessageLogger BEANS_LOGGER = (BeansMessageLogger)Logger.getMessageLogger(BeansMessageLogger.class, (String)"org.hibernate.orm.beans");

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=10005001, value="An explicit CDI BeanManager reference [%s] was passed to Hibernate, but CDI is not available on the Hibernate ClassLoader.  This is likely going to lead to exceptions later on in bootstrap")
    public void beanManagerButCdiNotAvailable(Object var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(id=10005002, value="No explicit CDI BeanManager reference was passed to Hibernate, but CDI is available on the Hibernate ClassLoader.")
    public void noBeanManagerButCdiAvailable();

    @LogMessage(level=Logger.Level.INFO)
    @Message(id=10005003, value="Stopping ManagedBeanRegistry : %s")
    public void stoppingManagedBeanRegistry(ManagedBeanRegistry var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(id=10005004, value="Stopping BeanContainer : %s")
    public void stoppingBeanContainer(BeanContainer var1);
}

