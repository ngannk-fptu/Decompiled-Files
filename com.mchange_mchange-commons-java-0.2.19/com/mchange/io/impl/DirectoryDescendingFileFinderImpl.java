/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.io.impl;

import com.mchange.io.FileEnumeration;
import com.mchange.io.IOEnumeration;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Stack;

public class DirectoryDescendingFileFinderImpl
implements IOEnumeration,
FileEnumeration {
    private static final Object dummy = new Object();
    Hashtable markedDirex = new Hashtable();
    Stack direx = new Stack();
    Stack files = new Stack();
    FilenameFilter filter;
    boolean canonical;

    public DirectoryDescendingFileFinderImpl(File file, FilenameFilter filenameFilter, boolean bl) throws IOException {
        if (!file.isDirectory()) {
            throw new IllegalArgumentException(file.getName() + " is not a directory.");
        }
        this.filter = filenameFilter;
        this.canonical = bl;
        this.blossomDirectory(file);
        while (this.files.empty() && !this.direx.empty()) {
            this.blossomDirectory((File)this.direx.pop());
        }
    }

    public DirectoryDescendingFileFinderImpl(File file) throws IOException {
        this(file, null, false);
    }

    @Override
    public boolean hasMoreFiles() {
        return !this.files.empty();
    }

    @Override
    public File nextFile() throws IOException {
        if (this.files.empty()) {
            throw new NoSuchElementException();
        }
        File file = (File)this.files.pop();
        while (this.files.empty() && !this.direx.empty()) {
            this.blossomDirectory((File)this.direx.pop());
        }
        return file;
    }

    @Override
    public boolean hasMoreElements() {
        return this.hasMoreFiles();
    }

    @Override
    public Object nextElement() throws IOException {
        return this.nextFile();
    }

    private void blossomDirectory(File file) throws IOException {
        String string = file.getCanonicalPath();
        String[] stringArray = this.filter == null ? file.list() : file.list(this.filter);
        int n = stringArray.length;
        while (--n >= 0) {
            if (this.filter != null && !this.filter.accept(file, stringArray[n])) continue;
            String string2 = (this.canonical ? string : file.getPath()) + File.separator + stringArray[n];
            File file2 = new File(string2);
            if (file2.isFile()) {
                this.files.push(file2);
                continue;
            }
            if (this.markedDirex.containsKey(file2.getCanonicalPath())) continue;
            this.direx.push(file2);
        }
        this.markedDirex.put(string, dummy);
    }

    public static void main(String[] stringArray) {
        try {
            File file = new File(stringArray[0]);
            DirectoryDescendingFileFinderImpl directoryDescendingFileFinderImpl = new DirectoryDescendingFileFinderImpl(file);
            while (directoryDescendingFileFinderImpl.hasMoreFiles()) {
                System.out.println(directoryDescendingFileFinderImpl.nextFile().getAbsolutePath());
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

