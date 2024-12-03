/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.service;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AdminConfigurationService {
    private static final Logger log = LoggerFactory.getLogger(AdminConfigurationService.class);
    public static String DISALLOW_USER_UPLOAD_CUSTOM_EMOJIS = "com.atlassian.confluence.plugins.hipchat.emoticons.disallowUserUploadCustomEmojis";
    public static final String EMOTICONS_MAX_FILE_SIZE = "confluence.emoticons.max.file.size";
    public static final int MAX_FILE_SIZE_DEFAULT = 1;
    public static final int MAX_FILE_SIZE = Integer.getInteger("confluence.emoticons.max.file.size", 1);
    private final PluginSettings settings;

    public static long getMaxFileSize() {
        return (long)MAX_FILE_SIZE * 0x100000L;
    }

    public static int getMaxFileSizeMb() {
        return MAX_FILE_SIZE;
    }

    public AdminConfigurationService(@ComponentImport PluginSettingsFactory pluginSettingsFactory) {
        this.settings = pluginSettingsFactory.createGlobalSettings();
    }

    public boolean isAllowUserUploadCustomEmojis() {
        String disallowUserUploadCustomEmojis = (String)StringUtils.defaultIfEmpty((CharSequence)((String)this.settings.get(DISALLOW_USER_UPLOAD_CUSTOM_EMOJIS)), (CharSequence)Boolean.FALSE.toString());
        return !BooleanUtils.toBoolean((String)disallowUserUploadCustomEmojis);
    }

    public void setAllowUserUploadCustomEmojis(boolean isAllow) {
        this.settings.put(DISALLOW_USER_UPLOAD_CUSTOM_EMOJIS, (Object)String.valueOf(!isAllow));
    }

    static {
        log.info("Value for \"confluence.emoticons.max.file.size\" is \"{}\"", (Object)MAX_FILE_SIZE);
    }
}

