/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.tiff;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public final class TIFFImageMetadataFormat
extends IIOMetadataFormatImpl {
    private static final TIFFImageMetadataFormat INSTANCE = new TIFFImageMetadataFormat();
    public static final String SUN_NATIVE_IMAGE_METADATA_FORMAT_NAME = "com_sun_media_imageio_plugins_tiff_image_1.0";

    public TIFFImageMetadataFormat() {
        super(SUN_NATIVE_IMAGE_METADATA_FORMAT_NAME, 2);
    }

    @Override
    public boolean canNodeAppear(String string, ImageTypeSpecifier imageTypeSpecifier) {
        return true;
    }

    public static TIFFImageMetadataFormat getInstance() {
        return INSTANCE;
    }
}

