/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package com.atlassian.plugins.authentication.impl.johnson;

import javax.servlet.ServletContext;

public interface JohnsonChecker {
    public boolean isInstanceJohnsoned(ServletContext var1);
}

