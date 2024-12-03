/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.dailysummary.content;

import com.atlassian.confluence.plugins.dailysummary.content.ImageDataSource;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import javax.imageio.ImageIO;

public class RenderedImageDataSource
implements ImageDataSource {
    private String imageName;
    private RenderedImage image;
    private String outputFormat;

    public RenderedImageDataSource(String imageName, RenderedImage image, String outputFormat) {
        this.imageName = imageName;
        this.image = image;
        this.outputFormat = outputFormat;
    }

    public String getContentType() {
        return "image/" + this.outputFormat.toLowerCase(Locale.ENGLISH);
    }

    public InputStream getInputStream() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(this.image, this.outputFormat.toUpperCase(Locale.ENGLISH), outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public String getName() {
        return this.imageName;
    }

    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getWidth() {
        return this.image.getWidth();
    }

    @Override
    public int getHeight() {
        return this.image.getHeight();
    }
}

