/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.HtmlToXmlConverter
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.TokenType
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.user.User
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  com.google.common.base.Throwables
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.customware.confluence.plugin.toc;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.HtmlToXmlConverter;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.user.User;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.google.common.base.Throwables;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.customware.confluence.plugin.toc.DocumentOutline;
import net.customware.confluence.plugin.toc.FlatHandler;
import net.customware.confluence.plugin.toc.ListHandler;
import net.customware.confluence.plugin.toc.OutlineRenderer;
import net.customware.confluence.plugin.toc.OutputHandler;
import net.customware.confluence.plugin.toc.SeparatorType;
import net.customware.confluence.plugin.toc.StaxDocumentOutlineCreator;
import net.customware.confluence.plugin.toc.TOCMacro;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTOCMacro
extends BaseMacro
implements Macro {
    private static final Logger log = LoggerFactory.getLogger(TOCMacro.class);
    private static final String CLASS_PARAM = "class";
    public static final int DEFAULT_MAX_LEVEL = 7;
    public static final int DEFAULT_MIN_LEVEL = 1;
    public static final String DEFAULT_STYLE = "default";
    private static final String FILTER_PARAM = "filter";
    public static final String FLAT_TYPE = "flat";
    public static final String LIST_TYPE = "list";
    private static final String MAX_LEVEL_PARAM = "maxLevel";
    private static final String MIN_LEVEL_PARAM = "minLevel";
    private static final String MAX_LEVEL_LOWCASE_PARAM = "maxlevel";
    private static final String MIN_LEVEL_LOWCASE_PARAM = "minlevel";
    private static final String OUTLINE_PARAM = "outline";
    private static final String PRINTABLE_PARAM = "printable";
    private static final String TYPE_PARAM = "type";
    private static final String ABSOLUTE_URL_PARAM = "absoluteUrl";
    private static final String SEPARATOR_PARAM = "separator";
    private static final String INDENT_PARAM = "indent";
    private static final String STYLE_PARAM = "style";
    static final String INCLUDE_PARAM = "include";
    static final String EXCLUDE_PARAM = "exclude";
    private static final ThreadLocal<Boolean> renderingThreadLocal = new ThreadLocal();
    protected static final String PLUGIN_KEY = "org.randombits.confluence.toc";
    protected static final String SERVER_IMPL_WEBRESOURCE_CONTEXT = "toc-macro-server-impl";
    protected static final String CLIENT_IMPL_WEBRESOURCE_CONTEXT = "toc-macro-client-impl";
    private final HtmlToXmlConverter htmlToXmlConverter;
    private final SettingsManager settingsManager;
    private final LocaleManager localeManager;
    private final I18NBeanFactory i18nBeanFactory;
    private final StaxDocumentOutlineCreator staxDocumentOutlineCreator;
    private final PageBuilderService pageBuilderService;

    public AbstractTOCMacro(StaxDocumentOutlineCreator staxDocumentOutlineCreator, HtmlToXmlConverter htmlToXmlConverter, SettingsManager settingsManager, LocaleManager localeManager, I18NBeanFactory i18nBeanFactory, PageBuilderService pageBuilderService) {
        this.staxDocumentOutlineCreator = staxDocumentOutlineCreator;
        this.htmlToXmlConverter = htmlToXmlConverter;
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;
        this.i18nBeanFactory = i18nBeanFactory;
        this.pageBuilderService = pageBuilderService;
    }

    public String getName() {
        return "toc";
    }

    public TokenType getTokenType(Map map, String body, RenderContext renderContext) {
        return TokenType.BLOCK;
    }

    protected String getDefaultSeparatorName() {
        return SeparatorType.BRACKET.toString();
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    protected abstract String createOutput(Map<String, String> var1, String var2, String var3);

    private String createTOC(Map<String, String> parameters, ConversionContext conversionContext, DocumentOutline outline) throws MacroExecutionException {
        OutlineRenderer outlineRenderer = this.getOutlineRenderer(parameters, conversionContext);
        OutputHandler outputHandler = this.getOutputHandler(parameters);
        try {
            return outlineRenderer.render(outline, outputHandler);
        }
        catch (IOException e) {
            throw new MacroExecutionException((Throwable)e);
        }
    }

    private OutlineRenderer getOutlineRenderer(Map<String, String> parameters, ConversionContext conversionContext) throws MacroExecutionException {
        String prefix = "";
        int minLevel = 1;
        int maxLevel = 7;
        try {
            minLevel = AbstractTOCMacro.getMinLevel(parameters);
            maxLevel = AbstractTOCMacro.getMaxLevel(parameters);
        }
        catch (Exception e) {
            log.warn(String.format("Invalid min/max level specified : %s , %s", StringUtils.isBlank((CharSequence)parameters.get(MIN_LEVEL_PARAM)) ? parameters.get(MIN_LEVEL_LOWCASE_PARAM) : Integer.valueOf(Integer.parseInt(parameters.get(MIN_LEVEL_PARAM))), StringUtils.isBlank((CharSequence)parameters.get(MAX_LEVEL_PARAM)) ? parameters.get(MAX_LEVEL_PARAM) : parameters.get(MAX_LEVEL_LOWCASE_PARAM)), (Throwable)e);
        }
        if (minLevel > maxLevel) {
            throw new MacroExecutionException(this.getI18nBean().getText("macro.error.minlevelgtmaxlevel", new Object[]{minLevel, maxLevel}));
        }
        String className = parameters.get(CLASS_PARAM);
        String include = (String)StringUtils.defaultIfEmpty((CharSequence)parameters.get(INCLUDE_PARAM), (CharSequence)parameters.get(FILTER_PARAM));
        String exclude = parameters.get(EXCLUDE_PARAM);
        if (Boolean.parseBoolean(parameters.get(ABSOLUTE_URL_PARAM))) {
            prefix = this.settingsManager.getGlobalSettings().getBaseUrl() + conversionContext.getEntity().getUrlPath();
        }
        boolean outlineNumbering = BooleanUtils.toBoolean((String)parameters.get(OUTLINE_PARAM));
        return this.getOutlineRenderer(className, minLevel, maxLevel, prefix, include, exclude, outlineNumbering);
    }

    private OutputHandler getOutputHandler(Map<String, String> parameters) throws MacroExecutionException {
        String type = (String)StringUtils.defaultIfEmpty((CharSequence)parameters.get(TYPE_PARAM), (CharSequence)this.getDefaultType());
        String separatorParam = (String)StringUtils.defaultIfEmpty((CharSequence)parameters.get(SEPARATOR_PARAM), (CharSequence)this.getDefaultSeparatorName());
        String indent = parameters.get(INDENT_PARAM);
        String style = (String)StringUtils.defaultIfEmpty((CharSequence)parameters.get(STYLE_PARAM), (CharSequence)DEFAULT_STYLE);
        if (FLAT_TYPE.equals(type)) {
            return new FlatHandler(separatorParam);
        }
        if (LIST_TYPE.equals(type)) {
            return this.createListHandler(indent, style);
        }
        throw new MacroExecutionException(this.getI18nBean().getText("macro.error.unsupportedtype", new Object[]{type}));
    }

    protected ListHandler createListHandler(String indent, String style) {
        return new ListHandler(style, indent);
    }

    private static int getMaxLevel(Map<String, String> parameters) {
        return Integer.parseInt((String)StringUtils.defaultIfEmpty((CharSequence)((String)StringUtils.defaultIfEmpty((CharSequence)parameters.get(MAX_LEVEL_PARAM), (CharSequence)parameters.get(MAX_LEVEL_LOWCASE_PARAM))), (CharSequence)String.valueOf(7)));
    }

    private static int getMinLevel(Map<String, String> parameters) {
        return Integer.parseInt((String)StringUtils.defaultIfEmpty((CharSequence)((String)StringUtils.defaultIfEmpty((CharSequence)parameters.get(MIN_LEVEL_PARAM), (CharSequence)parameters.get(MIN_LEVEL_LOWCASE_PARAM))), (CharSequence)String.valueOf(1)));
    }

    private DocumentOutline generateDocumentOutline(String xhtmlPageContent) {
        try {
            return this.staxDocumentOutlineCreator.getOutline(xhtmlPageContent);
        }
        catch (StaxDocumentOutlineCreator.StaxOutlineBuilderException ex) {
            throw Throwables.propagate((Throwable)new MacroExecutionException(this.getI18nBean().getText("macro.error.pageparsefail")));
        }
        catch (Exception e) {
            throw Throwables.propagate((Throwable)new MacroExecutionException((Throwable)e));
        }
    }

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        try {
            return this.execute((Map<String, String>)parameters, body, (ConversionContext)new DefaultConversionContext(renderContext));
        }
        catch (MacroExecutionException e) {
            throw new MacroException((Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext) throws MacroExecutionException {
        this.pageBuilderService.assembler().resources().requireContext(SERVER_IMPL_WEBRESOURCE_CONTEXT);
        boolean printable = BooleanUtils.toBoolean((String)StringUtils.defaultString((String)parameters.get(PRINTABLE_PARAM), (String)"true"));
        if (!printable && AbstractTOCMacro.isPdfOrWordExport(conversionContext)) {
            return this.getUnprintableHtml(body);
        }
        if (this.isBeingRendered()) {
            return "";
        }
        try {
            this.setBeingRendered(true);
            String toc = this.getTOC(parameters, body, conversionContext);
            String string = this.createOutput(parameters, body, toc);
            return string;
        }
        finally {
            this.setBeingRendered(false);
        }
    }

    private static boolean isPdfOrWordExport(ConversionContext conversionContext) {
        String outputType = conversionContext.getOutputType();
        return "pdf".equals(outputType) || "word".equals(outputType);
    }

    protected boolean isBeingRendered() {
        return Boolean.TRUE.equals(renderingThreadLocal.get());
    }

    protected void setBeingRendered(boolean beingRendered) {
        renderingThreadLocal.set(beingRendered);
    }

    protected OutlineRenderer getOutlineRenderer(String className, int minLevel, int maxLevel, String prefix, String include, String exclude, boolean outlineNumbering) {
        return new OutlineRenderer(className, minLevel, maxLevel, prefix, include, exclude, outlineNumbering);
    }

    private String getTOC(Map<String, String> parameters, String body, ConversionContext conversionContext) throws MacroExecutionException {
        try {
            DocumentOutline documentOutline = this.getDocumentOutline(parameters, body, conversionContext);
            if (documentOutline == null) {
                return "";
            }
            return this.createTOC(parameters, conversionContext, documentOutline);
        }
        catch (RuntimeException ex) {
            Throwables.propagateIfInstanceOf((Throwable)ex.getCause(), MacroExecutionException.class);
            throw ex;
        }
    }

    @Nullable
    private DocumentOutline getDocumentOutline(Map<String, String> parameters, String body, ConversionContext conversionContext) throws MacroExecutionException {
        boolean includeOriginalContext = !Boolean.parseBoolean(parameters.get(ABSOLUTE_URL_PARAM));
        String pageContent = this.getContent(parameters, body, AbstractTOCMacro.copy(conversionContext, includeOriginalContext));
        if (StringUtils.isBlank((CharSequence)pageContent)) {
            return null;
        }
        String xmlPageContent = this.htmlToXmlConverter.convert(pageContent);
        return this.generateDocumentOutline(xmlPageContent);
    }

    private static ConversionContext copy(ConversionContext conversionContext, boolean includeOriginalContext) {
        PageContext originalContext;
        PageContext pageContext = conversionContext.getPageContext();
        PageContext previousContext = pageContext != (originalContext = pageContext.getOriginalContext()) && includeOriginalContext ? originalContext.getEntity().toPageContext() : null;
        return new DefaultConversionContext((RenderContext)new PageContext(conversionContext.getEntity(), previousContext));
    }

    private I18NBean getI18nBean() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        Locale locale = this.localeManager.getLocale((User)user);
        return this.i18nBeanFactory.getI18NBean(locale);
    }

    protected abstract String getContent(Map<String, String> var1, String var2, ConversionContext var3);

    protected abstract String getDefaultType();

    protected abstract String getUnprintableHtml(String var1);
}

