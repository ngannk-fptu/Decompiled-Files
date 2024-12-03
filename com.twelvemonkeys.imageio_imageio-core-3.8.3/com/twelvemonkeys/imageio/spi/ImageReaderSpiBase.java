/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.spi;

import com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo;
import javax.imageio.spi.ImageReaderSpi;

public abstract class ImageReaderSpiBase
extends ImageReaderSpi {
    protected ImageReaderSpiBase(ReaderWriterProviderInfo readerWriterProviderInfo) {
        super(readerWriterProviderInfo.getVendorName(), readerWriterProviderInfo.getVersion(), readerWriterProviderInfo.formatNames(), readerWriterProviderInfo.suffixes(), readerWriterProviderInfo.mimeTypes(), readerWriterProviderInfo.readerClassName(), readerWriterProviderInfo.inputTypes(), readerWriterProviderInfo.writerSpiClassNames(), readerWriterProviderInfo.supportsStandardStreamMetadataFormat(), readerWriterProviderInfo.nativeStreamMetadataFormatName(), readerWriterProviderInfo.nativeStreamMetadataFormatClassName(), readerWriterProviderInfo.extraStreamMetadataFormatNames(), readerWriterProviderInfo.extraStreamMetadataFormatClassNames(), readerWriterProviderInfo.supportsStandardImageMetadataFormat(), readerWriterProviderInfo.nativeImageMetadataFormatName(), readerWriterProviderInfo.nativeImageMetadataFormatClassName(), readerWriterProviderInfo.extraImageMetadataFormatNames(), readerWriterProviderInfo.extraImageMetadataFormatClassNames());
    }
}

