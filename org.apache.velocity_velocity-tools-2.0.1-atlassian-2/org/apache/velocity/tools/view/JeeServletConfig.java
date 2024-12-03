/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 */
package org.apache.velocity.tools.view;

import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.apache.velocity.tools.view.JeeConfig;

public class JeeServletConfig
implements JeeConfig {
    protected ServletConfig servlet;

    public JeeServletConfig(ServletConfig servlet) {
        if (servlet == null) {
            throw new NullPointerException("ServletConfig should not be null; there must be a way to get a ServletContext");
        }
        this.servlet = servlet;
    }

    @Override
    public String getInitParameter(String name) {
        return this.servlet.getInitParameter(name);
    }

    @Override
    public String findInitParameter(String key) {
        String param = this.getInitParameter(key);
        if (param == null) {
            param = this.getServletContext().getInitParameter(key);
        }
        return param;
    }

    @Override
    public Enumeration getInitParameterNames() {
        return this.servlet.getInitParameterNames();
    }

    @Override
    public String getName() {
        return this.servlet.getServletName();
    }

    @Override
    public ServletContext getServletContext() {
        return this.servlet.getServletContext();
    }
}

