/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.image.ImageRenderUtils
 *  com.atlassian.confluence.languages.LocaleParser
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
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
import org.apache.commons.lang3.StringUtils;

public class ErrorPlaceholderServlet
extends AbstractPlaceholderServlet {
    private static final Color PLACEHOLDER_ERROR_BACKGROUND = new Color(255, 204, 204);
    private static final Color HEADER_ERROR_FOREGROUND = new Color(51, 51, 51);
    private final PlaceholderImageFactory placeholderImageFactory;
    private final I18NBeanFactory i18NBeanFactory;

    public ErrorPlaceholderServlet(PlaceholderImageFactory placeholderImageFactory, I18NBeanFactory i18NBeanFactory) {
        this.placeholderImageFactory = placeholderImageFactory;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Locale locale = LocaleParser.toLocale((String)req.getParameter("locale"));
        if (locale == null) {
            resp.sendError(400, "Request missing locale");
            return;
        }
        String errorI18nKey = this.getErrorI18nKey(req);
        String text = this.i18NBeanFactory.getI18NBean(locale).getText(errorI18nKey);
        try (InputStream iconStream = this.getServletContext().getResourceAsStream("/images/icons/emoticons/warning.png");){
            ImageRenderUtils.writePngToStream((BufferedImage)this.placeholderImageFactory.getPlaceholderImage(new StyledString(text, HEADER_ERROR_FOREGROUND), iconStream, PLACEHOLDER_ERROR_BACKGROUND), (HttpServletResponse)resp);
        }
    }

    String getErrorI18nKey(HttpServletRequest req) {
        return (String)StringUtils.defaultIfBlank((CharSequence)req.getParameter("i18nKey"), (CharSequence)"editor.placeholder.error");
    }
}

