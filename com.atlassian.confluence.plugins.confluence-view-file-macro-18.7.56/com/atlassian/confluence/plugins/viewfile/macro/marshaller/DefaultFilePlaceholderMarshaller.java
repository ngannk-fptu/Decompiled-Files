/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.image.ImageDimensions
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.view.embed.AttachedImageRenderHelper
 *  com.atlassian.confluence.content.render.xhtml.view.embed.AttachedImageUnresolvedCommentCountAggregator
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.macro.DefaultImagePlaceholder
 *  com.atlassian.confluence.macro.ImagePlaceholder
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.plugins.conversion.api.ConversionManager
 *  com.atlassian.confluence.plugins.conversion.api.ConversionResult
 *  com.atlassian.confluence.plugins.conversion.api.ConversionStatus
 *  com.atlassian.confluence.plugins.conversion.api.ConversionType
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.UrlUtils
 *  com.atlassian.confluence.web.UrlBuilder
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.plugins.capabilities.api.CapabilityService
 *  com.atlassian.soy.renderer.SoyException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.viewfile.macro.marshaller;

import com.atlassian.confluence.content.render.image.ImageDimensions;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.view.embed.AttachedImageRenderHelper;
import com.atlassian.confluence.content.render.xhtml.view.embed.AttachedImageUnresolvedCommentCountAggregator;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.macro.DefaultImagePlaceholder;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.conversion.api.ConversionManager;
import com.atlassian.confluence.plugins.conversion.api.ConversionResult;
import com.atlassian.confluence.plugins.conversion.api.ConversionStatus;
import com.atlassian.confluence.plugins.conversion.api.ConversionType;
import com.atlassian.confluence.plugins.viewfile.macro.FilePlaceholderSize;
import com.atlassian.confluence.plugins.viewfile.macro.ViewFileMacroUtils;
import com.atlassian.confluence.plugins.viewfile.macro.marshaller.FilePlaceholderMarshaller;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.UrlUtils;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.plugins.capabilities.api.CapabilityService;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.user.User;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultFilePlaceholderMarshaller
implements FilePlaceholderMarshaller {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFilePlaceholderMarshaller.class);
    private static final int DEFAULT_HEIGHT = 250;
    private static final int UNDEFINED_WIDTH = -1;
    private static final int MAX_CHARACTERS_FILENAME_IN_EMAIL = 25;
    private static final String ELLIPSIS_CHARACTER = "\u2026";
    public static final String CAPABILITY_FILE_CONVERSIONS = "file.conversions";
    public static final String CAPABILITY_FILE_CONVERSIONS_CLOUD = "file.conversions.cloud";
    private final ContextPathHolder contextPathHolder;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final SoyTemplateRenderer soyTemplateRenderer;
    private final PageManager pageManager;
    private final AttachedImageRenderHelper attachedImageRenderHelper;
    private final ConversionManager conversionManager;
    private final CapabilityService capabilityService;
    private final SettingsManager settingsManager;
    private final PermissionManager permissionManager;

    public DefaultFilePlaceholderMarshaller(ContextPathHolder contextPathHolder, WebResourceUrlProvider webResourceUrlProvider, SoyTemplateRenderer soyTemplateRenderer, PageManager pageManager, ConversionManager conversionManager, AttachedImageRenderHelper attachedImageRenderHelper, CapabilityService capabilityService, SettingsManager settingsManager, PermissionManager permissionManager) {
        this.contextPathHolder = contextPathHolder;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.pageManager = pageManager;
        this.conversionManager = conversionManager;
        this.attachedImageRenderHelper = attachedImageRenderHelper;
        this.capabilityService = capabilityService;
        this.settingsManager = settingsManager;
        this.permissionManager = permissionManager;
    }

    @Override
    public ImagePlaceholder getImagePlaceholder(Attachment attachment, Map<String, String> params) {
        ImageDimensions imageDimensions = this.createImageDimensions(params);
        return new DefaultImagePlaceholder(this.getImagePlaceHolderForEditor(attachment, imageDimensions), false, imageDimensions);
    }

    @Override
    public boolean handles(Attachment attachment) {
        return true;
    }

    @Override
    public Streamable getRenderedContentStreamable(Attachment attachment, Map<String, String> params, ConversionContext conversionContext) {
        this.attachedImageRenderHelper.getUnresolvedCommentCountAggregatorFrom(conversionContext).addAttachedImageId(Long.valueOf(attachment.getId()));
        return writer -> {
            try {
                String renderedContent = "email".equals(conversionContext.getOutputType()) ? this.renderForEmail(params, attachment, conversionContext) : ("pdf".equals(conversionContext.getOutputType()) || "word".equals(conversionContext.getOutputType()) ? this.renderForExport(params, attachment) : this.renderForAbstractPage(params, attachment, conversionContext));
                writer.write(renderedContent);
            }
            catch (SoyException exp) {
                throw new RuntimeException("Cannot render content of attachment: " + attachment.getFileName(), exp);
            }
        };
    }

    private String getFullContextUrl(String url) {
        return UrlUtils.addContextPath((String)url, (ContextPathHolder)this.contextPathHolder);
    }

    private String getImagePlaceHolderForEditor(Attachment attachment, ImageDimensions imageDimensions) {
        UrlBuilder builder;
        String thumbnailUrl = null;
        ConversionStatus thumbnailStatus = null;
        ConversionResult result = (ConversionResult)this.getThumbnailConversionResult(attachment).getOrNull();
        if (result != null && (thumbnailStatus = result.getConversionStatus()) == ConversionStatus.CONVERTED) {
            thumbnailUrl = result.getConversionUrl();
        }
        if (thumbnailUrl != null) {
            builder = new UrlBuilder(thumbnailUrl);
        } else {
            builder = new UrlBuilder("/plugins/servlet/view-file-macro/placeholder");
            String type = (String)StringUtils.defaultIfBlank((CharSequence)attachment.getNiceType(), (CharSequence)"unknown");
            builder.add("type", type);
            builder.add("name", attachment.getFileName());
        }
        builder.add("attachmentId", String.valueOf(attachment.getId()));
        builder.add("version", attachment.getVersion());
        builder.add("mimeType", attachment.getMediaType());
        builder.add("height", imageDimensions.getHeight());
        if (thumbnailStatus != null) {
            builder.add("thumbnailStatus", thumbnailStatus.getStatus());
        }
        return builder.toUrl();
    }

    private String getImagePlaceHolderForView(Attachment attachment, ImageDimensions imageDimensions) {
        return this.getImagePlaceHolderForView(attachment, imageDimensions.getHeight());
    }

    private String getImagePlaceHolderForView(Attachment attachment, int height) {
        String icon = ViewFileMacroUtils.getIconFileName(attachment.getNiceType(), FilePlaceholderSize.from(height));
        return this.webResourceUrlProvider.getStaticPluginResourceUrl("com.atlassian.confluence.plugins.confluence-view-file-macro:view-file-macro-resources", "images/" + icon, UrlMode.RELATIVE);
    }

    private String getDownloadLink(Attachment attachment) {
        return this.getFullContextUrl(attachment.getDownloadPath());
    }

    private ImageDimensions createImageDimensions(Map<String, String> params) {
        return new ImageDimensions(-1, this.getHeightParam(params));
    }

    private int getHeightParam(Map<String, String> params) {
        String height = params.get("height");
        return StringUtils.isEmpty((CharSequence)height) ? 250 : Integer.parseInt(height);
    }

    private String renderForEmail(Map<String, String> params, Attachment attachment, ConversionContext conversionContext) throws SoyException {
        String template;
        String previewURL = this.getPreviewURLForEmail(conversionContext, attachment);
        int height = FilePlaceholderSize.MEDIUM.getHeight();
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("previewURL", previewURL);
        Object renderForWorkBox = conversionContext.getProperty("renderForWorkBox");
        if (renderForWorkBox != null && ((Boolean)renderForWorkBox).booleanValue()) {
            data.put("fileName", attachment.getFileName());
            template = "Confluence.ViewFileMacro.Templates.embeddedFileInWorkBoxNotification";
        } else {
            template = "Confluence.ViewFileMacro.Templates.embeddedFileInNotification";
            data.put("fileName", this.shortenFileNameForEmail(attachment.getFileName()));
            data.put("placeholderSrc", this.getImagePlaceHolderForView(attachment, height));
            data.put("height", height);
            data.put("width", ViewFileMacroUtils.getPlaceholderWidth(attachment.getNiceType(), FilePlaceholderSize.MEDIUM));
        }
        return this.soyTemplateRenderer.render("com.atlassian.confluence.plugins.confluence-view-file-macro:view-file-macro-notification-resources", template, data);
    }

    private String shortenFileNameForEmail(String fileName) {
        int fileNameLength = fileName.length();
        if (fileNameLength <= 25) {
            return fileName;
        }
        return fileName.substring(0, 24) + ELLIPSIS_CHARACTER;
    }

    private String renderForExport(Map<String, String> params, Attachment attachment) throws SoyException {
        try {
            int height = this.getHeightParam(params);
            BufferedImage bufferedImage = ViewFileMacroUtils.getPlaceholderWithFileName(attachment.getFileName(), attachment.getType(), String.valueOf(height));
            String src = "data:image/png;base64," + ViewFileMacroUtils.encodeToString(bufferedImage);
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("placeholderSrc", src);
            data.put("height", height);
            return this.soyTemplateRenderer.render("com.atlassian.confluence.plugins.confluence-view-file-macro:view-file-macro-export-resources", "Confluence.ViewFileMacro.Templates.embeddedFileForExport", data);
        }
        catch (IOException e) {
            throw new RuntimeException("Cannot render content of attachment: " + attachment.getFileName(), e);
        }
    }

    private String renderForAbstractPage(Map<String, String> params, Attachment attachment, ConversionContext conversionContext) throws SoyException {
        boolean isFeedOutput = "feed".equals(conversionContext.getOutputType());
        String baseUrl = this.settingsManager.getGlobalSettings().getBaseUrl();
        String fileSrc = this.getFileSourceForAbstractPage(attachment, conversionContext);
        HashMap<String, Object> data = new HashMap<String, Object>();
        boolean hasEditPermission = this.permissionManager.hasCreatePermission((User)AuthenticatedUserThreadLocal.get(), (Object)attachment.getContainer(), Attachment.class);
        AttachedImageUnresolvedCommentCountAggregator aggregator = this.attachedImageRenderHelper.getUnresolvedCommentCountAggregatorFrom(conversionContext);
        data.put("unresolvedCommentCount", aggregator.getUnresolvedCommentCount(Long.valueOf(attachment.getId())));
        String thumbnailUrl = (String)this.getThumbnailUrl(attachment).getOrNull();
        if (thumbnailUrl != null) {
            if (isFeedOutput) {
                data.put("placeholderSrc", this.getAbsolutePath(baseUrl, this.getFullContextUrl(thumbnailUrl)));
            } else {
                data.put("placeholderSrc", this.getFullContextUrl(thumbnailUrl));
            }
        }
        data.put("hasThumbnail", thumbnailUrl != null);
        data.put("fileSrc", fileSrc);
        data.put("niceType", attachment.getNiceType());
        data.put("mimeType", attachment.getMediaType());
        data.put("attachmentId", String.valueOf(attachment.getId()));
        data.put("attachmentVersion", attachment.getVersion());
        data.put("containerId", String.valueOf(attachment.getContainer().getId()));
        data.put("fileName", attachment.getFileName());
        data.put("height", this.getHeightParam(params));
        data.put("canEdit", hasEditPermission);
        if (!data.containsKey("placeholderSrc")) {
            ImageDimensions imageDimensions = this.createImageDimensions(params);
            if (isFeedOutput) {
                data.put("placeholderSrc", this.getAbsolutePath(baseUrl, this.getImagePlaceHolderForView(attachment, imageDimensions)));
            } else {
                data.put("placeholderSrc", this.getImagePlaceHolderForView(attachment, imageDimensions));
            }
        }
        return this.soyTemplateRenderer.render("com.atlassian.confluence.plugins.confluence-view-file-macro:view-file-macro-embedded-file-view-soy-resources", "Confluence.ViewFileMacro.Templates.embeddedFile", data);
    }

    private String getPreviewURLForEmail(ConversionContext conversionContext, Attachment attachment) {
        String contextPath = this.contextPathHolder.getContextPath();
        String urlPath = this.pageManager.getAbstractPage(this.getConversionContainerId(conversionContext)).getUrlPath();
        UrlBuilder urlBuilder = new UrlBuilder(contextPath + urlPath);
        String containerId = attachment.getContainer().getIdAsString();
        String attachmentId = String.valueOf(attachment.getId());
        String attachmentTitle = attachment.getTitle();
        String attachmentUrl = "/" + containerId + "/" + attachmentId + "/" + attachmentTitle;
        urlBuilder.add("preview", attachmentUrl);
        return urlBuilder.toUrl();
    }

    private String getFileSourceForAbstractPage(Attachment attachment, ConversionContext conversionContext) {
        String baseUrl = this.settingsManager.getGlobalSettings().getBaseUrl();
        if (conversionContext != null && ConversionContextOutputType.FEED.value().equals(conversionContext.getOutputType())) {
            return this.getAbsolutePath(baseUrl, attachment.getDownloadPath());
        }
        return this.getDownloadLink(attachment);
    }

    private String getAbsolutePath(String baseUrl, String path) {
        URI uri = URI.create(baseUrl);
        String contextPath = uri.getPath();
        if (path.contains(contextPath)) {
            path = path.replace(contextPath, "");
        }
        String newPath = contextPath + path;
        return uri.resolve(newPath).toString();
    }

    private Option<String> getThumbnailUrl(Attachment attachment) {
        ConversionResult result = (ConversionResult)this.getThumbnailConversionResult(attachment).getOrNull();
        if (result != null && result.getConversionStatus() == ConversionStatus.CONVERTED) {
            return Option.option((Object)result.getConversionUrl());
        }
        return Option.none();
    }

    private Option<ConversionResult> getThumbnailConversionResult(Attachment attachment) {
        if (this.conversionManager.isConvertible(this.conversionManager.getFileFormat(attachment)) && (this.capabilityService.getHostApplication().hasCapability(CAPABILITY_FILE_CONVERSIONS) || this.capabilityService.getHostApplication().hasCapability(CAPABILITY_FILE_CONVERSIONS_CLOUD))) {
            return Option.option((Object)this.conversionManager.getConversionResult(attachment, ConversionType.THUMBNAIL));
        }
        return Option.none();
    }

    private long getConversionContainerId(ConversionContext conversionContext) {
        ContentEntityObject entity = conversionContext.getEntity();
        return entity instanceof Comment ? ((Comment)entity).getContainer().getId() : entity.getId();
    }
}

