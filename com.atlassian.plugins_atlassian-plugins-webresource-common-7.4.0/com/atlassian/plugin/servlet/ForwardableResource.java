/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceLocation
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.servlet;

import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.servlet.DownloadException;
import com.atlassian.plugin.servlet.DownloadableResource;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

public class ForwardableResource
implements DownloadableResource {
    private ResourceLocation resourceLocation;

    public ForwardableResource(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    @Override
    public boolean isResourceModified(HttpServletRequest request, HttpServletResponse response) {
        return true;
    }

    @Override
    public void serveResource(HttpServletRequest request, HttpServletResponse response) throws DownloadException {
        try {
            String type = this.getContentType();
            if (StringUtils.isNotBlank((CharSequence)type)) {
                response.setContentType(type);
            }
            request.getRequestDispatcher(this.getLocation()).forward((ServletRequest)request, (ServletResponse)response);
        }
        catch (IOException | ServletException e) {
            throw new DownloadException(e.getMessage());
        }
    }

    @Override
    public void streamResource(OutputStream out) {
    }

    @Override
    public String getContentType() {
        return this.resourceLocation.getContentType();
    }

    protected String getLocation() {
        return this.resourceLocation.getLocation();
    }

    public String toString() {
        return "Forwardable Resource: " + this.resourceLocation.getLocation();
    }
}

