/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.DataSize
 *  com.atlassian.dc.filestore.api.FileStore$Path
 *  com.atlassian.dc.filestore.api.FileStore$Reader
 *  com.atlassian.dc.filestore.api.FileStore$Writer
 *  software.amazon.awssdk.services.s3.model.ListObjectsV2Response
 *  software.amazon.awssdk.services.s3.model.NoSuchKeyException
 *  software.amazon.awssdk.services.s3.model.S3Object
 */
package com.atlassian.dc.filestore.impl.s3;

import com.atlassian.dc.filestore.api.DataSize;
import com.atlassian.dc.filestore.api.FileStore;
import com.atlassian.dc.filestore.impl.s3.OperationExecutor;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Object;

final class S3Path
implements FileStore.Path {
    private static final String PATH_SEPARATOR = "/";
    private final String bucketName;
    private final Deque<String> pathComponents;
    private final OperationExecutor executor;

    S3Path(String bucketName, Deque<String> pathComponents, OperationExecutor executor) {
        this.bucketName = Objects.requireNonNull(bucketName);
        this.pathComponents = Objects.requireNonNull(pathComponents);
        this.executor = Objects.requireNonNull(executor);
    }

    private S3Path withS3Key(String s3Key) {
        return new S3Path(this.bucketName, new LinkedList<String>(Arrays.asList(s3Key.split(PATH_SEPARATOR))), this.executor);
    }

    String getS3Key() {
        return String.join((CharSequence)PATH_SEPARATOR, this.pathComponents);
    }

    public FileStore.Path path(String ... pathComponents) {
        return new S3Path(this.bucketName, S3Path.append(this.pathComponents, pathComponents), this.executor);
    }

    private static Deque<String> append(Collection<String> base, String ... appendComponents) {
        LinkedList<String> newComponents = new LinkedList<String>(base);
        newComponents.addAll(Arrays.asList(appendComponents));
        return newComponents;
    }

    public boolean fileExists() throws IOException {
        return this.executor.performOperation(s3Client -> {
            try {
                s3Client.headObject(requestBuilder -> requestBuilder.key(this.getS3Key()).bucket(this.bucketName));
                return true;
            }
            catch (NoSuchKeyException noSuchKeyException) {
                return false;
            }
        });
    }

    public boolean exists() throws IOException {
        ListObjectsV2Response result = this.executor.performOperation(s3Client -> s3Client.listObjectsV2(requestBuilder -> requestBuilder.bucket(this.bucketName).prefix(this.getS3Key()).maxKeys(Integer.valueOf(1))));
        return result.contents().stream().map(S3Object::key).findAny().isPresent();
    }

    public FileStore.Reader fileReader() {
        return () -> this.executor.performOperation(s3Client -> s3Client.getObject(requestBuilder -> requestBuilder.key(this.getS3Key()).bucket(this.bucketName)));
    }

    public FileStore.Writer fileWriter() {
        return this::put;
    }

    public void deleteFile() throws IOException {
        this.executor.performOperation(s3Client -> s3Client.deleteObject(requestBuilder -> requestBuilder.key(this.getS3Key()).bucket(this.bucketName)));
    }

    public DataSize getFileSize() throws IOException {
        long lengthInBytes = this.executor.performOperation(s3Client -> s3Client.headObject(builder -> builder.key(this.getS3Key()).bucket(this.bucketName)).contentLength());
        return DataSize.ofBytes((long)lengthInBytes);
    }

    public String getPathName() {
        return this.getS3Key();
    }

    public Optional<String> getLeafName() {
        return Optional.of(this.pathComponents).filter(p -> !p.isEmpty()).map(Deque::getLast);
    }

    public Stream<FileStore.Path> getFileDescendents() throws IOException {
        String searchPrefix = this.getS3Key() + (this.pathComponents.size() > 0 && !this.getS3Key().endsWith(PATH_SEPARATOR) ? PATH_SEPARATOR : "");
        ListObjectsV2Response response = this.executor.performOperation(s3Client -> s3Client.listObjectsV2(builder -> builder.bucket(this.bucketName).prefix(searchPrefix)));
        return response.contents().stream().map(S3Object::key).map(this::withS3Key);
    }

    private void put(InputStream source) throws IOException {
        Path tempFile = Files.createTempFile(S3Path.class.getSimpleName(), null, new FileAttribute[0]);
        try {
            Files.copy(source, tempFile, StandardCopyOption.REPLACE_EXISTING);
            this.executor.performOperation(s3Client -> s3Client.putObject(builder -> builder.bucket(this.bucketName).key(this.getS3Key()), tempFile));
        }
        finally {
            Files.delete(tempFile);
        }
    }
}

