/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.core.util.FileUtils
 *  com.atlassian.core.util.ReusableBufferedInputStream
 *  com.atlassian.core.util.thumbnail.Thumber
 *  com.atlassian.core.util.thumbnail.Thumbnail$MimeType
 *  com.atlassian.core.util.thumbnail.ThumbnailDimension
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.RandomStringUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.impl.pages.thumbnail.renderer.AdaptiveThresholdPredicate;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.core.util.FileUtils;
import com.atlassian.core.util.ReusableBufferedInputStream;
import com.atlassian.core.util.thumbnail.Thumber;
import com.atlassian.core.util.thumbnail.Thumbnail;
import com.atlassian.core.util.thumbnail.ThumbnailDimension;
import com.google.common.base.Preconditions;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;
import java.util.function.Predicate;
import javax.imageio.ImageIO;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

public class TemporaryUploadedPicture
implements Serializable {
    private static final int MAX_WIDTH = 1024;
    private static final int MAX_HEIGHT = 768;
    private String originalFileName;
    private int sourceWidth;
    private int sourceHeight;
    private String thumbnailFileName;
    private int thumbnailWidth;
    private int thumbnailHeight;

    public static @Nullable TemporaryUploadedPicture getPicture(File sourceFile, String fileName, String owningUserName) throws IOException {
        return TemporaryUploadedPicture.getPicture(new FileInputStream(sourceFile), fileName, owningUserName);
    }

    public static @Nullable TemporaryUploadedPicture getPicture(InputStream source, String fileName, String owningUserName) throws IOException {
        String confluenceTempDirectory = BootstrapUtils.getBootstrapManager().getFilePathProperty("struts.multipart.saveDir");
        String prefix = TemporaryUploadedPicture.generatePrefix(owningUserName);
        fileName = TemporaryUploadedPicture.simplify(fileName);
        String imageFileName = prefix + "pp-" + fileName;
        File image = new File(confluenceTempDirectory, imageFileName);
        FileUtils.copyFile((InputStream)source, (File)image);
        image.deleteOnExit();
        String resizedImageFileName = prefix + "pp-t-" + fileName;
        File resizedImage = new File(confluenceTempDirectory, resizedImageFileName);
        return TemporaryUploadedPicture.getBean(image, resizedImage);
    }

    private static String simplify(String fileName) {
        if (!StringUtils.isBlank((CharSequence)fileName)) {
            return fileName;
        }
        return new BigInteger(40, new Random()).toString(32);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static TemporaryUploadedPicture getBean(File image, File resizedImage) throws IOException {
        Thumber thumber = new Thumber(Thumbnail.MimeType.PNG);
        ReusableBufferedInputStream reusableInputStream = new ReusableBufferedInputStream((InputStream)new FileInputStream(image));
        BufferedImage sourceImage = null;
        try {
            sourceImage = thumber.getImage(reusableInputStream, (Predicate)((Object)AdaptiveThresholdPredicate.createInputStreamPredicate()));
        }
        finally {
            try {
                reusableInputStream.destroy();
            }
            catch (IOException iOException) {}
        }
        if (sourceImage == null) {
            image.delete();
            return null;
        }
        int sourceWidth = ((Image)sourceImage).getWidth(null);
        int sourceHeight = ((Image)sourceImage).getHeight(null);
        if (sourceHeight > 768 || sourceWidth > 1024) {
            ThumbnailDimension dimensions = thumber.determineScaleSize(1024, 768, sourceWidth, sourceHeight);
            int thumbnailWidth = dimensions.getWidth();
            int thumbnailHeight = dimensions.getHeight();
            BufferedImage scaledImage = thumber.scaleImage((Image)sourceImage, dimensions);
            ImageIO.write((RenderedImage)scaledImage, "png", resizedImage);
            resizedImage.deleteOnExit();
            return new TemporaryUploadedPicture(image.getAbsolutePath(), sourceWidth, sourceHeight, resizedImage.getAbsolutePath(), thumbnailWidth, thumbnailHeight);
        }
        return new TemporaryUploadedPicture(image.getAbsolutePath(), sourceWidth, sourceHeight, image.getAbsolutePath(), sourceWidth, sourceHeight);
    }

    public TemporaryUploadedPicture(String originalFileName, int sourceWidth, int sourceHeight, String thumbnailFileName, int thumbnailWidth, int thumbnailHeight) {
        this.originalFileName = (String)Preconditions.checkNotNull((Object)originalFileName);
        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
        this.thumbnailFileName = (String)Preconditions.checkNotNull((Object)thumbnailFileName);
        this.thumbnailWidth = thumbnailWidth;
        this.thumbnailHeight = thumbnailHeight;
    }

    public File getOriginalFile() {
        return new File(this.originalFileName);
    }

    public File getThumbnailFile() {
        return new File(this.thumbnailFileName);
    }

    public String getThumbnailFileDownloadPath() {
        return "/download/temp/" + this.getThumbnailFile().getName();
    }

    public String getThumbnailFileDownloadUrl() {
        return "/download/temp/" + HtmlUtil.urlEncode(this.getThumbnailFile().getName());
    }

    public int getThumbnailWidth() {
        return this.thumbnailWidth;
    }

    public int getThumbnailHeight() {
        return this.thumbnailHeight;
    }

    public int getSourceWidth() {
        return this.sourceWidth;
    }

    public int getSourceHeight() {
        return this.sourceHeight;
    }

    public String getOriginalFileName() {
        return new File(this.originalFileName).getName();
    }

    public void cleanup() {
        this.getOriginalFile().delete();
        this.getThumbnailFile().delete();
    }

    private static String generatePrefix(String owningUserName) {
        return HtmlUtil.urlEncode(owningUserName) + "-" + RandomStringUtils.randomNumeric((int)6) + "-";
    }
}

