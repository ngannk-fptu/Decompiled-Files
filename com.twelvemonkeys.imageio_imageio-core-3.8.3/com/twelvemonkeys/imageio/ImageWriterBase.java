/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio;

import com.twelvemonkeys.imageio.util.IIOUtil;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

public abstract class ImageWriterBase
extends ImageWriter {
    protected ImageOutputStream imageOutput;

    protected ImageWriterBase(ImageWriterSpi imageWriterSpi) {
        super(imageWriterSpi);
    }

    public String getFormatName() throws IOException {
        return this.getOriginatingProvider().getFormatNames()[0];
    }

    @Override
    public void setOutput(Object object) {
        this.resetMembers();
        super.setOutput(object);
        this.imageOutput = object instanceof ImageOutputStream ? (ImageOutputStream)object : null;
    }

    protected void assertOutput() {
        if (this.getOutput() == null) {
            throw new IllegalStateException("getOutput() == null");
        }
    }

    @Override
    public void dispose() {
        this.resetMembers();
        super.dispose();
    }

    @Override
    public void reset() {
        this.resetMembers();
        super.reset();
    }

    protected void resetMembers() {
    }

    @Override
    public IIOMetadata getDefaultStreamMetadata(ImageWriteParam imageWriteParam) {
        return null;
    }

    @Override
    public IIOMetadata convertStreamMetadata(IIOMetadata iIOMetadata, ImageWriteParam imageWriteParam) {
        return null;
    }

    protected static Rectangle getSourceRegion(ImageWriteParam imageWriteParam, int n, int n2) {
        return IIOUtil.getSourceRegion(imageWriteParam, n, n2);
    }

    protected static BufferedImage fakeAOI(BufferedImage bufferedImage, ImageWriteParam imageWriteParam) {
        return IIOUtil.fakeAOI(bufferedImage, ImageWriterBase.getSourceRegion(imageWriteParam, bufferedImage.getWidth(), bufferedImage.getHeight()));
    }

    protected static Image fakeSubsampling(Image image, ImageWriteParam imageWriteParam) {
        return IIOUtil.fakeSubsampling(image, imageWriteParam);
    }
}

