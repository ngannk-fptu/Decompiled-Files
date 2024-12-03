/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.gzipfilter.integration;

import javax.servlet.http.HttpServletRequest;

public interface GzipFilterIntegration {
    public boolean useGzip();

    public String getResponseEncoding(HttpServletRequest var1);
}

