/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.spi;

import com.twelvemonkeys.imageio.spi.ProviderInfo;
import com.twelvemonkeys.lang.Validate;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

public abstract class ReaderWriterProviderInfo
extends ProviderInfo {
    private final String[] formatNames;
    private final String[] suffixes;
    private final String[] mimeTypes;
    private final String readerClassName;
    private final String[] readerSpiClassNames;
    private final Class<?>[] inputTypes = new Class[]{ImageInputStream.class};
    private final String writerClassName;
    private final String[] writerSpiClassNames;
    private final Class<?>[] outputTypes = new Class[]{ImageOutputStream.class};
    private final boolean supportsStandardStreamMetadata;
    private final String nativeStreamMetadataFormatName;
    private final String nativeStreamMetadataFormatClassName;
    private final String[] extraStreamMetadataFormatNames;
    private final String[] extraStreamMetadataFormatClassNames;
    private final boolean supportsStandardImageMetadata;
    private final String nativeImageMetadataFormatName;
    private final String nativeImageMetadataFormatClassName;
    private final String[] extraImageMetadataFormatNames;
    private final String[] extraImageMetadataFormatClassNames;

    protected ReaderWriterProviderInfo(Class<? extends ReaderWriterProviderInfo> clazz, String[] stringArray, String[] stringArray2, String[] stringArray3, String string, String[] stringArray4, String string2, String[] stringArray5, boolean bl, String string3, String string4, String[] stringArray6, String[] stringArray7, boolean bl2, String string5, String string6, String[] stringArray8, String[] stringArray9) {
        super(((Class)Validate.notNull(clazz)).getPackage());
        this.formatNames = stringArray;
        this.suffixes = stringArray2;
        this.mimeTypes = stringArray3;
        this.readerClassName = string;
        this.readerSpiClassNames = stringArray4;
        this.writerClassName = string2;
        this.writerSpiClassNames = stringArray5;
        this.supportsStandardStreamMetadata = bl;
        this.nativeStreamMetadataFormatName = string3;
        this.nativeStreamMetadataFormatClassName = string4;
        this.extraStreamMetadataFormatNames = stringArray6;
        this.extraStreamMetadataFormatClassNames = stringArray7;
        this.supportsStandardImageMetadata = bl2;
        this.nativeImageMetadataFormatName = string5;
        this.nativeImageMetadataFormatClassName = string6;
        this.extraImageMetadataFormatNames = stringArray8;
        this.extraImageMetadataFormatClassNames = stringArray9;
    }

    public String[] formatNames() {
        return this.formatNames;
    }

    public String[] suffixes() {
        return this.suffixes;
    }

    public String[] mimeTypes() {
        return this.mimeTypes;
    }

    public String readerClassName() {
        return this.readerClassName;
    }

    public String[] readerSpiClassNames() {
        return this.readerSpiClassNames;
    }

    public Class[] inputTypes() {
        return this.inputTypes;
    }

    public String writerClassName() {
        return this.writerClassName;
    }

    public String[] writerSpiClassNames() {
        return this.writerSpiClassNames;
    }

    public Class[] outputTypes() {
        return this.outputTypes;
    }

    public boolean supportsStandardStreamMetadataFormat() {
        return this.supportsStandardStreamMetadata;
    }

    public String nativeStreamMetadataFormatName() {
        return this.nativeStreamMetadataFormatName;
    }

    public String nativeStreamMetadataFormatClassName() {
        return this.nativeStreamMetadataFormatClassName;
    }

    public String[] extraStreamMetadataFormatNames() {
        return this.extraStreamMetadataFormatNames;
    }

    public String[] extraStreamMetadataFormatClassNames() {
        return this.extraStreamMetadataFormatClassNames;
    }

    public boolean supportsStandardImageMetadataFormat() {
        return this.supportsStandardImageMetadata;
    }

    public String nativeImageMetadataFormatName() {
        return this.nativeImageMetadataFormatName;
    }

    public String nativeImageMetadataFormatClassName() {
        return this.nativeImageMetadataFormatClassName;
    }

    public String[] extraImageMetadataFormatNames() {
        return this.extraImageMetadataFormatNames;
    }

    public String[] extraImageMetadataFormatClassNames() {
        return this.extraImageMetadataFormatClassNames;
    }
}

