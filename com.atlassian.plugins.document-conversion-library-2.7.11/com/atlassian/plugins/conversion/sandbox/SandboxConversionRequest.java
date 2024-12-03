/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.SandboxSerializer
 *  com.atlassian.confluence.util.sandbox.SandboxSerializers
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.plugins.conversion.sandbox;

import com.atlassian.confluence.util.sandbox.SandboxSerializer;
import com.atlassian.confluence.util.sandbox.SandboxSerializers;
import com.atlassian.plugins.conversion.convert.FileFormat;
import com.atlassian.plugins.conversion.sandbox.SandboxConversionType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SandboxConversionRequest {
    private final File inputFile;
    private final FileFormat fileFormat;
    private final File tempFile;
    private final File convertedFile;
    private final File errorFile;
    private final SandboxConversionType conversionType;
    private final String username;
    private final String filename;

    public SandboxConversionRequest(File inputFile, FileFormat fileFormat, File tempFile, File convertedFile, File errorFile, SandboxConversionType conversionType, @Nullable String username, @Nullable String filename) {
        this.inputFile = Objects.requireNonNull(inputFile);
        this.fileFormat = Objects.requireNonNull(fileFormat);
        this.tempFile = Objects.requireNonNull(tempFile);
        this.convertedFile = Objects.requireNonNull(convertedFile);
        this.errorFile = Objects.requireNonNull(errorFile);
        this.conversionType = Objects.requireNonNull(conversionType);
        this.username = username == null ? "" : username;
        this.filename = filename == null ? "" : filename;
    }

    public File getInputFile() {
        return this.inputFile;
    }

    public FileFormat getFileFormat() {
        return this.fileFormat;
    }

    public File getTempFile() {
        return this.tempFile;
    }

    public File getConvertedFile() {
        return this.convertedFile;
    }

    public File getErrorFile() {
        return this.errorFile;
    }

    public SandboxConversionType getConversionType() {
        return this.conversionType;
    }

    public String getUsername() {
        return this.username;
    }

    public String getFilename() {
        return this.filename;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SandboxConversionRequest)) {
            return false;
        }
        SandboxConversionRequest that = (SandboxConversionRequest)o;
        return Objects.equals(this.convertedFile.getAbsolutePath(), that.convertedFile.getAbsolutePath()) && Objects.equals((Object)this.fileFormat, (Object)that.fileFormat) && Objects.equals((Object)this.conversionType, (Object)that.conversionType);
    }

    public int hashCode() {
        return Objects.hash(this.inputFile.getAbsolutePath());
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("convertedFile", (Object)this.convertedFile.getAbsolutePath()).append("conversionType", (Object)this.conversionType).toString();
    }

    static Serializer serializer() {
        return Serializer.instance;
    }

    static final class Serializer
    implements SandboxSerializer<SandboxConversionRequest> {
        static final Serializer instance = new Serializer();

        private Serializer() {
        }

        public byte[] serialize(SandboxConversionRequest conversionRequest) {
            ArrayList<byte[]> fields = new ArrayList<byte[]>();
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)conversionRequest.getInputFile().getAbsolutePath()));
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)conversionRequest.getFileFormat().name()));
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)conversionRequest.getTempFile().getAbsolutePath()));
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)conversionRequest.getConvertedFile().getAbsolutePath()));
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)conversionRequest.getErrorFile().getAbsolutePath()));
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)conversionRequest.getConversionType().name()));
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)conversionRequest.getUsername()));
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)conversionRequest.getFilename()));
            return SandboxSerializers.compositeByteArraySerializer().serialize(fields);
        }

        public SandboxConversionRequest deserialize(byte[] bytes) {
            List fields = (List)SandboxSerializers.compositeByteArraySerializer().deserialize(bytes);
            return new SandboxConversionRequest(new File((String)SandboxSerializers.stringSerializer().deserialize((byte[])fields.get(0))), FileFormat.valueOf((String)SandboxSerializers.stringSerializer().deserialize((byte[])fields.get(1))), new File((String)SandboxSerializers.stringSerializer().deserialize((byte[])fields.get(2))), new File((String)SandboxSerializers.stringSerializer().deserialize((byte[])fields.get(3))), new File((String)SandboxSerializers.stringSerializer().deserialize((byte[])fields.get(4))), SandboxConversionType.valueOf((String)SandboxSerializers.stringSerializer().deserialize((byte[])fields.get(5))), (String)SandboxSerializers.stringSerializer().deserialize((byte[])fields.get(6)), (String)SandboxSerializers.stringSerializer().deserialize((byte[])fields.get(7)));
        }
    }
}

