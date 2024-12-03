/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DataSourceFactory
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.PersonalInformation
 *  com.atlassian.confluence.user.PersonalInformationManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.component.ComponentLocator
 *  com.atlassian.user.User
 *  com.opensymphony.module.propertyset.PropertySet
 *  javax.activation.DataSource
 *  javax.mail.util.ByteArrayDataSource
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.notification;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.extra.calendar3.notification.DefaultCalendarNotificationManager;
import com.atlassian.confluence.extra.calendar3.notification.ProfilePictureConst;
import com.atlassian.confluence.extra.calendar3.notification.ResourceDataHandler;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.user.User;
import com.opensymphony.module.propertyset.PropertySet;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultResourceDataHandler
implements ResourceDataHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultResourceDataHandler.class);
    private final UserAccessor userAccessor;
    private final PersonalInformationManager personalInformationManager;
    private final AttachmentManager attachmentManager;
    private final DataSourceFactory dataSourceFactory;

    @Autowired
    public DefaultResourceDataHandler(@ComponentImport UserAccessor userAccessor, @ComponentImport PersonalInformationManager personalInformationManager, @ComponentImport AttachmentManager attachmentManager) {
        this.userAccessor = userAccessor;
        this.personalInformationManager = personalInformationManager;
        this.attachmentManager = attachmentManager;
        this.dataSourceFactory = (DataSourceFactory)ComponentLocator.getComponent(DataSourceFactory.class, (String)"dataSourceFactory");
    }

    @Override
    public DefaultCalendarNotificationManager.IdentifiableContentDataHandler createAvatarDataHandler(ConfluenceUser user, Map<String, String> params) {
        String profilePicture;
        PropertySet propertySet = this.userAccessor.getPropertySet(user);
        StringBuilder stringBuilder = new StringBuilder();
        if (propertySet == null || (profilePicture = propertySet.getString("confluence.user.profile.picture")) == null) {
            return this.createDefaultProfilePictureDataHandler(params);
        }
        if (profilePicture.startsWith("/images/icons/profilepics/")) {
            String contentId;
            try {
                contentId = DigestUtils.sha1Hex(profilePicture.getBytes("UTF-8"));
            }
            catch (Exception ex) {
                LOG.error("Failed to get contentId for {}", (Object)profilePicture, (Object)ex);
                return null;
            }
            if (null != params && !params.isEmpty()) {
                contentId = stringBuilder.append(contentId).append('_').append(params.hashCode()).toString();
            }
            stringBuilder.setLength(0);
            String extension = profilePicture.lastIndexOf(46) >= 0 ? profilePicture.substring(profilePicture.lastIndexOf(46)) : "";
            return new DefaultCalendarNotificationManager.IdentifiableContentDataHandler(this.dataSourceFactory.getAvatar((User)user), contentId, stringBuilder.append(contentId).append(extension).toString());
        }
        PersonalInformation personalInformation = this.personalInformationManager.getOrCreatePersonalInformation((User)user);
        if (null == personalInformation) {
            return this.createDefaultProfilePictureDataHandler(params);
        }
        Attachment attachment = this.attachmentManager.getAttachment((ContentEntityObject)personalInformation, profilePicture);
        if (attachment != null) {
            return this.createAttachmentDataHandler(attachment, params);
        }
        return null;
    }

    @Override
    public DefaultCalendarNotificationManager.IdentifiableContentDataHandler createDefaultProfilePictureDataHandler(Map<String, String> params) {
        String contentId;
        String profilePath = ProfilePictureConst.DEFAULT_PROFILE.getDownloadPath();
        try {
            contentId = DigestUtils.sha1Hex(profilePath.getBytes("UTF-8"));
        }
        catch (IOException ex) {
            LOG.warn("Failed to get contentId for {}", (Object)profilePath, (Object)ex);
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (null != params && !params.isEmpty()) {
            contentId = stringBuilder.append(contentId).append('_').append(params.hashCode()).toString();
        }
        stringBuilder.setLength(0);
        String extension = profilePath.lastIndexOf(46) >= 0 ? profilePath.substring(profilePath.lastIndexOf(46)) : "";
        return new DefaultCalendarNotificationManager.IdentifiableContentDataHandler(this.dataSourceFactory.getServletContainerResource(profilePath, "default.gif"), contentId, stringBuilder.append(contentId).append(extension).toString());
    }

    @Override
    public DefaultCalendarNotificationManager.IdentifiableContentDataHandler createAvatarDataHandler(String userName, Map<String, String> params) {
        return this.createAvatarDataHandler(this.userAccessor.getUserByName(userName), params);
    }

    @Override
    public DefaultCalendarNotificationManager.IdentifiableContentDataHandler createAttachmentDataHandler(Attachment attachment, Map<String, String> params) {
        byte[] attachmentContent;
        try {
            attachmentContent = this.getAttachmentContentAsByteArray(attachment);
        }
        catch (IOException e) {
            LOG.warn("Failed to get content of attachment {} as byte array", (Object)attachment.getFileName(), (Object)e);
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        String contentId = DigestUtils.sha1Hex(attachmentContent);
        if (null != params && !params.isEmpty()) {
            contentId = stringBuilder.append(contentId).append(' ').append(params.hashCode()).toString();
        }
        stringBuilder.setLength(0);
        return new DefaultCalendarNotificationManager.IdentifiableContentDataHandler(new DefaultCalendarNotificationManager.ProfileImageDataSource((DataSource)new ByteArrayDataSource(attachmentContent, attachment.getMediaType())), contentId, stringBuilder.append(contentId).append('.').append(StringUtils.defaultString(attachment.getFileExtension())).toString());
    }

    private byte[] getAttachmentContentAsByteArray(Attachment attachment) throws IOException {
        try (InputStream attachmentInput = this.attachmentManager.getAttachmentData(attachment);){
            byte[] byArray = IOUtils.toByteArray((InputStream)attachmentInput);
            return byArray;
        }
    }
}

