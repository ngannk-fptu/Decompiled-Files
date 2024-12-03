/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 */
package org.springframework.scheduling.concurrent;

import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import javax.naming.NamingException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.jndi.JndiTemplate;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

public class DefaultManagedTaskScheduler
extends ConcurrentTaskScheduler
implements InitializingBean {
    private final JndiLocatorDelegate jndiLocator = new JndiLocatorDelegate();
    @Nullable
    private String jndiName = "java:comp/DefaultManagedScheduledExecutorService";

    public void setJndiTemplate(JndiTemplate jndiTemplate) {
        this.jndiLocator.setJndiTemplate(jndiTemplate);
    }

    public void setJndiEnvironment(Properties jndiEnvironment) {
        this.jndiLocator.setJndiEnvironment(jndiEnvironment);
    }

    public void setResourceRef(boolean resourceRef) {
        this.jndiLocator.setResourceRef(resourceRef);
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    public void afterPropertiesSet() throws NamingException {
        if (this.jndiName != null) {
            ScheduledExecutorService executor = this.jndiLocator.lookup(this.jndiName, ScheduledExecutorService.class);
            this.setConcurrentExecutor(executor);
            this.setScheduledExecutor(executor);
        }
    }
}

