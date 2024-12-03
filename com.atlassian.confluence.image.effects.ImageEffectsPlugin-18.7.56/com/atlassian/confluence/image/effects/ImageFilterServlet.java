/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.servlet.download.SafeContentHeaderGuesser
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.FilesystemUtils
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Either
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.User
 *  com.atlassian.util.concurrent.ConcurrentOperationMap
 *  com.atlassian.util.concurrent.ConcurrentOperationMapImpl
 *  com.atlassian.util.concurrent.Supplier
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  javax.annotation.WillClose
 *  javax.inject.Inject
 *  javax.inject.Named
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.exception.ExceptionUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.image.effects;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.image.effects.ImageCache;
import com.atlassian.confluence.image.effects.ImageEffectsConfig;
import com.atlassian.confluence.image.effects.ImageFilterTask;
import com.atlassian.confluence.image.effects.ImageFilterUtils;
import com.atlassian.confluence.image.effects.TransformContext;
import com.atlassian.confluence.image.effects.TransformContextBuilder;
import com.atlassian.confluence.image.effects.TransformFailure;
import com.atlassian.confluence.image.effects.analytics.DiskCacheUsageEvent;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.servlet.download.SafeContentHeaderGuesser;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.FilesystemUtils;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Either;
import com.atlassian.imageeffects.core.exif.ExifService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.User;
import com.atlassian.util.concurrent.ConcurrentOperationMap;
import com.atlassian.util.concurrent.ConcurrentOperationMapImpl;
import com.atlassian.util.concurrent.Supplier;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.WillClose;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

@Named(value="imageFilterServlet")
public class ImageFilterServlet
extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(ImageFilterServlet.class);
    private static final Splitter SPLITTER = Splitter.on((char)',').trimResults().omitEmptyStrings();
    private static final long MAX_PREVIEW_IMAGE_DATA_SIZE = 50000L;
    private static final String PREVIEW_CACHE_CONTROL_HEADER = "public, max-age=315360000";
    private static final String EXIF_ROTATE_EFFECT = "exif-rotate";
    private static final String THUMBNAIL_EFFECT = "thumbnail";
    private final ConcurrentOperationMap<ImageFilterTask, Future<Either<TransformFailure, byte[]>>> taskMap = new ConcurrentOperationMapImpl();
    private final ContentEntityManager contentEntityManager;
    private final AttachmentManager attachmentManager;
    private final ImageCache imageCache;
    private final PermissionManager permissionManager;
    private final TransactionTemplate txTemplate;
    private final I18NBeanFactory i18NBeanFactory;
    private final ExecutorService executorService;
    private final ImageEffectsConfig config;
    private final EventPublisher eventPublisher;
    private final ExifService exifService;
    private final SettingsManager settingsManager;
    private final SafeContentHeaderGuesser safeContentHeaderGuesser;

    @Inject
    public ImageFilterServlet(@ComponentImport ContentEntityManager contentEntityManager, @ComponentImport AttachmentManager attachmentManager, @ComponentImport PermissionManager permissionManager, ImageCache imageCache, @ComponentImport TransactionTemplate txTemplate, @ComponentImport I18NBeanFactory i18nBeanFactory, @Qualifier(value="imageFilterExecutor") ExecutorService executorService, ImageEffectsConfig config, @ComponentImport EventPublisher eventPublisher, ExifService exifService, @ComponentImport SettingsManager settingsManager, @ComponentImport SafeContentHeaderGuesser safeContentHeaderGuesser) {
        this.contentEntityManager = contentEntityManager;
        this.attachmentManager = attachmentManager;
        this.permissionManager = permissionManager;
        this.txTemplate = txTemplate;
        this.executorService = executorService;
        this.imageCache = imageCache;
        this.i18NBeanFactory = i18nBeanFactory;
        this.config = Objects.requireNonNull(config);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.exifService = Objects.requireNonNull(exifService);
        this.settingsManager = settingsManager;
        this.safeContentHeaderGuesser = safeContentHeaderGuesser;
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        this.txTemplate.execute(() -> {
            try {
                return this.doGetInTransaction(req, resp);
            }
            catch (IOException e) {
                return ImageFilterServlet.uncheckAndIgnoreClientAbortExceptions(e);
            }
        });
    }

    private Object doGetInTransaction(HttpServletRequest request, HttpServletResponse response) throws IOException {
        TransformContext context;
        log.debug("Doing a transform");
        if (!ImageFilterServlet.isValidEffects(request)) {
            response.sendError(400, "Invalid effects parameter");
            return null;
        }
        boolean isPreview = Boolean.parseBoolean(request.getParameter("preview"));
        if (isPreview && this.respondWithGeneratedPreview(request, response)) {
            return null;
        }
        TransformContext transformContext = context = isPreview ? this.buildPreviewContext(request) : this.buildAttachmentContext(request);
        if (context.isForbiddenAccess()) {
            response.sendError(403);
            return null;
        }
        this.writeTransformToResponse(request, response, isPreview, context);
        return null;
    }

    private boolean respondWithGeneratedPreview(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = this.previewCacheEntryName(request);
        InputStream previewStream = ((Object)((Object)this)).getClass().getResourceAsStream("previews/" + name + ".png");
        if (previewStream == null) {
            log.debug("Unable to find pre-generated preview for {}", (Object)name);
            return false;
        }
        this.copyToResponse(previewStream, imageStream -> {
            response.setContentType("image/png");
            response.setHeader("Cache-Control", PREVIEW_CACHE_CONTROL_HEADER);
            return response;
        });
        return true;
    }

    private void writeTransformToResponse(HttpServletRequest request, HttpServletResponse response, boolean isPreview, TransformContext context) throws IOException {
        InputStream cacheStream;
        Function<BufferedInputStream, HttpServletResponse> responseSupplier = imageBufferStream -> {
            this.guessContentTypeHeaders((BufferedInputStream)imageBufferStream, context).forEach((arg_0, arg_1) -> ((HttpServletResponse)response).setHeader(arg_0, arg_1));
            if (isPreview) {
                response.setHeader("Cache-Control", PREVIEW_CACHE_CONTROL_HEADER);
            }
            return response;
        };
        InputStream inputStream = cacheStream = this.config.isDisableCache() ? null : this.imageCache.get(context.getAttachmentId(), context.getCacheEntryName(), context.getLastModified());
        if (cacheStream != null) {
            this.eventPublisher.publish((Object)new DiskCacheUsageEvent(true, context.getCacheEntryName()));
            log.debug("Using the cached value");
            this.copyToResponse(cacheStream, responseSupplier);
        } else {
            InputStream imageStream;
            this.eventPublisher.publish((Object)new DiskCacheUsageEvent(false, context.getCacheEntryName()));
            boolean responseSent = this.transformAndRespond(context, this.buildEffectsList(request, isPreview), response);
            if (!responseSent && (imageStream = (InputStream)context.getImageSupplier().get()) != null) {
                log.debug("Sending back the original");
                this.copyToResponse(imageStream, responseSupplier);
            }
        }
    }

    @Nonnull
    private String previewCacheEntryName(HttpServletRequest request) {
        return "preview-" + request.getParameter("effects");
    }

    private static boolean isValidEffects(HttpServletRequest request) {
        String effectsParam = request.getParameter("effects");
        return FilesystemUtils.isSafeTitleForFilesystem((String)effectsParam);
    }

    @Nonnull
    private TransformContext buildPreviewContext(HttpServletRequest request) {
        String cacheEntryName = this.previewCacheEntryName(request);
        Supplier imageSupplier = () -> ((Object)((Object)this)).getClass().getResourceAsStream("previews/preview.jpg");
        return new TransformContextBuilder().cacheEntryName(cacheEntryName).forbiddenAccess(false).imageLabel("Attachment Comment").imageSupplier((Supplier<InputStream>)imageSupplier).imageDataSize(50000L).lastModified(0L).config(this.config).eventPublisher(this.eventPublisher).rotationOnly(this.rotationOnly(request)).rotationAndThumbnailOnly(this.rotationAndThumbnailOnly(request)).build();
    }

    @Nonnull
    private TransformContext buildAttachmentContext(HttpServletRequest request) {
        String ceoId = request.getParameter("ceo");
        ContentEntityObject ceo = this.contentEntityManager.getById(Long.parseLong(ceoId));
        String image = request.getParameter("image");
        Attachment attachment = this.attachmentManager.getAttachment(ceo, image);
        attachment.setContainer(ceo);
        String cacheEntryName = request.getParameter("effects");
        Supplier imageSupplier = () -> {
            InputStream attachmentData = this.attachmentManager.getAttachmentData(attachment);
            if (attachmentData == null) {
                log.debug("No data stream found for {}", (Object)attachment);
            }
            return attachmentData;
        };
        String attachmentContentType = attachment.getMediaType();
        String attachmentFilename = attachment.getFileName();
        return new TransformContextBuilder().cacheEntryName(cacheEntryName).forbiddenAccess(this.forbiddenAccess(attachment)).imageLabel(StringUtils.defaultString((String)attachment.getVersionComment())).imageSupplier((Supplier<InputStream>)imageSupplier).imageDataSize(attachment.getFileSize()).lastModified(attachment.getLastModificationDate().getTime()).config(this.config).eventPublisher(this.eventPublisher).rotationOnly(this.rotationOnly(request)).rotationAndThumbnailOnly(this.rotationAndThumbnailOnly(request)).attachmentId(attachment.getId()).attachmentContentType(attachmentContentType).attachmentFilename(attachmentFilename).build();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void copyToResponse(@WillClose InputStream imageStream, @Nonnull Function<BufferedInputStream, HttpServletResponse> responseSupplier) throws IOException {
        try {
            BufferedInputStream imageBufferStream = new BufferedInputStream(imageStream);
            IOUtils.copy((InputStream)imageBufferStream, (OutputStream)responseSupplier.apply(imageBufferStream).getOutputStream());
        }
        finally {
            imageStream.close();
        }
    }

    private Map<String, String> guessContentTypeHeaders(BufferedInputStream imageBufferStream, TransformContext context) {
        try {
            HashMap<String, String> contentTypeHeaders = new HashMap<String, String>(this.safeContentHeaderGuesser.computeAttachmentHeaders(this.guessMimeType(imageBufferStream).orElseGet(context::getAttachmentContentType), (InputStream)imageBufferStream, context.getAttachmentFilename(), null, context.getImageDataSize(), false, Collections.emptyMap()));
            contentTypeHeaders.remove("Content-Length");
            return contentTypeHeaders;
        }
        catch (IOException e) {
            log.warn("Exception while getting MIME type of image via Stream. Will use default value instead", (Throwable)e);
            return ImmutableMap.of((Object)"Content-Type", (Object)"image/jpeg", (Object)"Content-Disposition", (Object)"attachment");
        }
    }

    private Optional<String> guessMimeType(BufferedInputStream imageBufferStream) {
        String format;
        try {
            format = ImageFilterUtils.getImageFormat(imageBufferStream);
        }
        catch (IOException ignore) {
            return Optional.empty();
        }
        Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName(format);
        ImageWriter writer = imageWriters.next();
        String[] supportedMimeTypes = writer.getOriginatingProvider().getMIMETypes();
        if (supportedMimeTypes != null && supportedMimeTypes.length > 0) {
            return Optional.of(supportedMimeTypes[0]);
        }
        return Optional.empty();
    }

    private boolean transformAndRespond(TransformContext context, String[] effectsList, HttpServletResponse response) throws IOException {
        try {
            ImageFilterTask imageFilterProcessor = new ImageFilterTask(context, effectsList, this.exifService, this.settingsManager);
            Future<Either<TransformFailure, byte[]>> future = this.processTask(imageFilterProcessor);
            Either<TransformFailure, byte[]> transformResult = future.get(context.getConfig().getTransformTimeoutMs(), TimeUnit.MILLISECONDS);
            if (transformResult.isRight()) {
                byte[] imageBytes = (byte[])transformResult.right().get();
                if (!this.config.isDisableCache()) {
                    this.imageCache.put(context.getAttachmentId(), context.getCacheEntryName(), imageBytes);
                }
                this.copyToResponse(new ByteArrayInputStream(imageBytes), imageStream -> {
                    this.guessContentTypeHeaders((BufferedInputStream)imageStream, context).forEach((arg_0, arg_1) -> ((HttpServletResponse)response).setHeader(arg_0, arg_1));
                    return response;
                });
                ServletOutputStream respOut = response.getOutputStream();
                respOut.write(imageBytes);
                log.debug("Successfully transformed using: {}", (Object[])effectsList);
                return true;
            }
            TransformFailure failure = (TransformFailure)transformResult.left().get();
            log.warn("Transform failed for reason: {} {}", (Object)failure.getReason(), failure.getCause());
            switch (failure.getReason()) {
                case IMAGE_DATA_TOO_LARGE: {
                    this.displayError((ServletResponse)response, "image.effects.error.data.toobig");
                    return true;
                }
                case IMAGE_PIXEL_TOO_LARGE: {
                    this.displayError((ServletResponse)response, "image.effects.error.pixels.toobig");
                    return true;
                }
                case IMAGE_DATA_MISSING: {
                    this.displayError((ServletResponse)response, "image.effects.error.data.missing");
                    return true;
                }
            }
            return false;
        }
        catch (RejectedExecutionException e) {
            log.warn("Unable submit image for transform", (Throwable)e);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Interrupted while doing the transform", (Throwable)e);
        }
        catch (ExecutionException e) {
            log.warn("Error while performing the transform", (Throwable)e);
        }
        catch (TimeoutException e) {
            log.warn("Timed out while doing the transform", (Throwable)e);
        }
        return false;
    }

    private Future<Either<TransformFailure, byte[]>> processTask(ImageFilterTask imageFilterTask) {
        try {
            return (Future)this.taskMap.runOperation((Object)imageFilterTask, () -> this.executorService.submit(imageFilterTask));
        }
        catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean forbiddenAccess(Attachment attachment) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        return !this.permissionManager.hasPermission((User)currentUser, Permission.VIEW, (Object)attachment) && !this.permissionManager.isConfluenceAdministrator((User)currentUser);
    }

    @Nonnull
    private String[] buildEffectsList(HttpServletRequest request, boolean isPreview) {
        String effectsParameter = request.getParameter("effects");
        String effectsProcessed = isPreview ? effectsParameter.replace("tape", "tapeForThumb") : effectsParameter;
        Iterable splitEffects = SPLITTER.split((CharSequence)effectsProcessed);
        ArrayList listEffects = Lists.newArrayList((Iterable)splitEffects);
        return listEffects.toArray(new String[0]);
    }

    private boolean rotationOnly(HttpServletRequest request) {
        String[] effects = this.buildEffectsList(request, false);
        return effects.length == 1 && EXIF_ROTATE_EFFECT.equals(effects[0]);
    }

    private boolean rotationAndThumbnailOnly(HttpServletRequest request) {
        String[] effects = this.buildEffectsList(request, false);
        return effects.length == 2 && EXIF_ROTATE_EFFECT.equals(effects[0]) && THUMBNAIL_EFFECT.equals(effects[1]);
    }

    private static <T> T uncheckAndIgnoreClientAbortExceptions(Exception exception) {
        Throwable rootCause = ExceptionUtils.getRootCause((Throwable)exception);
        if (rootCause instanceof SocketException && "Broken pipe".equals(rootCause.getMessage())) {
            return null;
        }
        throw new RuntimeException(exception);
    }

    private void displayError(ServletResponse resp, String msgKey) throws IOException {
        I18NBean i18nBean = this.i18NBeanFactory.getI18NBean();
        String msg = i18nBean.getText(msgKey);
        Font f = new Font("SansSerif", 1, 16);
        BufferedImage img = new BufferedImage(1, 1, 1);
        FontMetrics fontMetrics = img.getGraphics().getFontMetrics(f);
        Rectangle2D stringBounds = fontMetrics.getStringBounds(msg, img.getGraphics());
        int pad = 5;
        BufferedImage errorImg = new BufferedImage((int)stringBounds.getWidth() + 10, (int)stringBounds.getHeight() + 10, 1);
        Graphics graphics = errorImg.getGraphics();
        graphics.setColor(Color.white);
        graphics.fillRect(0, 0, errorImg.getWidth(), errorImg.getHeight());
        graphics.setFont(f);
        graphics.setColor(Color.black);
        graphics.drawString(msg, 5, errorImg.getHeight() - 5);
        ImageIO.write((RenderedImage)errorImg, "jpg", (OutputStream)resp.getOutputStream());
    }

    public void destroy() {
        if (!this.executorService.isShutdown()) {
            log.debug("ThreadPoolExecutor of ImageEffect is shutdown");
            this.executorService.shutdown();
        }
        super.destroy();
    }
}

