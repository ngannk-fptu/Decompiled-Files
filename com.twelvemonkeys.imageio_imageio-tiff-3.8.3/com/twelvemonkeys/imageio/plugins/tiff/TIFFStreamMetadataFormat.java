/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.tiff;

import java.nio.ByteOrder;
import java.util.Arrays;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public final class TIFFStreamMetadataFormat
extends IIOMetadataFormatImpl {
    private static final TIFFStreamMetadataFormat INSTANCE = new TIFFStreamMetadataFormat();
    public static final String SUN_NATIVE_STREAM_METADATA_FORMAT_NAME = "com_sun_media_imageio_plugins_tiff_stream_1.0";

    private TIFFStreamMetadataFormat() {
        super(SUN_NATIVE_STREAM_METADATA_FORMAT_NAME, 1);
        this.addElement("ByteOrder", SUN_NATIVE_STREAM_METADATA_FORMAT_NAME, 0);
        this.addAttribute("ByteOrder", "value", 0, true, ByteOrder.BIG_ENDIAN.toString(), Arrays.asList(ByteOrder.BIG_ENDIAN.toString(), ByteOrder.LITTLE_ENDIAN.toString()));
    }

    @Override
    public boolean canNodeAppear(String string, ImageTypeSpecifier imageTypeSpecifier) {
        return true;
    }

    public static TIFFStreamMetadataFormat getInstance() {
        return INSTANCE;
    }
}

