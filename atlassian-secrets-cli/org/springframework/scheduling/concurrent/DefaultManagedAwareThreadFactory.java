/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling.concurrent;

import java.util.Properties;
import java.util.concurrent.ThreadFactory;
import javax.naming.NamingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.jndi.JndiTemplate;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

public class DefaultManagedAwareThreadFactory
extends CustomizableThreadFactory
implements InitializingBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private JndiLocatorDelegate jndiLocator = new JndiLocatorDelegate();
    @Nullable
    private String jndiName = "java:comp/DefaultManagedThreadFactory";
    @Nullable
    private ThreadFactory threadFactory;

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

    @Override
    public void afterPropertiesSet() throws NamingException {
        if (this.jndiName != null) {
            try {
                this.threadFactory = this.jndiLocator.lookup(this.jndiName, ThreadFactory.class);
            }
            catch (NamingException ex) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Failed to retrieve [" + this.jndiName + "] from JNDI", ex);
                }
                this.logger.info("Could not find default managed thread factory in JNDI - proceeding with default local thread factory");
            }
        }
    }

    @Override
    public Thread newThread(Runnable runnable) {
        if (this.threadFactory != null) {
            return this.threadFactory.newThread(runnable);
        }
        return super.newThread(runnable);
    }
}

