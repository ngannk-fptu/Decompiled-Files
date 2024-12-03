/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.NoSuchDirectoryException;

public class FileSwitchDirectory
extends Directory {
    private final Directory secondaryDir;
    private final Directory primaryDir;
    private final Set<String> primaryExtensions;
    private boolean doClose;

    public FileSwitchDirectory(Set<String> primaryExtensions, Directory primaryDir, Directory secondaryDir, boolean doClose) {
        this.primaryExtensions = primaryExtensions;
        this.primaryDir = primaryDir;
        this.secondaryDir = secondaryDir;
        this.doClose = doClose;
        this.lockFactory = primaryDir.getLockFactory();
    }

    public Directory getPrimaryDir() {
        return this.primaryDir;
    }

    public Directory getSecondaryDir() {
        return this.secondaryDir;
    }

    @Override
    public void close() throws IOException {
        if (this.doClose) {
            try {
                this.secondaryDir.close();
            }
            finally {
                this.primaryDir.close();
            }
            this.doClose = false;
        }
    }

    @Override
    public String[] listAll() throws IOException {
        NoSuchDirectoryException exc;
        HashSet<String> files;
        block8: {
            files = new HashSet<String>();
            exc = null;
            try {
                for (String f : this.primaryDir.listAll()) {
                    files.add(f);
                }
            }
            catch (NoSuchDirectoryException e) {
                exc = e;
            }
            try {
                for (String f : this.secondaryDir.listAll()) {
                    files.add(f);
                }
            }
            catch (NoSuchDirectoryException e) {
                if (exc != null) {
                    throw exc;
                }
                if (!files.isEmpty()) break block8;
                throw e;
            }
        }
        if (exc != null && files.isEmpty()) {
            throw exc;
        }
        return files.toArray(new String[files.size()]);
    }

    public static String getExtension(String name) {
        int i = name.lastIndexOf(46);
        if (i == -1) {
            return "";
        }
        return name.substring(i + 1, name.length());
    }

    private Directory getDirectory(String name) {
        String ext = FileSwitchDirectory.getExtension(name);
        if (this.primaryExtensions.contains(ext)) {
            return this.primaryDir;
        }
        return this.secondaryDir;
    }

    @Override
    public boolean fileExists(String name) throws IOException {
        return this.getDirectory(name).fileExists(name);
    }

    @Override
    public void deleteFile(String name) throws IOException {
        this.getDirectory(name).deleteFile(name);
    }

    @Override
    public long fileLength(String name) throws IOException {
        return this.getDirectory(name).fileLength(name);
    }

    @Override
    public IndexOutput createOutput(String name, IOContext context) throws IOException {
        return this.getDirectory(name).createOutput(name, context);
    }

    @Override
    public void sync(Collection<String> names) throws IOException {
        ArrayList<String> primaryNames = new ArrayList<String>();
        ArrayList<String> secondaryNames = new ArrayList<String>();
        for (String name : names) {
            if (this.primaryExtensions.contains(FileSwitchDirectory.getExtension(name))) {
                primaryNames.add(name);
                continue;
            }
            secondaryNames.add(name);
        }
        this.primaryDir.sync(primaryNames);
        this.secondaryDir.sync(secondaryNames);
    }

    @Override
    public IndexInput openInput(String name, IOContext context) throws IOException {
        return this.getDirectory(name).openInput(name, context);
    }

    @Override
    public Directory.IndexInputSlicer createSlicer(String name, IOContext context) throws IOException {
        return this.getDirectory(name).createSlicer(name, context);
    }
}

