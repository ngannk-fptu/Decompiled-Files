/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jndi;

import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jndi.JndiTemplate;
import org.springframework.lang.Nullable;

public class JndiAccessor {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private JndiTemplate jndiTemplate = new JndiTemplate();

    public void setJndiTemplate(@Nullable JndiTemplate jndiTemplate) {
        this.jndiTemplate = jndiTemplate != null ? jndiTemplate : new JndiTemplate();
    }

    public JndiTemplate getJndiTemplate() {
        return this.jndiTemplate;
    }

    public void setJndiEnvironment(@Nullable Properties jndiEnvironment) {
        this.jndiTemplate = new JndiTemplate(jndiEnvironment);
    }

    @Nullable
    public Properties getJndiEnvironment() {
        return this.jndiTemplate.getEnvironment();
    }
}

