/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.spi;

import com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo;
import javax.imageio.spi.ImageWriterSpi;

public abstract class ImageWriterSpiBase
extends ImageWriterSpi {
    protected ImageWriterSpiBase(ReaderWriterProviderInfo readerWriterProviderInfo) {
        super(readerWriterProviderInfo.getVendorName(), readerWriterProviderInfo.getVersion(), readerWriterProviderInfo.formatNames(), readerWriterProviderInfo.suffixes(), readerWriterProviderInfo.mimeTypes(), readerWriterProviderInfo.writerClassName(), readerWriterProviderInfo.outputTypes(), readerWriterProviderInfo.readerSpiClassNames(), readerWriterProviderInfo.supportsStandardStreamMetadataFormat(), readerWriterProviderInfo.nativeStreamMetadataFormatName(), readerWriterProviderInfo.nativeStreamMetadataFormatClassName(), readerWriterProviderInfo.extraStreamMetadataFormatNames(), readerWriterProviderInfo.extraStreamMetadataFormatClassNames(), readerWriterProviderInfo.supportsStandardImageMetadataFormat(), readerWriterProviderInfo.nativeImageMetadataFormatName(), readerWriterProviderInfo.nativeImageMetadataFormatClassName(), readerWriterProviderInfo.extraImageMetadataFormatNames(), readerWriterProviderInfo.extraImageMetadataFormatClassNames());
    }
}

