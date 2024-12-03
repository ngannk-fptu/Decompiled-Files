/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.plugin.servlet;

import com.atlassian.plugin.servlet.DownloadException;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface DownloadableResource {
    public boolean isResourceModified(HttpServletRequest var1, HttpServletResponse var2);

    public void serveResource(HttpServletRequest var1, HttpServletResponse var2) throws DownloadException;

    public void streamResource(OutputStream var1) throws DownloadException;

    public String getContentType();
}

