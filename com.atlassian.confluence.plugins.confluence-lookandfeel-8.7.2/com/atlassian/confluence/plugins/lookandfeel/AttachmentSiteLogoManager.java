/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.CachedReference
 *  com.atlassian.confluence.core.AttachmentResource
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.core.UploadedResource
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.FileUploadManager
 *  com.atlassian.confluence.setup.settings.GlobalDescriptionManager
 *  com.atlassian.confluence.themes.events.SiteLogoChangedEvent
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.lookandfeel;

import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.CachedReference;
import com.atlassian.confluence.core.AttachmentResource;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.core.UploadedResource;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.FileUploadManager;
import com.atlassian.confluence.plugins.lookandfeel.ImageScaler;
import com.atlassian.confluence.plugins.lookandfeel.SiteLogo;
import com.atlassian.confluence.plugins.lookandfeel.SiteLogoManager;
import com.atlassian.confluence.plugins.lookandfeel.events.SiteLogoChangedEvent;
import com.atlassian.confluence.setup.settings.GlobalDescriptionManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ExportAsService(value={SiteLogoManager.class})
@Component
public class AttachmentSiteLogoManager
implements SiteLogoManager {
    private static final Logger log = LoggerFactory.getLogger(AttachmentSiteLogoManager.class);
    private static final List<String> SUPPORTED_CONTENT_TYPES = ImmutableList.of((Object)"image/jpeg", (Object)"image/gif", (Object)"image/png", (Object)"image/pjpeg", (Object)"image/x-png");
    static final String SITE_LOGO_ATTACHMENT_NAME = "atl.site.logo";
    private static final String SITE_LOGO_MIME_TYPE = "image/png";
    public static final int MAX_LOGO_HEIGHT = 48;
    private final GlobalDescriptionManager globalDescriptionManager;
    private final ContextPathHolder contextPathHolder;
    private final FileUploadManager fileUploadManager;
    private final AttachmentManager attachmentManager;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final EventPublisher eventPublisher;
    private final ImageScaler imageScaler;
    private final CachedReference<Boolean> cachedCustomLogoPresent;

    @Autowired
    public AttachmentSiteLogoManager(@ComponentImport GlobalDescriptionManager globalDescriptionManager, @ComponentImport ContextPathHolder contextPathHolder, @ComponentImport FileUploadManager fileUploadManager, @ComponentImport AttachmentManager attachmentManager, @ComponentImport WebResourceUrlProvider webResourceUrlProvider, @ComponentImport EventPublisher eventPublisher, @ComponentImport CacheManager cacheManager, ImageScaler imageScaler) {
        this.globalDescriptionManager = Objects.requireNonNull(globalDescriptionManager);
        this.contextPathHolder = Objects.requireNonNull(contextPathHolder);
        this.fileUploadManager = Objects.requireNonNull(fileUploadManager);
        this.attachmentManager = Objects.requireNonNull(attachmentManager);
        this.webResourceUrlProvider = Objects.requireNonNull(webResourceUrlProvider);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.imageScaler = Objects.requireNonNull(imageScaler);
        this.cachedCustomLogoPresent = cacheManager.getCachedReference(this.getClass().getName() + ".customLogoPresent", () -> this.customLogoAttachment().isPresent(), new CacheSettingsBuilder().replicateViaInvalidation().remote().build());
    }

    @Override
    public void uploadLogo(File logo, String fileType) {
        this.validate(fileType);
        this.upload(logo);
    }

    @Override
    public String getSiteLogoUrl() {
        if (this.useCustomLogo()) {
            return this.getCustomLogoUrl().orElseGet(() -> {
                log.debug("Site logo is configured but couldn't be found. Try uploading it again.");
                return this.getDefaultLogoUrl();
            });
        }
        return this.getDefaultLogoUrl();
    }

    @Override
    public SiteLogo getCurrent() {
        return this.customLogoAttachment().map(attachment -> new SiteLogo(attachment.getDownloadPath(), this.attachmentManager.getAttachmentData(attachment))).orElseGet(() -> {
            log.debug("Site logo is configured but couldn't be found. Try uploading it again.");
            return null;
        });
    }

    @Override
    public void resetToDefault() {
        this.customLogoAttachment().ifPresent(attachment -> this.attachmentManager.removeAttachmentFromServer(attachment));
        this.siteLogoChanged();
    }

    @Override
    public boolean useCustomLogo() {
        return (Boolean)this.cachedCustomLogoPresent.get();
    }

    private Optional<Attachment> customLogoAttachment() {
        return this.attachmentTargetEntity().map(entity -> this.attachmentManager.getAttachment(entity, SITE_LOGO_ATTACHMENT_NAME));
    }

    private Optional<String> getCustomLogoUrl() {
        return this.customLogoAttachment().map(attachment -> this.contextPathHolder.getContextPath() + attachment.getDownloadPath());
    }

    private String getDefaultLogoUrl() {
        return this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.ABSOLUTE) + "/images/logo/confluence-logo.png";
    }

    private void upload(File logo) {
        File scaledLogo = this.resizeLogo(logo);
        UploadedResource resource = new UploadedResource(scaledLogo, SITE_LOGO_ATTACHMENT_NAME, SITE_LOGO_MIME_TYPE, null);
        this.fileUploadManager.storeResource((AttachmentResource)resource, this.attachmentTargetEntity().get());
        this.siteLogoChanged();
    }

    private void siteLogoChanged() {
        this.cachedCustomLogoPresent.reset();
        this.eventPublisher.publish((Object)new com.atlassian.confluence.themes.events.SiteLogoChangedEvent((Object)this, null));
        this.eventPublisher.publish((Object)new SiteLogoChangedEvent(this, null));
    }

    private void validate(String contentType) {
        String lower = StringUtils.trimToEmpty((String)contentType).toLowerCase();
        if (SUPPORTED_CONTENT_TYPES.stream().filter(x -> lower.contains((CharSequence)x)).findAny().isEmpty()) {
            throw new UnsupportedOperationException(String.format("The MIME type %s is unsupported.  PNG, JPG and GIF images are the only supported formats.", contentType));
        }
    }

    private File resizeLogo(File logo) {
        return this.imageScaler.scaleImageToMaxHeight(logo, 48);
    }

    private Optional<ContentEntityObject> attachmentTargetEntity() {
        return Optional.ofNullable(this.globalDescriptionManager.getGlobalDescription());
    }
}

