/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.lucene.analysis.util.ClasspathResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoader;

public final class FilesystemResourceLoader
implements ResourceLoader {
    private final File baseDirectory;
    private final ResourceLoader delegate;

    public FilesystemResourceLoader() {
        this(null);
    }

    public FilesystemResourceLoader(File baseDirectory) {
        this(baseDirectory, new ClasspathResourceLoader());
    }

    public FilesystemResourceLoader(File baseDirectory, ResourceLoader delegate) {
        if (baseDirectory != null && !baseDirectory.isDirectory()) {
            throw new IllegalArgumentException("baseDirectory is not a directory or null");
        }
        if (delegate == null) {
            throw new IllegalArgumentException("delegate ResourceLoader may not be null");
        }
        this.baseDirectory = baseDirectory;
        this.delegate = delegate;
    }

    @Override
    public InputStream openResource(String resource) throws IOException {
        try {
            File file = new File(resource);
            if (this.baseDirectory != null && !file.isAbsolute()) {
                file = new File(this.baseDirectory, resource);
            }
            return new FileInputStream(file);
        }
        catch (FileNotFoundException fnfe) {
            return this.delegate.openResource(resource);
        }
    }

    @Override
    public <T> T newInstance(String cname, Class<T> expectedType) {
        return this.delegate.newInstance(cname, expectedType);
    }

    @Override
    public <T> Class<? extends T> findClass(String cname, Class<T> expectedType) {
        return this.delegate.findClass(cname, expectedType);
    }
}

