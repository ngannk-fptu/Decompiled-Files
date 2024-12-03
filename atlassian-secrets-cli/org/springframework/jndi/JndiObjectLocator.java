/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jndi;

import javax.naming.NamingException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jndi.JndiLocatorSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public abstract class JndiObjectLocator
extends JndiLocatorSupport
implements InitializingBean {
    @Nullable
    private String jndiName;
    @Nullable
    private Class<?> expectedType;

    public void setJndiName(@Nullable String jndiName) {
        this.jndiName = jndiName;
    }

    @Nullable
    public String getJndiName() {
        return this.jndiName;
    }

    public void setExpectedType(@Nullable Class<?> expectedType) {
        this.expectedType = expectedType;
    }

    @Nullable
    public Class<?> getExpectedType() {
        return this.expectedType;
    }

    @Override
    public void afterPropertiesSet() throws IllegalArgumentException, NamingException {
        if (!StringUtils.hasLength(this.getJndiName())) {
            throw new IllegalArgumentException("Property 'jndiName' is required");
        }
    }

    protected Object lookup() throws NamingException {
        String jndiName = this.getJndiName();
        Assert.state(jndiName != null, "No JNDI name specified");
        return this.lookup(jndiName, this.getExpectedType());
    }
}

