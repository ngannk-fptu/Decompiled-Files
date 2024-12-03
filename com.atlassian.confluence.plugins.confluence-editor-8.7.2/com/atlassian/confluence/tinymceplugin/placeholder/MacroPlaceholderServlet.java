/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.image.ImageRenderUtils
 *  com.atlassian.confluence.languages.LocaleParser
 *  com.atlassian.confluence.macro.MacroDefinitionDeserializer
 *  com.atlassian.confluence.macro.browser.MacroIconManager
 *  com.atlassian.confluence.macro.browser.MacroMetadataManager
 *  com.atlassian.confluence.macro.browser.beans.MacroMetadata
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.tinymceplugin.placeholder;

import com.atlassian.confluence.content.render.image.ImageRenderUtils;
import com.atlassian.confluence.languages.LocaleParser;
import com.atlassian.confluence.macro.MacroDefinitionDeserializer;
import com.atlassian.confluence.macro.browser.MacroIconManager;
import com.atlassian.confluence.macro.browser.MacroMetadataManager;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.tinymceplugin.placeholder.AbstractPlaceholderServlet;
import com.atlassian.confluence.tinymceplugin.placeholder.PlaceholderImageFactory;
import com.atlassian.confluence.tinymceplugin.placeholder.PlaceholderStringUtils;
import com.atlassian.confluence.tinymceplugin.placeholder.StyledString;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;

public class MacroPlaceholderServlet
extends AbstractPlaceholderServlet {
    private static final String PARAM_TEXT_LENGTH_INIT_PARAM = "maxParameterTextLength";
    private static final String PARAM_PLACEHOLDER_TYPE = "placeholderType";
    private static final Color PLACEHOLDER_BACKGROUND = new Color(240, 240, 240);
    private static final Color MACRO_PARAMETER_TEXT_COLOR = Color.decode("0x666666");
    private int maxParameterTextLength = 30;
    private boolean heading = false;
    private final MacroMetadataManager macroMetadataManager;
    private final MacroIconManager macroIconManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final PlaceholderImageFactory placeholderImageFactory;
    private final MacroDefinitionDeserializer macroDefinitionDeserializer;

    public MacroPlaceholderServlet(MacroMetadataManager macroMetadataManager, MacroIconManager macroIconManager, PlaceholderImageFactory placeholderImageFactory, I18NBeanFactory i18NBeanFactory, @Qualifier(value="macroDefinitionRequestDeserializer") MacroDefinitionDeserializer macroDefinitionDeserializer) {
        this.macroMetadataManager = macroMetadataManager;
        this.macroIconManager = macroIconManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.placeholderImageFactory = placeholderImageFactory;
        this.macroDefinitionDeserializer = macroDefinitionDeserializer;
    }

    public void init() throws ServletException {
        String type;
        super.init();
        String length = this.getInitParameter(PARAM_TEXT_LENGTH_INIT_PARAM);
        if (StringUtils.isNotBlank((CharSequence)length)) {
            this.maxParameterTextLength = Integer.parseInt(length);
        }
        if ("heading".equalsIgnoreCase(type = this.getInitParameter(PARAM_PLACEHOLDER_TYPE))) {
            this.heading = true;
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String definition = req.getParameter("definition");
        if (definition == null) {
            resp.sendError(400, "Request missing macro definition");
            return;
        }
        Locale locale = LocaleParser.toLocale((String)req.getParameter("locale"));
        if (locale == null) {
            resp.sendError(400, "Request missing locale");
            return;
        }
        MacroDefinition macroDefinition = this.macroDefinitionDeserializer.deserialize(definition);
        MacroMetadata macroMetadata = this.macroMetadataManager.getMacroMetadataByName(macroDefinition.getName());
        String macroTitle = macroDefinition.getName();
        macroTitle = macroMetadata != null ? this.getMacroTitle(macroMetadata, locale) : this.i18NBeanFactory.getI18NBean(locale).getText("xhtml.unknown.macro", (Object[])new String[]{macroTitle});
        StyledString macroTitleText = new StyledString(macroTitle);
        String parameters = PlaceholderStringUtils.createParametersString(macroDefinition, macroMetadata);
        StyledString parametersText = new StyledString(PlaceholderStringUtils.truncate(parameters, this.maxParameterTextLength), MACRO_PARAMETER_TEXT_COLOR);
        try (InputStream iconStream = this.macroIconManager.getIconStream(macroMetadata);){
            ImageRenderUtils.writePngToStream((BufferedImage)this.getPlaceholderImage(macroTitleText, parametersText, iconStream), (HttpServletResponse)resp);
        }
    }

    private String getMacroTitle(MacroMetadata metadata, Locale locale) {
        String title = this.i18NBeanFactory.getI18NBean(locale).getText(metadata.getTitle());
        if (title.equals(metadata.getPluginKey() + "." + metadata.getMacroName() + ".label")) {
            title = StringUtils.capitalize((String)metadata.getMacroName()).replace('-', ' ');
        }
        return title;
    }

    private BufferedImage getPlaceholderImage(StyledString title, StyledString parameters, InputStream icon) {
        if (this.heading) {
            return this.placeholderImageFactory.getPlaceholderHeading(Arrays.asList(title, parameters), icon, PLACEHOLDER_BACKGROUND);
        }
        return this.placeholderImageFactory.getPlaceholderImage(Arrays.asList(title, parameters), icon, PLACEHOLDER_BACKGROUND);
    }
}

