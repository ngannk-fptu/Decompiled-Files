/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.image.ImageRenderUtils
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.plugins.templates.servlet;

import com.atlassian.confluence.content.render.image.ImageRenderUtils;
import com.atlassian.confluence.plugins.templates.servlet.PlaceholderImageFactory;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TemplateVariablePlaceholderServlet
extends HttpServlet {
    private final PlaceholderImageFactory placeholderImageFactory;

    public TemplateVariablePlaceholderServlet(PlaceholderImageFactory placeholderImageFactory) {
        this.placeholderImageFactory = placeholderImageFactory;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String variableName = req.getParameter("name");
        if (variableName == null) {
            resp.sendError(400, "Request missing variable name in path");
            return;
        }
        ImageRenderUtils.writePngToStream((BufferedImage)this.placeholderImageFactory.getPlaceholderImage("$" + variableName), (HttpServletResponse)resp);
    }

    @Deprecated
    final void writePngToStream(BufferedImage image, HttpServletResponse response) throws IOException {
        ImageRenderUtils.writePngToStream((BufferedImage)image, (HttpServletResponse)response);
    }
}

