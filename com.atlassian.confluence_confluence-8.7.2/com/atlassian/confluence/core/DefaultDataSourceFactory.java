/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.thumbnail.Thumbnail
 *  com.atlassian.fugue.Pair
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.user.User
 *  com.google.common.base.Function
 *  javax.activation.DataSource
 *  javax.activation.FileTypeMap
 *  javax.activation.URLDataSource
 *  javax.mail.util.ByteArrayDataSource
 *  javax.servlet.ServletContext
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.web.context.ServletContextAware
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.core.DefaultPluginDataSourceFactory;
import com.atlassian.confluence.core.PluginDataSourceFactory;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.thumbnail.ThumbnailManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceLogoManager;
import com.atlassian.confluence.user.UserProfilePictureAccessor;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.core.util.thumbnail.Thumbnail;
import com.atlassian.fugue.Pair;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.user.User;
import com.google.common.base.Function;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import javax.activation.DataSource;
import javax.activation.FileTypeMap;
import javax.activation.URLDataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.ServletContext;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;

public class DefaultDataSourceFactory
implements DataSourceFactory,
ServletContextAware {
    private static final Logger log = LoggerFactory.getLogger(DefaultDataSourceFactory.class);
    private ServletContext servletContext;
    private final UserProfilePictureAccessor userProfilePictureAccessor;
    private final AttachmentManager attachmentManager;
    private final FileTypeMap fileTypeMap;
    private final ThumbnailManager thumbnailManager;
    private final PluginAccessor pluginAccessor;
    private final SpaceLogoManager spaceLogoManager;
    private final Function<Pair<Plugin, PluginDataSourceFactory.ResourceView>, InputStream> resourceStreamFactory;

    public DefaultDataSourceFactory(UserProfilePictureAccessor userProfilePictureAccessor, AttachmentManager attachmentManager, FileTypeMap fileTypeMap, ThumbnailManager thumbnailManager, PluginAccessor pluginAccessor, SpaceLogoManager spaceLogoManager) {
        this.userProfilePictureAccessor = userProfilePictureAccessor;
        this.attachmentManager = attachmentManager;
        this.fileTypeMap = fileTypeMap;
        this.thumbnailManager = thumbnailManager;
        this.pluginAccessor = pluginAccessor;
        this.spaceLogoManager = spaceLogoManager;
        this.resourceStreamFactory = resource -> {
            String location = ((PluginDataSourceFactory.ResourceView)resource.right()).location();
            InputStream inputStream = ((Plugin)resource.left()).getResourceAsStream(location);
            if (inputStream != null) {
                return inputStream;
            }
            return this.servletContext.getResourceAsStream(location);
        };
    }

    @Override
    public DataSource getAvatar(User user) {
        Object profilePicture;
        ProfilePictureInfo profilePictureInfo = this.userProfilePictureAccessor.getUserProfilePicture(user);
        if (!profilePictureInfo.isExternal()) {
            try (InputStream is = profilePictureInfo.getBytes();){
                profilePicture = !profilePictureInfo.isDefault() && !profilePictureInfo.isAnonymousPicture() && is != null ? new ByteArrayDataSource(is, profilePictureInfo.getContentType()) : (profilePictureInfo.isAnonymousPicture() ? this.getServletContainerResource("/images/icons/profilepics/anonymous.png") : this.getServletContainerResource("/images/icons/profilepics/default.png"));
            }
            catch (IOException e) {
                String name = user == null ? "null" : user.getName();
                log.warn(String.format("Error getting profile picture for user=[%s], profilePicture=[%s] : %s", name, ToStringBuilder.reflectionToString((Object)profilePictureInfo), e.getMessage()), (Throwable)e);
                profilePicture = this.getServletContainerResource("/images/icons/profilepics/default.png");
            }
        } else {
            profilePicture = DefaultDataSourceFactory.getExternalAvatarResource(profilePictureInfo.getUriReference());
        }
        String avatarContentId = this.getAvatarContentId(user, (DataSource)profilePicture);
        return new NamedDataSource((DataSource)profilePicture, avatarContentId);
    }

    @Override
    public DataSource getSpaceLogo(Space space) {
        DataSource logoDataSource;
        String logoPath = this.spaceLogoManager.getLogoDownloadPath(space, null);
        String spaceKey = space.getKey();
        if ("/images/logo/default-space-logo.svg".equals(logoPath)) {
            logoDataSource = this.getServletContainerResource("/images/logo/default-space-logo.svg");
        } else {
            try {
                logoDataSource = this.getDataSourceFromAttachmentDownloadPath(logoPath);
            }
            catch (Exception e) {
                logoDataSource = this.getServletContainerResource("/images/logo/default-space-logo.svg");
            }
        }
        String spaceLogoContentId = spaceKey + "-space-logo";
        return new NamedDataSource(logoDataSource, spaceLogoContentId);
    }

    private DataSource getDataSourceFromAttachmentDownloadPath(String downloadPath) throws IOException {
        Attachment attachment = this.attachmentManager.findAttachmentForDownloadPath(downloadPath).get();
        return this.getUnnamedDatasourceForAttachment(attachment, false);
    }

    private DataSource getUnnamedDatasourceForAttachment(Attachment attachment, boolean useThumbnails) throws IOException {
        Thumbnail thumb;
        if (useThumbnails && this.thumbnailManager.isThumbnailable(attachment) && (thumb = this.thumbnailManager.getThumbnail(attachment)) != null && thumb.getMimeType() != null) {
            try (InputStream is = this.thumbnailManager.getThumbnailData(attachment);){
                ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(is, thumb.getMimeType().toString());
                return byteArrayDataSource;
            }
        }
        try (InputStream is = this.attachmentManager.getAttachmentData(attachment);){
            ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(is, attachment.getMediaType());
            return byteArrayDataSource;
        }
    }

    @Override
    public DataSource getServletContainerResource(String path, String name) {
        return new NamedDataSource(this.getServletContainerResource(path), name);
    }

    private DataSource getServletContainerResource(String path) {
        ByteArrayDataSource byteArrayDataSource;
        block9: {
            if (StringUtils.isBlank((CharSequence)path)) {
                throw new IllegalArgumentException("Path is required.");
            }
            String filename = StringUtils.substringAfterLast((String)path, (String)"/");
            String mimeType = this.fileTypeMap.getContentType(filename);
            InputStream is = this.servletContext.getResourceAsStream(path);
            try {
                ByteArrayDataSource urlResource = new ByteArrayDataSource(is, mimeType);
                urlResource.setName(filename);
                byteArrayDataSource = urlResource;
                if (is == null) break block9;
            }
            catch (Throwable throwable) {
                try {
                    if (is != null) {
                        try {
                            is.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            is.close();
        }
        return byteArrayDataSource;
    }

    @Override
    public DataSource getDatasource(Attachment attachment, boolean useThumbnail) throws IOException {
        return new NamedDataSource(this.getUnnamedDatasourceForAttachment(attachment, useThumbnail), "attach_" + DigestUtils.md5Hex((String)attachment.getDownloadPath()));
    }

    @Override
    public DataSource getURLResource(URL url, String name) {
        return new NamedDataSource((DataSource)new URLDataSource(url), name);
    }

    private static DataSource getExternalAvatarResource(String uriReference) {
        try {
            return new URLDataSource(new URL(uriReference));
        }
        catch (MalformedURLException e) {
            log.warn("Malformed avatar url {}", (Object)uriReference);
            throw new RuntimeException(e);
        }
    }

    private String getAvatarContentId(User modifier, DataSource profilePicture) {
        String username = modifier != null ? modifier.getName() : "anonymous";
        String avatar = !StringUtils.isEmpty((CharSequence)profilePicture.getName()) ? profilePicture.getName() : this.userProfilePictureAccessor.getUserProfilePicture(modifier).getFileName();
        return "avatar_" + DigestUtils.md5Hex((String)(username + avatar));
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public Optional<PluginDataSourceFactory> createForPlugin(String pluginKey) {
        Plugin plugin = this.pluginAccessor.getPlugin(pluginKey);
        if (plugin == null) {
            return Optional.empty();
        }
        return Optional.of(new DefaultPluginDataSourceFactory(this.fileTypeMap, this.resourceStreamFactory, plugin));
    }

    public static class NamedDataSource
    implements DataSource {
        private final DataSource delegate;
        private final String name;

        public NamedDataSource(DataSource delegate, String name) {
            this.name = name;
            this.delegate = delegate;
        }

        public InputStream getInputStream() throws IOException {
            return this.delegate.getInputStream();
        }

        public OutputStream getOutputStream() throws IOException {
            return this.delegate.getOutputStream();
        }

        public String getContentType() {
            return this.delegate.getContentType();
        }

        public String getName() {
            return this.name;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            NamedDataSource that = (NamedDataSource)o;
            return !(this.name != null ? !this.name.equals(that.name) : that.name != null);
        }

        public int hashCode() {
            return this.name != null ? this.name.hashCode() : 0;
        }
    }
}

