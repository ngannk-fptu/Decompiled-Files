/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.plugin.servlet;

import com.atlassian.plugin.servlet.DownloadException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface DownloadStrategy {
    public boolean matches(String var1);

    public void serveFile(HttpServletRequest var1, HttpServletResponse var2) throws DownloadException;
}

