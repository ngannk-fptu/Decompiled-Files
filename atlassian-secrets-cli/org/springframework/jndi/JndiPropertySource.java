/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jndi;

import javax.naming.NamingException;
import org.springframework.core.env.PropertySource;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.lang.Nullable;

public class JndiPropertySource
extends PropertySource<JndiLocatorDelegate> {
    public JndiPropertySource(String name) {
        this(name, JndiLocatorDelegate.createDefaultResourceRefLocator());
    }

    public JndiPropertySource(String name, JndiLocatorDelegate jndiLocator) {
        super(name, jndiLocator);
    }

    @Override
    @Nullable
    public Object getProperty(String name) {
        if (((JndiLocatorDelegate)this.getSource()).isResourceRef() && name.indexOf(58) != -1) {
            return null;
        }
        try {
            Object value = ((JndiLocatorDelegate)this.source).lookup(name);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("JNDI lookup for name [" + name + "] returned: [" + value + "]");
            }
            return value;
        }
        catch (NamingException ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("JNDI lookup for name [" + name + "] threw NamingException with message: " + ex.getMessage() + ". Returning null.");
            }
            return null;
        }
    }
}

