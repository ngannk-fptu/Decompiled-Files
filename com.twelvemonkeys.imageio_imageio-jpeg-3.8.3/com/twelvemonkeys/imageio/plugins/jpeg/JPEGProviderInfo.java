/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo;

final class JPEGProviderInfo
extends ReaderWriterProviderInfo {
    JPEGProviderInfo() {
        super(JPEGProviderInfo.class, new String[]{"JPEG", "jpeg", "JPG", "jpg", "JPEG-LOSSLESS", "jpeg-lossless"}, new String[]{"jpg", "jpeg"}, new String[]{"image/jpeg"}, "com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader", new String[]{"com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReaderSpi"}, "com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageWriter", new String[]{"com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageWriterSpi"}, false, "javax_imageio_jpeg_stream_1.0", null, null, null, true, "javax_imageio_jpeg_image_1.0", null, null, null);
    }
}

