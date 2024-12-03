/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.admin.actions.lookandfeel.DefaultDecorator
 *  com.atlassian.confluence.core.PersistentDecorator
 *  com.atlassian.confluence.setup.settings.CustomHtmlSettings
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.DefaultSpaceManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.persistence.dao.SpaceDao
 *  com.atlassian.confluence.themes.StylesheetManager
 *  com.atlassian.confluence.themes.persistence.PersistentDecoratorDao
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.spring.container.ContainerManager
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.bundle;

import com.atlassian.confluence.admin.actions.lookandfeel.DefaultDecorator;
import com.atlassian.confluence.core.PersistentDecorator;
import com.atlassian.confluence.setup.settings.CustomHtmlSettings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.DefaultSpaceManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.persistence.dao.SpaceDao;
import com.atlassian.confluence.themes.StylesheetManager;
import com.atlassian.confluence.themes.persistence.PersistentDecoratorDao;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.bundle.BundleManifest;
import com.atlassian.troubleshooting.stp.salext.bundle.CustomisationFileBundle;
import java.util.HashMap;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfluenceCustomisationFileBundle
extends CustomisationFileBundle {
    private final SpaceManager spaceManager;
    private final StylesheetManager stylesheetManager;
    private final SettingsManager settingsManager;

    @Autowired
    public ConfluenceCustomisationFileBundle(SupportApplicationInfo info, I18nResolver i18nResolver, SpaceManager spaceManager, StylesheetManager stylesheetManager, SettingsManager settingsManager) {
        super(BundleManifest.CONF_CUSTOMISATIONS, "stp.zip.include.confluence.cust", "stp.zip.include.confluence.cust.description", info, i18nResolver);
        this.spaceManager = spaceManager;
        this.stylesheetManager = stylesheetManager;
        this.settingsManager = settingsManager;
    }

    @Override
    protected TreeMap<String, String> getCustomDecorators() {
        TreeMap<String, String> customDecorators = new TreeMap<String, String>();
        PersistentDecoratorDao decDao = (PersistentDecoratorDao)ContainerManager.getComponent((String)"persistentDecoratorDao");
        DefaultSpaceManager spaceManager = new DefaultSpaceManager();
        SpaceDao spaceDao = (SpaceDao)ContainerManager.getComponent((String)"spaceDao");
        spaceManager.setSpaceDao(spaceDao);
        for (DefaultDecorator dec : DefaultDecorator.getDecorators()) {
            PersistentDecorator customDec = decDao.get(null, dec.getFileName());
            if (customDec != null) {
                customDecorators.put(String.format("Global customised layout - %s", dec.getFileName().replace('/', '-').replace('\\', '-')), customDec.getBody());
            }
            for (Space space : spaceManager.getAllSpaces()) {
                PersistentDecorator spaceDec = decDao.get(space.getKey(), dec.getFileName());
                if (spaceDec == null) continue;
                customDecorators.put(String.format("Space %s customised layout - %s", space.getName(), dec.getFileName().replace('/', '-').replace('\\', '-')), spaceDec.getBody());
            }
        }
        return customDecorators;
    }

    @Override
    protected HashMap<String, String> getCustomHtml() {
        HashMap<String, String> customHtml = new HashMap<String, String>();
        CustomHtmlSettings customHtmlSettings = new CustomHtmlSettings(this.settingsManager.getGlobalSettings().getCustomHtmlSettings());
        String headHtml = customHtmlSettings.getBeforeHeadEnd();
        String bodyStartHtml = customHtmlSettings.getAfterBodyStart();
        String bodyEndHtml = customHtmlSettings.getBeforeBodyEnd();
        if (!headHtml.isEmpty()) {
            customHtml.put("Before HEAD end", headHtml);
        }
        if (!bodyStartHtml.isEmpty()) {
            customHtml.put("After BODY start", bodyStartHtml);
        }
        if (!bodyEndHtml.isEmpty()) {
            customHtml.put("Before BODY end", bodyEndHtml);
        }
        return customHtml;
    }

    @Override
    protected HashMap<String, String> getCustomStylesheet() {
        HashMap<String, String> customStylesheet = new HashMap<String, String>();
        String globalStylesheet = this.stylesheetManager.getGlobalStylesheet();
        if (StringUtils.isNotEmpty((CharSequence)globalStylesheet)) {
            customStylesheet.put("Global Custom Stylesheet", globalStylesheet);
        }
        for (Space space : this.spaceManager.getAllSpaces()) {
            String spaceStylesheet = this.stylesheetManager.getSpaceStylesheet(space.getKey(), false);
            if (!StringUtils.isNotEmpty((CharSequence)spaceStylesheet)) continue;
            customStylesheet.put("Space " + space.getName() + " custom stylesheet", spaceStylesheet);
        }
        return customStylesheet;
    }
}

