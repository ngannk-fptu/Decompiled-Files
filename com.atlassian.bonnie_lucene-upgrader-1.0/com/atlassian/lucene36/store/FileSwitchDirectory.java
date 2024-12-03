/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.store.IndexOutput;
import com.atlassian.lucene36.store.NoSuchDirectoryException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws IOException {
        if (this.doClose) {
            try {
                this.secondaryDir.close();
                Object var2_1 = null;
            }
            catch (Throwable throwable) {
                Object var2_2 = null;
                this.primaryDir.close();
                throw throwable;
            }
            this.primaryDir.close();
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
    public long fileModified(String name) throws IOException {
        return this.getDirectory(name).fileModified(name);
    }

    @Override
    @Deprecated
    public void touchFile(String name) throws IOException {
        this.getDirectory(name).touchFile(name);
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
    public IndexOutput createOutput(String name) throws IOException {
        return this.getDirectory(name).createOutput(name);
    }

    @Override
    @Deprecated
    public void sync(String name) throws IOException {
        this.sync(Collections.singleton(name));
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
    public IndexInput openInput(String name) throws IOException {
        return this.getDirectory(name).openInput(name);
    }
}

