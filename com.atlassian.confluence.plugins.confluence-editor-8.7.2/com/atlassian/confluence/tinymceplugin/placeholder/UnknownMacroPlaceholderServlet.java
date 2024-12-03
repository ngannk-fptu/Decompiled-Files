/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.image.ImageRenderUtils
 *  com.atlassian.confluence.languages.LocaleParser
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.tinymceplugin.placeholder;

import com.atlassian.confluence.content.render.image.ImageRenderUtils;
import com.atlassian.confluence.languages.LocaleParser;
import com.atlassian.confluence.tinymceplugin.placeholder.AbstractPlaceholderServlet;
import com.atlassian.confluence.tinymceplugin.placeholder.PlaceholderImageFactory;
import com.atlassian.confluence.tinymceplugin.placeholder.StyledString;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UnknownMacroPlaceholderServlet
extends AbstractPlaceholderServlet {
    private static final Color PLACEHOLDER_ERROR_BACKGROUND = new Color(255, 253, 246);
    private static final Color PLACEHOLDER_ERROR_BORDER = new Color(255, 234, 174);
    private static final Color HEADER_ERROR_FOREGROUND = new Color(51, 51, 51);
    private final PlaceholderImageFactory placeholderImageFactory;
    private final I18NBeanFactory i18NBeanFactory;

    public UnknownMacroPlaceholderServlet(PlaceholderImageFactory placeholderImageFactory, I18NBeanFactory i18NBeanFactory) {
        this.placeholderImageFactory = placeholderImageFactory;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String macroName = req.getParameter("name");
        if (macroName == null) {
            resp.sendError(400, "Request missing macro name");
            return;
        }
        Locale locale = LocaleParser.toLocale((String)req.getParameter("locale"));
        if (locale == null) {
            resp.sendError(400, "Request missing locale");
            return;
        }
        String text = this.i18NBeanFactory.getI18NBean(locale).getText("xhtml.unknown.macro", (Object[])new String[]{macroName});
        try (InputStream iconStream = this.getServletContext().getResourceAsStream("/images/icons/macrobrowser/macro-placeholder-default.png");){
            BufferedImage placeholderImage = this.placeholderImageFactory.getPlaceholderImage(new StyledString(text, HEADER_ERROR_FOREGROUND), iconStream, PLACEHOLDER_ERROR_BACKGROUND, PLACEHOLDER_ERROR_BORDER, true);
            ImageRenderUtils.writePngToStream((BufferedImage)placeholderImage, (HttpServletResponse)resp);
        }
    }
}

