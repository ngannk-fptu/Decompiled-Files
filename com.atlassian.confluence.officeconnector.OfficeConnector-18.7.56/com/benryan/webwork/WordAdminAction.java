/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.RequireSecurityToken
 *  com.benryan.components.CustomCacheDirectorySetting
 *  com.benryan.components.OcSettingsManager
 */
package com.benryan.webwork;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;
import com.benryan.components.CustomCacheDirectorySetting;
import com.benryan.components.HtmlCacheManager;
import com.benryan.components.OcSettingsManager;
import com.benryan.components.SlideCacheManager;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.Collections;

public class WordAdminAction
extends ConfluenceActionSupport {
    public static final String locationBandanaKey = "com.benryan.confluence.word.edit.location";
    public static final String warningBandanaKey = "com.benryan.confluence.word.edit.warning";
    public static final String footnotesBandanaKey = "com.benryan.confluence.word.edit.footnotes";
    public static final String cacheTypeKey = "com.benryan.confluence.word.edit.cacheType";
    public static final String cacheDirKey = "com.benryan.confluence.word.edit.cacheDir";
    public static final String maxQueuesKey = "com.atlassian.confluence.officeconnector.maxQueues";
    public static final String maxCacheSizeKey = "com.atlassian.confluence.officeconnector.maxCacheSize";
    public static final String usePathAuthKey = "com.atlassian.confluence.officeconnector.usePathAuth";
    public static final String maxImportImageHeightKey = "com.atlassian.confluence.officeconnector.maxImageHeight";
    public static final String maxImportImageWidthKey = "com.atlassian.confluence.officeconnector.maxImageWidth";
    public static final String editInOfficeDarkFeature = "enable.legacy.edit.in.office";
    public static final String CACHE_DIRECTORIES_FILE = "resources/directories.properties";
    public static final int ON_PAGEACTION = 2;
    public static final int CACHE_TYPE_HOME = 0;
    public static final int CACHE_TYPE_FILE = 1;
    public static final int CACHE_TYPE_MEM = 2;
    private static final int MIN_IMPORT_IMAGE_SIZE_PX = 100;
    OcSettingsManager ocSettingsManager;
    boolean showWarning;
    boolean doFootnotes;
    int cacheType;
    int maxQueues;
    int locationCode;
    int maxCacheSize;
    int maxImportImageWidth;
    int maxImportImageHeight;
    private SlideCacheManager slideManager;
    private HtmlCacheManager htmlManager;
    private boolean pathAuth;
    private String customCacheDir = null;
    private String customCacheDirErrors;
    private boolean customCacheDirBandana;
    private boolean updated = false;
    private boolean editInOffice;
    private DarkFeatureManager darkFeatureManager;

    public int getMaxQueues() {
        return this.ocSettingsManager.getMaxQueues();
    }

    public void setMaxQueues(int maxQueues) {
        this.maxQueues = maxQueues;
    }

    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        this.readCustomCacheDirSetting();
        return "success";
    }

    @RequireSecurityToken(value=true)
    public String processSubmit() {
        this.validation();
        this.readCustomCacheDirSetting();
        if (this.hasErrors()) {
            return "error";
        }
        this.ocSettingsManager.setShowWarning(this.showWarning);
        this.ocSettingsManager.setDoFootnotes(this.doFootnotes);
        this.ocSettingsManager.setMaxQueues(this.maxQueues);
        this.ocSettingsManager.setCacheType(this.cacheType);
        this.ocSettingsManager.setEditInWordLocation(this.locationCode);
        this.ocSettingsManager.setMaxCacheSize(this.maxCacheSize);
        this.ocSettingsManager.setPathAuth(this.pathAuth);
        this.ocSettingsManager.setMaxImportImageSize(new Dimension(this.maxImportImageWidth, this.maxImportImageHeight));
        this.slideManager.initCache();
        this.htmlManager.initCache();
        this.toggleEditInOfficeDarkFeature(this.editInOffice);
        return "success";
    }

    private void validation() {
        if (this.maxQueues <= 0) {
            super.addActionError("The maximum number of conversion queues must be greater than zero.");
        }
        if (this.maxCacheSize < 0) {
            super.addActionError("The cache size must be greater than or equal to 0.");
        } else if (this.maxCacheSize == Integer.MAX_VALUE) {
            super.addActionError("The cache size must be less than 2147483647.");
        }
        if (this.maxImportImageHeight < 100 || this.maxImportImageWidth < 100) {
            super.addActionError("The maximum image import size must be greater than or equal to 100 pixels");
        }
    }

    public String getPathUnderHomeDir() {
        return this.ocSettingsManager.getHomeCachePath();
    }

    public int getLocationCode() {
        return this.ocSettingsManager.getEditInWordLocation();
    }

    public void setLocationCode(int location) {
        this.locationCode = location;
    }

    public void setPathAuth(boolean pathAuth) {
        this.pathAuth = pathAuth;
    }

    public boolean getPathAuth() {
        return this.ocSettingsManager.getPathAuth();
    }

    public void setDoFootnotes(boolean footnotes) {
        this.doFootnotes = footnotes;
    }

    public String getCacheDir() {
        return this.ocSettingsManager.getCacheDir();
    }

    public void setCacheType(int type) {
        this.cacheType = type;
    }

    public int getCacheType() {
        return this.ocSettingsManager.getCacheType();
    }

    public boolean getDoFootnotes() {
        return this.ocSettingsManager.isDoFootnotes();
    }

    public String getCustomCacheDir() {
        return this.customCacheDir;
    }

    public void setShowWarning(boolean warning) {
        this.showWarning = warning;
    }

    public boolean getShowWarning() {
        return this.ocSettingsManager.isShowWarning();
    }

    public void setOcSettingsManager(OcSettingsManager settingsManager) {
        this.ocSettingsManager = settingsManager;
    }

    public void setSlideCacheManager(SlideCacheManager manager) {
        this.slideManager = manager;
    }

    public void setHtmlCacheManager(HtmlCacheManager manager) {
        this.htmlManager = manager;
    }

    public int getMaxCacheSize() {
        return this.ocSettingsManager.getMaxCacheSize();
    }

    public void setMaxCacheSize(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }

    public boolean isCustomCacheDirBandana() {
        return this.customCacheDirBandana;
    }

    private void readCustomCacheDirSetting() {
        CustomCacheDirectorySetting customCacheDirectorySetting = this.ocSettingsManager.getCustomCacheDirectorySetting();
        String directory = customCacheDirectorySetting.getDirectory();
        if (directory != null) {
            this.customCacheDir = directory;
            this.customCacheDirBandana = customCacheDirectorySetting.isBandana();
        } else {
            this.customCacheDirErrors = customCacheDirectorySetting.getError();
        }
    }

    private void toggleEditInOfficeDarkFeature(boolean editInOffice) {
        if (editInOffice) {
            this.darkFeatureManager.enableFeatureForAllUsers(editInOfficeDarkFeature);
        } else {
            this.darkFeatureManager.disableFeatureForAllUsers(editInOfficeDarkFeature);
        }
    }

    public String getCustomCacheDirErrors() {
        return this.customCacheDirErrors;
    }

    public boolean isCacheFileValid() {
        return this.customCacheDir != null;
    }

    public String getCacheFileMessage() {
        if (this.getCustomCacheDir() == null) {
            return this.getText("office.connector.config.caching.custom.disabled", Arrays.asList(this.getText(this.getCustomCacheDirErrors()), CACHE_DIRECTORIES_FILE));
        }
        Object message = this.getText("office.connector.config.caching.custom", Collections.singletonList(GeneralUtil.htmlEncode((String)this.getCustomCacheDir())));
        if (this.isCustomCacheDirBandana()) {
            message = (String)message + "<br>" + this.getText("office.connector.config.caching.bandana.warning", Collections.singletonList(CACHE_DIRECTORIES_FILE));
        }
        return message;
    }

    public int getMaxImportImageWidth() {
        return this.ocSettingsManager.getMaxImportImageSize().width;
    }

    public void setMaxImportImageWidth(int maxImportImageWidth) {
        this.maxImportImageWidth = maxImportImageWidth;
    }

    public int getMaxImportImageHeight() {
        return this.ocSettingsManager.getMaxImportImageSize().height;
    }

    public void setMaxImportImageHeight(int maxImportImageHeight) {
        this.maxImportImageHeight = maxImportImageHeight;
    }

    public boolean isUpdated() {
        return this.updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public boolean getEditInOffice() {
        return this.darkFeatureManager.isEnabledForAllUsers(editInOfficeDarkFeature).orElse(false);
    }

    public void setEditInOffice(boolean editInOffice) {
        this.editInOffice = editInOffice;
    }

    public DarkFeatureManager getDarkFeatureManager() {
        return this.darkFeatureManager;
    }

    public void setDarkFeatureManager(DarkFeatureManager darkFeatureManager) {
        this.darkFeatureManager = darkFeatureManager;
    }
}

