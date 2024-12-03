/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Renderer
 *  com.atlassian.confluence.content.render.xhtml.view.excerpt.ExcerptConfig
 *  com.atlassian.confluence.content.render.xhtml.view.excerpt.Excerpter
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.ExcerptHelper
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.macros.advanced.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.content.render.xhtml.view.excerpt.ExcerptConfig;
import com.atlassian.confluence.content.render.xhtml.view.excerpt.Excerpter;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.macros.advanced.xhtml.ExcerptType;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.ExcerptHelper;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.user.User;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdvancedMacrosExcerpter {
    private static final Logger log = LoggerFactory.getLogger(AdvancedMacrosExcerpter.class);
    private Excerpter excerpter;
    private ExcerptHelper excerptHelper;
    private Renderer viewRenderer;
    private I18NBeanFactory i18NBeanFactory;
    private LocaleManager localeManager;

    public String createExcerpt(ContentEntityObject contentEntityObject, String outputType) {
        try {
            ExcerptConfig excerptConfig = ExcerptConfig.builder().ignoreUserDefinedExcerpt(false).maxBlocks(3).maxCharCount(300).build();
            return this.excerpter.createExcerpt(contentEntityObject, outputType, excerptConfig);
        }
        catch (XMLStreamException e) {
            log.warn("Unable to render excerpt", (Throwable)e);
            return RenderUtils.error((String)this.getI18nBean().getText("advanced.macros.excerpt.error"));
        }
    }

    public String createExcerpt(ContentEntityObject entity, ExcerptType excerptType) {
        String excerpt = null;
        if (excerptType == ExcerptType.LEGACY) {
            excerpt = this.viewRenderer.render(this.excerptHelper.getExcerpt(entity), (ConversionContext)new DefaultConversionContext((RenderContext)entity.toPageContext()));
        } else if (excerptType == ExcerptType.RENDERED) {
            excerpt = this.createExcerpt(entity, entity.toPageContext().getOutputType());
        }
        return (String)StringUtils.defaultIfBlank(excerpt, (CharSequence)"");
    }

    public String createExcerpt(ContentEntityObject entity, ExcerptType excerptType, ConversionContext conversionContext, String legacyWrapperStart, String legacyWrapperEnd) {
        Object excerpt = null;
        if (excerptType == ExcerptType.LEGACY) {
            String renderedExcerpt = HtmlUtil.htmlEncode((String)this.excerptHelper.getExcerptSummary(entity));
            if (StringUtils.isNotEmpty((CharSequence)renderedExcerpt)) {
                excerpt = legacyWrapperStart + renderedExcerpt + legacyWrapperEnd;
            }
        } else if (excerptType == ExcerptType.RENDERED) {
            excerpt = this.createExcerpt(entity, conversionContext.getOutputType());
        }
        return (String)StringUtils.defaultIfBlank(excerpt, (CharSequence)"");
    }

    private I18NBean getI18nBean() {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
    }

    public void setExcerpter(Excerpter excerpter) {
        this.excerpter = excerpter;
    }

    public void setExcerptHelper(ExcerptHelper excerptHelper) {
        this.excerptHelper = excerptHelper;
    }

    public void setViewRenderer(Renderer viewRenderer) {
        this.viewRenderer = viewRenderer;
    }

    public void setI18NBeanFactory(I18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
    }

    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }
}

