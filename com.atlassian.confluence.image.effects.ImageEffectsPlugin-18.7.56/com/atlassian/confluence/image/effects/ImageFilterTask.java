/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.core.util.thumbnail.ThumbnailDimension
 *  com.atlassian.core.util.thumbnail.ThumbnailUtil
 *  com.atlassian.fugue.Either
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.image.effects;

import com.atlassian.confluence.image.effects.ImageFilterUtils;
import com.atlassian.confluence.image.effects.TransformContext;
import com.atlassian.confluence.image.effects.TransformFailure;
import com.atlassian.confluence.image.effects.analytics.ImageProcessingOutcomeEvent;
import com.atlassian.confluence.image.effects.analytics.ImageRotationByExifEvent;
import com.atlassian.confluence.image.effects.analytics.ImageSizeTooLargeEvent;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.core.util.thumbnail.ThumbnailDimension;
import com.atlassian.core.util.thumbnail.ThumbnailUtil;
import com.atlassian.fugue.Either;
import com.atlassian.imageeffects.core.BaseEffect;
import com.atlassian.imageeffects.core.BlurBorderEffect;
import com.atlassian.imageeffects.core.DropShadowEffect;
import com.atlassian.imageeffects.core.GlassEffect;
import com.atlassian.imageeffects.core.MirrorEffect;
import com.atlassian.imageeffects.core.PolaroidBorderEffect;
import com.atlassian.imageeffects.core.RotateEffect;
import com.atlassian.imageeffects.core.ScaleForThumbEffect;
import com.atlassian.imageeffects.core.ShadowKnEffect;
import com.atlassian.imageeffects.core.SimpleBorderEffect;
import com.atlassian.imageeffects.core.TapeForThumbEffect;
import com.atlassian.imageeffects.core.ThumbnailEffect;
import com.atlassian.imageeffects.core.exif.ExifException;
import com.atlassian.imageeffects.core.exif.ExifInfo;
import com.atlassian.imageeffects.core.exif.ExifService;
import com.google.common.collect.ImmutableList;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.Callable;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class ImageFilterTask
implements Callable<Either<TransformFailure, byte[]>> {
    private static final Logger log = LoggerFactory.getLogger(ImageFilterTask.class);
    private static final ImmutableList<BaseEffect> IMAGE_EFFECTS = ImmutableList.of((Object)new ThumbnailEffect("thumbnail"), (Object)new MirrorEffect("mirror"), (Object)new RotateEffect("rotate"), (Object)new SimpleBorderEffect("border-simple"), (Object)new PolaroidBorderEffect("border-polaroid"), (Object)new DropShadowEffect("drop-shadow"), (Object)new ShadowKnEffect("shadow-kn"), (Object)new BlurBorderEffect("blur-border"), (Object)new ScaleForThumbEffect("scaleForThumb"), (Object)new TapeForThumbEffect("tapeForThumb"), (Object)new GlassEffect("glass"));
    private final TransformContext context;
    private final String[] effectList;
    private final ExifService exifService;
    private final SettingsManager settingsManager;

    ImageFilterTask(TransformContext context, String[] effectList, ExifService exifService, SettingsManager settingsManager) {
        this.context = Objects.requireNonNull(context);
        this.effectList = Objects.requireNonNull(effectList);
        this.exifService = exifService;
        this.settingsManager = settingsManager;
    }

    @Override
    public Either<TransformFailure, byte[]> call() throws IOException {
        if (this.isImageDataMissing()) {
            return ImageFilterTask.transformFailure(TransformFailure.Reason.IMAGE_DATA_MISSING);
        }
        if (this.context.isRotationAndThumbnailOnly()) {
            return this.processRotationAndThumbnail();
        }
        if (this.context.getImageDataSize() > (long)this.context.getConfig().getTransformMaxDataSize()) {
            log.debug("Could not process the image as the image size is too big.");
            this.context.getEventPublisher().publish((Object)new ImageSizeTooLargeEvent(this.context.getImageDataSize(), this.context.getCacheEntryName()));
            if (this.context.isRotationOnly()) {
                return this.readOriginalImage();
            }
            return ImageFilterTask.transformFailure(TransformFailure.Reason.IMAGE_DATA_TOO_LARGE);
        }
        return this.processInMemory();
    }

    /*
     * Loose catch block
     * Enabled aggressive exception aggregation
     */
    private Either<TransformFailure, byte[]> processRotationAndThumbnail() {
        try (InputStream originalStream = (InputStream)this.context.getImageSupplier().get();){
            Either<TransformFailure, byte[]> thumbnail;
            ExifInfo exifInfo;
            String format;
            BufferedInputStream bufferedInputStream;
            block22: {
                block21: {
                    bufferedInputStream = new BufferedInputStream(originalStream);
                    format = ImageFilterUtils.getImageFormat(bufferedInputStream);
                    exifInfo = this.exifService.readExifInfo(bufferedInputStream);
                    if (exifInfo != null && exifInfo.getOrientation() != null) break block21;
                    Either<TransformFailure, byte[]> either = this.generateThumbnail(bufferedInputStream);
                    bufferedInputStream.close();
                    return either;
                }
                thumbnail = this.generateThumbnail(bufferedInputStream);
                if (!thumbnail.isLeft()) break block22;
                Either<TransformFailure, byte[]> either = thumbnail;
                bufferedInputStream.close();
                return either;
            }
            long startTime = System.currentTimeMillis();
            BufferedImage rotatedImage = this.exifService.rotate(new ByteArrayInputStream((byte[])thumbnail.right().get()), exifInfo);
            this.context.getEventPublisher().publish((Object)new ImageRotationByExifEvent(this.context.getImageDataSize(), "", this.context.getCacheEntryName(), this.context.isRotationOnly(), this.context.isRotationAndThumbnailOnly(), true, System.currentTimeMillis() - startTime));
            Either either = Either.right((Object)ImageFilterUtils.renderImage(rotatedImage, format));
            bufferedInputStream.close();
            return either;
            {
                catch (ExifException e) {
                    Either<TransformFailure, byte[]> either2;
                    block23: {
                        log.warn("Failure when retrieving exif info.", (Throwable)e);
                        this.context.getEventPublisher().publish((Object)new ImageRotationByExifEvent(this.context.getImageDataSize(), e.getMessage(), this.context.getCacheEntryName(), this.context.isRotationOnly(), this.context.isRotationAndThumbnailOnly(), false, 0L));
                        either2 = this.generateThumbnail(bufferedInputStream);
                        bufferedInputStream.close();
                        if (originalStream == null) break block23;
                        originalStream.close();
                    }
                    return either2;
                    {
                        catch (Throwable throwable) {
                            try {
                                bufferedInputStream.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                            throw throwable;
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            return ImageFilterTask.transformFailure(e);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private Either<TransformFailure, byte[]> generateThumbnail(InputStream inputStream) {
        try (ImageInputStream imageInputStream = ImageIO.createImageInputStream(inputStream);){
            Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInputStream);
            ImageReader imageReader = readers.next();
            if (imageReader == null) {
                Either either2 = Either.left((Object)new TransformFailure(TransformFailure.Reason.TRANSFORM_FAILURE));
                return either2;
            }
            imageReader.setInput(imageInputStream);
            ImageReadParam param = imageReader.getDefaultReadParam();
            int samplingPeriod = this.getSamplingPeriod(imageReader.getWidth(0), imageReader.getHeight(0));
            param.setSourceSubsampling(samplingPeriod, samplingPeriod, 0, 0);
            BufferedImage scaledImage = imageReader.read(0, param);
            Either either = Either.right((Object)ImageFilterUtils.renderImage(scaledImage, imageReader.getFormatName()));
            return either;
        }
        catch (IOException e) {
            return ImageFilterTask.transformFailure(e);
        }
    }

    private int getSamplingPeriod(int imageWidth, int imageHeight) {
        ThumbnailDimension dimensions = ThumbnailUtil.determineScaledDimensions((int)this.settingsManager.getGlobalSettings().getMaxThumbWidth(), (int)this.settingsManager.getGlobalSettings().getMaxThumbHeight(), (int)imageWidth, (int)imageHeight);
        return (int)Math.round((double)imageWidth / (double)dimensions.getWidth());
    }

    private static Either<TransformFailure, byte[]> transformFailure(TransformFailure.Reason reason) {
        return Either.left((Object)new TransformFailure(reason));
    }

    private static Either<TransformFailure, byte[]> transformFailure(Throwable cause) {
        if (cause instanceof RuntimeException && cause.getCause() != null) {
            cause = cause.getCause();
        }
        return Either.left((Object)new TransformFailure(TransformFailure.Reason.TRANSFORM_FAILURE, cause));
    }

    @Nonnull
    private Either<TransformFailure, byte[]> processInMemory() {
        log.debug("Processing transformation in memory");
        try {
            if (this.inMemoryImagePixelTooLarge()) {
                log.debug("Could not process the image as the image size is too big.");
                this.context.getEventPublisher().publish((Object)new ImageSizeTooLargeEvent(this.context.getImageDataSize(), this.context.getCacheEntryName()));
                if (this.context.isRotationOnly()) {
                    return this.readOriginalImage();
                }
                return ImageFilterTask.transformFailure(TransformFailure.Reason.IMAGE_PIXEL_TOO_LARGE);
            }
            return Either.right((Object)this.inMemoryTransform());
        }
        catch (Exception e) {
            return ImageFilterTask.transformFailure(e);
        }
    }

    private Either<TransformFailure, byte[]> readOriginalImage() {
        Either either;
        block8: {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            InputStream inputStream = (InputStream)this.context.getImageSupplier().get();
            try {
                IOUtils.copy((InputStream)inputStream, (OutputStream)outputStream);
                either = Either.right((Object)outputStream.toByteArray());
                if (inputStream == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    return ImageFilterTask.transformFailure(e);
                }
            }
            inputStream.close();
        }
        return either;
    }

    private boolean isImageDataMissing() throws IOException {
        try (InputStream inputStream = (InputStream)this.context.getImageSupplier().get();){
            boolean bl = inputStream == null;
            return bl;
        }
    }

    private boolean inMemoryImagePixelTooLarge() throws IOException {
        try (InputStream originalStream = (InputStream)this.context.getImageSupplier().get();){
            boolean bl = ImageFilterUtils.isImageTooBig(originalStream, this.context.getCacheEntryName());
            return bl;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private BufferedImage rotateWhenExifExist(InputStream inputStream) throws IOException {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);){
            ExifInfo exifInfo = this.exifService.readExifInfo(bufferedInputStream);
            if (exifInfo == null || exifInfo.getOrientation() == null) {
                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream((byte[])this.readOriginalImage().right().get()));
                return bufferedImage;
            }
            long startTime = System.currentTimeMillis();
            BufferedImage rotatedImage = this.exifService.rotate(bufferedInputStream, exifInfo);
            this.context.getEventPublisher().publish((Object)new ImageRotationByExifEvent(this.context.getImageDataSize(), "", this.context.getCacheEntryName(), this.context.isRotationOnly(), this.context.isRotationAndThumbnailOnly(), true, System.currentTimeMillis() - startTime));
            BufferedImage bufferedImage = rotatedImage;
            return bufferedImage;
        }
        catch (ExifException e) {
            log.warn("Could not retrieve exif info.", (Throwable)e);
            this.context.getEventPublisher().publish((Object)new ImageRotationByExifEvent(this.context.getImageDataSize(), e.getMessage(), this.context.getCacheEntryName(), this.context.isRotationOnly(), this.context.isRotationAndThumbnailOnly(), false, 0L));
            return ImageIO.read(new ByteArrayInputStream((byte[])this.readOriginalImage().right().get()));
        }
    }

    /*
     * Enabled aggressive exception aggregation
     */
    @Nonnull
    private byte[] inMemoryTransform() throws IOException {
        long startTime = System.currentTimeMillis();
        try (InputStream originalStream = (InputStream)this.context.getImageSupplier().get();){
            BufferedImage rotatedImage;
            String format;
            BufferedInputStream bufferedInputStream;
            block15: {
                byte[] byArray;
                bufferedInputStream = new BufferedInputStream(originalStream);
                try {
                    format = ImageFilterUtils.getImageFormat(bufferedInputStream);
                    rotatedImage = this.rotateWhenExifExist(bufferedInputStream);
                    if (!this.context.isRotationOnly()) break block15;
                    byArray = ImageFilterUtils.renderImage(rotatedImage, format);
                }
                catch (Throwable throwable) {
                    try {
                        bufferedInputStream.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
                bufferedInputStream.close();
                return byArray;
            }
            ImageFilterUtils.ImageEffectFunction effectApplicator = ImageFilterUtils.imageEffectFunction(this.context.getImageLabel());
            byte[] result = ImageFilterUtils.renderImage(Arrays.stream(this.effectList).flatMap(effectName -> IMAGE_EFFECTS.stream().filter(imageEffect -> imageEffect.handles((String)effectName))).reduce(rotatedImage, effectApplicator, (l, r) -> null), format);
            this.context.getEventPublisher().publish((Object)new ImageProcessingOutcomeEvent(true, true, "n/a", this.context.getCacheEntryName(), System.currentTimeMillis() - startTime));
            byte[] byArray = result;
            bufferedInputStream.close();
            return byArray;
        }
        catch (IOException ioe) {
            this.context.getEventPublisher().publish((Object)new ImageProcessingOutcomeEvent(true, false, "i/o exception", this.context.getCacheEntryName(), System.currentTimeMillis() - startTime));
            throw ioe;
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ImageFilterTask that = (ImageFilterTask)o;
        if (!Objects.equals(this.context, that.context)) {
            return false;
        }
        return Arrays.equals(this.effectList, that.effectList);
    }

    public int hashCode() {
        int result = this.context != null ? this.context.hashCode() : 0;
        result = 31 * result + (this.effectList != null ? Arrays.hashCode(this.effectList) : 0);
        return result;
    }
}

