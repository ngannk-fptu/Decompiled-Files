/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.util;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;

public class FSImageWriter {
    private String imageFormat;
    private float writeCompressionQuality;
    private int writeCompressionMode;
    private String writeCompressionType;
    public static final String DEFAULT_IMAGE_FORMAT = "png";

    public FSImageWriter() {
        this(DEFAULT_IMAGE_FORMAT);
    }

    public FSImageWriter(String imageFormat) {
        this.imageFormat = imageFormat;
        this.writeCompressionMode = 3;
        this.writeCompressionType = null;
        this.writeCompressionQuality = 1.0f;
    }

    public static FSImageWriter newJpegWriter(float quality) {
        FSImageWriter writer = new FSImageWriter("jpg");
        writer.setWriteCompressionMode(2);
        writer.setWriteCompressionType("JPEG");
        writer.setWriteCompressionQuality(quality);
        return writer;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void write(BufferedImage bimg, String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            if (!file.delete()) {
                throw new IOException("File " + filePath + " exists already, and call to .delete() failed unexpectedly");
            }
        } else if (!file.createNewFile()) {
            throw new IOException("Unable to create file at path " + filePath + ", call to .createNewFile() failed unexpectedly.");
        }
        BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(file));
        try {
            this.write(bimg, fos);
        }
        finally {
            try {
                ((OutputStream)fos).close();
            }
            catch (IOException iOException) {}
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void write(BufferedImage bimg, OutputStream os) throws IOException {
        ImageWriter writer = null;
        ImageInputStream ios = null;
        try {
            writer = this.lookupImageWriterForFormat(this.imageFormat);
            ios = ImageIO.createImageOutputStream(os);
            writer.setOutput(ios);
            ImageWriteParam iwparam = this.getImageWriteParameters(writer);
            writer.write(null, new IIOImage(bimg, null, null), iwparam);
        }
        finally {
            if (ios != null) {
                try {
                    ios.flush();
                }
                catch (IOException iOException) {}
                try {
                    ios.close();
                }
                catch (IOException iOException) {}
            }
            if (writer != null) {
                writer.dispose();
            }
        }
    }

    protected ImageWriteParam getImageWriteParameters(ImageWriter writer) {
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed() && this.writeCompressionMode != 3) {
            param.setCompressionMode(this.writeCompressionMode);
            if (this.writeCompressionMode == 2) {
                param.setCompressionType(this.writeCompressionType);
                param.setCompressionQuality(this.writeCompressionQuality);
            }
        }
        return param;
    }

    public void setWriteCompressionQuality(float q) {
        this.writeCompressionQuality = q;
    }

    public void setWriteCompressionMode(int mode) {
        this.writeCompressionMode = mode;
    }

    public void setWriteCompressionType(String type) {
        this.writeCompressionType = type;
    }

    private ImageWriter lookupImageWriterForFormat(String imageFormat) {
        ImageWriter writer = null;
        Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName(imageFormat);
        if (iter.hasNext()) {
            writer = iter.next();
        }
        return writer;
    }
}

