/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.user.util.ClassLoaderUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.spaces.listeners;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.FormatConverter;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.BodyType;
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
import com.atlassian.confluence.spaces.listeners.PersonalSpaceHelper;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.user.util.ClassLoaderUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatePersonalSpaceListener
extends AbstractSpaceContentListener {
    private static final Logger log = LoggerFactory.getLogger(CreatePersonalSpaceListener.class);
    private static final String VAR_USER_FULL_NAME = "userFullName";
    private static final String VAR_USER_EMAIL = "userEmail";
    private static final String VAR_USER_PERSONAL_INFO = "userPersonalInfo";
    @Deprecated
    public static final String DEFAULT_HOMEPAGE_TITLE = "Home";
    private static String defaultHomePageContent = null;
    private final PersonalInformationManager personalInformationManager;

    public CreatePersonalSpaceListener(FormatConverter formatConverter, I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, PageManager pageManager, SystemTemplateManager systemTemplateManager, PluginAccessor pluginAccessor, XhtmlContent xhtmlContent, PersonalInformationManager personalInformationManager) {
        super(formatConverter, i18NBeanFactory, localeManager, pageManager, systemTemplateManager, pluginAccessor, xhtmlContent);
        this.personalInformationManager = personalInformationManager;
    }

    @Deprecated(since="8.0", forRemoval=true)
    public CreatePersonalSpaceListener(FormatConverter formatConverter, I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, PageManager pageManager, SystemTemplateManager systemTemplateManager, PluginAccessor pluginAccessor, XhtmlContent xhtmlContent, PersonalInformationManager personalInformationManager, UserAccessor userAccessor) {
        this(formatConverter, i18NBeanFactory, localeManager, pageManager, systemTemplateManager, pluginAccessor, xhtmlContent, personalInformationManager);
    }

    @Override
    protected void handleSpaceCreate(SpaceCreateEvent event) {
        PersonalSpaceHelper helper = new PersonalSpaceHelper(this.personalInformationManager);
        Space space = event.getSpace();
        if (!helper.isPersonalSpace(space)) {
            return;
        }
        PersonalInformation personalInformation = helper.getPersonalInformation(space.getCreator());
        if (personalInformation == null) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("Creating initial space content for personal space {}", (Object)space);
        }
        Page homePage = this.createHomePage(space, personalInformation);
        DefaultSaveContext saveContext = ((DefaultSaveContext.Builder)DefaultSaveContext.builder().updateLastModifier(true).updateTrigger(PageUpdateTrigger.SPACE_CREATE)).build();
        this.pageManager.saveContentEntity(homePage, saveContext);
        space.setHomePage(homePage);
        helper.blankPersonalInformation(personalInformation);
        if (log.isDebugEnabled()) {
            log.debug("Finished creating initial space content for personal space " + space);
        }
    }

    @Deprecated
    public static String getDefaultHomePageContent() {
        if (defaultHomePageContent == null) {
            StringBuilder content = new StringBuilder();
            try (InputStream resourceStream = ClassLoaderUtils.getResourceAsStream((String)"com/atlassian/confluence/spaces/defaultPersonalSpaceContent.xmlf", CreatePersonalSpaceListener.class);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream));){
                String line = reader.readLine();
                while (line != null) {
                    content.append(line).append('\n');
                    line = reader.readLine();
                }
            }
            catch (IOException ex) {
                log.warn("Failed to read default personal space home page content.", (Object)ex.getMessage());
            }
            defaultHomePageContent = content.toString();
        }
        return defaultHomePageContent;
    }

    private Page createHomePage(Space space, PersonalInformation personalInfo) {
        Page homePage = new Page();
        I18NBean systemLocaleI18nBean = this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale(personalInfo.getUser()));
        String homePageTitle = systemLocaleI18nBean.getText(space.getDefaultHomepageTitle());
        homePage.setTitle(homePageTitle);
        homePage.setSpace(space);
        this.createHomePageContent(homePage, personalInfo, systemLocaleI18nBean);
        if (homePage.getBodyContent().getBodyType() == BodyType.WIKI) {
            homePage = this.xhtmlContent.convertWikiBodyToStorage(homePage);
        }
        return homePage;
    }

    private void createHomePageContent(Page homePage, PersonalInformation personalInfo, I18NBean systemLocaleI18nBean) {
        DefaultConversionContext conversionContext = new DefaultConversionContext(homePage.toPageContext());
        conversionContext.getPageContext().pushRenderMode(RenderMode.INLINE);
        ArrayList<Variable> substitutionValues = new ArrayList<Variable>(3);
        substitutionValues.add(new StringVariable(VAR_USER_FULL_NAME, personalInfo.getFullName()));
        String displayEmail = GeneralUtil.maskEmail(personalInfo.getEmail());
        if (StringUtils.isBlank((CharSequence)displayEmail)) {
            displayEmail = systemLocaleI18nBean.getText("personal.space.no.email");
        }
        substitutionValues.add(new StringVariable(VAR_USER_EMAIL, displayEmail));
        String personalContent = personalInfo.getBodyAsString();
        personalContent = StringUtils.isBlank((CharSequence)personalContent) ? systemLocaleI18nBean.getText("personal.space.default.aboutme") : this.xhtmlContent.convertWikiToStorage(personalContent, conversionContext, new ArrayList<RuntimeException>());
        substitutionValues.add(new StringVariable(VAR_USER_PERSONAL_INFO, personalContent));
        BodyContent bodyContent = this.getDefaultHomePageContent(homePage, substitutionValues, "com.atlassian.confluence.plugins.confluence-default-space-content-plugin:spacecontent-personal");
        homePage.setBodyContent(bodyContent);
    }
}

