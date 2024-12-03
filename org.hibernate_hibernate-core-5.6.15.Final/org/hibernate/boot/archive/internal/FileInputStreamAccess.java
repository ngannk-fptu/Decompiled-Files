/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.internal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.hibernate.HibernateException;
import org.hibernate.boot.archive.spi.ArchiveException;
import org.hibernate.boot.archive.spi.InputStreamAccess;

public class FileInputStreamAccess
implements InputStreamAccess {
    private final String name;
    private final File file;

    public FileInputStreamAccess(String name, File file) {
        this.name = name;
        this.file = file;
        if (!file.exists()) {
            throw new HibernateException("File must exist : " + file.getAbsolutePath());
        }
    }

    @Override
    public String getStreamName() {
        return this.name;
    }

    @Override
    public InputStream accessInputStream() {
        try {
            return new BufferedInputStream(new FileInputStream(this.file));
        }
        catch (FileNotFoundException e) {
            throw new ArchiveException("File believed to exist based on File.exists threw error when passed to FileInputStream ctor", e);
        }
    }
}

