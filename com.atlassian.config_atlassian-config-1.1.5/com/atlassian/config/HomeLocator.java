/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package com.atlassian.config;

import javax.servlet.ServletContext;

public interface HomeLocator {
    public String getHomePath();

    public String getConfigFileName();

    public void lookupServletHomeProperty(ServletContext var1);
}

