/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.labels.Labelable
 *  com.atlassian.confluence.labels.Namespace
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.util.concurrent.LazyReference
 *  com.google.common.collect.Maps
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.softwareproject.components;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.util.concurrent.LazyReference;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;

public class LabelCreator {
    private static final String BUSINESS_BLUEPRINTS_PLUGIN = "com.atlassian.confluence.plugins.confluence-business-blueprints";
    private static final String SOFTWARE_BLUEPRINTS_PLUGIN = "com.atlassian.confluence.plugins.confluence-software-blueprints";
    private static final String FILE_LISTS_MODULE_KEY = "com.atlassian.confluence.plugins.confluence-business-blueprints:file-list-blueprint";
    private static final String MEETING_NOTES_MODULE_KEY = "com.atlassian.confluence.plugins.confluence-business-blueprints:meeting-notes-blueprint";
    private static final String DECISIONS_MODULE_KEY = "com.atlassian.confluence.plugins.confluence-business-blueprints:decisions-blueprint";
    private static final String PRODUCT_REQUIREMENTS_MODULE_KEY = "com.atlassian.confluence.plugins.confluence-software-blueprints:requirements-blueprint";
    private static final String SPRINT_PLANS_MODULE_KEY = "com.atlassian.confluence.plugins.confluence-software-blueprints:jira-sprints-blueprint";
    private static final String RETROSPECTIVES_MODULE_KEY = "com.atlassian.confluence.plugins.confluence-software-blueprints:retrospectives-blueprint";
    private final LabelManager labelManager;
    private final PageManager pageManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;
    private final LazyReference<Map<String, String>> moduleKeyMap = new LazyReference<Map<String, String>>(){

        protected Map<String, String> create() throws Exception {
            return LabelCreator.this.buildModuleKeyMap();
        }
    };

    @Autowired
    public LabelCreator(LabelManager labelManager, PageManager pageManager, I18NBeanFactory i18NBeanFactory, LocaleManager localeManager) {
        this.labelManager = Objects.requireNonNull(labelManager);
        this.pageManager = Objects.requireNonNull(pageManager);
        this.i18NBeanFactory = Objects.requireNonNull(i18NBeanFactory);
        this.localeManager = Objects.requireNonNull(localeManager);
    }

    public void addLabelsToIndexPages(Space space) {
        List pages = this.pageManager.getPages(space, false);
        for (Page page : pages) {
            String pageTitle = page.getDisplayTitle();
            String nameSpace = (String)((Map)this.moduleKeyMap.get()).get(pageTitle);
            if (nameSpace == null) continue;
            Label label = new Label("blueprint-index-page", Namespace.getNamespace((String)nameSpace));
            this.labelManager.addLabel((Labelable)page, label);
        }
    }

    private Map<String, String> buildModuleKeyMap() {
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean(this.localeManager.getSiteDefaultLocale());
        HashMap moduleKeyMap = Maps.newHashMap();
        moduleKeyMap.put(i18NBean.getText("confluence.blueprints.space.sp.file-lists.name"), FILE_LISTS_MODULE_KEY);
        moduleKeyMap.put(i18NBean.getText("confluence.blueprints.space.sp.meeting-notes.name"), MEETING_NOTES_MODULE_KEY);
        moduleKeyMap.put(i18NBean.getText("confluence.blueprints.space.sp.product-requirements.name"), PRODUCT_REQUIREMENTS_MODULE_KEY);
        moduleKeyMap.put(i18NBean.getText("confluence.blueprints.space.sp.decisions.name"), DECISIONS_MODULE_KEY);
        moduleKeyMap.put(i18NBean.getText("confluence.blueprints.space.sp.retrospectives.name"), RETROSPECTIVES_MODULE_KEY);
        moduleKeyMap.put(i18NBean.getText("confluence.blueprints.space.sp.sprint-plans.name"), SPRINT_PLANS_MODULE_KEY);
        return moduleKeyMap;
    }
}

