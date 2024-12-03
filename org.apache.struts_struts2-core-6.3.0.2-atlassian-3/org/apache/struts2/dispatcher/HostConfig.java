/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package org.apache.struts2.dispatcher;

import java.util.Iterator;
import javax.servlet.ServletContext;

public interface HostConfig {
    public String getInitParameter(String var1);

    public Iterator<String> getInitParameterNames();

    public ServletContext getServletContext();
}

