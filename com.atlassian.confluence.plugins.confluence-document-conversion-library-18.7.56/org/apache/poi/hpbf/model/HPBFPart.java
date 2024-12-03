/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.UnsynchronizedByteArrayInputStream
 */
package org.apache.poi.hpbf.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.util.IOUtils;

public abstract class HPBFPart {
    private byte[] data;
    private final String[] path;

    public HPBFPart(DirectoryNode baseDir, String[] path) throws IOException {
        this.path = path;
        DirectoryNode dir = HPBFPart.getDir(baseDir, path);
        String name = path[path.length - 1];
        if (!dir.hasEntry(name)) {
            throw new IllegalArgumentException("File invalid - failed to find document entry '" + name + "'");
        }
        try (DocumentInputStream is = dir.createDocumentInputStream(name);){
            this.data = IOUtils.toByteArray(is);
        }
    }

    private static DirectoryNode getDir(DirectoryNode baseDir, String[] path) {
        DirectoryNode dir = baseDir;
        for (int i = 0; i < path.length - 1; ++i) {
            try {
                dir = (DirectoryNode)dir.getEntry(path[i]);
                continue;
            }
            catch (FileNotFoundException e) {
                throw new IllegalArgumentException("File invalid - failed to find directory entry '" + path[i] + "': " + e);
            }
        }
        return dir;
    }

    public void writeOut(DirectoryNode baseDir) throws IOException {
        String[] path = this.getPath();
        DirectoryNode dir = baseDir;
        for (int i = 0; i < path.length - 1; ++i) {
            try {
                dir = (DirectoryNode)dir.getEntry(path[i]);
                continue;
            }
            catch (FileNotFoundException e) {
                dir.createDirectory(path[i]);
            }
        }
        this.generateData();
        try (UnsynchronizedByteArrayInputStream bais = new UnsynchronizedByteArrayInputStream(this.data);){
            dir.createDocument(path[path.length - 1], (InputStream)bais);
        }
    }

    protected abstract void generateData();

    public final byte[] getData() {
        return this.data;
    }

    protected final void setData(byte[] data) {
        this.data = (byte[])data.clone();
    }

    public final String[] getPath() {
        return this.path;
    }
}

