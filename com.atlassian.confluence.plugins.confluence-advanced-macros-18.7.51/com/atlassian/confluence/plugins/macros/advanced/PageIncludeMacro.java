/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultXmlEventReaderFactory
 *  com.atlassian.confluence.content.render.xhtml.Renderer
 *  com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactoryFactoryBean
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.service.NotAuthorizedException
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.renderer.ContentIncludeStack
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.xhtml.api.Link
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.macros.advanced;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultXmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactoryFactoryBean;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugins.macros.advanced.BlogPostsMacro;
import com.atlassian.confluence.plugins.macros.advanced.IncludeMacroUtils;
import com.atlassian.confluence.plugins.macros.advanced.PageProvider;
import com.atlassian.confluence.plugins.macros.advanced.xhtml.deprecated.HTMLParagraphStripper;
import com.atlassian.confluence.renderer.ContentIncludeStack;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import java.util.Map;
import java.util.Optional;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageIncludeMacro
extends BaseMacro
implements Macro {
    private static final Logger log = LoggerFactory.getLogger(BlogPostsMacro.class);
    private PageProvider pageProvider;
    private Renderer viewRenderer;
    private I18NBeanFactory i18NBeanFactory;
    private final String RENDER_ERROR_PREFIX = "confluence.macros.advanced.include.unable-to-render";
    private final String NOT_FOUND_ERROR = "confluence.macros.advanced.include.error.content.not.found";
    private final HTMLParagraphStripper htmlParagraphStripper;

    public PageIncludeMacro() {
        XMLOutputFactory xmlOutputFactory;
        try {
            xmlOutputFactory = new XmlOutputFactoryFactoryBean(true).getObject();
        }
        catch (Exception e) {
            throw new RuntimeException("Error occurred trying to construct a XML output factory", e);
        }
        this.htmlParagraphStripper = new HTMLParagraphStripper(xmlOutputFactory, (XmlEventReaderFactory)new DefaultXmlEventReaderFactory());
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public boolean hasBody() {
        return false;
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext) throws MacroExecutionException {
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean();
        try {
            ContentEntityObject page;
            Optional<Link> linkObj = IncludeMacroUtils.getLink(conversionContext);
            if (linkObj.isPresent()) {
                page = this.pageProvider.resolve(linkObj.get(), conversionContext);
            } else {
                this.validate(parameters);
                String location = this.getLocation(parameters);
                if (StringUtils.isEmpty((CharSequence)location)) {
                    throw new MacroException(i18NBean.getText("confluence.macros.advanced.include.error.no.location"));
                }
                page = this.pageProvider.resolve(location, conversionContext);
            }
            return this.getIncludedContent(page, i18NBean, conversionContext);
        }
        catch (NotAuthorizedException | IllegalArgumentException exception) {
            log.debug(exception.getMessage(), exception);
            return RenderUtils.blockError((String)i18NBean.getText("confluence.macros.advanced.include.unable-to-render"), (String)i18NBean.getText("confluence.macros.advanced.include.error.content.not.found"));
        }
        catch (MacroException exception) {
            log.debug(exception.getMessage(), (Throwable)exception);
            throw new MacroExecutionException((Throwable)exception);
        }
    }

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean();
        if (!(renderContext instanceof PageContext)) {
            return RenderUtils.blockError((String)i18NBean.getText("confluence.macros.advanced.include.unable-to-render"), (String)i18NBean.getText("confluence.macros.advanced.include.error.can-only-be-used-in-confluence"));
        }
        this.validate(parameters);
        String location = this.getLocation(parameters);
        if (StringUtils.isEmpty((CharSequence)location)) {
            throw new MacroException(i18NBean.getText("confluence.macros.advanced.include.error.no.location"));
        }
        DefaultConversionContext conversionContext = new DefaultConversionContext(renderContext);
        return this.getIncludedContent(this.pageProvider.resolve(location, (ConversionContext)conversionContext), i18NBean, (ConversionContext)conversionContext);
    }

    private String getIncludedContent(ContentEntityObject page, I18NBean i18NBean, ConversionContext conversionContext) {
        try {
            if (page == null) {
                return RenderUtils.blockError((String)i18NBean.getText("confluence.macros.advanced.include.unable-to-render"), (String)i18NBean.getText("confluence.macros.advanced.include.error.content.not.found"));
            }
            return this.fetchPageContent(page, conversionContext);
        }
        catch (NotAuthorizedException exception) {
            log.debug(exception.getMessage(), (Throwable)exception);
            return RenderUtils.blockError((String)i18NBean.getText("confluence.macros.advanced.include.unable-to-render"), (String)i18NBean.getText("confluence.macros.advanced.include.error.content.not.found"));
        }
        catch (IllegalArgumentException exception) {
            log.debug(exception.getMessage(), (Throwable)exception);
            return RenderUtils.blockError((String)i18NBean.getText("confluence.macros.advanced.include.unable-to-render"), (String)exception.getMessage());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected String fetchPageContent(ContentEntityObject page, ConversionContext conversionContext) {
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean();
        if (ContentIncludeStack.contains((ContentEntityObject)page)) {
            return RenderUtils.blockError((String)i18NBean.getText("confluence.macros.advanced.include.unable-to-render"), (String)i18NBean.getText("confluence.macros.advanced.include.error.already.included", (Object[])new String[]{HtmlUtil.htmlEncode((String)page.getTitle())}));
        }
        ContentIncludeStack.push((ContentEntityObject)page);
        try {
            String strippedBody = page.getBodyAsString();
            try {
                strippedBody = this.htmlParagraphStripper.stripFirstParagraph(page.getBodyAsString());
            }
            catch (XMLStreamException e) {
                log.warn("Could not strip first paragraph, using unstripped body", (Throwable)e);
            }
            DefaultConversionContext context = new DefaultConversionContext((RenderContext)new PageContext(page, conversionContext.getPageContext()));
            String string = this.viewRenderer.render(strippedBody, (ConversionContext)context);
            return string;
        }
        finally {
            ContentIncludeStack.pop();
        }
    }

    String getLocation(Map parameters) {
        String spaceKey = StringUtils.defaultString((String)((String)parameters.get("spaceKey")), (String)"").trim();
        String pageTitle = GeneralUtil.unescapeEntities((String)StringUtils.defaultString((String)((String)parameters.get("pageTitle")), (String)"").trim());
        String location = GeneralUtil.unescapeEntities((String)StringUtils.defaultString((String)((String)parameters.get("0")), (String)"").trim());
        if (StringUtils.isBlank((CharSequence)pageTitle)) {
            return location;
        }
        return this.toPageLink(spaceKey, pageTitle);
    }

    void validate(Map parameters) throws MacroException {
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean();
        String pageTitle = (String)parameters.get("pageTitle");
        String location = (String)parameters.get("0");
        if (StringUtils.isBlank((CharSequence)location) && StringUtils.isBlank((CharSequence)pageTitle)) {
            throw new MacroException(i18NBean.getText("confluence.macros.advanced.include.error.no.page-title"));
        }
    }

    String toPageLink(String space, String pageTitle) {
        return StringUtils.isBlank((CharSequence)space) ? pageTitle : space + ":" + pageTitle;
    }

    public void setViewRenderer(Renderer viewRenderer) {
        this.viewRenderer = viewRenderer;
    }

    public void setPageProvider(PageProvider pageProvider) {
        this.pageProvider = pageProvider;
    }

    public void setUserI18NBeanFactory(I18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
    }
}

