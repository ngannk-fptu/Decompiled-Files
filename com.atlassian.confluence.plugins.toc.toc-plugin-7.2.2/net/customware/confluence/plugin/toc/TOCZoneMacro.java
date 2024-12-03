/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.HtmlToXmlConverter
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 */
package net.customware.confluence.plugin.toc;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.HtmlToXmlConverter;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import java.util.Map;
import net.customware.confluence.plugin.toc.AbstractTOCMacro;
import net.customware.confluence.plugin.toc.StaxDocumentOutlineCreator;

public class TOCZoneMacro
extends AbstractTOCMacro {
    private static final String LOCATION_PARAM = "location";
    private static final String TOP_LOCATION = "top";
    private static final String BOTTOM_LOCATION = "bottom";

    public TOCZoneMacro(StaxDocumentOutlineCreator staxDocumentOutlineCreator, HtmlToXmlConverter htmlToXmlConverter, SettingsManager settingsManager, LocaleManager localeManager, I18NBeanFactory i18nBeanFactory, PageBuilderService pageBuilderService) {
        super(staxDocumentOutlineCreator, htmlToXmlConverter, settingsManager, localeManager, i18nBeanFactory, pageBuilderService);
    }

    @Override
    public String getName() {
        return "toc-zone";
    }

    @Override
    protected String getContent(Map<String, String> parameters, String body, ConversionContext conversionContext) {
        return body;
    }

    @Override
    protected String createOutput(Map<String, String> parameters, String body, String toc) {
        String where = parameters.get(LOCATION_PARAM);
        boolean atTop = !BOTTOM_LOCATION.equals(where);
        boolean atBottom = !TOP_LOCATION.equals(where);
        StringBuilder out = new StringBuilder();
        if (atTop) {
            out.append(toc);
        }
        out.append(body);
        if (atBottom) {
            out.append(toc);
        }
        return out.toString();
    }

    @Override
    protected String getDefaultType() {
        return "list";
    }

    @Override
    protected String getUnprintableHtml(String body) {
        return body;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.ALL;
    }

    public boolean hasBody() {
        return true;
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.RICH_TEXT;
    }
}

