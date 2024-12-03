/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package org.apache.velocity.tools.view;

import java.util.Enumeration;
import javax.servlet.ServletContext;
import org.apache.velocity.tools.view.JeeConfig;

public class JeeContextConfig
implements JeeConfig {
    protected ServletContext context;

    public JeeContextConfig(ServletContext context) {
        if (context == null) {
            throw new NullPointerException("ServletContext should not be null; there must be a way to get a ServletContext");
        }
        this.context = context;
    }

    @Override
    public String getInitParameter(String name) {
        return null;
    }

    @Override
    public String findInitParameter(String key) {
        return this.context.getInitParameter(key);
    }

    @Override
    public Enumeration getInitParameterNames() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public ServletContext getServletContext() {
        return this.context;
    }
}

