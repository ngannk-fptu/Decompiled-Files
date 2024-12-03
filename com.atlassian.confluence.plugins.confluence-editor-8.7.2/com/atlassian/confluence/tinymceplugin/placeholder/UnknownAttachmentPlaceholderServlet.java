/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.image.ImageRenderUtils
 *  com.atlassian.confluence.languages.LocaleParser
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.tinymceplugin.placeholder;

import com.atlassian.confluence.content.render.image.ImageRenderUtils;
import com.atlassian.confluence.languages.LocaleParser;
import com.atlassian.confluence.tinymceplugin.placeholder.AbstractPlaceholderServlet;
import com.atlassian.confluence.tinymceplugin.placeholder.PlaceholderImageFactory;
import com.atlassian.confluence.tinymceplugin.placeholder.StyledString;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnknownAttachmentPlaceholderServlet
extends AbstractPlaceholderServlet {
    private static final Logger log = LoggerFactory.getLogger(UnknownAttachmentPlaceholderServlet.class);
    private static final Color PLACEHOLDER_BACKGROUND = new Color(240, 240, 240);
    private static final Color HEADER_FOREGROUND = Color.BLACK;
    private final PlaceholderImageFactory placeholderImageFactory;
    private final I18NBeanFactory i18NBeanFactory;

    public UnknownAttachmentPlaceholderServlet(PlaceholderImageFactory placeholderImageFactory, I18NBeanFactory i18NBeanFactory) {
        this.placeholderImageFactory = placeholderImageFactory;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Locale locale = LocaleParser.toLocale((String)req.getParameter("locale"));
        I18NBean i18nBean = null;
        if (locale == null) {
            log.warn("The unknown attachment placeholder request was missing a locale parameter.");
            i18nBean = this.i18NBeanFactory.getI18NBean();
        } else {
            i18nBean = this.i18NBeanFactory.getI18NBean(locale);
        }
        String text = i18nBean.getText("xhtml.unknown.attachment.error");
        try (InputStream iconStream = this.getServletContext().getResourceAsStream("/images/icons/question-mark-verdana.png");){
            ImageRenderUtils.writePngToStream((BufferedImage)this.placeholderImageFactory.getPlaceholderImage(new StyledString(text, HEADER_FOREGROUND), iconStream, PLACEHOLDER_BACKGROUND), (HttpServletResponse)resp);
        }
    }
}

