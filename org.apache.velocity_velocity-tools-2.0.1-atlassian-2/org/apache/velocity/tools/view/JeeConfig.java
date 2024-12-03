/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package org.apache.velocity.tools.view;

import java.util.Enumeration;
import javax.servlet.ServletContext;

public interface JeeConfig {
    public String getInitParameter(String var1);

    public String findInitParameter(String var1);

    public Enumeration getInitParameterNames();

    public String getName();

    public ServletContext getServletContext();
}

