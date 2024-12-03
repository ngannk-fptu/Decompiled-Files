/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.annotations.ExperimentalSpi
 *  javax.annotation.WillNotClose
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.dc.filestore.api;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.dc.filestore.api.DataSize;
import com.atlassian.dc.filestore.api.Snapshot;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.WillNotClose;
import org.slf4j.LoggerFactory;

@ExperimentalApi
@ExperimentalSpi
public interface FileStore {
    public Path root();

    default public Path path(String ... pathComponents) {
        return this.root().path(pathComponents);
    }

    default public Optional<DataSize> getAvailableSpace() {
        return Optional.empty();
    }

    default public Optional<DataSize> getTotalSpace() {
        return Optional.empty();
    }

    @FunctionalInterface
    public static interface InputStreamSupplier {
        public InputStream get() throws IOException;
    }

    @FunctionalInterface
    public static interface OutputStreamWriter {
        public void writeTo(@WillNotClose OutputStream var1) throws IOException;
    }

    @FunctionalInterface
    public static interface InputStreamExtractor<T> {
        public T extract(@WillNotClose InputStream var1) throws IOException;
    }

    @FunctionalInterface
    public static interface InputStreamConsumer {
        public void consume(@WillNotClose InputStream var1) throws IOException;
    }

    @FunctionalInterface
    public static interface Writer {
        public void write(@WillNotClose InputStream var1) throws IOException;

        default public void write(InputStreamSupplier inputStreamSupplier) throws IOException {
            try (InputStream inputStream = inputStreamSupplier.get();){
                this.write(inputStream);
            }
        }

        default public void write(byte[] data) throws IOException {
            this.write(new ByteArrayInputStream(data));
        }

        default public void write(OutputStreamWriter bufferWriter) throws IOException {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            bufferWriter.writeTo(buffer);
            this.write(buffer.toByteArray());
        }
    }

    @FunctionalInterface
    public static interface Reader {
        public InputStream openInputStream() throws IOException;

        default public void consume(InputStreamConsumer inputStreamConsumer) throws IOException {
            try (InputStream inputStream = this.openInputStream();){
                inputStreamConsumer.consume(inputStream);
            }
        }

        default public <T> T read(InputStreamExtractor<T> inputStreamExtractor) throws IOException {
            try (InputStream inputStream = this.openInputStream();){
                T t = inputStreamExtractor.extract(inputStream);
                return t;
            }
        }
    }

    public static interface Path {
        public Path path(String ... var1);

        public boolean fileExists() throws IOException;

        public boolean exists() throws IOException;

        default public boolean tryFileExists() {
            try {
                return this.fileExists();
            }
            catch (IOException ex) {
                LoggerFactory.getLogger(this.getClass()).warn("Failed to determine if file {} exists; assuming it does not", (Object)this, (Object)ex);
                return false;
            }
        }

        public Reader fileReader();

        public Writer fileWriter();

        public void deleteFile() throws IOException;

        default public boolean tryDeleteFile() {
            try {
                this.deleteFile();
                return true;
            }
            catch (FileNotFoundException ex) {
                LoggerFactory.getLogger(this.getClass()).debug("Cannot delete non-existent file {}", (Object)this, (Object)ex);
                return false;
            }
            catch (IOException ex) {
                LoggerFactory.getLogger(this.getClass()).warn("Failed to delete file {}", (Object)this, (Object)ex);
                return false;
            }
        }

        default public void moveFile(Path toFile) throws IOException {
            this.copyFile(toFile);
            this.deleteFile();
        }

        default public void copyFile(Path toFile) throws IOException {
            toFile.fileWriter().write(outputStream -> this.fileReader().consume(inputStream -> {
                int length;
                byte[] buffer = new byte[8192];
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }));
        }

        default public void copyFiles(Path toPath) throws IOException {
            try (Snapshot snapshot = this.snapshot();){
                toPath.unpack(snapshot);
            }
        }

        default public DataSize getFileSize() throws IOException {
            return this.fileReader().read(inputStream -> {
                int count = 0;
                while (inputStream.read() != -1) {
                    ++count;
                }
                return DataSize.ofBytes(count);
            });
        }

        public String getPathName();

        public Optional<String> getLeafName();

        public Stream<? extends Path> getFileDescendents() throws IOException;

        default public Snapshot snapshot() throws IOException {
            throw new UnsupportedOperationException("Snapshots not supported");
        }

        default public void unpack(Snapshot snapshot) throws IOException {
            throw new UnsupportedOperationException("Snapshots not supported");
        }
    }
}

