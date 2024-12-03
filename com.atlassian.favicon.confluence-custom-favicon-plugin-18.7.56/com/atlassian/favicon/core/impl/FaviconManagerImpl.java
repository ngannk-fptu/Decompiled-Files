/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.thumbnail.Thumber
 *  com.atlassian.core.util.thumbnail.ThumbnailDimension
 *  com.atlassian.favicon.core.Constants
 *  com.atlassian.favicon.core.Favicon
 *  com.atlassian.favicon.core.FaviconManager
 *  com.atlassian.favicon.core.FaviconSize
 *  com.atlassian.favicon.core.FaviconStore
 *  com.atlassian.favicon.core.ImageType
 *  com.atlassian.favicon.core.StoredFavicon
 *  com.atlassian.favicon.core.UploadedFaviconFile
 *  com.atlassian.favicon.core.exceptions.ImageStorageException
 *  com.atlassian.favicon.core.exceptions.InvalidImageDataException
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  net.sf.image4j.codec.ico.ICODecoder
 *  net.sf.image4j.codec.ico.ICOEncoder
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.favicon.core.impl;

import com.atlassian.core.util.thumbnail.Thumber;
import com.atlassian.core.util.thumbnail.ThumbnailDimension;
import com.atlassian.favicon.core.Constants;
import com.atlassian.favicon.core.Favicon;
import com.atlassian.favicon.core.FaviconManager;
import com.atlassian.favicon.core.FaviconSize;
import com.atlassian.favicon.core.FaviconStore;
import com.atlassian.favicon.core.ImageType;
import com.atlassian.favicon.core.StoredFavicon;
import com.atlassian.favicon.core.UploadedFaviconFile;
import com.atlassian.favicon.core.exceptions.ImageStorageException;
import com.atlassian.favicon.core.exceptions.InvalidImageDataException;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.imageio.ImageIO;
import net.sf.image4j.codec.ico.ICODecoder;
import net.sf.image4j.codec.ico.ICOEncoder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={FaviconManager.class})
public class FaviconManagerImpl
implements FaviconManager {
    public static final List<ThumbnailDimension> DESIRED_SIZES = Collections.unmodifiableList(new ArrayList(FaviconSize.STANDARD_FAVICON_SIZES.values()));
    private static final String FAVICON_PNG_FORMAT = "png";
    private static final long MAX_IMAGE_DIMENSION = 500L;
    private final FaviconStore faviconStore;
    private final PluginSettings pluginSettings;

    @Autowired
    public FaviconManagerImpl(FaviconStore faviconStore, @ComponentImport PluginSettingsFactory pluginSettingsFactory) {
        this.faviconStore = faviconStore;
        this.pluginSettings = pluginSettingsFactory.createGlobalSettings();
    }

    public Optional<StoredFavicon> getFavicon(ImageType anImageType, ThumbnailDimension aDesiredSize) {
        Objects.requireNonNull(anImageType);
        Objects.requireNonNull(aDesiredSize);
        if (this.isFaviconConfigured() && this.isSupportedMediaTypeForDownload(anImageType)) {
            return this.faviconStore.getFavicon(anImageType, aDesiredSize);
        }
        return Optional.empty();
    }

    private boolean isSupportedMediaTypeForDownload(ImageType anImageType) {
        return ImageType.ICO == anImageType || ImageType.PNG == anImageType;
    }

    public boolean isFaviconConfigured() {
        Object setting = this.pluginSettings.get("com.atlassian.favicon:usingCustomFavicon");
        if (setting != null) {
            return Boolean.parseBoolean(setting.toString());
        }
        return false;
    }

    public void setFavicon(UploadedFaviconFile anImage) throws InvalidImageDataException, ImageStorageException {
        Objects.requireNonNull(anImage);
        List<BufferedImage> sourceImages = this.getAndValidateImages(anImage);
        List<BufferedImage> scaledImages = this.generateScaledImages(sourceImages);
        try {
            this.saveImages(scaledImages);
            this.saveImagesAsICO(scaledImages);
        }
        catch (IOException e) {
            throw new ImageStorageException((Throwable)e);
        }
        this.setFaviconFlags(true);
    }

    public void resetFavicon() {
        this.setFaviconFlags(false);
    }

    public void setFaviconFlags(boolean isUsingCustomFavicon) {
        this.pluginSettings.put("com.atlassian.favicon:usingCustomFavicon", (Object)Boolean.toString(isUsingCustomFavicon));
        this.faviconStore.notifyChangedFavicon();
    }

    private List<BufferedImage> getAndValidateImages(UploadedFaviconFile anImageFile) throws InvalidImageDataException {
        List<BufferedImage> images;
        try {
            if (anImageFile.getContentType() == ImageType.ICO) {
                images = ICODecoder.read((File)anImageFile.getFile());
            } else {
                BufferedImage image = this.checkImage(ImageIO.read(anImageFile.getFile()));
                images = Collections.singletonList(image);
            }
        }
        catch (IOException e) {
            throw new InvalidImageDataException((Throwable)e);
        }
        return images;
    }

    private BufferedImage checkImage(@Nullable BufferedImage image) throws InvalidImageDataException {
        if (image == null) {
            throw new InvalidImageDataException();
        }
        return image;
    }

    private List<BufferedImage> generateScaledImages(List<BufferedImage> anOriginalImageList) {
        ArrayList<BufferedImage> scaledImages = new ArrayList<BufferedImage>();
        for (BufferedImage image : anOriginalImageList) {
            if ((long)image.getHeight() > 500L || (long)image.getWidth() > 500L) continue;
            scaledImages.add(image);
        }
        BufferedImage largestImage = Collections.max(anOriginalImageList, Comparator.comparing(BufferedImage::getHeight));
        Iterator<ThumbnailDimension> iterator = DESIRED_SIZES.iterator();
        while (iterator.hasNext()) {
            ThumbnailDimension s;
            ThumbnailDimension size = s = iterator.next();
            Optional<BufferedImage> existingImage = scaledImages.stream().filter(anImage -> anImage.getHeight() == size.getHeight() && anImage.getWidth() == size.getWidth()).findFirst();
            if (existingImage.isPresent()) continue;
            BufferedImage scaledImage = new Thumber().scaleImage((Image)largestImage, size);
            scaledImages.add(scaledImage);
        }
        return scaledImages;
    }

    private void saveImages(List<BufferedImage> aListOfImages) throws IOException {
        for (BufferedImage image : aListOfImages) {
            this.saveImage(image);
        }
    }

    private void saveImage(BufferedImage anImage) throws IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();){
            ImageIO.write((RenderedImage)anImage, FAVICON_PNG_FORMAT, os);
            Favicon favicon = new Favicon(os.toByteArray(), FaviconSize.fromWidthAndHeight((int)anImage.getWidth(), (int)anImage.getHeight()), ImageType.PNG);
            this.faviconStore.saveFavicon(favicon);
        }
    }

    private void saveImagesAsICO(List<BufferedImage> aListOfImages) throws IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();){
            ICOEncoder.write(aListOfImages, (OutputStream)os);
            Favicon favicon = new Favicon(os.toByteArray(), Constants.DEFAULT_DIMENSION, ImageType.ICO);
            this.faviconStore.saveFavicon(favicon);
        }
    }
}

