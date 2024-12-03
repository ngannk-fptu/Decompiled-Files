/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.tiff;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormatImpl;

@Deprecated
public final class TIFFMedataFormat
extends IIOMetadataFormatImpl {
    private static final TIFFMedataFormat INSTANCE = new TIFFMedataFormat();
    public static final String SUN_NATIVE_IMAGE_METADATA_FORMAT_NAME = "com_sun_media_imageio_plugins_tiff_image_1.0";

    public TIFFMedataFormat() {
        super(SUN_NATIVE_IMAGE_METADATA_FORMAT_NAME, 2);
    }

    @Override
    public boolean canNodeAppear(String string, ImageTypeSpecifier imageTypeSpecifier) {
        return true;
    }

    public static TIFFMedataFormat getInstance() {
        return INSTANCE;
    }
}

