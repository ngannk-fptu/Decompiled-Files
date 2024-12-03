/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.plugins.avatar.Avatar
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.concurrent.Supplier
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.plugins.avatar.Avatar;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.Supplier;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfilePictureInfo {
    private static final Logger log = LoggerFactory.getLogger(ProfilePictureInfo.class);
    private static final Supplier<WebResourceUrlProvider> webResourceUrlProviderReference = new LazyComponentReference("webResourceUrlProvider");
    @Deprecated
    public static final String DEFAULT_PROFILE_PATH = "/images/icons/profilepics/default.png";
    @Deprecated
    public static final String ANONYMOUS_PROFILE_PATH = "/images/icons/profilepics/anonymous.png";
    public static final String ADGS_DEFAULT_PROFILE_PATH = "/images/icons/profilepics/default.svg";
    public static final String ADGS_ANONYMOUS_PROFILE_PATH = "/images/icons/profilepics/anonymous.svg";
    private final String downloadPath;
    private final String contentType;
    private final String fileName;
    private final boolean uploaded;
    private Avatar userAvatar;
    private boolean external;

    public ProfilePictureInfo(String downloadPath) {
        this(downloadPath, null, false);
    }

    public ProfilePictureInfo(String downloadPath, String contentType, boolean external) {
        this.downloadPath = downloadPath;
        this.contentType = contentType;
        this.external = external;
        this.uploaded = false;
        File file = new File(downloadPath);
        this.fileName = file.getName();
    }

    public ProfilePictureInfo(Attachment attachment) {
        this.downloadPath = attachment.getDownloadPath();
        this.fileName = attachment.getFileName();
        this.uploaded = true;
        this.contentType = attachment.getMediaType();
        this.external = false;
    }

    public ProfilePictureInfo(Avatar userAvatar) {
        this(userAvatar.getUrl(), userAvatar.getContentType(), userAvatar.isExternal());
        this.userAvatar = userAvatar;
    }

    public String getFileName() {
        return this.fileName;
    }

    @Deprecated
    public String getDownloadPath() {
        log.debug("ProfilePictureInfo.getDownloadPath is deprecated: {}", (Object)this.downloadPath);
        return this.downloadPath;
    }

    public String getUriReference() {
        return (this.external ? "" : ((WebResourceUrlProvider)webResourceUrlProviderReference.get()).getBaseUrl(UrlMode.RELATIVE)) + this.downloadPath;
    }

    public boolean isUploaded() {
        return this.uploaded;
    }

    public boolean isDefault() {
        return ADGS_DEFAULT_PROFILE_PATH.equals(this.downloadPath);
    }

    public boolean isAnonymousPicture() {
        return ADGS_ANONYMOUS_PROFILE_PATH.equals(this.downloadPath);
    }

    public String getContentType() {
        return this.contentType;
    }

    public boolean isExternal() {
        return this.external;
    }

    public InputStream getBytes() throws IOException {
        return this.userAvatar != null ? this.userAvatar.getBytes() : null;
    }
}

