/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.SandboxSerializer
 *  com.atlassian.confluence.util.sandbox.SandboxSerializers
 */
package com.atlassian.plugins.conversion.sandbox.thumbnail;

import com.atlassian.confluence.util.sandbox.SandboxSerializer;
import com.atlassian.confluence.util.sandbox.SandboxSerializers;
import com.atlassian.plugins.conversion.convert.FileFormat;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SandboxThumbnailRequest {
    private final File inputFile;
    private final FileFormat inFileFormat;
    private final File tempFile;
    private final File convertedFile;
    private final int pageNumber;
    private final int width;
    private final int height;

    public SandboxThumbnailRequest(File inputFile, FileFormat inFileFormat, File tempFile, File convertedFile, int pageNumber, int width, int height) {
        this.inputFile = Objects.requireNonNull(inputFile);
        this.inFileFormat = Objects.requireNonNull(inFileFormat);
        this.tempFile = Objects.requireNonNull(tempFile);
        this.convertedFile = Objects.requireNonNull(convertedFile);
        this.pageNumber = pageNumber;
        this.width = width;
        this.height = height;
    }

    public File getInputFile() {
        return this.inputFile;
    }

    public FileFormat getFileFormat() {
        return this.inFileFormat;
    }

    public File getTempFile() {
        return this.tempFile;
    }

    public File getConvertedFile() {
        return this.convertedFile;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    static Serializer serializer() {
        return Serializer.instance;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SandboxThumbnailRequest that = (SandboxThumbnailRequest)o;
        return this.pageNumber == that.pageNumber && this.width == that.width && this.height == that.height && Objects.equals(this.inputFile.getAbsolutePath(), that.inputFile.getAbsolutePath()) && this.inFileFormat == that.inFileFormat && Objects.equals(this.tempFile.getAbsolutePath(), that.tempFile.getAbsolutePath()) && Objects.equals(this.convertedFile.getAbsolutePath(), that.convertedFile.getAbsolutePath());
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.inputFile, this.inFileFormat, this.tempFile, this.convertedFile, this.pageNumber, this.width, this.height});
    }

    static final class Serializer
    implements SandboxSerializer<SandboxThumbnailRequest> {
        static final Serializer instance = new Serializer();

        private Serializer() {
        }

        public byte[] serialize(SandboxThumbnailRequest sandboxThumbnailRequest) {
            ArrayList<byte[]> fields = new ArrayList<byte[]>();
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)sandboxThumbnailRequest.getInputFile().getAbsolutePath()));
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)sandboxThumbnailRequest.getFileFormat().name()));
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)sandboxThumbnailRequest.getTempFile().getAbsolutePath()));
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)sandboxThumbnailRequest.getConvertedFile().getAbsolutePath()));
            fields.add(SandboxSerializers.intSerializer().serialize((Object)sandboxThumbnailRequest.getPageNumber()));
            fields.add(SandboxSerializers.intSerializer().serialize((Object)sandboxThumbnailRequest.getWidth()));
            fields.add(SandboxSerializers.intSerializer().serialize((Object)sandboxThumbnailRequest.getHeight()));
            return SandboxSerializers.compositeByteArraySerializer().serialize(fields);
        }

        public SandboxThumbnailRequest deserialize(byte[] bytes) {
            List fields = (List)SandboxSerializers.compositeByteArraySerializer().deserialize(bytes);
            return new SandboxThumbnailRequest(new File((String)SandboxSerializers.stringSerializer().deserialize((byte[])fields.get(0))), FileFormat.valueOf((String)SandboxSerializers.stringSerializer().deserialize((byte[])fields.get(1))), new File((String)SandboxSerializers.stringSerializer().deserialize((byte[])fields.get(2))), new File((String)SandboxSerializers.stringSerializer().deserialize((byte[])fields.get(3))), (Integer)SandboxSerializers.intSerializer().deserialize((byte[])fields.get(4)), (Integer)SandboxSerializers.intSerializer().deserialize((byte[])fields.get(5)), (Integer)SandboxSerializers.intSerializer().deserialize((byte[])fields.get(6)));
        }
    }
}

