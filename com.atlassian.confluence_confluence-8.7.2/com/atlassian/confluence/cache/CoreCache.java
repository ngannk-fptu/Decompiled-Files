/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 */
package com.atlassian.confluence.cache;

import com.atlassian.annotations.Internal;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import java.util.function.Function;

@Internal
public enum CoreCache {
    ATTACHMENT_DOWNLOAD_PATH_BY_CONTENT_ID_AND_FILENAME("com.atlassian.confluence.pages.AttachmentDownloadPathCache"),
    ATTACHMENT_ID_BY_CONTENT_ID_AND_FILENAME("com.atlassian.confluence.pages.attachments.AttachmentCache"),
    BANDANA_VALUE_BY_CONTEXT_AND_KEY("com.atlassian.bandana.BandanaPersister"),
    CAPTCHA_BY_ID("com.atlassian.confluence.cache.jcaptcha.ConfluenceCachingCaptchaStore"),
    CONTENT_PERMISSION_SETS_BY_CONTENT_ID("com.atlassian.confluence.impl.security.CachingInheritedContentPermissionManager.permissionSets"),
    CONTENT_PROPERTY_BY_CONTENT_ID_AND_KEY("com.atlassian.confluence.core.DefaultContentPropertyManager"),
    CROWD_GROUPS_BY_NAME("com.atlassian.confluence.impl.user.crowd.CachedCrowdGroupDao.GROUP_CACHE"),
    CROWD_GROUP_ATTRIBUTES_BY_NAME("com.atlassian.confluence.impl.user.crowd.CachedCrowdGroupDao.ATTRIBUTE_CACHE"),
    CROWD_USERS_BY_NAME("com.atlassian.confluence.impl.user.crowd.CachedCrowdUserDao.USER_CACHE"),
    CROWD_USERS_BY_EMAIL("com.atlassian.confluence.impl.user.crowd.CachedCrowdUserDao.USER_EMAIL_CACHE"),
    CROWD_USER_ATTRIBUTES_BY_NAME("com.atlassian.confluence.impl.user.crowd.CachedCrowdUserDao.ATTRIBUTE_CACHE"),
    DECORATORS_BY_SPACE_KEY("com.atlassian.confluence.impl.themes.persistence.PersistentDecoratorCache"),
    DECORATORS_EXIST_BY_SPACE_KEY("com.atlassian.confluence.impl.themes.persistence.PersistentDecoratorCache.any"),
    DIFF_RESULT_BY_KEY("com.atlassian.confluence.html.diffs"),
    FORMAT_SETTINGS("com.atlassian.confluence.core.FormatSettingsManager"),
    GLOBAL_THEME_KEY("com.atlassian.confluence.themes.ThemeManager.globalThemeKey"),
    GROUP_MEMBERSHIPS_BY_USER("com.atlassian.confluence.impl.user.crowd.CachedCrowdMembershipDao.STRING_PARENT_CACHE"),
    I18N_BY_LOCALE("com.atlassian.confluence.util.i18n.I18NBeanFactory.by.locale"),
    IS_USER_WATCHING_CONTENT("com.atlassian.confluence.mail.notification.persistence.NotificationDao.isUserWatchingContent"),
    LOCALE_BY_USER_NAME("com.atlassian.confluence.locale.requestLang"),
    LOGIN_MANAGER_FAILURE_CACHE("com.atlassian.confluence.security.login.DefaultLoginManager"),
    MACRO_METADATA("com.atlassian.confluence.impl.macro.metadata.AllMacroMetadataCache"),
    MAU_LAST_SENT_TIME_BY_USER("com.atlassian.confluence.api.impl.service.event.mau.MauEventServiceImpl.lastSent"),
    MOST_POPULAR_BY_SPACE("com.atlassian.confluence.labels.CachingLabelManager.mostPopular"),
    MOST_RECENT_JOURNAL_ID("com.atlassian.confluence.impl.journal.CachingJournalStateStore"),
    PAGE_ID_BY_SPACE_KEY_AND_TITLE("com.atlassian.confluence.pages.persistence.dao.PageDao.getPage"),
    PERMITTED_GROUP_NAMES_BY_SPACE("com.atlassian.confluence.security.SpacePermissionGroupNamesCache"),
    PLUGIN_PERSISTENT_STATE("com.atlassian.confluence.plugin.CachingPluginStateStore"),
    REGISTERED_USERS("com.atlassian.confluence.util.UserChecker"),
    REMOTE_DIRECTORY_BY_ID("com.atlassian.confluence.impl.user.crowd.CacheableDirectoryInstanceLoader"),
    SCHEDULED_JOB_STATUS("com.atlassian.confluence.schedule.ScheduledJobStatus"),
    SITE_ENABLED_DARK_FEATURES("com.atlassian.confluence.impl.feature.CachingSiteDarkFeaturesDao"),
    SPACE_ID_BY_SPACE_KEY("com.atlassian.confluence.spaces.persistence.dao.hibernate.HibernateSpaceDao.SpaceKeyToIdCache"),
    SPACE_PERMISSIONS("com.atlassian.confluence.security.CachingSpacePermissionManager.permissions"),
    SPACE_PERMISSIONS_BY_SPACE_KEY("com.atlassian.confluence.impl.security.CoarseGrainedCachingSpacePermissionManager.spacePermissions"),
    SPACE_RETENTION_POLICY_BY_SPACE_KEY("com.atlassian.confluence.retention.SpaceRetentionPolicy.keyToPolicy"),
    SPACE_THEME_KEY("com.atlassian.confluence.themes.ThemeManager.spaceThemeKeys"),
    UPGRADE_HISTORY("com.atlassian.confluence.core.persistence.VersionHistoryDao.fullUpgradeHistory"),
    FINALIZED_BUILD_NUMBER("com.atlassian.confluence.core.persistence.VersionHistoryDao.finalizedBuildNumber"),
    USER_ID_BY_USER_KEY("com.atlassian.confluence.impl.user.persistence.dao.ReadThroughCachingPersonalInformationDao.userKeyCache"),
    USER_KEY_BY_USER_NAME("com.atlassian.confluence.impl.user.persistence.dao.CachingConfluenceUserDao"),
    USER_PROPERTY_SETS("com.atlassian.confluence.user.ConfluenceUserPropertySetFactory.propertysets"),
    USER_PROPERTY_SET_REFERENCES("PropertySetReferences.Users"),
    VELOCITY_RESOURCES("com.atlassian.confluence.util.velocity.ConfluenceVelocityResourceCache");

    private final String systemCacheName;

    private CoreCache(String systemCacheName) {
        this.systemCacheName = systemCacheName;
    }

    public <T> T resolve(Function<String, T> resolver) {
        return resolver.apply(this.systemCacheName);
    }

    public <K, V> Cache<K, V> getCache(CacheFactory cacheFactory) {
        return this.resolve(arg_0 -> ((CacheFactory)cacheFactory).getCache(arg_0));
    }
}

