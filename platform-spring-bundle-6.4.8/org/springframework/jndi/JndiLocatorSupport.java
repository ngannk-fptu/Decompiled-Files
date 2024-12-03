/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jndi;

import javax.naming.NamingException;
import org.springframework.jndi.JndiAccessor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class JndiLocatorSupport
extends JndiAccessor {
    public static final String CONTAINER_PREFIX = "java:comp/env/";
    private boolean resourceRef = false;

    public void setResourceRef(boolean resourceRef) {
        this.resourceRef = resourceRef;
    }

    public boolean isResourceRef() {
        return this.resourceRef;
    }

    protected Object lookup(String jndiName) throws NamingException {
        return this.lookup(jndiName, null);
    }

    protected <T> T lookup(String jndiName, @Nullable Class<T> requiredType) throws NamingException {
        T jndiObject;
        Assert.notNull((Object)jndiName, "'jndiName' must not be null");
        String convertedName = this.convertJndiName(jndiName);
        try {
            jndiObject = this.getJndiTemplate().lookup(convertedName, requiredType);
        }
        catch (NamingException ex) {
            if (!convertedName.equals(jndiName)) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Converted JNDI name [" + convertedName + "] not found - trying original name [" + jndiName + "]. " + ex));
                }
                jndiObject = this.getJndiTemplate().lookup(jndiName, requiredType);
            }
            throw ex;
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Located object with JNDI name [" + convertedName + "]"));
        }
        return jndiObject;
    }

    protected String convertJndiName(String jndiName) {
        if (this.isResourceRef() && !jndiName.startsWith(CONTAINER_PREFIX) && jndiName.indexOf(58) == -1) {
            jndiName = CONTAINER_PREFIX + jndiName;
        }
        return jndiName;
    }
}

