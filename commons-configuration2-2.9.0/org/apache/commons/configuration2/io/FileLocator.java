/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.io;

import java.net.URL;
import java.util.Objects;
import org.apache.commons.configuration2.io.FileLocationStrategy;
import org.apache.commons.configuration2.io.FileSystem;
import org.apache.commons.configuration2.io.URLConnectionOptions;

public final class FileLocator {
    private final String basePath;
    private final String encoding;
    private final String fileName;
    private final FileSystem fileSystem;
    private final FileLocationStrategy locationStrategy;
    private final URL sourceURL;
    private final URLConnectionOptions urlConnectionOptions;

    public FileLocator(FileLocatorBuilder builder) {
        this.fileName = builder.fileName;
        this.basePath = builder.basePath;
        this.sourceURL = builder.sourceURL;
        this.urlConnectionOptions = builder.urlConnectionOptions;
        this.encoding = builder.encoding;
        this.fileSystem = builder.fileSystem;
        this.locationStrategy = builder.locationStrategy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FileLocator)) {
            return false;
        }
        FileLocator other = (FileLocator)obj;
        return Objects.equals(this.basePath, other.basePath) && Objects.equals(this.encoding, other.encoding) && Objects.equals(this.fileName, other.fileName) && Objects.equals(this.fileSystem, other.fileSystem) && Objects.equals(this.locationStrategy, other.locationStrategy) && Objects.equals(this.sourceURL, other.sourceURL) && Objects.equals(this.urlConnectionOptions, other.urlConnectionOptions);
    }

    public String getBasePath() {
        return this.basePath;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public String getFileName() {
        return this.fileName;
    }

    public FileSystem getFileSystem() {
        return this.fileSystem;
    }

    public FileLocationStrategy getLocationStrategy() {
        return this.locationStrategy;
    }

    public URL getSourceURL() {
        return this.sourceURL;
    }

    public URLConnectionOptions getURLConnectionOptions() {
        return this.urlConnectionOptions;
    }

    public int hashCode() {
        return Objects.hash(this.basePath, this.encoding, this.fileName, this.fileSystem, this.locationStrategy, this.sourceURL, this.urlConnectionOptions);
    }

    public String toString() {
        return "FileLocator [basePath=" + this.basePath + ", encoding=" + this.encoding + ", fileName=" + this.fileName + ", fileSystem=" + this.fileSystem + ", locationStrategy=" + this.locationStrategy + ", sourceURL=" + this.sourceURL + ", urlConnectionOptions=" + this.urlConnectionOptions + "]";
    }

    public static final class FileLocatorBuilder {
        private String basePath;
        private String encoding;
        private String fileName;
        private FileSystem fileSystem;
        private FileLocationStrategy locationStrategy;
        private URL sourceURL;
        private URLConnectionOptions urlConnectionOptions;

        FileLocatorBuilder(FileLocator src) {
            if (src != null) {
                this.initBuilder(src);
            }
        }

        public FileLocatorBuilder basePath(String path) {
            this.basePath = path;
            return this;
        }

        public FileLocator create() {
            return new FileLocator(this);
        }

        public FileLocatorBuilder encoding(String enc) {
            this.encoding = enc;
            return this;
        }

        public FileLocatorBuilder fileName(String name) {
            this.fileName = name;
            return this;
        }

        public FileLocatorBuilder fileSystem(FileSystem fs) {
            this.fileSystem = fs;
            return this;
        }

        private void initBuilder(FileLocator src) {
            this.basePath = src.getBasePath();
            this.fileName = src.getFileName();
            this.sourceURL = src.getSourceURL();
            this.urlConnectionOptions = src.getURLConnectionOptions();
            this.encoding = src.getEncoding();
            this.fileSystem = src.getFileSystem();
            this.locationStrategy = src.getLocationStrategy();
        }

        public FileLocatorBuilder locationStrategy(FileLocationStrategy strategy) {
            this.locationStrategy = strategy;
            return this;
        }

        public FileLocatorBuilder sourceURL(URL url) {
            this.sourceURL = url;
            return this;
        }

        public FileLocatorBuilder urlConnectionOptions(URLConnectionOptions urlConnectionOptions) {
            this.urlConnectionOptions = urlConnectionOptions;
            return this;
        }
    }
}

