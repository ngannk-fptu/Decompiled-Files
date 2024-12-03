/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.image.ImageRenderUtils
 *  com.atlassian.confluence.macro.browser.MacroIconManager
 *  com.atlassian.confluence.macro.browser.MacroMetadataManager
 *  com.atlassian.confluence.macro.browser.beans.MacroMetadata
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.tinymceplugin.placeholder;

import com.atlassian.confluence.content.render.image.ImageRenderUtils;
import com.atlassian.confluence.macro.browser.MacroIconManager;
import com.atlassian.confluence.macro.browser.MacroMetadataManager;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.tinymceplugin.placeholder.AbstractPlaceholderServlet;
import com.atlassian.confluence.tinymceplugin.placeholder.PlaceholderImageFactory;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MacroIconPlaceholderServlet
extends AbstractPlaceholderServlet {
    private final MacroMetadataManager macroMetadataManager;
    private final PlaceholderImageFactory placeholderImageFactory;
    private final MacroIconManager macroIconManager;

    public MacroIconPlaceholderServlet(MacroMetadataManager macroMetadataManager, MacroIconManager macroIconManager, PlaceholderImageFactory placeholderImageFactory) {
        this.macroMetadataManager = macroMetadataManager;
        this.macroIconManager = macroIconManager;
        this.placeholderImageFactory = placeholderImageFactory;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String macroName = req.getParameter("name");
        if (macroName == null) {
            resp.sendError(400, "Request missing macro name");
            return;
        }
        MacroMetadata macroMetadata = this.macroMetadataManager.getMacroMetadataByName(macroName);
        try (InputStream iconStream = this.macroIconManager.getIconStream(macroMetadata);){
            ImageRenderUtils.writePngToStream((BufferedImage)this.placeholderImageFactory.getPlaceholderImage(iconStream, 20), (HttpServletResponse)resp);
        }
    }
}

