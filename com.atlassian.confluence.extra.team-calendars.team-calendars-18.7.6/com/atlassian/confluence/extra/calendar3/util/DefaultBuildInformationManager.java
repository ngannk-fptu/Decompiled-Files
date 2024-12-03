/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  org.dom4j.Document
 *  org.dom4j.DocumentException
 *  org.dom4j.Element
 *  org.dom4j.io.SAXReader
 *  org.joda.time.DateTime
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.util;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.extra.calendar3.calendarstore.CalendarBandanaContext;
import com.atlassian.confluence.extra.calendar3.util.AbstractBuildInformationManager;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.BooleanUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.joda.time.DateTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="buildInformationManager")
@ExportAsService
public class DefaultBuildInformationManager
extends AbstractBuildInformationManager
implements LifecycleAware,
InitializingBean {
    private static final BandanaContext BUILD_INFORMATION_BANDANA_CONTEXT = new CalendarBandanaContext(DigestUtils.sha1Hex(DefaultBuildInformationManager.class.getName()));
    public static final String BANDANA_KEY_MIGRATION_CUTOFF_DATE_MILLIS = "legacySubCalendarsMigrationCutoffDate";
    private String pluginKey;
    private String version;
    private DateTime legacySubCalendarsMigrationCutoffDate;
    private final BandanaManager bandanaManager;

    @Autowired
    public DefaultBuildInformationManager(@ComponentImport BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    public void onStart() {
        this.initLegacySubCalendarsMigrationCuffoffDate();
    }

    public void onStop() {
    }

    public void afterPropertiesSet() throws Exception {
        this.initPropertiesFromPluginDescriptor();
    }

    private void initLegacySubCalendarsMigrationCuffoffDate() {
        Long cutoffDateMillis = (Long)this.bandanaManager.getValue(BUILD_INFORMATION_BANDANA_CONTEXT, BANDANA_KEY_MIGRATION_CUTOFF_DATE_MILLIS);
        if (cutoffDateMillis != null) {
            this.legacySubCalendarsMigrationCutoffDate = new DateTime(cutoffDateMillis.longValue());
        } else {
            this.legacySubCalendarsMigrationCutoffDate = new DateTime(System.currentTimeMillis());
            this.bandanaManager.setValue(BUILD_INFORMATION_BANDANA_CONTEXT, BANDANA_KEY_MIGRATION_CUTOFF_DATE_MILLIS, (Object)this.legacySubCalendarsMigrationCutoffDate.getMillis());
        }
    }

    private void initPropertiesFromPluginDescriptor() throws DocumentException, IOException {
        try (InputStream pluginDescInputStream = this.getClass().getClassLoader().getResourceAsStream("atlassian-plugin.xml");){
            SAXReader saxReader = new SAXReader();
            Document pluginDescDoc = saxReader.read(pluginDescInputStream);
            Element root = pluginDescDoc.getRootElement();
            this.pluginKey = root.attribute("key").getValue();
            this.version = root.element("plugin-info").element("version").getTextTrim();
        }
    }

    @Override
    public String getPluginKey() {
        return this.pluginKey;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public DateTime getLegacySubCalendarsMigrationCutoffDate() {
        return this.legacySubCalendarsMigrationCutoffDate;
    }

    @Override
    public boolean isNotificationsEnabled() {
        return BooleanUtils.toBoolean(System.getProperty("com.atlassian.confluence.extra.calendar3.notification.enabled", "true"));
    }

    @Override
    public boolean isShowingWhatsNew() {
        return BooleanUtils.toBoolean(System.getProperty("com.atlassian.confluence.extra.calendar3.whatsnew.enabled", "false"));
    }

    @Override
    public boolean isWorkboxNotificationEnabled() {
        return BooleanUtils.toBoolean(System.getProperty("com.atlassian.confluence.extra.calendar3.notification.workbox.enabled", "false"));
    }
}

