/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.imageeffects.core.exif;

import com.atlassian.imageeffects.core.exif.ExifException;
import com.atlassian.imageeffects.core.exif.ExifInfo;
import com.atlassian.imageeffects.core.exif.ExifService;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

public class ExifServiceImpl
implements ExifService {
    private static final Integer MAXIMUM_META_SIZE = 0x7FFFFFF7;

    @Override
    public ExifInfo readExifInfo(InputStream image) throws ExifException {
        try {
            if (!image.markSupported()) {
                return null;
            }
            image.mark(MAXIMUM_META_SIZE);
            ImageMetadata imageMetadata = Imaging.getMetadata(image, null);
            image.reset();
            if (imageMetadata instanceof JpegImageMetadata) {
                JpegImageMetadata metadata = (JpegImageMetadata)imageMetadata;
                return new ExifInfo(this.readExifProperty(metadata, ExifTagConstants.EXIF_TAG_EXIF_IMAGE_WIDTH), this.readExifProperty(metadata, ExifTagConstants.EXIF_TAG_EXIF_IMAGE_LENGTH), this.readExifProperty(metadata, TiffTagConstants.TIFF_TAG_ORIENTATION));
            }
            return null;
        }
        catch (ImageReadException e) {
            throw new ExifException(e.getMessage(), e);
        }
        catch (IOException e) {
            throw new ExifException(e.getMessage(), e);
        }
    }

    @Override
    public BufferedImage rotate(InputStream image, ExifInfo exifInfo) throws ExifException {
        if (exifInfo.getOrientation() == null) {
            throw new ExifException("The rotation information should not be null!");
        }
        BufferedImage source = null;
        try {
            source = ImageIO.read(image);
        }
        catch (IOException e) {
            throw new ExifException(e.getMessage(), e);
        }
        return this.rotate(source, exifInfo);
    }

    @Override
    public BufferedImage rotate(BufferedImage image, ExifInfo exifInfo) throws ExifException {
        if (exifInfo.getOrientation() == null) {
            throw new ExifException("The rotation information should not be null!");
        }
        AffineTransform transform = this.getExifTransformation(image, exifInfo);
        AffineTransformOp affineTransformOp = new AffineTransformOp(transform, 1);
        return affineTransformOp.filter(image, null);
    }

    private Integer readExifProperty(JpegImageMetadata metadata, TagInfo tagInfo) throws ImageReadException {
        TiffField tiffField = metadata.findEXIFValue(tagInfo);
        return tiffField == null ? null : Integer.valueOf(tiffField.getIntValue());
    }

    private AffineTransform getExifTransformation(BufferedImage bufferedImage, ExifInfo exifInfo) {
        AffineTransform transform = new AffineTransform();
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        switch (exifInfo.getOrientation()) {
            case 1: {
                break;
            }
            case 2: {
                transform.scale(-1.0, 1.0);
                transform.translate(-width, 0.0);
                break;
            }
            case 3: {
                transform.translate(width, height);
                transform.rotate(Math.PI);
                break;
            }
            case 4: {
                transform.scale(1.0, -1.0);
                transform.translate(0.0, -height);
                break;
            }
            case 5: {
                transform.rotate(-1.5707963267948966);
                transform.scale(-1.0, 1.0);
                break;
            }
            case 6: {
                transform.translate(height, 0.0);
                transform.rotate(1.5707963267948966);
                break;
            }
            case 7: {
                transform.scale(-1.0, 1.0);
                transform.translate(-height, 0.0);
                transform.translate(0.0, width);
                transform.rotate(4.71238898038469);
                break;
            }
            case 8: {
                transform.translate(0.0, width);
                transform.rotate(4.71238898038469);
            }
        }
        return transform;
    }
}

