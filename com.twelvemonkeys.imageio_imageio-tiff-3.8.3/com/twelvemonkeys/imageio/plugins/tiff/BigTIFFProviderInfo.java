/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo
 */
package com.twelvemonkeys.imageio.plugins.tiff;

import com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo;

final class BigTIFFProviderInfo
extends ReaderWriterProviderInfo {
    protected BigTIFFProviderInfo() {
        super(BigTIFFProviderInfo.class, new String[]{"bigtiff", "BigTIFF", "BIGTIFF"}, new String[]{"tif", "tiff", "btf", "tf8", "btiff"}, new String[]{"image/tiff", "image/x-tiff"}, "com.twelvemonkeys.imageio.plugins.tiff.TIFFImageReader", new String[]{"com.twelvemonkeys.imageio.plugins.tiff.BigTIFFImageReaderSpi"}, "com.twelvemonkeys.imageio.plugins.tiff.TIFFImageWriter", new String[]{"com.twelvemonkeys.imageio.plugins.tiff.BigTIFFImageWriterSpi"}, false, "com_sun_media_imageio_plugins_tiff_stream_1.0", "com.twelvemonkeys.imageio.plugins.tiff.TIFFStreamMetadataFormat", null, null, true, "com_sun_media_imageio_plugins_tiff_image_1.0", "com.twelvemonkeys.imageio.plugins.tiff.TIFFMedataFormat", null, null);
    }
}

