/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.codec.png;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.stream.ImageInputStream;
import org.apache.xmlgraphics.image.codec.png.PNGDecodeParam;
import org.apache.xmlgraphics.image.codec.png.PNGImage;
import org.apache.xmlgraphics.image.codec.util.ImageDecodeParam;
import org.apache.xmlgraphics.image.codec.util.ImageDecoderImpl;
import org.apache.xmlgraphics.image.codec.util.ImageInputStreamSeekableStreamAdapter;
import org.apache.xmlgraphics.image.codec.util.PropertyUtil;
import org.apache.xmlgraphics.image.loader.ImageSize;

public class PNGImageDecoder
extends ImageDecoderImpl {
    public PNGImageDecoder(InputStream input, PNGDecodeParam param) {
        super(input, (ImageDecodeParam)param);
    }

    @Override
    public RenderedImage decodeAsRenderedImage(int page) throws IOException {
        if (page != 0) {
            throw new IOException(PropertyUtil.getString("PNGImageDecoder19"));
        }
        return new PNGImage(this.input, (PNGDecodeParam)this.param);
    }

    public static void readPNGHeader(ImageInputStream inputStream, ImageSize size) throws IOException {
        ImageInputStreamSeekableStreamAdapter seekStream = new ImageInputStreamSeekableStreamAdapter(inputStream){

            @Override
            public void close() throws IOException {
            }
        };
        PNGImage pngImage = new PNGImage(seekStream);
        size.setSizeInPixels(pngImage.getWidth(), pngImage.getHeight());
        double dpiHorz = size.getDpiHorizontal();
        double dpiVert = size.getDpiVertical();
        if (pngImage.unitSpecifier == 1) {
            if (pngImage.xPixelsPerUnit != 0) {
                dpiHorz = (double)pngImage.xPixelsPerUnit * 0.0254;
            }
            if (pngImage.yPixelsPerUnit != 0) {
                dpiVert = (double)pngImage.yPixelsPerUnit * 0.0254;
            }
        }
        size.setResolution(dpiHorz, dpiVert);
        size.calcSizeFromPixels();
    }
}

