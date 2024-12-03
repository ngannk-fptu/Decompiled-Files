/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImagingException;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegPhotoshopMetadata;
import org.apache.commons.imaging.formats.tiff.JpegImageData;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageData;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.apache.commons.imaging.internal.Debug;

public class JpegImageMetadata
implements ImageMetadata {
    private final JpegPhotoshopMetadata photoshop;
    private final TiffImageMetadata exif;
    private static final String NEWLINE = System.getProperty("line.separator");

    public JpegImageMetadata(JpegPhotoshopMetadata photoshop, TiffImageMetadata exif) {
        this.photoshop = photoshop;
        this.exif = exif;
    }

    public TiffImageMetadata getExif() {
        return this.exif;
    }

    public JpegPhotoshopMetadata getPhotoshop() {
        return this.photoshop;
    }

    public TiffField findEXIFValue(TagInfo tagInfo) {
        try {
            return this.exif != null ? this.exif.findField(tagInfo) : null;
        }
        catch (ImageReadException cannotHappen) {
            return null;
        }
    }

    public TiffField findEXIFValueWithExactMatch(TagInfo tagInfo) {
        try {
            return this.exif != null ? this.exif.findField(tagInfo, true) : null;
        }
        catch (ImageReadException cannotHappen) {
            return null;
        }
    }

    public Dimension getEXIFThumbnailSize() throws ImageReadException, IOException {
        byte[] data = this.getEXIFThumbnailData();
        if (data != null) {
            return Imaging.getImageSize(data);
        }
        return null;
    }

    public byte[] getEXIFThumbnailData() throws ImageReadException, IOException {
        if (this.exif == null) {
            return null;
        }
        List<? extends ImageMetadata.ImageMetadataItem> dirs = this.exif.getDirectories();
        for (ImageMetadata.ImageMetadataItem imageMetadataItem : dirs) {
            TiffImageMetadata.Directory dir = (TiffImageMetadata.Directory)imageMetadataItem;
            byte[] data = null;
            if (dir.getJpegImageData() != null) {
                data = dir.getJpegImageData().getData();
            }
            if (data == null) continue;
            return data;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public BufferedImage getEXIFThumbnail() throws ImageReadException, IOException {
        if (this.exif == null) {
            return null;
        }
        List<? extends ImageMetadata.ImageMetadataItem> dirs = this.exif.getDirectories();
        for (ImageMetadata.ImageMetadataItem imageMetadataItem : dirs) {
            TiffImageMetadata.Directory dir = (TiffImageMetadata.Directory)imageMetadataItem;
            BufferedImage image = dir.getThumbnail();
            if (null != image) {
                return image;
            }
            JpegImageData jpegImageData = dir.getJpegImageData();
            if (jpegImageData == null) continue;
            boolean imageSucceeded = false;
            try {
                image = Imaging.getBufferedImage(jpegImageData.getData());
                imageSucceeded = true;
            }
            catch (ImagingException input) {
            }
            catch (IOException input) {
            }
            finally {
                if (!imageSucceeded) {
                    ByteArrayInputStream input = new ByteArrayInputStream(jpegImageData.getData());
                    image = ImageIO.read(input);
                }
            }
            if (image == null) continue;
            return image;
        }
        return null;
    }

    public TiffImageData getRawImageData() {
        if (this.exif == null) {
            return null;
        }
        List<? extends ImageMetadata.ImageMetadataItem> dirs = this.exif.getDirectories();
        for (ImageMetadata.ImageMetadataItem imageMetadataItem : dirs) {
            TiffImageMetadata.Directory dir = (TiffImageMetadata.Directory)imageMetadataItem;
            TiffImageData rawImageData = dir.getTiffImageData();
            if (null == rawImageData) continue;
            return rawImageData;
        }
        return null;
    }

    public List<ImageMetadata.ImageMetadataItem> getItems() {
        ArrayList<ImageMetadata.ImageMetadataItem> result = new ArrayList<ImageMetadata.ImageMetadataItem>();
        if (null != this.exif) {
            result.addAll(this.exif.getItems());
        }
        if (null != this.photoshop) {
            result.addAll(this.photoshop.getItems());
        }
        return result;
    }

    public String toString() {
        return this.toString(null);
    }

    @Override
    public String toString(String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        StringBuilder result = new StringBuilder();
        result.append(prefix);
        if (null == this.exif) {
            result.append("No Exif metadata.");
        } else {
            result.append("Exif metadata:");
            result.append(NEWLINE);
            result.append(this.exif.toString("\t"));
        }
        result.append(NEWLINE);
        result.append(prefix);
        if (null == this.photoshop) {
            result.append("No Photoshop (IPTC) metadata.");
        } else {
            result.append("Photoshop (IPTC) metadata:");
            result.append(NEWLINE);
            result.append(this.photoshop.toString("\t"));
        }
        return result.toString();
    }

    public void dump() {
        Debug.debug(this.toString());
    }
}

