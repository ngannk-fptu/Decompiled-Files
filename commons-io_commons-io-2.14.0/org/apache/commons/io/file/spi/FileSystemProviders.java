/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io.file.spi;

import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FileSystemProviders {
    private static final FileSystemProviders INSTALLED = new FileSystemProviders(FileSystemProvider.installedProviders());
    private final List<FileSystemProvider> providers;

    public static FileSystemProvider getFileSystemProvider(Path path) {
        return Objects.requireNonNull(path, "path").getFileSystem().provider();
    }

    public static FileSystemProviders installed() {
        return INSTALLED;
    }

    private FileSystemProviders(List<FileSystemProvider> providers) {
        this.providers = providers != null ? providers : Collections.emptyList();
    }

    public FileSystemProvider getFileSystemProvider(String scheme) {
        Objects.requireNonNull(scheme, "scheme");
        if (scheme.equalsIgnoreCase("file")) {
            return FileSystems.getDefault().provider();
        }
        return this.providers.stream().filter(provider -> provider.getScheme().equalsIgnoreCase(scheme)).findFirst().orElse(null);
    }

    public FileSystemProvider getFileSystemProvider(URI uri) {
        return this.getFileSystemProvider(Objects.requireNonNull(uri, "uri").getScheme());
    }

    public FileSystemProvider getFileSystemProvider(URL url) {
        return this.getFileSystemProvider(Objects.requireNonNull(url, "url").getProtocol());
    }
}

