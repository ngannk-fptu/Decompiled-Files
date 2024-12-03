/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.user.util.ClassLoaderUtils
 *  com.google.common.io.CharStreams
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.spaces.listeners;

import com.atlassian.confluence.content.render.xhtml.FormatConverter;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.event.events.space.SpaceCreateEvent;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import com.atlassian.confluence.pages.templates.variables.StringVariable;
import com.atlassian.confluence.pages.templates.variables.Variable;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SystemTemplateManager;
import com.atlassian.confluence.spaces.listeners.AbstractSpaceContentListener;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.user.util.ClassLoaderUtils;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitialSpaceContentListener
extends AbstractSpaceContentListener {
    private static final Logger log = LoggerFactory.getLogger(InitialSpaceContentListener.class);
    @Deprecated
    public static final String DEFAULT_HOMEPAGE_TITLE = "Home";
    private static final String VAR_SPACE_NAME = "spaceName";
    private static final String VAR_SPACE_KEY = "spaceKey";
    @Deprecated
    private static final String DEFAULT_HOME_PAGE_CONTENT;

    public InitialSpaceContentListener(FormatConverter formatConverter, I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, PageManager pageManager, SystemTemplateManager systemTemplateManager, PluginAccessor pluginAccessor, XhtmlContent xhtmlContent) {
        super(formatConverter, i18NBeanFactory, localeManager, pageManager, systemTemplateManager, pluginAccessor, xhtmlContent);
    }

    @Override
    protected void handleSpaceCreate(SpaceCreateEvent event) {
        Space space = event.getSpace();
        if (space.isPersonal()) {
            return;
        }
        log.debug("Creating initial space content for {}", (Object)space);
        Page homePage = this.createHomePage(space);
        DefaultSaveContext saveContext = ((DefaultSaveContext.Builder)DefaultSaveContext.builder().updateLastModifier(true).updateTrigger(PageUpdateTrigger.SPACE_CREATE)).build();
        this.pageManager.saveContentEntity(homePage, saveContext);
        space.setHomePage(homePage);
        log.debug("Finished creating initial space content for {}", (Object)space);
    }

    @Deprecated
    public static String getDefaultHomePageContent() {
        return DEFAULT_HOME_PAGE_CONTENT;
    }

    private Page createHomePage(Space space) {
        Page homePage = new Page();
        I18NBean systemLocaleI18nBean = this.i18NBeanFactory.getI18NBean(this.localeManager.getSiteDefaultLocale());
        String title = systemLocaleI18nBean.getText(space.getDefaultHomepageTitle());
        homePage.setTitle(title);
        ArrayList<Variable> variables = new ArrayList<Variable>();
        variables.add(new StringVariable(VAR_SPACE_NAME, space.getName()));
        variables.add(new StringVariable(VAR_SPACE_KEY, space.getKey()));
        BodyContent bodyContent = this.getDefaultHomePageContent(homePage, variables, "com.atlassian.confluence.plugins.confluence-default-space-content-plugin:spacecontent-global");
        homePage.setBodyContent(bodyContent);
        homePage.setSpace(space);
        return homePage;
    }

    static {
        try (InputStream resourceAsStream = ClassLoaderUtils.getResourceAsStream((String)"com/atlassian/confluence/spaces/defaultGlobalSpaceContent.xmlf", InitialSpaceContentListener.class);){
            DEFAULT_HOME_PAGE_CONTENT = CharStreams.toString((Readable)new InputStreamReader(resourceAsStream, Charset.defaultCharset()));
        }
        catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}

