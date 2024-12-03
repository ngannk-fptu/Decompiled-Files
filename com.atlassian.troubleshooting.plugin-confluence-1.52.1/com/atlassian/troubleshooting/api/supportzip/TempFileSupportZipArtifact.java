/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.api.supportzip;

import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;

public class TempFileSupportZipArtifact
implements SupportZipBundle.Artifact {
    private static final int BUFFER_SIZE = 65536;
    private final String targetPath;
    private final File tempFile;

    public TempFileSupportZipArtifact(ReadableByteChannel channel, String fileName, String targetPath) {
        this(fileName, targetPath);
        this.write(channel);
    }

    public TempFileSupportZipArtifact(String content, String fileName, String targetPath) {
        this(fileName, targetPath);
        this.write(content);
    }

    private TempFileSupportZipArtifact(String fileName, String targetPath) {
        this.targetPath = targetPath;
        this.tempFile = TempFileSupportZipArtifact.createTempFile(fileName);
    }

    @Override
    public File getFile() {
        return this.tempFile;
    }

    @Override
    public String getTargetPath() {
        return this.targetPath;
    }

    @Override
    public void close() {
        if (this.tempFile != null) {
            this.tempFile.delete();
        }
    }

    private static File createTempFile(String fileName) {
        try {
            Path tempDir = Files.createTempDirectory(null, new FileAttribute[0]);
            return tempDir.resolve(fileName).toFile();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void write(String text) {
        try (FileWriter writer = new FileWriter(this.tempFile);){
            writer.write(text);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void write(ReadableByteChannel inputChannel) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(65536);
            try (FileChannel tmpFileChannel = FileChannel.open(this.tempFile.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE);){
                while (inputChannel.read(buffer) != -1) {
                    buffer.flip();
                    tmpFileChannel.write(buffer);
                    buffer.compact();
                }
                buffer.flip();
                while (buffer.hasRemaining()) {
                    tmpFileChannel.write(buffer);
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

