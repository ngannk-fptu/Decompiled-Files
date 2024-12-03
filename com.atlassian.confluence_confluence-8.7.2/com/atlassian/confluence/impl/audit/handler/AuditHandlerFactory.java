/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.mail.server.MailServer
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.impl.audit.handler;

import com.atlassian.confluence.core.AbstractVersionedEntityObject;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.PersistentDecorator;
import com.atlassian.confluence.impl.audit.AuditHelper;
import com.atlassian.confluence.impl.audit.handler.AuditHandler;
import com.atlassian.confluence.impl.audit.handler.DefaultAuditHandler;
import com.atlassian.confluence.impl.audit.handler.HandlerFactory;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.setup.settings.CustomHtmlSettings;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.beans.CaptchaSettings;
import com.atlassian.confluence.setup.settings.beans.LoginManagerSettings;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.util.http.ConfluenceHttpParameters;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.mail.server.MailServer;
import java.security.Principal;

public class AuditHandlerFactory {
    private final HandlerFactory handlerFactory;
    private final AuditHelper helper;

    public AuditHandlerFactory(HandlerFactory handlerFactory, AuditHelper helper) {
        this.handlerFactory = handlerFactory;
        this.helper = helper;
    }

    public DefaultAuditHandler<CustomHtmlSettings> createCustomHtmlSettingsHandler() {
        return this.builder(CustomHtmlSettings.class).addGetter("getBeforeHeadEnd", CustomHtmlSettings::getBeforeHeadEnd).addGetter("getAfterBodyStart", CustomHtmlSettings::getAfterBodyStart).addGetter("getBeforeBodyEnd", CustomHtmlSettings::getBeforeBodyEnd).build();
    }

    public DefaultAuditHandler<PersistentDecorator> createDecoratorHandler() {
        return this.builder(PersistentDecorator.class).addGetter("getId", PersistentDecorator::getId).addGetter("getSpaceKey", PersistentDecorator::getSpaceKey).addGetter("getName", PersistentDecorator::getName).addGetter("getBody", PersistentDecorator::getBody).excludedMethodName("getLastModificationDate").referenceNameGetter(PersistentDecorator::getName).build();
    }

    public DefaultAuditHandler<MailServer> createMailServerHandler() {
        return this.builder(MailServer.class).addGetter("getDebug", MailServer::getDebug).addGetter("getDescription", MailServer::getDescription).addGetter("getHostname", MailServer::getHostname).addGetter("getName", MailServer::getName).addGetter("getUsername", MailServer::getUsername).addGetter("getPassword", MailServer::getPassword).addGetter("getPort", MailServer::getPort).addGetter("getSocksHost", MailServer::getSocksHost).addGetter("getSocksPort", MailServer::getSocksPort).addGetter("getType", MailServer::getType).addGetter("getTimeout", MailServer::getTimeout).addGetter("isTlsHostnameCheckRequired", MailServer::isTlsHostnameCheckRequired).addGetter("getMailProtocol", m -> m.getMailProtocol().getMailServerType()).excludedMethodName("getSession").excludedMethodName("getId").excludedMethodName("getProperties").build();
    }

    public DefaultAuditHandler<SpacePermission> createSpacePermissionHandler(AuditHandler<Space> spaceAuditHandler, AuditHandler<com.atlassian.user.User> userAuditHandler) {
        return this.builder(SpacePermission.class).addGetter("getGroup", SpacePermission::getGroup).addGetter("getType", SpacePermission::getType).addGetter("getAllUsersSubject", SpacePermission::getAllUsersSubject).addGetter("getSpace", SpacePermission::getSpace, spaceAuditHandler.reference()).addGetter("getUserSubject", SpacePermission::getUserSubject, userAuditHandler.reference()).excludedMethodNames("getSpaceId", "isUserPermission", "isGroupPermission", "isAnonymousPermission", "isAuthenticatedUsersPermission", "isGlobalPermission", "isSpacePermission", "isGuardPermission", "isDependentOn", "isInvalidAnonymousPermission", "isInvalidAuthenticatedUsersPermission", "getCreatorName", "getCreator", "getLastModifierName", "getLastModifier", "isPersistent", "getId", "getCreationDate", "getLastModificationDate", "getCurrentDate").build();
    }

    public AuditHandler<ConfluenceHttpParameters> createConfluenceHttpParametersHandler() {
        return this.builder(ConfluenceHttpParameters.class).addGetter("isEnabled", ConfluenceHttpParameters::isEnabled).addGetter("getSocketTimeout", ConfluenceHttpParameters::getSocketTimeout).addGetter("getConnectionTimeout", ConfluenceHttpParameters::getConnectionTimeout).build();
    }

    public AuditHandler<com.atlassian.user.User> createUserHandler() {
        return this.builder(com.atlassian.user.User.class).addGetter("getFullName", com.atlassian.user.User::getFullName).addGetter("getEmail", com.atlassian.user.User::getEmail).addGetter("getName", Principal::getName).referenceNameGetter(Principal::getName).build();
    }

    public AuditHandler<User> createCrowdUserHandler() {
        return this.builder(User.class).addGetter("getDisplayName", User::getDisplayName).addGetter("getEmailAddress", User::getEmailAddress).addGetter("getName", Principal::getName).addGetter("isActive", User::isActive).excludedMethodName("getDirectoryId").referenceNameGetter(Principal::getName).build();
    }

    public AuditHandler<LoginManagerSettings> createLoginManagerSettingsHandler() {
        return this.builder(LoginManagerSettings.class).addGetter("isEnableElevatedSecurityCheck", LoginManagerSettings::isEnableElevatedSecurityCheck).addGetter("getLoginAttemptsThreshold", LoginManagerSettings::getLoginAttemptsThreshold).build();
    }

    public AuditHandler<PageTemplate> createPageTemplateHandler(AuditHandler<Space> spaceAuditHandler) {
        return this.builder(PageTemplate.class).addGetter("getReferencingModuleKey", PageTemplate::getReferencingModuleKey).addGetter("getReferencingPluginKey", PageTemplate::getReferencingPluginKey).addGetter("getPluginKey", PageTemplate::getPluginKey).addGetter("getModuleKey", PageTemplate::getModuleKey).addGetter("getName", PageTemplate::getName).addGetter("getDescription", PageTemplate::getDescription).addGetter("getContent", PageTemplate::getContent).addGetter("getTitle", PageTemplate::getTitle).addGetter("getBodyType", PageTemplate::getBodyType).addGetter("getSpace", PageTemplate::getSpace, spaceAuditHandler.reference()).excludedMethodNames("getModuleCompleteKey", "getReferencingModuleCompleteKey", "getOriginalVersionPageTemplate", "getLabels", "getLabelCount", "isFavourite", "getVisibleLabels", "getPersonalLabels", "getGlobalLabels", "getTeamLabels", "getLabelsForDisplay", "getLabellings", "getVersion", "isNew", "getOriginalVersion", "getLatestVersion", "isLatestVersion", "getVersionChildPolicy", "getCreatorName", "getCreator", "getLastModifierName", "getLastModifier", "isPersistent", "getId", "getCreationDate", "getLastModificationDate", "getCurrentDate", "isGlobalPageTemplate", "getContentTemplateId").build();
    }

    public AuditHandler<? extends AbstractPage> createAbstractPageHandler() {
        return this.builder(AbstractPage.class).addGetter("getTitle", ContentEntityObject::getTitle).addGetter("getType", ContentEntityObject::getType).addGetter("getContentStatus", ContentEntityObject::getContentStatus).addGetter("getVersion", AbstractVersionedEntityObject::getVersion).excludedMethodNames("getAttachmentNamed", "getAttachmentUrlPath", "getAttachments", "getAttachmentsUrlPath", "getBodyAsString", "getBodyAsStringWithoutMarkup", "getBodyContent", "getBodyContents", "getCollaborativeEditingUuid", "getComments", "getConfluenceRevision", "getContentId", "getContentPermissionSet", "getContentStatusObject", "getCreationDate", "getCreator", "getCreatorName", "getCurrentDate", "getDefaultBodyType", "getDisplayTitle", "getEditUrlPath", "getEntity", "getExcerpt", "getGlobalLabels", "getId", "getIdAsString", "getLabelCount", "getLabellings", "getLabels", "getLabelsForDisplay", "getLastModificationDate", "getLastModifier", "getLastModifierName", "getLatestVersion", "getLatestVersionId", "getLinkWikiMarkup", "getLowerTitle", "getNameForComparison", "getOriginalVersionId", "getOriginalVersionPage", "getOutgoingLinks", "getPageLevelComments", "getPersonalLabels", "getPreviousVersion", "getProperties", "getRenderedVersionComment", "getSearchableDependants", "getSelector", "getShareId", "getSpace", "getSpaceKey", "getSynchronyRevision", "getSynchronyRevisionSource", "getTeamLabels", "getTopLevelComments", "getTypeEnum", "getUrlPath", "getVersionChildPolicy", "getVersionComment", "getVisibleLabels", "hasContentPermissions", "hasPermissions", "isCurrent", "isDeleted", "isDraft", "isFavourite", "isInSpace", "isIndexable", "isLatestVersion", "isNew", "isPersistent", "isUnpublished", "isVersionCommentAvailable").build();
    }

    public AuditHandler<Comment> createCommentHandler() {
        return this.builder(Comment.class).excludedMethodNames("getAttachmentNamed", "getAttachmentUrlPath", "getAttachments", "getAttachmentsUrlPath", "getBodyAsString", "getBodyAsStringWithoutMarkup", "getBodyContent", "getBodyContents", "getChildren", "getCollaborativeEditingUuid", "getComments", "getContainer", "getContentEntityObject", "getContentId", "getContentPermissionSet", "getContentStatus", "getContentStatusObject", "getContentTypeObject", "getCreationDate", "getCreator", "getCreatorName", "getCurrentDate", "getDefaultBodyType", "getDepth", "getDescendantAuthors", "getDescendantsCount", "getDisplayTitle", "getEntity", "getExcerpt", "getGlobalLabels", "getId", "getIdAsString", "getLabelCount", "getLabellings", "getLabels", "getLabelsForDisplay", "getLastModificationDate", "getLastModifier", "getLastModifierName", "getLatestVersion", "getLatestVersionId", "getLinkWikiMarkup", "getLowerTitle", "getNameForComparison", "getOriginalVersionId", "getOutgoingLinks", "getParent", "getPersonalLabels", "getProperties", "getRenderedVersionComment", "getSearchableDependants", "getSelector", "getShareId", "getSpace", "getStatus", "getSynchronyRevision", "getSynchronyRevisionSource", "getTeamLabels", "getThreadChangedDate", "getTitle", "getType", "getTypeEnum", "getUrlPath", "getVersion", "getVersionChildPolicy", "getVersionComment", "getVisibleLabels", "hasContentPermissions", "hasPermissions", "isCurrent", "isDeleted", "isDraft", "isFavourite", "isIndexable", "isInlineComment", "isLatestVersion", "isNew", "isPersistent", "isUnpublished", "isVersionCommentAvailable").build();
    }

    public DefaultAuditHandler<Attachment> createAttachmentHandler() {
        return this.builder(Attachment.class).addGetter("getVersion", AbstractVersionedEntityObject::getVersion).addGetter("getCreatorName", ConfluenceEntityObject::getCreatorName).addGetter("getNiceFileSize", Attachment::getNiceFileSize).excludedMethodNames("getAttachmentNamed", "getAttachmentUrlPath", "getAttachments", "getAttachmentsUrlPath", "getBodyAsString", "getBodyAsStringWithoutMarkup", "getBodyContent", "getBodyContent", "getBodyContents", "getCollaborativeEditingUuid", "getComments", "getContainer", "getContentId", "getContentPermissionSet", "getContentStatus", "getContentStatusObject", "getContentTypeObject", "getCreationDate", "getCreator", "getCurrentDate", "getDefaultBodyType", "getDisplayTitle", "getDownloadPath", "getDownloadPath", "getDownloadPathWithoutEncoding", "getDownloadPathWithoutVersion", "getDownloadPathWithoutVersionOrApiRevision", "getEntity", "getExcerpt", "getExportPath", "getExportPathForThumbnail", "getFileExtension", "getFileName", "getFileSize", "getFileStoreId", "getGlobalLabels", "getId", "getIdAsString", "getLabelCount", "getLabellings", "getLabels", "getLabelsForDisplay", "getLastModificationDate", "getLastModifier", "getLastModifierName", "getLatestVersion", "getLatestVersionId", "getLinkWikiMarkup", "getLowerTitle", "getMediaType", "getNameForComparison", "getNiceType", "getOriginalVersionId", "getOutgoingLinks", "getPersonalLabels", "getProperties", "getRenderedVersionComment", "getSearchableDependants", "getSelector", "getShareId", "getSpace", "getSpaceKey", "getSynchronyRevision", "getSynchronyRevisionSource", "getTeamLabels", "getTitle", "getType", "getTypeEnum", "getUrlPath", "getVersionChildPolicy", "getVersionComment", "getVisibleLabels", "hasContentPermissions", "hasPermissions", "isCurrent", "isDeleted", "isDraft", "isFavourite", "isHidden", "isInSpace", "isIndexable", "isLatestVersion", "isMinorEdit", "isNew", "isPersistent", "isUnpublished", "isUserProfilePicture", "isVersionCommentAvailable").build();
    }

    public DefaultAuditHandler<Space> createSpaceHandler(AuditHandler<SpaceDescription> spaceDescriptionHandler) {
        return this.builder(Space.class).addGetter("getSpaceType", Space::getSpaceType).addGetter("getSpaceStatus", Space::getSpaceStatus).addGetter("getName", Space::getName).addGetter("getKey", Space::getKey).addGetter("getHomePage", Space::getHomePage).addGetter("getDescription", Space::getDescription, spaceDescriptionHandler).excludedMethodNames("getLowerKey", "getPageTemplates", "getPermissions", "getSearchableDependants", "getUrlPath", "getBrowseUrlPath", "getAdvancedTabUrlPath", "getBlogTabUrlPath", "getDisplayTitle", "getType", "isValidSpaceKey", "isValidGlobalSpaceKey", "isValidPersonalSpaceKey", "isIndexable", "getSpaceManager", "getDeepLinkUri", "isPersonal", "isGlobal", "getDefaultHomepageTitle", "isArchived", "getCreatorName", "getCreator", "getLastModifierName", "getLastModifier", "isPersistent", "getId", "getCreationDate", "getLastModificationDate", "getCurrentDate").referenceNameGetter(Space::getKey).build();
    }

    public AuditHandler<SpaceDescription> createSpaceDescriptionHandler() {
        return this.builder(SpaceDescription.class).addGetter("getBodyAsString", ContentEntityObject::getBodyAsString).excludedMethodNames("getDisplayTitle", "getLatestVersion", "getUrlPath", "getType", "getSpaceKey", "getTrashDate", "isPersonalSpace", "getDefaultBodyType", "getAttachmentUrlPath", "getSpace", "getNameForComparison", "isIndexable", "isInSpace", "getTypeEnum", "getIdAsString", "getTitle", "getLowerTitle", "getBodyContent", "getBodyContent", "getBodyAsString", "getBodyContents", "getOutgoingLinks", "getReferralLinks", "getTrackbackLinks", "getSearchableDependants", "getPermissions", "getContentPermission", "getContentStatusObject", "getContentStatus", "isCurrent", "isDeleted", "isDraft", "sharedAccessAllowed", "getShareId", "getSynchronyRevision", "getSynchronyRevisionSource", "getCollaborativeEditingUuid", "isUnpublished", "wasCreatedBy", "getAttachments", "getLatestVersionsOfAttachments", "getBodyAsStringWithoutMarkup", "getExcerpt", "getAttachmentsUrlPath", "getAttachmentNamed", "getVersionComment", "isVersionCommentAvailable", "getRenderedVersionComment", "getContentPermissionSet", "hasPermissions", "hasContentPermissions", "getComments", "getEntity", "getContentId", "getSelector", "getProperties", "getOriginalVersionId", "getLatestVersionId", "getLabels", "getLabelCount", "isFavourite", "getVisibleLabels", "getPersonalLabels", "getGlobalLabels", "getTeamLabels", "getLabelsForDisplay", "getLabellings", "getVersion", "isNew", "isLatestVersion", "getVersionChildPolicy", "getCreatorName", "getCreator", "getLastModifierName", "getLastModifier", "isPersistent", "getRealClass", "getId", "getCreationDate", "getLastModificationDate", "getCurrentDate", "getClass", "getCustomContent").build();
    }

    public AuditHandler<CaptchaSettings> createCaptchaSettingsHandler() {
        return this.builder(CaptchaSettings.class).addGetter("isEnableCaptcha", CaptchaSettings::isEnableCaptcha).addGetter("isEnableDebug", CaptchaSettings::isEnableDebug).addGetter("getExclude", CaptchaSettings::getExclude).addGetter("getCaptchaGroups", CaptchaSettings::getCaptchaGroups, HandlerFactory.collectionHandler(HandlerFactory.stringHandler())).build();
    }

    public AuditHandler<Directory> createCrowdDirectoryHandler() {
        return this.builder(Directory.class).addGetter("getId", Directory::getId).addGetter("getName", Directory::getName).addGetter("isActive", Directory::isActive).addGetter("getEncryptionType", Directory::getEncryptionType).addGetter("getDescription", Directory::getDescription).addGetter("getType", Directory::getType).addGetter("getAllowedOperations", Directory::getAllowedOperations, HandlerFactory.collectionHandler(this.handlerFactory.toStringHandler())).excludedMethodNames("getAttributes", "getImplementationClass", "getCreatedDate", "getUpdatedDate", "getKeys", "getValue", "getValues", "isEmpty").build();
    }

    public AuditHandler<Settings> createSettingsHandler(AuditHandler<CaptchaSettings> captchaSettingsAuditHandler, AuditHandler<CustomHtmlSettings> customHtmlSettingsAuditHandler, AuditHandler<LoginManagerSettings> loginManagerSettingsAuditHandler, AuditHandler<ConfluenceHttpParameters> confluenceHttpParametersAuditHandler) {
        return this.builder(Settings.class).addGetter("isAddWildcardsToUserAndGroupSearches", Settings::isAddWildcardsToUserAndGroupSearches).addGetter("isAllowCamelCase", Settings::isAllowCamelCase).addGetter("isAllowRemoteApi", Settings::isAllowRemoteApi).addGetter("isAllowRemoteApiAnonymous", Settings::isAllowRemoteApiAnonymous).addGetter("isAllowThreadedComments", Settings::isAllowThreadedComments).addGetter("isAntiXssMode", Settings::isAntiXssMode).addGetter("isAlmostSupportPeriodEndMessageOff", Settings::isAlmostSupportPeriodEndMessageOff).addGetter("isBackupAttachmentsDaily", Settings::isBackupAttachmentsDaily).addGetter("isBackupDaily", Settings::isBackupDaily).addGetter("isBaseUrlAdminMessageOff", Settings::isBaseUrlAdminMessageOff).addGetter("isDenyPublicSignup", Settings::isDenyPublicSignup).addGetter("isDisableLogo", Settings::isDisableLogo).addGetter("isEmailAdminMessageOff", Settings::isEmailAdminMessageOff).addGetter("isEnableOpenSearch", Settings::isEnableOpenSearch).addGetter("isEnableQuickNav", Settings::isEnableQuickNav).addGetter("isEnableSpaceStyles", Settings::isEnableSpaceStyles).addGetter("isExternalUserManagement", Settings::isExternalUserManagement).addGetter("isGzippingResponse", Settings::isGzippingResponse).addGetter("isNofollowExternalLinks", Settings::isNofollowExternalLinks).addGetter("isSenMissingInLicenseMessageOff", Settings::isSenMissingInLicenseMessageOff).addGetter("isShowContactAdministratorsForm", Settings::isShowContactAdministratorsForm).addGetter("isShowSystemInfoIn500Page", Settings::isShowSystemInfoIn500Page).addGetter("isXsrfAddComments", Settings::isXsrfAddComments).addGetter("getAuditLogRetentionNumber", Settings::getAuditLogRetentionNumber).addGetter("getAuditLogRetentionUnit", Settings::getAuditLogRetentionUnit).addGetter("getEmailAddressVisibility", Settings::getEmailAddressVisibility).addGetter("getWebSudoEnabled", Settings::getWebSudoEnabled).addGetter("getAttachmentDataStore", Settings::getAttachmentDataStore).addGetter("getAttachmentSecurityLevel", Settings::getAttachmentSecurityLevel).addGetter("getBackupPath", Settings::getBackupPath).addGetter("getBaseUrl", Settings::getBaseUrl).addGetter("getCustomContactMessage", Settings::getCustomContactMessage).addGetter("getDailyBackupDateFormatPattern", Settings::getDailyBackupDateFormatPattern).addGetter("getDailyBackupFilePrefix", Settings::getDailyBackupFilePrefix).addGetter("getDefaultEncoding", Settings::getDefaultEncoding).addGetter("getGlobalDefaultLocale", Settings::getGlobalDefaultLocale).addGetter("getDefaultSpaceHomepageTitle", Settings::getDefaultSpaceHomepageTitle).addGetter("getDefaultTimezoneId", Settings::getDefaultTimezoneId).addGetter("getDefaultUsersGroup", Settings::getDefaultUsersGroup).addGetter("getIgnoredAdminTasks", Settings::getIgnoredAdminTasks).addGetter("getIndexingLanguage", Settings::getIndexingLanguage).addGetter("getSiteHomePage", Settings::getSiteHomePage).addGetter("getSiteTitle", Settings::getSiteTitle).addGetter("getSupportRequestEmail", Settings::getSupportRequestEmail).addGetter("getAttachmentMaxSize", Settings::getAttachmentMaxSize).addGetter("getDraftSaveInterval", Settings::getDraftSaveInterval).addGetter("getMaxAttachmentsInUI", Settings::getMaxAttachmentsInUI).addGetter("getMaxRssItems", Settings::getMaxRssItems).addGetter("getMaxSimultaneousQuickNavRequests", Settings::getMaxSimultaneousQuickNavRequests).addGetter("getMaxThumbHeight", Settings::getMaxThumbHeight).addGetter("getMaxThumbWidth", Settings::getMaxThumbWidth).addGetter("getPageTimeout", Settings::getPageTimeout).addGetter("getRssTimeout", Settings::getRssTimeout).addGetter("getWebSudoTimeout", Settings::getWebSudoTimeout).addGetter("getCaptchaSettings", Settings::getCaptchaSettings, captchaSettingsAuditHandler).addGetter("getCustomHtmlSettings", Settings::getCustomHtmlSettings, customHtmlSettingsAuditHandler).addGetter("isMaintenanceBannerMessageOn", Settings::isMaintenanceBannerMessageOn).addGetter("getMaintenanceBannerMessage", Settings::getMaintenanceBannerMessage).addGetter("getLoginManagerSettings", Settings::getLoginManagerSettings, loginManagerSettingsAuditHandler).addGetter("getConfluenceHttpParameters", Settings::getConfluenceHttpParameters, confluenceHttpParametersAuditHandler).excludedMethodNames("getColourSchemesSettings", "getMaxThumbDimensions", "getMaxThumbnailDimensions", "isSaveable").build();
    }

    public DefaultAuditHandler<ContentPermission> createContentPermissionHandler(AuditHandler<com.atlassian.user.User> userAuditHandler) {
        return this.builder(ContentPermission.class).addGetter("getGroupName", ContentPermission::getGroupName).addGetter("getType", ContentPermission::getType).addGetter("getUserSubject", ContentPermission::getUserSubject, userAuditHandler.reference()).excludedMethodNames("isPermitted", "isValid", "isGroupPermission", "isUserPermission", "isPersistent", "getCreatorName", "getCreator", "getCreationDate", "getCrowdService", "getCurrentDate", "getId", "getLastModificationDate", "getLastModifier", "getLastModifierName", "getOwningSet", "getRealClass", "getUserName", "getClass").build();
    }

    private <T> DefaultAuditHandler.Builder<T> builder(Class<T> handledClass) {
        return DefaultAuditHandler.builder(handledClass, this.handlerFactory.toStringHandler()).auditHelper(this.helper);
    }
}

