/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.spi.ImageReaderSpiBase
 *  com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo
 *  com.twelvemonkeys.imageio.util.IIOUtil
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader;
import com.twelvemonkeys.imageio.plugins.jpeg.JPEGProviderInfo;
import com.twelvemonkeys.imageio.spi.ImageReaderSpiBase;
import com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo;
import com.twelvemonkeys.imageio.util.IIOUtil;
import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.spi.IIOServiceProvider;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;

public final class JPEGImageReaderSpi
extends ImageReaderSpiBase {
    protected ImageReaderSpi delegateProvider;

    public JPEGImageReaderSpi() {
        this(new JPEGProviderInfo());
    }

    JPEGImageReaderSpi(ImageReaderSpi imageReaderSpi) {
        this();
        this.delegateProvider = (ImageReaderSpi)Validate.notNull((Object)imageReaderSpi);
    }

    private JPEGImageReaderSpi(ReaderWriterProviderInfo readerWriterProviderInfo) {
        super(readerWriterProviderInfo);
    }

    public void onRegistration(ServiceRegistry serviceRegistry, Class<?> clazz) {
        if (this.delegateProvider == null) {
            this.delegateProvider = (ImageReaderSpi)IIOUtil.lookupProviderByName((ServiceRegistry)serviceRegistry, (String)"com.sun.imageio.plugins.jpeg.JPEGImageReaderSpi", ImageReaderSpi.class);
        }
        if (this.delegateProvider != null) {
            serviceRegistry.setOrdering(clazz, this, this.delegateProvider);
        } else {
            IIOUtil.deregisterProvider((ServiceRegistry)serviceRegistry, (IIOServiceProvider)((Object)this), clazz);
        }
    }

    public String getVendorName() {
        return String.format("%s/%s", super.getVendorName(), this.delegateProvider.getVendorName());
    }

    public String getVersion() {
        return String.format("%s/%s", super.getVersion(), this.delegateProvider.getVersion());
    }

    public ImageReader createReaderInstance(Object object) throws IOException {
        return new JPEGImageReader((ImageReaderSpi)((Object)this), this.delegateProvider.createReaderInstance(object));
    }

    public boolean canDecodeInput(Object object) throws IOException {
        return this.delegateProvider.canDecodeInput(object);
    }

    public boolean isStandardStreamMetadataFormatSupported() {
        return this.delegateProvider.isStandardStreamMetadataFormatSupported();
    }

    public String getNativeStreamMetadataFormatName() {
        return this.delegateProvider.getNativeStreamMetadataFormatName();
    }

    public String[] getExtraStreamMetadataFormatNames() {
        return this.delegateProvider.getExtraStreamMetadataFormatNames();
    }

    public boolean isStandardImageMetadataFormatSupported() {
        return this.delegateProvider.isStandardImageMetadataFormatSupported();
    }

    public String getNativeImageMetadataFormatName() {
        return this.delegateProvider.getNativeImageMetadataFormatName();
    }

    public String[] getExtraImageMetadataFormatNames() {
        return this.delegateProvider.getExtraImageMetadataFormatNames();
    }

    public IIOMetadataFormat getStreamMetadataFormat(String string) {
        return this.delegateProvider.getStreamMetadataFormat(string);
    }

    public IIOMetadataFormat getImageMetadataFormat(String string) {
        return this.delegateProvider.getImageMetadataFormat(string);
    }

    public String getDescription(Locale locale) {
        return this.delegateProvider.getDescription(locale);
    }

    public Class[] getInputTypes() {
        return this.delegateProvider.getInputTypes();
    }
}

