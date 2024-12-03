/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.FileResourceIterator;

public abstract class ArchiveScanner
extends DirectoryScanner {
    protected File srcFile;
    private Resource src;
    private Resource lastScannedResource;
    private Map<String, Resource> fileEntries = new TreeMap<String, Resource>();
    private Map<String, Resource> dirEntries = new TreeMap<String, Resource>();
    private Map<String, Resource> matchFileEntries = new TreeMap<String, Resource>();
    private Map<String, Resource> matchDirEntries = new TreeMap<String, Resource>();
    private String encoding;
    private boolean errorOnMissingArchive = true;

    public void setErrorOnMissingArchive(boolean errorOnMissingArchive) {
        this.errorOnMissingArchive = errorOnMissingArchive;
    }

    @Override
    public void scan() {
        if (this.src == null || !this.src.isExists() && !this.errorOnMissingArchive) {
            return;
        }
        super.scan();
    }

    public void setSrc(File srcFile) {
        this.setSrc(new FileResource(srcFile));
    }

    public void setSrc(Resource src) {
        this.src = src;
        FileProvider fp = src.as(FileProvider.class);
        if (fp != null) {
            this.srcFile = fp.getFile();
        }
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public String[] getIncludedFiles() {
        if (this.src == null) {
            return super.getIncludedFiles();
        }
        this.scanme();
        return this.matchFileEntries.keySet().toArray(new String[0]);
    }

    @Override
    public int getIncludedFilesCount() {
        if (this.src == null) {
            return super.getIncludedFilesCount();
        }
        this.scanme();
        return this.matchFileEntries.size();
    }

    @Override
    public String[] getIncludedDirectories() {
        if (this.src == null) {
            return super.getIncludedDirectories();
        }
        this.scanme();
        return this.matchDirEntries.keySet().toArray(new String[0]);
    }

    @Override
    public int getIncludedDirsCount() {
        if (this.src == null) {
            return super.getIncludedDirsCount();
        }
        this.scanme();
        return this.matchDirEntries.size();
    }

    Iterator<Resource> getResourceFiles(Project project) {
        if (this.src == null) {
            return new FileResourceIterator(project, this.getBasedir(), this.getIncludedFiles());
        }
        this.scanme();
        return this.matchFileEntries.values().iterator();
    }

    Iterator<Resource> getResourceDirectories(Project project) {
        if (this.src == null) {
            return new FileResourceIterator(project, this.getBasedir(), this.getIncludedDirectories());
        }
        this.scanme();
        return this.matchDirEntries.values().iterator();
    }

    public void init() {
        if (this.includes == null) {
            this.includes = new String[1];
            this.includes[0] = "**";
        }
        if (this.excludes == null) {
            this.excludes = new String[0];
        }
    }

    public boolean match(String path) {
        String vpath = path;
        if (!path.isEmpty() && (vpath = path.replace('/', File.separatorChar).replace('\\', File.separatorChar)).charAt(0) == File.separatorChar) {
            vpath = vpath.substring(1);
        }
        return this.isIncluded(vpath) && !this.isExcluded(vpath);
    }

    @Override
    public Resource getResource(String name) {
        if (this.src == null) {
            return super.getResource(name);
        }
        if (name.isEmpty()) {
            return new Resource("", true, Long.MAX_VALUE, true);
        }
        this.scanme();
        if (this.fileEntries.containsKey(name)) {
            return this.fileEntries.get(name);
        }
        if (this.dirEntries.containsKey(name = ArchiveScanner.trimSeparator(name))) {
            return this.dirEntries.get(name);
        }
        return new Resource(name);
    }

    protected abstract void fillMapsFromArchive(Resource var1, String var2, Map<String, Resource> var3, Map<String, Resource> var4, Map<String, Resource> var5, Map<String, Resource> var6);

    private void scanme() {
        if (!this.src.isExists() && !this.errorOnMissingArchive) {
            return;
        }
        Resource thisresource = new Resource(this.src.getName(), this.src.isExists(), this.src.getLastModified());
        if (this.lastScannedResource != null && this.lastScannedResource.getName().equals(thisresource.getName()) && this.lastScannedResource.getLastModified() == thisresource.getLastModified()) {
            return;
        }
        this.init();
        this.fileEntries.clear();
        this.dirEntries.clear();
        this.matchFileEntries.clear();
        this.matchDirEntries.clear();
        this.fillMapsFromArchive(this.src, this.encoding, this.fileEntries, this.matchFileEntries, this.dirEntries, this.matchDirEntries);
        this.lastScannedResource = thisresource;
    }

    protected static final String trimSeparator(String s) {
        return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
    }
}

