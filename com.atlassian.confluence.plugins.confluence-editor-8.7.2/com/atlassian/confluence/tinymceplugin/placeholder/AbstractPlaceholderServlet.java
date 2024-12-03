/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.image.ImageRenderUtils
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.tinymceplugin.placeholder;

import com.atlassian.confluence.content.render.image.ImageRenderUtils;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

abstract class AbstractPlaceholderServlet
extends HttpServlet {
    AbstractPlaceholderServlet() {
    }

    @Deprecated
    final void writePngToStream(BufferedImage image, HttpServletResponse response) throws IOException {
        ImageRenderUtils.writePngToStream((BufferedImage)image, (HttpServletResponse)response);
    }
}

