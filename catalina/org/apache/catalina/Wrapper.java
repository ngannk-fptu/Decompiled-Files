/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.MultipartConfigElement
 *  javax.servlet.Servlet
 *  javax.servlet.ServletException
 *  javax.servlet.UnavailableException
 */
package org.apache.catalina;

import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import org.apache.catalina.Container;

public interface Wrapper
extends Container {
    public static final String ADD_MAPPING_EVENT = "addMapping";
    public static final String REMOVE_MAPPING_EVENT = "removeMapping";

    public long getAvailable();

    public void setAvailable(long var1);

    public int getLoadOnStartup();

    public void setLoadOnStartup(int var1);

    public String getRunAs();

    public void setRunAs(String var1);

    public String getServletClass();

    public void setServletClass(String var1);

    public String[] getServletMethods() throws ServletException;

    public boolean isUnavailable();

    public Servlet getServlet();

    public void setServlet(Servlet var1);

    public void addInitParameter(String var1, String var2);

    public void addMapping(String var1);

    public void addSecurityReference(String var1, String var2);

    public Servlet allocate() throws ServletException;

    public void deallocate(Servlet var1) throws ServletException;

    public String findInitParameter(String var1);

    public String[] findInitParameters();

    public String[] findMappings();

    public String findSecurityReference(String var1);

    public String[] findSecurityReferences();

    public void incrementErrorCount();

    public void load() throws ServletException;

    public void removeInitParameter(String var1);

    public void removeMapping(String var1);

    public void removeSecurityReference(String var1);

    public void unavailable(UnavailableException var1);

    public void unload() throws ServletException;

    public MultipartConfigElement getMultipartConfigElement();

    public void setMultipartConfigElement(MultipartConfigElement var1);

    public boolean isAsyncSupported();

    public void setAsyncSupported(boolean var1);

    public boolean isEnabled();

    public void setEnabled(boolean var1);

    public boolean isOverridable();

    public void setOverridable(boolean var1);
}

